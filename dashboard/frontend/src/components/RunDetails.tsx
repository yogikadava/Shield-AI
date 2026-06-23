import React from 'react';
import { useWorkflowStore } from '../store/store';
import { Play, Pause, RotateCcw, CheckCircle2, GitBranch, GitCommit, Clock, FileArchive, Zap } from 'lucide-react';

export const RunDetails: React.FC = () => {
  const {
    playbackState,
    playbackSpeed,
    totalDurationSeconds,
    setPlaybackState,
    setPlaybackSpeed,
    resetWorkflow,
    jobs
  } = useWorkflowStore();

  // Aggregate stats
  const totalTests = 1800;
  const totalPassed = Object.values(jobs).reduce((acc, job) => acc + job.passedTests, 0);
  
  // Overall workflow status
  let overallStatus: 'waiting' | 'running' | 'success' | 'failed' = 'waiting';
  const anyRunning = Object.values(jobs).some(j => j.status === 'running');
  const allSuccess = Object.values(jobs).every(j => j.status === 'success');
  if (anyRunning) overallStatus = 'running';
  else if (allSuccess) overallStatus = 'success';
  else if (Object.values(jobs).some(j => j.status === 'failed')) overallStatus = 'failed';

  const formatTime = (sec: number): string => {
    if (sec < 60) return `${sec}s`;
    const m = Math.floor(sec / 60);
    const s = sec % 60;
    return `${m}m ${s}s`;
  };

  return (
    <div className="bg-github-bgHeader border-b border-github-border px-6 py-4 flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
      {/* Title & Metadata */}
      <div className="flex-1">
        <div className="flex items-center gap-3">
          <div className={`p-1.5 rounded-full ${
            overallStatus === 'success' 
              ? 'bg-github-successGreenMuted text-github-successGreen' 
              : overallStatus === 'running' 
                ? 'bg-github-yellowMuted text-github-warning animate-spin' 
                : 'bg-github-border text-github-textMuted'
          }`}>
            {overallStatus === 'success' ? (
              <CheckCircle2 className="w-6 height-6 w-6 h-6" />
            ) : (
              <Zap className="w-6 h-6 animate-pulse" />
            )}
          </div>
          <div>
            <h1 className="text-lg font-bold text-github-text flex items-center gap-2">
              Scale E2E Suites to 1800 Test Cases with Robust Selenium/Appium Fallback
              <span className="text-xs px-2 py-0.5 rounded-full border border-github-border bg-github-card text-github-textMuted font-mono">
                #739210
              </span>
            </h1>
            <div className="flex flex-wrap items-center gap-x-4 gap-y-1 text-sm text-github-textMuted mt-1 font-mono">
              <span className="flex items-center gap-1">
                <GitBranch className="w-4 h-4 text-github-blue" />
                <span>main</span>
              </span>
              <span className="text-github-border">|</span>
              <span className="flex items-center gap-1">
                <GitCommit className="w-4 h-4 text-github-textMuted" />
                <span className="text-github-blue hover:underline cursor-pointer">f3c9d1d</span>
              </span>
              <span className="text-github-border">|</span>
              <span className="flex items-center gap-1">
                <span>triggered via push by</span>
                <span className="text-github-text font-semibold">github-actions-bot</span>
              </span>
            </div>
          </div>
        </div>
      </div>

      {/* Simulator Controls & Timers */}
      <div className="flex flex-wrap items-center gap-4 bg-github-card border border-github-border p-2.5 rounded-lg">
        {/* Play / Pause / Reset */}
        <div className="flex items-center gap-1.5 border-r border-github-border pr-3">
          {playbackState === 'running' ? (
            <button
              onClick={() => setPlaybackState('paused')}
              className="p-1.5 hover:bg-github-border rounded text-github-warning transition-colors"
              title="Pause Simulation"
            >
              <Pause className="w-4 h-4 fill-current" />
            </button>
          ) : (
            <button
              onClick={() => setPlaybackState('running')}
              className="p-1.5 hover:bg-github-border rounded text-github-successGreen transition-colors"
              title="Start / Resume Simulation"
              disabled={playbackState === 'completed'}
            >
              <Play className="w-4 h-4 fill-current" />
            </button>
          )}

          <button
            onClick={resetWorkflow}
            className="p-1.5 hover:bg-github-border rounded text-github-textMuted hover:text-github-text transition-colors"
            title="Reset Simulation"
          >
            <RotateCcw className="w-4 h-4" />
          </button>
        </div>

        {/* Speed Adjustment */}
        <div className="flex items-center gap-1 border-r border-github-border pr-3">
          <span className="text-xs text-github-textMuted font-mono">Speed:</span>
          {([1, 2, 5] as const).map((speed) => (
            <button
              key={speed}
              onClick={() => setPlaybackSpeed(speed)}
              className={`px-2 py-0.5 text-xs font-mono rounded transition-colors ${
                playbackSpeed === speed
                  ? 'bg-github-blue text-github-bg font-bold'
                  : 'text-github-textMuted hover:bg-github-border'
              }`}
            >
              {speed}x
            </button>
          ))}
        </div>

        {/* Real-time stats & metrics */}
        <div className="flex items-center gap-4 pl-1 text-sm font-mono">
          <div className="flex items-center gap-1.5" title="Total Run Time">
            <Clock className="w-4 h-4 text-github-textMuted" />
            <span className="text-github-text">{formatTime(totalDurationSeconds)}</span>
            <span className="text-xs text-github-textMuted">/ 14m 32s</span>
          </div>

          <div className="flex items-center gap-1.5" title="Total Test Case Success">
            <CheckCircle2 className="w-4 h-4 text-github-successGreen" />
            <span className="text-github-successGreen font-semibold">{totalPassed}</span>
            <span className="text-github-textMuted">/ {totalTests}</span>
          </div>

          <div className="flex items-center gap-1.5" title="Artifacts Available">
            <FileArchive className="w-4 h-4 text-github-blue" />
            <span className="text-github-blue">3 artifacts</span>
          </div>
        </div>
      </div>
    </div>
  );
};
