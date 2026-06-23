import { useEffect } from 'react';
import { useWorkflowStore } from './store/store';
import { RunDetails } from './components/RunDetails';
import { WorkflowGraph } from './components/WorkflowGraph';
import { Sidebar } from './components/Sidebar';
import { MetricsDashboard } from './components/MetricsDashboard';
import { WorkflowViewer } from './components/WorkflowViewer';
import { JobLogs } from './components/JobLogs';
import { Terminal, FileCode, LayoutDashboard } from 'lucide-react';

export default function App() {
  const {
    playbackState,
    selectedJobId,
    activeTab,
    setActiveTab,
    tickWorkflow,
    setPlaybackState
  } = useWorkflowStore();

  // Run simulation interval when running
  useEffect(() => {
    let timer: any = null;
    if (playbackState === 'running') {
      timer = setInterval(() => {
        tickWorkflow();
      }, 1000);
    }
    return () => {
      if (timer) clearInterval(timer);
    };
  }, [playbackState, tickWorkflow]);

  // Auto-start simulation on load to wow the user instantly
  useEffect(() => {
    setPlaybackState('running');
  }, [setPlaybackState]);

  return (
    <div className="min-h-screen bg-github-bg text-github-text flex flex-col antialiased">
      
      {/* Top Navbar */}
      <header className="bg-github-bgHeader border-b border-github-border px-6 py-3.5 flex items-center justify-between">
        <div className="flex items-center gap-3">
          <svg className="w-8 h-8 text-github-text fill-current" viewBox="0 0 16 16" version="1.1" aria-hidden="true">
            <path fillRule="evenodd" d="M8 0C3.58 0 0 3.58 0 8c0 3.54 2.29 6.53 5.47 7.59.4.07.55-.17.55-.38 0-.19-.01-.82-.01-1.49-2.01.37-2.53-.49-2.69-.94-.09-.23-.48-.94-.82-1.13-.28-.15-.68-.52-.01-.53.63-.01 1.08.58 1.23.82.72 1.21 1.87.87 2.33.66.07-.52.28-.87.51-1.07-1.78-.2-3.64-.89-3.64-3.95 0-.87.31-1.59.82-2.15-.08-.2-.36-1.02.08-2.12 0 0 .67-.21 2.2.82.64-.18 1.32-.27 2-.27.68 0 1.36.09 2 .27 1.53-1.04 2.2-.82 2.2-.82.44 1.1.16 1.92.08 2.12.51.56.82 1.27.82 2.15 0 3.07-1.87 3.75-3.65 3.95.29.25.54.73.54 1.48 0 1.07-.01 1.93-.01 2.2 0 .21.15.46.55.38A8.013 8.013 0 0016 8c0-4.42-3.58-8-8-8z"></path>
          </svg>
          <div className="h-4 w-px bg-github-border" />
          <span className="font-semibold text-sm tracking-tight text-github-text">Actions</span>
          <span className="text-xs text-github-textMuted font-mono">/ Runs</span>
        </div>
        <div className="text-xs font-mono text-github-textMuted bg-github-card px-3 py-1 rounded-full border border-github-border">
          Runner: ubuntu-latest-1800-suite
        </div>
      </header>

      {/* Main Container */}
      <main className="flex-1 flex flex-col">
        {/* Header execution details & playback panel */}
        <RunDetails />

        {/* Workflow SVG graph */}
        <WorkflowGraph />

        {/* Workspace body (Sidebar + Content Panel) */}
        <div className="flex-1 flex flex-col md:flex-row min-h-[500px]">
          <Sidebar />

          {/* Details view area */}
          <div className="flex-1 flex flex-col bg-github-bg">
            
            {/* View Selection Tabs */}
            <div className="border-b border-github-border px-6 bg-github-bgHeader flex items-center justify-between">
              <div className="flex gap-4">
                {selectedJobId === null ? (
                  <>
                    <button
                      onClick={() => setActiveTab('summary')}
                      className={`py-3 px-1 text-sm border-b-2 flex items-center gap-2 font-medium transition-all ${
                        activeTab === 'summary'
                          ? 'border-github-blue text-github-blue'
                          : 'border-transparent text-github-textMuted hover:text-github-text'
                      }`}
                    >
                      <LayoutDashboard className="w-4 h-4" />
                      Summary Dashboard
                    </button>
                    <button
                      onClick={() => setActiveTab('yaml')}
                      className={`py-3 px-1 text-sm border-b-2 flex items-center gap-2 font-medium transition-all ${
                        activeTab === 'yaml'
                          ? 'border-github-blue text-github-blue'
                          : 'border-transparent text-github-textMuted hover:text-github-text'
                      }`}
                    >
                      <FileCode className="w-4 h-4" />
                      Workflow Config (e2e.yml)
                    </button>
                  </>
                ) : (
                  <>
                    <button
                      onClick={() => setActiveTab('logs')}
                      className={`py-3 px-1 text-sm border-b-2 flex items-center gap-2 font-medium transition-all ${
                        activeTab === 'logs'
                          ? 'border-github-blue text-github-blue'
                          : 'border-transparent text-github-textMuted hover:text-github-text'
                      }`}
                    >
                      <Terminal className="w-4 h-4" />
                      Job Terminal Logs
                    </button>
                    <button
                      onClick={() => setActiveTab('yaml')}
                      className={`py-3 px-1 text-sm border-b-2 flex items-center gap-2 font-medium transition-all ${
                        activeTab === 'yaml'
                          ? 'border-github-blue text-github-blue'
                          : 'border-transparent text-github-textMuted hover:text-github-text'
                      }`}
                    >
                      <FileCode className="w-4 h-4" />
                      Workflow Config (e2e.yml)
                    </button>
                  </>
                )}
              </div>
            </div>

            {/* Content Switcher */}
            <div className="flex-1 flex flex-col overflow-hidden">
              {activeTab === 'summary' && <MetricsDashboard />}
              {activeTab === 'logs' && <JobLogs />}
              {activeTab === 'yaml' && <WorkflowViewer />}
            </div>

          </div>
        </div>
      </main>
    </div>
  );
}
