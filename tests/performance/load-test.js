import http from "k6/http";
import { check, sleep } from "k6";
import { Rate } from "k6/metrics";
import { htmlReport } from "https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js";
import { textSummary } from "https://jslib.k6.io/k6-summary/0.0.2/index.js";

const availabilityRate = new Rate("availability_rate");

export const options = {
  stages: [
    { duration: "1m", target: 300 },
    { duration: "3m", target: 300 },
    { duration: "1m", target: 0 },
  ],
  thresholds: {
    http_req_duration: ["p(95)<500", "p(99)<800"],
    http_req_failed: ["rate<0.01"],
    availability_rate: ["rate>0.99"],
  },
};

export default function () {
  const targetUrl = __ENV.TARGET_URL || "https://your-domain.com";
  const params = {
    headers: {
      "User-Agent": "k6-load-testing-agent",
      "Content-Type": "application/json",
    },
    timeout: "5s",
  };
  const res = http.get(targetUrl, params);
  const isSuccess = check(res, {
    "status is 200": (r) => r.status === 200,
    "response has data": (r) => r.body && r.body.length > 0,
  });
  availabilityRate.add(isSuccess);
  sleep(1);
}

export function handleSummary(data) {
  return {
    "performance-report.html": htmlReport(data),
    "stdout": textSummary(data, { indent: " ", enableColors: true }),
    "k6-summary.txt": textSummary(data, { indent: " ", enableColors: false }),
    "performance-report.json": JSON.stringify(data),
  };
}
