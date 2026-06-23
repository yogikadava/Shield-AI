const express = require("express");
const cors = require("cors");
const PDFDocument = require("pdfkit");
const path = require("path");

const app = express();
const PORT = process.env.PORT || 5000;

app.use(cors());
app.use(express.json());

// Simulated Workflow Data (Streamlined to 6 main stages totaling 1800 tests)
const workflowStatus = {
  workflowName: "Scale E2E Suites to 1800 Test Cases with Robust Selenium/Appium Fallback",
  fileName: "e2e.yml",
  trigger: "push",
  status: "success",
  branch: "main",
  commit: "d22f357b98d227f54c8651a56112a9e52dfbc071",
  commitShort: "d22f357",
  author: "github-actions-bot",
  totalDuration: "11m 47s",
  totalTestCases: 1800,
  totalPassed: 1800,
  totalFailed: 0,
  artifactsCount: 3,
  artifactsList: [
    { name: "performance-report-html", size: "1.5 MB", type: "HTML" },
    { name: "performance-report-json", size: "650 KB", type: "JSON" },
    { name: "k6-summary-txt", size: "12 KB", type: "TXT" }
  ],
  runId: "#739215",
  runNumber: 155,
  createdAt: "2026-06-23T12:00:00Z"
};

const suiteMetrics = {
  selenium: { name: "Selenium — Website Tests", total: 300, passed: 300, failed: 0, executionTime: "3m 45s", seconds: 225, status: "success" },
  appium: { name: "Appium — Android Tests", total: 300, passed: 300, failed: 0, executionTime: "4m 12s", seconds: 252, status: "success" },
  api: { name: "Unit Tests — API", total: 300, passed: 300, failed: 0, executionTime: "1m 15s", seconds: 75, status: "success" },
  validation: { name: "Validation Tests", total: 300, passed: 300, failed: 0, executionTime: "2m 05s", seconds: 125, status: "success" },
  k6: { name: "📊 K6 Load Testing — Performance", total: 300, passed: 300, failed: 0, executionTime: "2m 55s", seconds: 175, status: "success" },
  deploy: { name: "Compile Master Report & Deploy", total: 300, passed: 300, failed: 0, executionTime: "0m 40s", seconds: 40, status: "success" }
};

// API Endpoints
app.get("/api/workflow-status", (req, res) => {
  res.json(workflowStatus);
});

app.get("/api/test-metrics", (req, res) => {
  res.json(suiteMetrics);
});

// HTML Report Downloader
app.get("/api/download/html", (req, res) => {
  res.setHeader("Content-Type", "text/html");
  res.setHeader("Content-Disposition", "attachment; filename=performance-report.html");

  const htmlContent = `
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>E2E and Performance Load Test Master Report</title>
    <style>
        body { font-family: -apple-system, BlinkMacSystemFont, sans-serif; background-color: #0d1117; color: #c9d1d9; padding: 20px; }
        .container { max-width: 1000px; margin: 0 auto; background-color: #161b22; border: 1px solid #30363d; border-radius: 8px; padding: 30px; }
        h1 { color: #58a6ff; border-bottom: 1px solid #30363d; padding-bottom: 10px; }
        .badge { background-color: #238636; color: #ffffff; padding: 4px 8px; border-radius: 12px; font-size: 14px; font-weight: bold; display: inline-block; }
        .stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 15px; margin: 25px 0; }
        .stat-card { background-color: #21262d; border: 1px solid #30363d; border-radius: 6px; padding: 15px; text-align: center; }
        .stat-value { font-size: 24px; font-weight: bold; color: #58a6ff; }
        .success-value { color: #3fb950; }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        th, td { text-align: left; padding: 12px; border-bottom: 1px solid #30363d; }
        th { background-color: #21262d; color: #8b949e; }
        .status-cell { color: #3fb950; font-weight: bold; }
        .footer { margin-top: 40px; text-align: center; font-size: 12px; color: #8b949e; border-top: 1px solid #30363d; padding-top: 15px; }
    </style>
</head>
<body>
    <div class="container">
        <h1>K6 Load Testing & E2E Master Report</h1>
        <p><strong>Workflow Name:</strong> ${workflowStatus.workflowName}</p>
        <p><strong>Commit:</strong> <code>${workflowStatus.commit}</code></p>
        <p><strong>Status:</strong> <span class="badge">SUCCESS</span></p>
        <div class="stats-grid">
            <div class="stat-card"><div>Total Test Cases</div><div class="stat-value">1800</div></div>
            <div class="stat-card"><div>Passed Checks</div><div class="stat-value success-value">1800</div></div>
            <div class="stat-card"><div>Failed Checks</div><div class="stat-value" style="color: #f85149;">0</div></div>
            <div class="stat-card"><div>Test Duration</div><div class="stat-value" style="color: #d2a8ff;">11m 47s</div></div>
        </div>
        <h2>Execution Details by Suite</h2>
        <table>
            <thead>
                <tr><th>Suite Name</th><th>Total Tests</th><th>Passed</th><th>Failed</th><th>Execution Time</th><th>Status</th></tr>
            </thead>
            <tbody>
                ${Object.values(suiteMetrics).map(suite => `
                <tr>
                    <td><strong>${suite.name}</strong></td>
                    <td>${suite.total}</td>
                    <td>${suite.passed}</td>
                    <td>${suite.failed}</td>
                    <td>${suite.executionTime}</td>
                    <td class="status-cell">PASSED ✓</td>
                </tr>
                `).join("")}
            </tbody>
        </table>
        <div class="footer">Report generated automatically by K6 Performance Actions Runner.</div>
    </div>
</body>
</html>
  `;
  res.send(htmlContent);
});

// PDF Report Downloader (using pdfkit)
app.get("/api/download/pdf", (req, res) => {
  try {
    const doc = new PDFDocument({ margin: 50 });
    res.setHeader("Content-Type", "application/pdf");
    res.setHeader("Content-Disposition", "attachment; filename=performance-report.pdf");
    doc.pipe(res);

    doc.fillColor("#0d1117").rect(0, 0, 612, 120).fill();
    doc.fillColor("#ffffff").fontSize(18).text("K6 PERFORMANCE & E2E MASTER REPORT", 50, 40);
    doc.fontSize(10).fillColor("#58a6ff")
       .text(`Workflow: ${workflowStatus.workflowName}`, 50, 65)
       .text(`Commit: ${workflowStatus.commitShort} | Branch: ${workflowStatus.branch} | Trigger: ${workflowStatus.trigger}`, 50, 80);

    doc.fillColor("#238636").rect(500, 38, 70, 22).fill();
    doc.fillColor("#ffffff").fontSize(10).text("SUCCESS", 512, 44);
    doc.y = 150;

    doc.fillColor("#000000").fontSize(14).text("Execution Summary", 50, doc.y);
    doc.moveDown(0.5);
    const startY = doc.y;
    doc.rect(50, startY, 150, 60).stroke("#30363d");
    doc.fontSize(10).text("TOTAL TEST CASES", 60, startY + 12);
    doc.fontSize(18).fillColor("#0969da").text("1800", 60, startY + 28);
    
    doc.rect(210, startY, 150, 60).stroke("#30363d");
    doc.fontSize(10).fillColor("#000000").text("PASSED TESTS", 220, startY + 12);
    doc.fontSize(18).fillColor("#1a7f37").text("1800", 220, startY + 28);
    
    doc.rect(370, startY, 150, 60).stroke("#30363d");
    doc.fontSize(10).fillColor("#000000").text("TOTAL RUN TIME", 380, startY + 12);
    doc.fontSize(18).fillColor("#8250df").text("11m 47s", 380, startY + 28);

    doc.fillColor("#000000");
    doc.y = startY + 80;
    doc.fontSize(14).text("Suite Breakdown", 50, doc.y);
    doc.moveDown(0.5);
    const tableTop = doc.y;
    doc.fontSize(9).fillColor("#57606a");
    doc.text("TEST SUITE NAME", 50, tableTop);
    doc.text("TOTAL", 250, tableTop);
    doc.text("PASSED", 310, tableTop);
    doc.text("FAILED", 370, tableTop);
    doc.text("DURATION", 430, tableTop);
    doc.text("STATUS", 500, tableTop);
    doc.moveTo(50, tableTop + 12).lineTo(560, tableTop + 12).stroke("#d0d7de");

    let currentY = tableTop + 20;
    doc.fillColor("#000000");
    Object.values(suiteMetrics).forEach((suite) => {
      doc.fontSize(9).text(suite.name, 50, currentY);
      doc.text(suite.total.toString(), 250, currentY);
      doc.text(suite.passed.toString(), 310, currentY);
      doc.text(suite.failed.toString(), 370, currentY);
      doc.text(suite.executionTime, 430, currentY);
      doc.fillColor("#1a7f37").text("PASSED", 500, currentY);
      doc.fillColor("#000000");
      doc.moveTo(50, currentY + 12).lineTo(560, currentY + 12).stroke("#f6f8fa");
      currentY += 20;
    });
    doc.moveDown(2);
    doc.fontSize(10).fillColor("#57606a").text("Report verified. All K6 load test scenarios met the SLA targets successfully.", 50, doc.y, { align: "center" });
    doc.end();
  } catch (error) {
    console.error("Error generating PDF:", error);
    res.status(500).send("Error generating PDF report");
  }
});

if (process.env.NODE_ENV === "production") {
  app.use(express.static(path.join(__dirname, "../frontend/dist")));
  app.get("*", (req, res) => {
    res.sendFile(path.join(__dirname, "../frontend/dist/index.html"));
  });
}

app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});
