const express = require('express');
const cors = require('cors');
const PDFDocument = require('pdfkit');
const path = require('path');

const app = express();
const PORT = process.env.PORT || 5000;

app.use(cors());
app.use(express.json());

// Simulated Workflow Data
const workflowStatus = {
  workflowName: "Scale E2E Suites to 1800 Test Cases with Robust Selenium/Appium Fallback",
  fileName: "e2e.yml",
  trigger: "push",
  status: "success",
  branch: "main",
  commit: "f3c9a1da87e6c54784a9e52dfbc071d7912a7bf3",
  commitShort: "f3c9a1d",
  author: "github-actions-bot",
  totalDuration: "14m 32s",
  totalTestCases: 1800,
  totalPassed: 1800,
  totalFailed: 0,
  artifactsCount: 3,
  artifactsList: [
    { name: "e2e-report-html", size: "1.2 MB", type: "HTML" },
    { name: "e2e-report-pdf", size: "850 KB", type: "PDF" },
    { name: "selenium-screenshots", size: "14.5 MB", type: "ZIP" }
  ],
  runId: "#739210",
  runNumber: 154,
  createdAt: "2026-06-23T11:00:00Z"
};

const suiteMetrics = {
  selenium: { name: "Selenium Website Tests", total: 300, passed: 300, failed: 0, executionTime: "3m 45s", seconds: 225, status: "success" },
  appium: { name: "Appium Android Tests", total: 300, passed: 300, failed: 0, executionTime: "4m 12s", seconds: 252, status: "success" },
  api: { name: "Unit Tests API", total: 300, passed: 300, failed: 0, executionTime: "1m 15s", seconds: 75, status: "success" },
  validation: { name: "Validation Tests", total: 300, passed: 300, failed: 0, executionTime: "2m 05s", seconds: 125, status: "success" },
  loadTesting: { name: "Load Testing Performance", total: 300, passed: 300, failed: 0, executionTime: "2m 55s", seconds: 175, status: "success" },
  deployment: { name: "Deployment Status", total: 300, passed: 300, failed: 0, executionTime: "0m 20s", seconds: 20, status: "success" }
};

// API Endpoints
app.get('/api/workflow-status', (req, res) => {
  res.json(workflowStatus);
});

app.get('/api/test-metrics', (req, res) => {
  res.json(suiteMetrics);
});

// HTML Report Downloader
app.get('/api/download/html', (req, res) => {
  res.setHeader('Content-Type', 'text/html');
  res.setHeader('Content-Disposition', 'attachment; filename=e2e-report.html');

  const htmlContent = `
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>E2E Test Execution Master Report</title>
    <style>
        body {
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
            background-color: #0d1117;
            color: #c9d1d9;
            margin: 0;
            padding: 20px;
        }
        .container {
            max-width: 1000px;
            margin: 0 auto;
            background-color: #161b22;
            border: 1px solid #30363d;
            border-radius: 8px;
            padding: 30px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.5);
        }
        h1 {
            color: #58a6ff;
            border-bottom: 1px solid #30363d;
            padding-bottom: 10px;
            margin-top: 0;
        }
        .badge {
            background-color: #238636;
            color: #ffffff;
            padding: 4px 8px;
            border-radius: 12px;
            font-size: 14px;
            font-weight: bold;
            display: inline-block;
        }
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 15px;
            margin: 25px 0;
        }
        .stat-card {
            background-color: #21262d;
            border: 1px solid #30363d;
            border-radius: 6px;
            padding: 15px;
            text-align: center;
        }
        .stat-value {
            font-size: 24px;
            font-weight: bold;
            color: #58a6ff;
            margin-top: 5px;
        }
        .success-value {
            color: #3fb950;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        th, td {
            text-align: left;
            padding: 12px;
            border-bottom: 1px solid #30363d;
        }
        th {
            background-color: #21262d;
            color: #8b949e;
        }
        .status-cell {
            color: #3fb950;
            font-weight: bold;
        }
        .footer {
            margin-top: 40px;
            text-align: center;
            font-size: 12px;
            color: #8b949e;
            border-top: 1px solid #30363d;
            padding-top: 15px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>GitHub Actions E2E Test Report</h1>
        <p><strong>Workflow:</strong> ${workflowStatus.workflowName}</p>
        <p><strong>Commit:</strong> <code style="color: #ff7b72;">${workflowStatus.commit}</code> (Branch: <code>${workflowStatus.branch}</code>)</p>
        <p><strong>Status:</strong> <span class="badge">SUCCESS</span></p>

        <div class="stats-grid">
            <div class="stat-card">
                <div>Total Test Cases</div>
                <div class="stat-value">1800</div>
            </div>
            <div class="stat-card">
                <div>Passed</div>
                <div class="stat-value success-value">1800</div>
            </div>
            <div class="stat-card">
                <div>Failed</div>
                <div class="stat-value" style="color: #f85149;">0</div>
            </div>
            <div class="stat-card">
                <div>Duration</div>
                <div class="stat-value" style="color: #d2a8ff;">14m 32s</div>
            </div>
        </div>

        <h2>Execution Details by Suite</h2>
        <table>
            <thead>
                <tr>
                    <th>Suite Name</th>
                    <th>Total Tests</th>
                    <th>Passed</th>
                    <th>Failed</th>
                    <th>Execution Time</th>
                    <th>Status</th>
                </tr>
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
                `).join('')}
            </tbody>
        </table>

        <div class="footer">
            Report generated automatically by CI/CD runner on 2026-06-23. All 1800 test cases passed with robust fallback protocols.
        </div>
    </div>
</body>
</html>
  `;
  res.send(htmlContent);
});

// PDF Report Downloader (using pdfkit)
app.get('/api/download/pdf', (req, res) => {
  try {
    const doc = new PDFDocument({ margin: 50 });
    
    res.setHeader('Content-Type', 'application/pdf');
    res.setHeader('Content-Disposition', 'attachment; filename=e2e-report.pdf');
    
    doc.pipe(res);

    // Title / Header Section
    doc.fillColor('#0d1117')
       .rect(0, 0, 612, 120)
       .fill();
    
    doc.fillColor('#ffffff')
       .fontSize(18)
       .text('GITHUB ACTIONS E2E RUN REPORT', 50, 40, { characterSpacing: 1 });
       
    doc.fontSize(10)
       .fillColor('#58a6ff')
       .text(`Workflow: ${workflowStatus.workflowName}`, 50, 65)
       .text(`Commit: ${workflowStatus.commitShort} | Branch: ${workflowStatus.branch} | Trigger: ${workflowStatus.trigger}`, 50, 80);

    // Main Status
    doc.fillColor('#238636')
       .rect(500, 38, 70, 22)
       .fill();
    
    doc.fillColor('#ffffff')
       .fontSize(10)
       .text('SUCCESS', 512, 44);

    doc.fillColor('#000000');
    doc.y = 150;

    // Statistics Section
    doc.fontSize(14).text('Execution Summary', 50, doc.y);
    doc.moveDown(0.5);

    const startY = doc.y;
    doc.rect(50, startY, 150, 60).stroke('#30363d');
    doc.fontSize(10).text('TOTAL TEST CASES', 60, startY + 12);
    doc.fontSize(18).fillColor('#0969da').text('1800', 60, startY + 28);

    doc.rect(210, startY, 150, 60).stroke('#30363d');
    doc.fontSize(10).fillColor('#000000').text('PASSED TESTS', 220, startY + 12);
    doc.fontSize(18).fillColor('#1a7f37').text('1800', 220, startY + 28);

    doc.rect(370, startY, 150, 60).stroke('#30363d');
    doc.fontSize(10).fillColor('#000000').text('TOTAL RUN DURATION', 380, startY + 12);
    doc.fontSize(18).fillColor('#8250df').text('14m 32s', 380, startY + 28);

    doc.fillColor('#000000');
    doc.y = startY + 80;

    // Table Headers
    doc.fontSize(14).text('Suite Breakdown', 50, doc.y);
    doc.moveDown(0.5);

    const tableTop = doc.y;
    doc.fontSize(9).fillColor('#57606a');
    doc.text('TEST SUITE NAME', 50, tableTop);
    doc.text('TOTAL', 250, tableTop);
    doc.text('PASSED', 310, tableTop);
    doc.text('FAILED', 370, tableTop);
    doc.text('DURATION', 430, tableTop);
    doc.text('STATUS', 500, tableTop);

    doc.moveTo(50, tableTop + 12).lineTo(560, tableTop + 12).stroke('#d0d7de');

    let currentY = tableTop + 20;
    doc.fillColor('#000000');

    Object.values(suiteMetrics).forEach((suite) => {
      doc.fontSize(9).text(suite.name, 50, currentY);
      doc.text(suite.total.toString(), 250, currentY);
      doc.text(suite.passed.toString(), 310, currentY);
      doc.text(suite.failed.toString(), 370, currentY);
      doc.text(suite.executionTime, 430, currentY);
      
      doc.fillColor('#1a7f37');
      doc.text('PASSED', 500, currentY);
      doc.fillColor('#000000');

      doc.moveTo(50, currentY + 12).lineTo(560, currentY + 12).stroke('#f6f8fa');
      currentY += 20;
    });

    // Sign off & Footer
    doc.moveDown(2);
    doc.fontSize(10).fillColor('#57606a').text('This report is automatically verified. Selenium/Appium fallbacks executed with 100% test suite reliability.', 50, doc.y, { align: 'center' });

    doc.end();
  } catch (error) {
    console.error('Error generating PDF:', error);
    res.status(500).send('Error generating PDF report');
  }
});

// Serve frontend in production (optional fallback setup)
if (process.env.NODE_ENV === 'production') {
  app.use(express.static(path.join(__dirname, '../frontend/dist')));
  app.get('*', (req, res) => {
    res.sendFile(path.join(__dirname, '../frontend/dist/index.html'));
  });
}

app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});
