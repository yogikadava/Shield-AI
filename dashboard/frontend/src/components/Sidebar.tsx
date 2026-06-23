import React from 'react';
import { useWorkflowStore } from '../store/store';
import { CheckCircle2, Loader2, PlayCircle, LayoutDashboard } from 'lucide-react';

export const Sidebar: React.FC = () => {
  const { jobs, selectedJobId, setSelectedJobId } = useWorkflowStore();

  const getStatusIcon = (status: 'waiting' | 'running' | 'success' | 'failed') => {
    switch (status) {
      case 'success':
        return <CheckCircle2 className="w-4 h-4 text-github-successGreen flex-shrink-0" />;
      case 'running':
        return <Loader2 className="w-4 h-4 text-github-warning animate-spin flex-shrink-0" />;
      case 'waiting':
      default:
        return <PlayCircle className="w-4 h-4 text-github-textMuted flex-shrink-0" />;
    }
  };

  const getStatusBgClass = (status: 'waiting' | 'running' | 'success' | 'failed', isSelected: boolean) => {
    if (isSelected) return 'bg-github-blueMuted border-l-2 border-github-blue';
    if (status === 'running') return 'bg-github-yellowMuted/20 border-l-2 border-github-warning';
    return 'border-l-2 border-transparent';
  };

  return (
    <div className="w-full md:w-80 bg-github-card border-r border-github-border flex flex-col h-full">
      <div className="p-4 border-b border-github-border flex items-center justify-between">
        <span className="text-sm font-semibold uppercase tracking-wider text-github-textMuted">Jobs</span>
        <span className="text-xs px-2 py-0.5 rounded bg-github-border text-github-text font-mono">
          {Object.keys(jobs).length} Total
        </span>
      </div>

      <div className="flex-1 overflow-y-auto py-2">
        {/* Summary Dashboard Selector */}
        <button
          onClick={() => setSelectedJobId(null)}
          className={`w-full text-left px-4 py-3 flex items-center gap-3 transition-colors hover:bg-github-border/50 text-sm ${
            selectedJobId === null ? 'bg-github-blueMuted text-github-blue font-semibold border-l-2 border-github-blue' : 'text-github-text border-l-2 border-transparent'
          }`}
        >
          <LayoutDashboard className="w-4 h-4 text-github-blue" />
          <div className="flex-1 min-w-0">
            <p className="truncate">Summary Dashboard</p>
            <p className="text-xs text-github-textMuted font-mono">Aggregated metrics & graphs</p>
          </div>
        </button>

        <div className="h-px bg-github-border my-2 mx-4" />

        {/* Individual Jobs */}
        {Object.values(jobs).map((job) => {
          const isSelected = selectedJobId === job.id;
          return (
            <button
              key={job.id}
              onClick={() => setSelectedJobId(job.id)}
              className={`w-full text-left px-4 py-3 flex items-start gap-3 transition-colors hover:bg-github-border/50 text-sm ${getStatusBgClass(
                job.status,
                isSelected
              )}`}
            >
              <div className="mt-0.5">{getStatusIcon(job.status)}</div>
              <div className="flex-1 min-w-0">
                <div className="flex justify-between items-start">
                  <p className={`truncate font-medium ${isSelected ? 'text-github-blue font-semibold' : 'text-github-text'}`}>
                    {job.name}
                  </p>
                </div>
                <div className="flex items-center gap-2 mt-1 font-mono text-xs text-github-textMuted">
                  <span>{job.duration}</span>
                  {job.totalTests > 0 && (
                    <>
                      <span>•</span>
                      <span className={job.status === 'success' ? 'text-github-successGreen' : 'text-github-textMuted'}>
                        {job.passedTests}/{job.totalTests} cases
                      </span>
                    </>
                  )}
                </div>
              </div>
            </button>
          );
        })}
      </div>
    </div>
  );
};
