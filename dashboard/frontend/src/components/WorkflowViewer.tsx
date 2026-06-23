import React from 'react';
import { e2eWorkflowYaml } from '../data/e2e.yml';
import { FileCode, Copy, Check } from 'lucide-react';

export const WorkflowViewer: React.FC = () => {
  const [copied, setCopied] = React.useState(false);

  const handleCopy = () => {
    navigator.clipboard.writeText(e2eWorkflowYaml);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const lines = e2eWorkflowYaml.trim().split('\n');

  return (
    <div className="flex-1 p-6 overflow-hidden flex flex-col h-full max-w-7xl mx-auto w-full">
      <div className="bg-github-card border border-github-border rounded-lg flex flex-col overflow-hidden h-[500px]">
        {/* Header toolbar */}
        <div className="bg-github-bgHeader px-4 py-2 border-b border-github-border flex items-center justify-between">
          <div className="flex items-center gap-2">
            <FileCode className="w-4 h-4 text-github-blue" />
            <span className="text-xs font-semibold text-github-text font-mono">.github/workflows/e2e.yml</span>
          </div>
          <button
            onClick={handleCopy}
            className="flex items-center gap-1 px-2.5 py-1 text-[11px] font-medium border border-github-border rounded hover:bg-github-border transition-colors text-github-text"
          >
            {copied ? (
              <>
                <Check className="w-3.5 h-3.5 text-github-successGreen" />
                <span className="text-github-successGreen font-semibold">Copied!</span>
              </>
            ) : (
              <>
                <Copy className="w-3.5 h-3.5 text-github-textMuted" />
                <span>Copy Raw</span>
              </>
            )}
          </button>
        </div>

        {/* Code viewing area */}
        <div className="flex-1 overflow-auto bg-github-terminalBg p-4 font-mono text-xs leading-relaxed select-text flex">
          {/* Line Numbers */}
          <div className="text-right pr-4 text-github-border select-none border-r border-github-border/30 w-10 flex-shrink-0">
            {lines.map((_, i) => (
              <div key={i}>{i + 1}</div>
            ))}
          </div>

          {/* Code Text */}
          <div className="pl-4 overflow-x-auto text-github-text whitespace-pre flex-1">
            {lines.map((line, i) => {
              // Very basic syntax highlighting based on text matching
              let styleClass = '';
              if (line.trim().startsWith('- name:') || line.trim().startsWith('name:')) {
                styleClass = 'text-github-blue font-semibold';
              } else if (line.trim().startsWith('uses:') || line.trim().startsWith('run:')) {
                styleClass = 'text-github-warning';
              } else if (line.trim().startsWith('#') || line.includes('# ')) {
                styleClass = 'text-github-textMuted italic';
              } else if (line.includes(':')) {
                styleClass = 'text-github-successGreen';
              }

              return (
                <div key={i} className={styleClass}>
                  {line || ' '}
                </div>
              );
            })}
          </div>
        </div>
      </div>
    </div>
  );
};
