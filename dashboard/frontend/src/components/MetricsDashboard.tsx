import React, { useState } from "react";
import { useWorkflowStore } from "../store/store";
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, CartesianGrid } from "recharts";
import { Download, FileText, CheckCircle2, HelpCircle, HardDrive, AlertTriangle, Users, Activity, Zap, Clock } from "lucide-react";

export const MetricsDashboard: React.FC = () => {
  const { jobs } = useWorkflowStore();
  const [downloading, setDownloading] = useState<string | null>(null);

  const suites = [
    { key: "selenium-website-tests", name: "🌐 Selenium — Website Tests", description: "Desktop and responsive browser tests with Xvfb fallback.", hasFallback: true, fallbackText: "Virtual Framebuffer (Xvfb)" },
    { key: "appium-android-tests", name: "📱 Appium — Android Tests", description: "Android application interface checks with Cloud Firebase Test Lab fallback.", hasFallback: true, fallbackText: "Firebase Test Lab Cloud Execution" },
    { key: "unit-tests-api", name: "🔬 Unit Tests — API", description: "Tests for all REST and gRPC gateway services." },
    { key: "validation-tests", name: "✅ Validation Tests", description: "JSON schema verification, compliance audits, mutation checks." },
    { key: "deployment-status", name: "🚀 Deployment Status", description: "Verify deployment health, API availability, DB connection and SSL certificates." },
    { key: "load-testing-performance", name: "📊 Load Testing — Performance", description: "K6 performance load testing under virtual user loads with latency SLAs." },
    { key: "compile-master-report", name: "📊 Compile Master Report & Deploy", description: "Compile consolidates and deploys master test execution outputs." }
  ];

  const totalTests = 1800;
  let totalPassed = 0;
  let totalFailed = 0;

  suites.forEach(suite => {
    const job = jobs[suite.key];
    if (job) {
      totalPassed += job.passedTests;
      totalFailed += job.failedTests;
    }
  });

  const passRate = totalTests > 0 ? ((totalPassed / totalTests) * 100).toFixed(1) : "0.0";

  const chartData = suites.map(suite => {
    const job = jobs[suite.key];
    return {
      name: suite.name.replace(/ —.*/, "").replace(/[🌐📱🔬✅🚀📊]\s*/, ""),
      duration: job ? job.seconds : 0,
      passed: job ? job.passedTests : 0
    };
  });

  const handleDownload = async (type: "pdf" | "html") => {
    setDownloading(type);
    try {
      window.location.href = `/api/download/${type}`;
    } catch (e) {
      console.error(e);
    } finally {
      setTimeout(() => setDownloading(null), 1500);
    }
  };

  return (
    <div className="flex-1 p-6 space-y-6 overflow-y-auto max-w-7xl mx-auto w-full">
      
      {/* 4 Stats Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="bg-github-card border border-github-border rounded-lg p-4 flex flex-col justify-between">
          <span className="text-xs font-semibold text-github-textMuted uppercase">Total Test Cases</span>
          <span className="text-3xl font-extrabold text-github-text mt-2 font-mono">{totalTests}</span>
          <span className="text-xs text-github-textMuted mt-1">Distributed across 6 jobs (300 each)</span>
        </div>
        <div className="bg-github-card border border-github-border rounded-lg p-4 flex flex-col justify-between">
          <span className="text-xs font-semibold text-github-textMuted uppercase">Passed Tests</span>
          <span className="text-3xl font-extrabold text-github-successGreen mt-2 font-mono">{totalPassed}</span>
          <span className="text-xs text-github-successGreen flex items-center gap-1 mt-1">
            <CheckCircle2 className="w-3.5 h-3.5" /> All run assertions successful
          </span>
        </div>
        <div className="bg-github-card border border-github-border rounded-lg p-4 flex flex-col justify-between">
          <span className="text-xs font-semibold text-github-textMuted uppercase">Test Pass Rate</span>
          <span className="text-3xl font-extrabold text-github-blue mt-2 font-mono">{passRate}%</span>
          <div className="w-full bg-github-border h-1.5 rounded-full overflow-hidden mt-2">
            <div style={{ width: `${passRate}%` }} className="h-full bg-github-blue transition-all duration-500" />
          </div>
        </div>
        <div className="bg-github-card border border-github-border rounded-lg p-4 flex flex-col justify-between">
          <span className="text-xs font-semibold text-github-textMuted uppercase">Pipeline Stage Status</span>
          <span className="text-3xl font-extrabold text-github-successGreen mt-2 font-mono">SUCCESS</span>
          <span className="text-xs text-github-textMuted mt-1">No failures detected</span>
        </div>
      </div>

      {/* Dedicated K6 Performance SLA Summary Panel */}
      <div className="bg-github-card border border-github-border rounded-lg p-5">
        <h3 className="text-sm font-semibold text-github-text mb-4 flex items-center gap-2">
          <Activity className="w-4 h-4 text-github-blue" /> 📊 K6 Performance Load Test SLA Metrics
        </h3>
        <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-7 gap-4 text-center font-mono">
          <div className="p-3 bg-github-bg border border-github-border rounded-lg">
            <Users className="w-4 h-4 mx-auto mb-1 text-github-textMuted" />
            <p className="text-[10px] text-github-textMuted uppercase">Virtual Users</p>
            <p className="text-base font-bold text-github-text mt-1">300 VUs</p>
          </div>
          <div className="p-3 bg-github-bg border border-github-border rounded-lg">
            <Clock className="w-4 h-4 mx-auto mb-1 text-github-textMuted" />
            <p className="text-[10px] text-github-textMuted uppercase">Duration</p>
            <p className="text-base font-bold text-github-text mt-1">5m 00s</p>
          </div>
          <div className="p-3 bg-github-bg border border-github-border rounded-lg">
            <Activity className="w-4 h-4 mx-auto mb-1 text-github-textMuted" />
            <p className="text-[10px] text-github-textMuted uppercase">Total Requests</p>
            <p className="text-base font-bold text-github-text mt-1">15,204</p>
          </div>
          <div className="p-3 bg-github-bg border border-github-border rounded-lg">
            <Zap className="w-4 h-4 mx-auto mb-1 text-github-textMuted" />
            <p className="text-[10px] text-github-textMuted uppercase">Peak RPS</p>
            <p className="text-base font-bold text-github-text mt-1">450 req/s</p>
          </div>
          <div className="p-3 bg-github-bg border border-github-border rounded-lg">
            <Clock className="w-4 h-4 mx-auto mb-1 text-github-textMuted" />
            <p className="text-[10px] text-github-textMuted uppercase">Avg Latency</p>
            <p className="text-base font-bold text-github-successGreen mt-1">112ms</p>
          </div>
          <div className="p-3 bg-github-bg border border-github-border rounded-lg">
            <CheckCircle2 className="w-4 h-4 mx-auto mb-1 text-github-successGreen" />
            <p className="text-[10px] text-github-textMuted uppercase">Success Rate</p>
            <p className="text-base font-bold text-github-successGreen mt-1">100.00%</p>
          </div>
          <div className="p-3 bg-github-bg border border-github-border rounded-lg">
            <AlertTriangle className="w-4 h-4 mx-auto mb-1 text-github-textMuted" />
            <p className="text-[10px] text-github-textMuted uppercase">Error Rate</p>
            <p className="text-base font-bold text-github-successGreen mt-1">0.00%</p>
          </div>
        </div>
      </div>

      {/* Main Grid: Charts & Reports */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Recharts Column */}
        <div className="lg:col-span-2 bg-github-card border border-github-border rounded-lg p-5">
          <h3 className="text-sm font-semibold text-github-text mb-4">Stage Durations (Seconds)</h3>
          <div className="h-64">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={chartData} margin={{ left: -10, right: 10, bottom: 0, top: 10 }}>
                <CartesianGrid strokeDasharray="3 3" stroke="#21262d" />
                <XAxis dataKey="name" stroke="#8b949e" fontSize={11} tickLine={false} />
                <YAxis stroke="#8b949e" fontSize={11} tickLine={false} />
                <Tooltip contentStyle={{ backgroundColor: "#161b22", borderColor: "#30363d", color: "#c9d1d9" }} cursor={{ fill: "rgba(48, 54, 61, 0.2)" }} />
                <Bar dataKey="duration" fill="#238636" radius={[4, 4, 0, 0]} maxBarSize={45} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Artifacts & Downloads Panel */}
        <div className="bg-github-card border border-github-border rounded-lg p-5 flex flex-col justify-between">
          <div>
            <h3 className="text-sm font-semibold text-github-text mb-1 flex items-center gap-1.5">
              <HardDrive className="w-4 h-4 text-github-blue" /> Generated Reports & Artifacts
            </h3>
            <p className="text-xs text-github-textMuted mb-4">Click below to download compiled test runner results.</p>
            <div className="space-y-3">
              {/* HTML Report */}
              <div className="flex items-center justify-between p-3 rounded-lg border border-github-border bg-github-bg hover:bg-github-card transition-colors">
                <div className="flex items-center gap-2.5 min-w-0">
                  <FileText className="w-5 h-5 text-github-blue flex-shrink-0" />
                  <div className="truncate">
                    <p className="text-xs font-semibold text-github-text truncate">performance-report.html</p>
                    <p className="text-[10px] text-github-textMuted">Interactive HTML Summary • 1.5 MB</p>
                  </div>
                </div>
                <button onClick={() => handleDownload("html")} disabled={downloading !== null} className="p-2 bg-github-card hover:bg-github-border text-github-text rounded border border-github-border transition-colors flex items-center justify-center">
                  <Download className="w-4 h-4" />
                </button>
              </div>
              {/* PDF Report */}
              <div className="flex items-center justify-between p-3 rounded-lg border border-github-border bg-github-bg hover:bg-github-card transition-colors">
                <div className="flex items-center gap-2.5 min-w-0">
                  <FileText className="w-5 h-5 text-github-successGreen flex-shrink-0" />
                  <div className="truncate">
                    <p className="text-xs font-semibold text-github-text truncate">performance-report.pdf</p>
                    <p className="text-[10px] text-github-textMuted">Official Executive PDF Report • 850 KB</p>
                  </div>
                </div>
                <button onClick={() => handleDownload("pdf")} disabled={downloading !== null} className="p-2 bg-github-card hover:bg-github-border text-github-text rounded border border-github-border transition-colors flex items-center justify-center">
                  <Download className="w-4 h-4" />
                </button>
              </div>
              {/* TXT Report */}
              <div className="flex items-center justify-between p-3 rounded-lg border border-github-border bg-github-bg hover:bg-github-card transition-colors opacity-70">
                <div className="flex items-center gap-2.5 min-w-0">
                  <FileText className="w-5 h-5 text-github-textMuted flex-shrink-0" />
                  <div className="truncate">
                    <p className="text-xs font-semibold text-github-text truncate">k6-summary.txt</p>
                    <p className="text-[10px] text-github-textMuted">Raw Terminal Export • 12 KB</p>
                  </div>
                </div>
                <button disabled className="p-2 bg-github-card hover:bg-github-border text-github-textMuted rounded border border-github-border transition-colors flex items-center justify-center cursor-not-allowed">
                  <Download className="w-4 h-4" />
                </button>
              </div>
            </div>
          </div>
          <div className="mt-4 pt-4 border-t border-github-border text-[11px] text-github-textMuted flex items-center gap-1 bg-github-terminalBg/50 p-2 rounded">
            <HelpCircle className="w-3.5 h-3.5 text-github-blue flex-shrink-0" />
            <span>Files are hosted locally on Node.js dashboard backend.</span>
          </div>
        </div>
      </div>

      {/* Robust Fallback Notices */}
      <div className="bg-github-bgHeader border border-github-border rounded-lg p-4">
        <h4 className="text-sm font-semibold text-github-text mb-2 flex items-center gap-1.5">
          <AlertTriangle className="w-4 h-4 text-github-warning" /> Robust CI/CD Fallback Systems Active
        </h4>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mt-3">
          <div className="bg-github-card border border-github-border rounded-lg p-3 text-xs">
            <h5 className="font-bold text-github-successGreen">Selenium Browser Fallback:</h5>
            <p className="text-github-textMuted mt-1 leading-relaxed">
              If headless web client testing fails due to session connection failures, runner automatically falls back to spawning a virtual frame buffer via <code className="text-github-blue">Xvfb (X Virtual Framebuffer)</code> to execute web steps with simulated display resolution.
            </p>
          </div>
          <div className="bg-github-card border border-github-border rounded-lg p-3 text-xs">
            <h5 className="font-bold text-github-warning">Appium Mobile Emulator Fallback:</h5>
            <p className="text-github-textMuted mt-1 leading-relaxed">
              If local Android Emulator launching times out or driver initialization fails on macOS runner, app package will be automatically bundled and routed to execute remotely on <code className="text-github-blue">Firebase Test Lab Cloud Devices</code> to prevent build failures.
            </p>
          </div>
        </div>
      </div>

      {/* Individual Suite Breakdown List */}
      <div>
        <h3 className="text-sm font-semibold text-github-text mb-4">Detailed Test Suite Breakdown</h3>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {suites.map(suite => {
            const job = jobs[suite.key];
            const activeProgress = job ? job.progress : 0;
            const completed = job?.status === "success";
            return (
              <div key={suite.key} className="bg-github-card border border-github-border hover:border-github-textMuted rounded-lg p-4 transition-all duration-200">
                <div className="flex justify-between items-start mb-2">
                  <h4 className="text-xs font-bold text-github-text truncate">{suite.name}</h4>
                  {completed ? (
                    <span className="px-2 py-0.5 rounded text-[10px] bg-github-successGreenMuted text-github-successGreen font-mono">PASSED</span>
                  ) : job?.status === "running" ? (
                    <span className="px-2 py-0.5 rounded text-[10px] bg-github-yellowMuted text-github-warning font-mono animate-pulse">RUNNING</span>
                  ) : (
                    <span className="px-2 py-0.5 rounded text-[10px] bg-github-border text-github-textMuted font-mono">PENDING</span>
                  )}
                </div>
                <p className="text-[11px] text-github-textMuted leading-relaxed mb-3 h-8 line-clamp-2">{suite.description}</p>
                <div className="space-y-1 text-[11px] font-mono">
                  <div className="flex justify-between"><span className="text-github-textMuted">Test Case Volume:</span><span className="text-github-text">{job ? job.totalTests : 300} cases</span></div>
                  <div className="flex justify-between"><span className="text-github-textMuted">Pass Rate:</span><span className="text-github-successGreen">{job && job.totalTests > 0 ? ((job.passedTests / job.totalTests) * 100).toFixed(0) : 0}%</span></div>
                  <div className="flex justify-between"><span className="text-github-textMuted">Execution Time:</span><span className="text-github-text">{job ? job.duration : "0s"}</span></div>
                </div>
                {job && job.status === "running" && (
                  <div className="w-full bg-github-border h-1 rounded-full overflow-hidden mt-3">
                    <div style={{ width: `${activeProgress}%` }} className="h-full bg-github-warning transition-all duration-150" />
                  </div>
                )}
              </div>
            );
          })}
        </div>
      </div>
    </div>
);
};
