import React from 'react';
import { useWorkflowStore } from '../store/store';
import type { Job } from '../store/store';
import { CheckCircle2, Loader2, PlayCircle, Layers } from 'lucide-react';

interface NodePosition {
  x: number;
  y: number;
  width: number;
  height: number;
}

export const WorkflowGraph: React.FC = () => {
  const { jobs, selectedJobId, setSelectedJobId } = useWorkflowStore();

  // Coordinates mapping for standard desktop view
  const positions: Record<string, NodePosition> = {
    'unit-tests-api': { x: 50, y: 30, width: 220, height: 60 },
    'selenium-website-tests': { x: 50, y: 120, width: 220, height: 60 },
    'appium-android-tests': { x: 50, y: 210, width: 220, height: 60 },
    
    'validation-tests': { x: 330, y: 30, width: 220, height: 60 },
    'load-testing-performance': { x: 330, y: 120, width: 220, height: 60 },
    
    'deployment-status': { x: 610, y: 30, width: 220, height: 60 },
    
    'compile-master-report': { x: 890, y: 120, width: 220, height: 60 },
  };

  // Helper to draw smooth curves
  const getBezierPath = (startX: number, startY: number, endX: number, endY: number) => {
    const midX = (startX + endX) / 2;
    return `M ${startX} ${startY} C ${midX} ${startY}, ${midX} ${endY}, ${endX} ${endY}`;
  };

  const getStatusBorderClass = (status: Job['status'], isSelected: boolean) => {
    if (isSelected) return 'border-github-blue bg-github-blueMuted/5 ring-1 ring-github-blue/50';
    if (status === 'success') return 'border-github-successGreen/70 bg-github-successGreenMuted/5 hover:border-github-successGreen';
    if (status === 'running') return 'border-github-warning bg-github-yellowMuted/10 shadow-lg shadow-github-warning/10';
    return 'border-github-border bg-github-card hover:border-github-textMuted';
  };

  const renderStatusIcon = (status: Job['status']) => {
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

  // Draw connections
  const connections = [
    // unit-tests-api to validation-tests
    { from: 'unit-tests-api', to: 'validation-tests' },
    // unit-tests-api to load-testing-performance
    { from: 'unit-tests-api', to: 'load-testing-performance' },
    // validation-tests to deployment-status
    { from: 'validation-tests', to: 'deployment-status' },
    
    // All dependencies leading to compile-master-report
    { from: 'selenium-website-tests', to: 'compile-master-report' },
    { from: 'appium-android-tests', to: 'compile-master-report' },
    { from: 'validation-tests', to: 'compile-master-report' },
    { from: 'load-testing-performance', to: 'compile-master-report' },
    { from: 'deployment-status', to: 'compile-master-report' },
  ];

  return (
    <div className="bg-github-bg border-b border-github-border p-6 overflow-x-auto select-none">
      <div className="flex items-center gap-2 mb-4">
        <Layers className="w-4 h-4 text-github-blue" />
        <h2 className="text-sm font-semibold text-github-text">Workflow Graph Dependency View</h2>
      </div>

      <div className="relative min-w-[1160px] h-[300px]">
        {/* SVG connection lines */}
        <svg className="absolute inset-0 w-full h-full pointer-events-none">
          <defs>
            <linearGradient id="gradient-line" x1="0%" y1="0%" x2="100%" y2="0%">
              <stop offset="0%" stopColor="#30363d" />
              <stop offset="100%" stopColor="#238636" />
            </linearGradient>
            <marker
              id="arrow"
              viewBox="0 0 10 10"
              refX="8"
              refY="5"
              markerWidth="6"
              markerHeight="6"
              orient="auto-start-reverse"
            >
              <path d="M 0 1 L 10 5 L 0 9 z" fill="#30363d" />
            </marker>
            <marker
              id="arrow-active"
              viewBox="0 0 10 10"
              refX="8"
              refY="5"
              markerWidth="6"
              markerHeight="6"
              orient="auto-start-reverse"
            >
              <path d="M 0 1 L 10 5 L 0 9 z" fill="#3fb950" />
            </marker>
          </defs>

          {connections.map((conn, idx) => {
            const fromPos = positions[conn.from];
            const toPos = positions[conn.to];
            if (!fromPos || !toPos) return null;

            const startX = fromPos.x + fromPos.width;
            const startY = fromPos.y + fromPos.height / 2;
            const endX = toPos.x;
            const endY = toPos.y + toPos.height / 2;

            const fromJob = jobs[conn.from];
            const toJob = jobs[conn.to];
            const isActive = fromJob?.status === 'success' && (toJob?.status === 'running' || toJob?.status === 'success');

            return (
              <path
                key={idx}
                d={getBezierPath(startX, startY, endX, endY)}
                fill="none"
                stroke={isActive ? '#3fb950' : '#30363d'}
                strokeWidth={isActive ? 2 : 1.5}
                markerEnd={isActive ? 'url(#arrow-active)' : 'url(#arrow)'}
                className={toJob?.status === 'running' ? 'dependency-line-active' : ''}
              />
            );
          })}
        </svg>

        {/* HTML Job Nodes */}
        {Object.entries(positions).map(([jobId, pos]) => {
          const job = jobs[jobId];
          if (!job) return null;

          const isSelected = selectedJobId === jobId;

          return (
            <div
              key={jobId}
              style={{
                position: 'absolute',
                left: `${pos.x}px`,
                top: `${pos.y}px`,
                width: `${pos.width}px`,
                height: `${pos.height}px`,
              }}
              onClick={() => setSelectedJobId(jobId)}
              className={`rounded-lg border px-3 py-2 flex flex-col justify-between cursor-pointer transition-all duration-200 select-none ${getStatusBorderClass(
                job.status,
                isSelected
              )}`}
            >
              <div className="flex items-center justify-between gap-1.5 min-w-0">
                <span className={`text-xs font-semibold truncate ${isSelected ? 'text-github-blue' : 'text-github-text'}`}>
                  {job.name.replace(/ \(\d+\)/, '')}
                </span>
                {renderStatusIcon(job.status)}
              </div>

              <div className="flex justify-between items-center text-[10px] font-mono text-github-textMuted mt-1">
                <span>{job.duration}</span>
                {job.totalTests > 0 ? (
                  <span className={job.status === 'success' ? 'text-github-successGreen' : ''}>
                    {job.passedTests} / {job.totalTests} passed
                  </span>
                ) : (
                  <span>Compile & Deploy</span>
                )}
              </div>

              {/* Progress bar for running jobs */}
              {job.status === 'running' && (
                <div className="absolute bottom-0 left-0 right-0 h-1 bg-github-border rounded-b-lg overflow-hidden">
                  <div
                    style={{ width: `${job.progress}%` }}
                    className="h-full bg-github-warning transition-all duration-300"
                  />
                </div>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
};
