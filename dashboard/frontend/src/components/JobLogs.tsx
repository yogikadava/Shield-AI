import React, { useEffect, useRef, useState } from 'react';
import { useWorkflowStore } from '../store/store';
import { Terminal, Search, ArrowDownCircle, CheckCircle2, Loader2, PlayCircle } from 'lucide-react';

export const JobLogs: React.FC = () => {
  const { jobs, selectedJobId } = useWorkflowStore();
  const [filterText, setFilterText] = useState('');
  const logsEndRef = useRef<HTMLDivElement>(null);

  const job = selectedJobId ? jobs[selectedJobId] : null;
  
  // Auto-scroll to bottom of logs when they grow
  useEffect(() => {
    if (logsEndRef.current) {
      logsEndRef.current.scrollIntoView({ behavior: 'smooth' });
    }
  }, [job?.logs.length]);

  if (!job) {
    return (
      <div className="flex-1 flex flex-col items-center justify-center text-github-textMuted p-6">
        <Terminal className="w-12 h-12 text-github-border mb-3" />
        <p className="text-sm">Select a job from the sidebar to inspect execution terminal logs.</p>
      </div>
    );
  }

  const filteredLogs = job.logs.filter(line => 
    line.toLowerCase().includes(filterText.toLowerCase())
  );

  const getStatusBadge = (status: typeof job.status) => {
    switch (status) {
      case 'success':
        return (
          <span className="flex items-center gap-1 text-xs text-github-successGreen bg-github-successGreenMuted px-2 py-0.5 rounded border border-github-successGreen/30">
            <CheckCircle2 className="w-3.5 h-3.5" /> PASSED
          </span>
        );
      case 'running':
        return (
          <span className="flex items-center gap-1 text-xs text-github-warning bg-github-yellowMuted px-2 py-0.5 rounded border border-github-warning/30 animate-pulse">
            <Loader2 className="w-3.5 h-3.5 animate-spin" /> RUNNING
          </span>
        );
      case 'waiting':
      default:
        return (
          <span className="flex items-center gap-1 text-xs text-github-textMuted bg-github-border px-2 py-0.5 rounded">
            <PlayCircle className="w-3.5 h-3.5" /> QUEUED
          </span>
        );
    }
  };

  return (
    <div className="flex-1 p-6 flex flex-col overflow-hidden h-full max-w-7xl mx-auto w-full">
      {/* Terminal Title Info */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 mb-4">
        <div>
          <h3 className="text-sm font-semibold text-github-text flex items-center gap-2">
            <Terminal className="w-4.5 h-4.5 text-github-blue" />
            Terminal Console: <span className="font-mono text-github-blue">{job.name}</span>
          </h3>
          <p className="text-xs text-github-textMuted mt-0.5 font-mono">
            Elapsed duration: {job.duration}
          </p>
        </div>

        <div className="flex items-center gap-3">
          {getStatusBadge(job.status)}

          {/* Search bar */}
          <div className="relative">
            <Search className="absolute left-2.5 top-2 w-3.5 h-3.5 text-github-textMuted" />
            <input
              type="text"
              placeholder="Filter logs..."
              value={filterText}
              onChange={(e) => setFilterText(e.target.value)}
              className="bg-github-card border border-github-border text-github-text text-xs rounded pl-8 pr-3 py-1.5 focus:outline-none focus:border-github-blue w-40 sm:w-56 font-mono"
            />
          </div>
        </div>
      </div>

      {/* Terminal Body */}
      <div className="flex-1 bg-github-terminalBg border border-github-border rounded-lg overflow-hidden flex flex-col min-h-[300px]">
        {/* Terminal Header */}
        <div className="bg-github-bgHeader border-b border-github-border/50 px-4 py-2 flex items-center justify-between text-[10px] font-mono text-github-textMuted">
          <span>bash --login (x86_64-pc-linux-gnu)</span>
          <span className="flex items-center gap-1 cursor-pointer hover:text-github-text" onClick={() => setFilterText('')}>
            <ArrowDownCircle className="w-3.5 h-3.5" /> Clear Filters
          </span>
        </div>

        {/* Terminal Content */}
        <div className="flex-1 p-4 overflow-y-auto font-mono text-xs text-github-text space-y-1.5 select-text">
          {filteredLogs.length > 0 ? (
            filteredLogs.map((line, idx) => {
              let lineClass = 'text-github-text';
              if (line.includes('Warning:')) {
                lineClass = 'text-github-warning';
              } else if (line.includes('✓') || line.includes('passed')) {
                lineClass = 'text-github-successGreen';
              } else if (line.includes('Starting') || line.includes('queued')) {
                lineClass = 'text-github-blue';
              }

              return (
                <div key={idx} className={`${lineClass} border-b border-github-border/10 pb-0.5 leading-relaxed break-all`}>
                  {line}
                </div>
              );
            })
          ) : (
            <div className="text-github-textMuted italic text-center py-8">
              {job.status === 'waiting' ? 'Waiting in runner queue...' : 'No logs matching query.'}
            </div>
          )}
          <div ref={logsEndRef} />
        </div>
      </div>
    </div>
  );
};
