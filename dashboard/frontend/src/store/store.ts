import { create } from "zustand";

export interface Job {
  id: string;
  name: string;
  totalTests: number;
  passedTests: number;
  failedTests: number;
  duration: string;
  seconds: number;
  status: "waiting" | "running" | "success" | "failed";
  dependencies: string[];
  progress: number;
  logs: string[];
}

interface WorkflowState {
  jobs: Record<string, Job>;
  selectedJobId: string | null;
  playbackState: "idle" | "running" | "paused" | "completed";
  playbackSpeed: 1 | 2 | 5;
  activeTab: "summary" | "logs" | "yaml";
  totalDurationSeconds: number;
  setPlaybackState: (state: "idle" | "running" | "paused" | "completed") => void;
  setPlaybackSpeed: (speed: 1 | 2 | 5) => void;
  setSelectedJobId: (id: string | null) => void;
  setActiveTab: (tab: "summary" | "logs" | "yaml") => void;
  resetWorkflow: () => void;
  tickWorkflow: () => void;
}

const initialJobs: Record<string, Job> = {
  "selenium-website-tests": {
    id: "selenium-website-tests",
    name: "🌐 Selenium — Website Tests (300)",
    totalTests: 300,
    passedTests: 0,
    failedTests: 0,
    duration: "0s",
    seconds: 225,
    status: "waiting",
    dependencies: [],
    progress: 0,
    logs: []
  },
  "appium-android-tests": {
    id: "appium-android-tests",
    name: "📱 Appium — Android Tests (300)",
    totalTests: 300,
    passedTests: 0,
    failedTests: 0,
    duration: "0s",
    seconds: 252,
    status: "waiting",
    dependencies: [],
    progress: 0,
    logs: []
  },
  "unit-tests-api": {
    id: "unit-tests-api",
    name: "🔬 Unit Tests — API (300)",
    totalTests: 300,
    passedTests: 0,
    failedTests: 0,
    duration: "0s",
    seconds: 75,
    status: "waiting",
    dependencies: [],
    progress: 0,
    logs: []
  },
  "validation-tests": {
    id: "validation-tests",
    name: "✅ Validation Tests (300)",
    totalTests: 300,
    passedTests: 0,
    failedTests: 0,
    duration: "0s",
    seconds: 125,
    status: "waiting",
    dependencies: [],
    progress: 0,
    logs: []
  },
  "deployment-status": {
    id: "deployment-status",
    name: "🚀 Deployment Status (300)",
    totalTests: 300,
    passedTests: 0,
    failedTests: 0,
    duration: "0s",
    seconds: 100,
    status: "waiting",
    dependencies: [],
    progress: 0,
    logs: []
  },
  "load-testing-performance": {
    id: "load-testing-performance",
    name: "📊 Load Testing — Performance (300)",
    totalTests: 300,
    passedTests: 0,
    failedTests: 0,
    duration: "0s",
    seconds: 175,
    status: "waiting",
    dependencies: [],
    progress: 0,
    logs: []
  },
  "compile-master-report": {
    id: "compile-master-report",
    name: "📊 Compile Master Report & Deploy",
    totalTests: 0,
    passedTests: 0,
    failedTests: 0,
    duration: "0s",
    seconds: 40,
    status: "waiting",
    dependencies: [
      "selenium-website-tests",
      "appium-android-tests",
      "unit-tests-api",
      "validation-tests",
      "deployment-status",
      "load-testing-performance"
    ],
    progress: 0,
    logs: []
  }
};

const jobLogsTemplate: Record<string, string[]> = {
  "selenium-website-tests": [
    "Setting up Headless Chrome environment...",
    "Warning: Headless Chrome launch timeout. Spawning Virtual Framebuffer (Xvfb)...",
    "Xvfb instance spawned on display :99 successfully.",
    "Running website selenium integration suites (300 test cases)...",
    "✓ Home page layout and responsive breakpoints validation",
    "✓ Auth forms validation and password strength indicators",
    "✓ Navigation bar links and active menu highlighting",
    "All 300 browser verification actions completed."
  ],
  "appium-android-tests": [
    "Launching local Android Emulator (API level 33)...",
    "Warning: ADB emulator connection timeout. Appium Fallback activated...",
    "Submitting APK to Firebase Test Lab (Cloud Execution)...",
    "Test Lab device queue acquired: running on Google Pixel 6 (Android 13)...",
    "✓ Splash screen rendering and auto-redirect sequence",
    "✓ Sign-in screen validation with bio-auth prompt",
    "All 300 Mobile E2E assertions passed."
  ],
  "unit-tests-api": [
    "Initializing API test runner environment...",
    "Running API unit test suites (300 test cases)...",
    "✓ GET /api/v1/auth/session - Status 200 OK [12ms]",
    "✓ POST /api/v1/auth/login - Status 200 OK [45ms]",
    "✓ GET /api/v1/users/profile - Status 200 OK [15ms]",
    "✓ POST /api/v1/users/update - Status 200 OK [32ms]",
    "✓ GET /api/v1/projects - Status 200 OK [18ms]",
    "✓ POST /api/v1/projects/create - Status 201 Created [55ms]",
    "✓ GET /api/v1/health - Status 200 OK [5ms]",
    "All 300 API endpoints unit test assertions passed."
  ],
  "validation-tests": [
    "Initializing schema structural validators...",
    "Validating JSON Schema compliance of 300 endpoint payloads...",
    "✓ Schema structures matched spec V7 successfully.",
    "✓ Swagger definitions and production code interfaces match.",
    "All 300 validation assertions completed successfully."
  ],
  "deployment-status": [
    "Checking target deployment environment variables...",
    "Verifying SSL/TLS certificate validity for https://your-domain.com...",
    "✓ TLS security handshake and DNS verification successful.",
    "Performing health checks on API gateway...",
    "✓ GET https://api.your-domain.com/health - Status 200 OK [14ms]",
    "Verifying database connectivity pool...",
    "✓ DB Connection Pool active (15/50 active connections).",
    "Running deployment status verification (300 assertions)...",
    "✓ 300 deployment audit checks passed."
  ],
  "load-testing-performance": [
    "Initializing Grafana k6 engine...",
    "SLA checks configured: availability > 99%, error rate < 1%, p(95) < 500ms",
    "Ramping up virtual users to 300 VUs in 10s...",
    "Running load test metrics targeting https://your-domain.com...",
    "📊 Metrics gathered:",
    "  - http_req_duration: avg=112ms, p(95)=184ms, p(99)=295ms (PASSED)",
    "  - http_req_failed: 0.00% (0 errors out of 15,204 requests) (PASSED)",
    "  - availability_rate: 100.00% (PASSED)",
    "Saving load test outputs (performance-report.json, k6-summary.txt)...",
    "All 300 performance checks and SLA thresholds satisfied."
  ],
  "compile-master-report": [
    "Downloading reports from API (300), Selenium (300), Appium (300), Validation (300), Deployment Status (300), and Performance (300)...",
    "Consolidating 1800 test cases total...",
    "Running post-deployment integrity verification (300 assertions)...",
    "✓ Staging pod replica check: 6/6 pods online",
    "✓ TLS security handshake and DNS verification",
    "Generating consolidated HTML and PDF Master Reports...",
    "✓ Artifact generated: performance-report.html (1.5 MB)",
    "✓ Artifact generated: performance-report.json (650 KB)",
    "✓ Artifact generated: k6-summary.txt (12 KB)",
    "Workflow completed successfully with green success badges! (1800/1800 passed)"
  ]
};

const formatTime = (sec: number): string => {
  if (sec < 60) return `${sec}s`;
  const m = Math.floor(sec / 60);
  const s = sec % 60;
  return `${m}m ${s}s`;
};

export const useWorkflowStore = create<WorkflowState>((set, get) => ({
  jobs: JSON.parse(JSON.stringify(initialJobs)),
  selectedJobId: null,
  playbackState: "idle",
  playbackSpeed: 1,
  activeTab: "summary",
  totalDurationSeconds: 0,
  setPlaybackState: (state) => set({ playbackState: state }),
  setPlaybackSpeed: (speed) => set({ playbackSpeed: speed }),
  setSelectedJobId: (id) => {
    set({ selectedJobId: id });
    if (id) {
      set({ activeTab: "logs" });
    } else {
      set({ activeTab: "summary" });
    }
  },
  setActiveTab: (tab) => set({ activeTab: tab }),
  resetWorkflow: () => set({
    jobs: JSON.parse(JSON.stringify(initialJobs)),
    playbackState: "idle",
    totalDurationSeconds: 0
  }),
  tickWorkflow: () => {
    const { jobs, playbackState, playbackSpeed, totalDurationSeconds } = get();
    if (playbackState !== "running") return;
    let updatedJobs = { ...jobs };
    let anyChange = false;
    const tickIncrement = playbackSpeed;
    Object.keys(updatedJobs).forEach((jobId) => {
      const job = updatedJobs[jobId];
      if (job.status === "waiting") {
        const depsResolved = job.dependencies.every((depId) => updatedJobs[depId]?.status === "success");
        if (depsResolved) {
          job.status = "running";
          job.progress = 0;
          job.logs = [`[0s] Job \"${job.name}\" queued and started.`];
          anyChange = true;
        }
      }
      if (job.status === "running") {
        const currentProgress = job.progress;
        const totalDuration = job.seconds;
        const progressIncrement = (tickIncrement / totalDuration) * 100;
        const nextProgress = Math.min(100, currentProgress + progressIncrement);
        job.progress = nextProgress;
        const elapsedSec = Math.floor((nextProgress / 100) * totalDuration);
        job.duration = formatTime(elapsedSec);
        if (job.totalTests > 0) {
          job.passedTests = Math.floor((nextProgress / 100) * job.totalTests);
        }
        const templateLogs = jobLogsTemplate[jobId] || [];
        const logsToShowCount = Math.min(templateLogs.length, Math.ceil((nextProgress / 100) * templateLogs.length));
        const logsSlice = templateLogs.slice(0, logsToShowCount);
        job.logs = [
          `[${formatTime(elapsedSec)}] Starting step executions...`,
          ...logsSlice.map((log, index) => `[${formatTime(Math.min(elapsedSec, Math.floor((index / templateLogs.length) * elapsedSec)))}] ${log}`)
        ];
        if (nextProgress >= 100) {
          job.status = "success";
          job.progress = 100;
          job.passedTests = job.totalTests;
          job.duration = formatTime(totalDuration);
          job.logs.push(`[${formatTime(totalDuration)}] Job \"${job.name}\" finished successfully.`);
        }
        anyChange = true;
      }
    });
    if (anyChange) {
      const nextDuration = totalDurationSeconds + tickIncrement;
      set({ jobs: updatedJobs, totalDurationSeconds: nextDuration });
    }
    const allCompleted = Object.values(updatedJobs).every((job) => job.status === "success");
    if (allCompleted) {
      set({ playbackState: "completed" });
    }
  }
}));
