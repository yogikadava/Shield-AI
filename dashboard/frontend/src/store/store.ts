import { create } from 'zustand';

export interface Job {
  id: string;
  name: string;
  totalTests: number;
  passedTests: number;
  failedTests: number;
  duration: string;
  seconds: number;
  status: 'waiting' | 'running' | 'success' | 'failed';
  dependencies: string[];
  progress: number;
  logs: string[];
}

interface WorkflowState {
  jobs: Record<string, Job>;
  selectedJobId: string | null; // null means summary dashboard
  playbackState: 'idle' | 'running' | 'paused' | 'completed';
  playbackSpeed: 1 | 2 | 5;
  activeTab: 'summary' | 'logs' | 'yaml';
  totalDurationSeconds: number;
  
  // Actions
  setPlaybackState: (state: 'idle' | 'running' | 'paused' | 'completed') => void;
  setPlaybackSpeed: (speed: 1 | 2 | 5) => void;
  setSelectedJobId: (id: string | null) => void;
  setActiveTab: (tab: 'summary' | 'logs' | 'yaml') => void;
  resetWorkflow: () => void;
  tickWorkflow: () => void;
}

const initialJobs: Record<string, Job> = {
  'unit-tests-api': {
    id: 'unit-tests-api',
    name: 'Unit Tests — API (300)',
    totalTests: 300,
    passedTests: 0,
    failedTests: 0,
    duration: '0s',
    seconds: 75,
    status: 'waiting',
    dependencies: [],
    progress: 0,
    logs: []
  },
  'selenium-website-tests': {
    id: 'selenium-website-tests',
    name: 'Selenium — Website Tests (300)',
    totalTests: 300,
    passedTests: 0,
    failedTests: 0,
    duration: '0s',
    seconds: 225,
    status: 'waiting',
    dependencies: [],
    progress: 0,
    logs: []
  },
  'appium-android-tests': {
    id: 'appium-android-tests',
    name: 'Appium — Android Tests (300)',
    totalTests: 300,
    passedTests: 0,
    failedTests: 0,
    duration: '0s',
    seconds: 252,
    status: 'waiting',
    dependencies: [],
    progress: 0,
    logs: []
  },
  'validation-tests': {
    id: 'validation-tests',
    name: 'Validation Tests (300)',
    totalTests: 300,
    passedTests: 0,
    failedTests: 0,
    duration: '0s',
    seconds: 125,
    status: 'waiting',
    dependencies: ['unit-tests-api'],
    progress: 0,
    logs: []
  },
  'load-testing-performance': {
    id: 'load-testing-performance',
    name: 'Load Testing — Performance (300)',
    totalTests: 300,
    passedTests: 0,
    failedTests: 0,
    duration: '0s',
    seconds: 175,
    status: 'waiting',
    dependencies: ['unit-tests-api'],
    progress: 0,
    logs: []
  },
  'deployment-status': {
    id: 'deployment-status',
    name: 'Deployment Status (300)',
    totalTests: 300,
    passedTests: 0,
    failedTests: 0,
    duration: '0s',
    seconds: 20,
    status: 'waiting',
    dependencies: ['validation-tests'],
    progress: 0,
    logs: []
  },
  'compile-master-report': {
    id: 'compile-master-report',
    name: 'Compile Master Report & Deploy',
    totalTests: 0, // Master step is integration step, has 0 test cases itself
    passedTests: 0,
    failedTests: 0,
    duration: '0s',
    seconds: 40,
    status: 'waiting',
    dependencies: ['selenium-website-tests', 'appium-android-tests', 'validation-tests', 'load-testing-performance', 'deployment-status'],
    progress: 0,
    logs: []
  }
};

const jobLogsTemplate: Record<string, string[]> = {
  'unit-tests-api': [
    'Initializing API test runner environment...',
    'Checking environment variables and configuration...',
    'Connecting to test database container...',
    'Running API unit test suites (300 test cases)...',
    '✓ GET /api/v1/auth/session - Status 200 OK [12ms]',
    '✓ POST /api/v1/auth/login - Status 200 OK [45ms]',
    '✓ GET /api/v1/users/profile - Status 200 OK [15ms]',
    '✓ POST /api/v1/users/update - Status 200 OK [32ms]',
    '✓ GET /api/v1/projects - Status 200 OK [18ms]',
    '✓ POST /api/v1/projects/create - Status 201 Created [55ms]',
    '✓ GET /api/v1/health - Status 200 OK [5ms]',
    'Executing schema check on 120 API entities...',
    '✓ JSON schema validations passed for all API endpoints.',
    'Verifying middleware rules for CORS and Rate Limiting...',
    '✓ Middleware security headers validated.',
    'All 300 API endpoints unit test assertions passed.'
  ],
  'selenium-website-tests': [
    'Setting up Chrome Headless browser environment...',
    'Warning: Headless Chrome launch timeout on port 9222. Running fallback check...',
    'Appling robust Selenium Fallback protocol: launching Virtual Framebuffer (Xvfb)...',
    'Xvfb instance spawned on display :99 successfully.',
    'Running website selenium integration suites (300 test cases)...',
    '✓ Home page layout and responsive breakpoints validation',
    '✓ Auth forms validation and password strength indicators',
    '✓ Navigation bar links and active menu highlighting',
    '✓ Interactive dashboard grids and chart resize triggers',
    '✓ Profile setting photo upload and crop logic',
    '✓ Offline caching and Service Worker offline fallback loading',
    '✓ Billing portal checkout integrations & security checks',
    'All 300 browser verification actions completed.',
    'Saving reports & capture screenshots to workspace artifacts...'
  ],
  'appium-android-tests': [
    'Launching local Android Emulator (API level 33)...',
    'Warning: ADB emulator connection timeout. Running Appium Fallback protocol...',
    'Fallback activated: Submitting APK to Firebase Test Lab...',
    'Uploading app-debug.apk and app-debug-androidTest.apk (18.5 MB)...',
    'Test Lab device queue acquired: running on Google Pixel 6 (Android 13)...',
    'Running Appium automated tests (300 test cases)...',
    '✓ Splash screen rendering and auto-redirect sequence',
    '✓ Sign-in screen validation with bio-auth prompt',
    '✓ Main navigation drawer items check',
    '✓ Map rendering, location permissions, and routing mocks',
    '✓ Background push notifications receipt and badge increment',
    '✓ Local database encryption & SQLite schema migration checks',
    'Pulling Firebase Test Lab device logs and video artifacts...',
    'All 300 Mobile E2E assertions passed.'
  ],
  'validation-tests': [
    'Initializing schema structural validators...',
    'Validating JSON Schema compliance of 300 endpoint payloads...',
    '✓ Schema structures matched spec V7 successfully.',
    'Validating swagger/openapi definition synchronicity...',
    '✓ Swagger definitions and production code interfaces match.',
    'Testing data mutation constraints and SQL-Injection sanitization...',
    '✓ SQL Injection checks: 300 payload attempts returned 400 Bad Request (Safe).',
    'All 300 validation assertions completed successfully.'
  ],
  'load-testing-performance': [
    'Initializing Grafana k6 engine...',
    'Ramping up virtual users (VUs) from 0 to 100 VUs in 10s...',
    'Running load tests against 300 REST/gRPC endpoints...',
    '✓ HTTP Request Rate: 450 req/sec',
    '✓ HTTP Request Duration: p(95) = 112ms, p(99) = 184ms',
    '✓ Data Sent: 4.8 MB, Data Received: 24.3 MB',
    '✓ Error Rate: 0.00% (0 errors out of 15,204 requests)',
    'Performance thresholds satisfied for all 300 targets.'
  ],
  'deployment-status': [
    'Acquiring Kubernetes staging cluster config...',
    'Checking pods scaling policies and active ingress rules...',
    'Running 300 deployment readiness probes...',
    '✓ Pod replica check: 6/6 pods online and healthy',
    '✓ Ingress certificate validation: TLS valid until 2027',
    '✓ DNS propagation and CDN cache response timing verified',
    'All 300 staging deployment checks completed. Green status confirmed.'
  ],
  'compile-master-report': [
    'Aggregating reports from Selenium (300), Appium (300), API (300), Validation (300), Load (300), and Deployment (300)...',
    'Consolidating 1800 total test cases assertions...',
    'Generating E2E execution dashboards...',
    'Compiling Master HTML Report...',
    'Compiling Master PDF Summary...',
    'Saving reports to static artifacts server...',
    '✓ Artifact compiled: e2e-report-html (1.2 MB)',
    '✓ Artifact compiled: e2e-report-pdf (850 KB)',
    '✓ Artifact compiled: selenium-screenshots (14.5 MB)',
    'Triggering Slack notification and Webhook deployment...',
    'Workflow completed successfully with green success badges! (1800/1800 passed)'
  ]
};

// Formatting helper: seconds -> duration string
const formatTime = (sec: number): string => {
  if (sec < 60) return `${sec}s`;
  const m = Math.floor(sec / 60);
  const s = sec % 60;
  return `${m}m ${s}s`;
};

export const useWorkflowStore = create<WorkflowState>((set, get) => ({
  jobs: JSON.parse(JSON.stringify(initialJobs)),
  selectedJobId: null,
  playbackState: 'idle',
  playbackSpeed: 1,
  activeTab: 'summary',
  totalDurationSeconds: 0,

  setPlaybackState: (state) => set({ playbackState: state }),
  setPlaybackSpeed: (speed) => set({ playbackSpeed: speed }),
  setSelectedJobId: (id) => {
    set({ selectedJobId: id });
    if (id) {
      set({ activeTab: 'logs' });
    } else {
      set({ activeTab: 'summary' });
    }
  },
  setActiveTab: (tab) => set({ activeTab: tab }),

  resetWorkflow: () => set({
    jobs: JSON.parse(JSON.stringify(initialJobs)),
    playbackState: 'idle',
    totalDurationSeconds: 0
  }),

  tickWorkflow: () => {
    const { jobs, playbackState, playbackSpeed, totalDurationSeconds } = get();
    if (playbackState !== 'running') return;

    let updatedJobs = { ...jobs };
    let anyChange = false;
    
    // We increment ticks by the playback speed
    const tickIncrement = playbackSpeed;

    // Check dependency graph resolution to schedule jobs
    Object.keys(updatedJobs).forEach((jobId) => {
      const job = updatedJobs[jobId];

      if (job.status === 'waiting') {
        // A job can run if all its dependencies are 'success'
        const depsResolved = job.dependencies.every(
          (depId) => updatedJobs[depId]?.status === 'success'
        );

        if (depsResolved) {
          job.status = 'running';
          job.progress = 0;
          job.logs = [ `[0s] Job '${job.name}' queued and started.` ];
          anyChange = true;
        }
      }

      if (job.status === 'running') {
        const currentProgress = job.progress;
        
        // Calculate new progress (scaled by job's simulated duration)
        const totalDuration = job.seconds;
        const progressIncrement = (tickIncrement / totalDuration) * 100;
        const nextProgress = Math.min(100, currentProgress + progressIncrement);
        
        job.progress = nextProgress;
        
        // Simulated execution times
        const elapsedSec = Math.floor((nextProgress / 100) * totalDuration);
        job.duration = formatTime(elapsedSec);

        // Update test counts proportionally
        if (job.totalTests > 0) {
          job.passedTests = Math.floor((nextProgress / 100) * job.totalTests);
        }

        // Streaming logs simulation
        const templateLogs = jobLogsTemplate[jobId] || [];
        const logsToShowCount = Math.min(
          templateLogs.length,
          Math.ceil((nextProgress / 100) * templateLogs.length)
        );
        
        const logsSlice = templateLogs.slice(0, logsToShowCount);
        job.logs = [
          `[${formatTime(elapsedSec)}] Starting step executions...`,
          ...logsSlice.map((log, index) => `[${formatTime(Math.min(elapsedSec, Math.floor((index / templateLogs.length) * elapsedSec)))}] ${log}`)
        ];

        if (nextProgress >= 100) {
          job.status = 'success';
          job.progress = 100;
          job.passedTests = job.totalTests;
          job.duration = formatTime(totalDuration);
          job.logs.push(`[${formatTime(totalDuration)}] Job '${job.name}' finished successfully.`);
        }
        anyChange = true;
      }
    });

    if (anyChange) {
      const nextDuration = totalDurationSeconds + tickIncrement;
      set({ 
        jobs: updatedJobs,
        totalDurationSeconds: nextDuration
      });
    }

    // Check if everything is complete
    const allCompleted = Object.values(updatedJobs).every(
      (job) => job.status === 'success'
    );

    if (allCompleted) {
      set({ playbackState: 'completed' });
    }
  }
}));
