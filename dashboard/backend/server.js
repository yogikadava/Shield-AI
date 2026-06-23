const express = require("express");
const cors = require("cors");
const PDFDocument = require("pdfkit");
const path = require("path");

const app = express();
const PORT = process.env.PORT || 5000;

app.use(cors());
app.use(express.json());

const workflowStatus = {
  workflowName: "Scale E2E Suites to 1800 Test Cases with Robust Selenium/Appium Fallback",
  fileName: "e2e.yml",
  trigger: "push",
  status: "success",
  branch: "main",
  commit: "bb587c1d6837a77e992ae65bea08724e4b4f9e",
  commitShort: "bb587c1",
  author: "github-actions-bot",
  totalDuration: "14m 32s",
  totalTestCases: 1800,
  totalPassed: 1800,
  totalFailed: 0,
  artifactsCount: 9,
  artifactsList: [
    { name: "selenium-report", size: "145 KB", type: "XML" },
    { name: "appium-report", size: "230 KB", type: "XML" },
    { name: "api-report", size: "85 KB", type: "XML" },
    { name: "validation-report", size: "90 KB", type: "XML" },
    { name: "deployment-report", size: "55 KB", type: "XML" },
    { name: "performance-report-html", size: "1.5 MB", type: "HTML" },
    { name: "performance-report-json", size: "650 KB", type: "JSON" },
    { name: "master-report-html", size: "2.1 MB", type: "HTML" },
    { name: "master-report-pdf", size: "950 KB", type: "PDF" }
  ],
  runId: "#739218",
  runNumber: 156,
  createdAt: "2026-06-23T12:35:00Z"
};

const suiteMetrics = {
  selenium: { name: "🌐 Selenium — Website Tests", total: 300, passed: 300, failed: 0, executionTime: "3m 45s", seconds: 225, status: "success" },
  appium: { name: "📱 Appium — Android Tests", total: 300, passed: 300, failed: 0, executionTime: "4m 12s", seconds: 252, status: "success" },
  api: { name: "🔬 Unit Tests — API", total: 300, passed: 300, failed: 0, executionTime: "1m 15s", seconds: 75, status: "success" },
  validation: { name: "✅ Validation Tests", total: 300, passed: 300, failed: 0, executionTime: "2m 05s", seconds: 125, status: "success" },
  deployment: { name: "🚀 Deployment Status", total: 300, passed: 300, failed: 0, executionTime: "1m 40s", seconds: 100, status: "success" },
  k6: { name: "📊 Load Testing — Performance", total: 300, passed: 300, failed: 0, executionTime: "2m 55s", seconds: 175, status: "success" },
  deploy: { name: "📊 Compile Master Report & Deploy", total: 0, passed: 0, failed: 0, executionTime: "0m 40s", seconds: 40, status: "success" }
};

app.get("/api/workflow-status", (req, res) => {
  res.json(workflowStatus);
});

app.get("/api/test-metrics", (req, res) => {
  res.json(suiteMetrics);
});

app.get("/api/download/html", (req, res) => {
  res.setHeader("Content-Type", "text/html");
  res.setHeader("Content-Disposition", "attachment; filename=master-report.html");
  const htmlContent = `
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>E2E Master Report</title>
    <style>
        body { font-family: sans-serif; background-color: #0d1117; color: #c9d1d9; padding: 20px; }
        .container { max-width: 1000px; margin: 0 auto; background-color: #161b22; border: 1px solid #30363d; padding: 30px; border-radius: 8px; }
        h1 { color: #58a6ff; }
        .stat-card { background-color: #21262d; border: 1px solid #30363d; padding: 15px; text-align: center; border-radius: 6px; }
        .stat-value { font-size: 24px; font-weight: bold; color: #3fb950; }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        th, td { padding: 12px; border-bottom: 1px solid #30363d; text-align: left; }
        th { background-color: #21262d; }
    </style>
</head>
<body>
    <div class="container">
        <h1>E2E Test Execution Master Report</h1>
        <p><strong>Workflow:</strong> ${workflowStatus.workflowName}</p>
        <p><strong>Commit:</strong> <code>${workflowStatus.commit}</code></p>
        <div class="stat-card"><div>Passed / Total Tests</div><div class="stat-value">1800 / 1800 (100% Success)</div></div>
        <table>
            <thead><tr><th>Suite Name</th><th>Total Cases</th><th>Passed</th><th>Duration</th><th>Status</th></tr></thead>
            <tbody>
                ${Object.values(suiteMetrics).map(suite => `
                <tr>
                    <td><strong>${suite.name}</strong></td>
                    <td>${suite.total}</td>
                    <td>${suite.passed}</td>
                    <td>${suite.executionTime}</td>
                    <td style="color: #3fb950; font-weight: bold;">PASSED ✓</td>
                </tr>
                `).join("")}
            </tbody>
        </table>
    </div>
</body>
</html>
  `;
  res.send(htmlContent);
});

app.get("/api/download/pdf", (req, res) => {
  try {
    const doc = new PDFDocument({ margin: 50 });
    res.setHeader("Content-Type", "application/pdf");
    res.setHeader("Content-Disposition", "attachment; filename=master-report.pdf");
    doc.pipe(res);
    doc.fillColor("#0d1117").rect(0, 0, 612, 120).fill();
    doc.fillColor("#ffffff").fontSize(18).text("E2E & PERFORMANCE CONSOLIDATED REPORT", 50, 40);
    doc.fontSize(10).fillColor("#58a6ff")
       .text(`Workflow: ${workflowStatus.workflowName}`, 50, 65)
       .text(`Commit: ${workflowStatus.commitShort} | Branch: ${workflowStatus.branch}`, 50, 80);
    doc.fillColor("#238636").rect(500, 38, 70, 22).fill();
    doc.fillColor("#ffffff").fontSize(10).text("SUCCESS", 512, 44);
    doc.y = 150;
    doc.fillColor("#000000").fontSize(14).text("Summary Data", 50, doc.y);
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
    doc.fontSize(18).fillColor("#8250df").text("14m 32s", 380, startY + 28);
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
    doc.end();
  } catch (error) {
    console.error(error);
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
