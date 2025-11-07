import React from 'react';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import './MarkdownRenderer.css';

interface MarkdownRendererProps {
  content: string;
  isDarkMode?: boolean;
}

/**
 * Markdown renderer component with proper styling for code blocks, lists, tables, etc.
 */
export default function MarkdownRenderer({ content, isDarkMode = false }: MarkdownRendererProps) {
  return (
    <div className={`markdown-content ${isDarkMode ? 'dark' : 'light'}`}>
      <ReactMarkdown
        remarkPlugins={[remarkGfm]}
        components={{
          h1: ({ node, ...props }) => (
            <h1 className="text-2xl font-bold my-4 text-blue-900 border-b-2 border-blue-500 pb-2" {...props} />
          ),
          h2: ({ node, ...props }) => (
            <h2 className="text-xl font-bold my-3 text-blue-800 border-b border-blue-400 pb-1" {...props} />
          ),
          h3: ({ node, ...props }) => (
            <h3 className="text-lg font-bold my-2 text-blue-700" {...props} />
          ),
          h4: ({ node, ...props }) => (
            <h4 className="text-base font-bold my-2 text-blue-700" {...props} />
          ),
          h5: ({ node, ...props }) => (
            <h5 className="text-sm font-bold my-1 text-blue-700" {...props} />
          ),
          h6: ({ node, ...props }) => (
            <h6 className="text-xs font-bold my-1 text-blue-700" {...props} />
          ),
          p: ({ node, ...props }) => (
            <p className="my-3 text-gray-900 leading-relaxed text-sm" {...props} />
          ),
          a: ({ node, ...props }) => (
            <a className="text-blue-600 hover:text-blue-800 hover:underline font-semibold transition-colors" {...props} />
          ),
          ul: ({ node, ...props }) => (
            <ul className="list-disc list-outside my-2 ml-6 text-gray-900 space-y-1" {...props} />
          ),
          ol: ({ node, ...props }) => (
            <ol className="list-decimal list-outside my-2 ml-6 text-gray-900 space-y-1" {...props} />
          ),
          li: ({ node, ...props }) => (
            <li className="text-gray-900 text-sm leading-relaxed" {...props} />
          ),
          blockquote: ({ node, ...props }) => (
            <blockquote
              className="border-l-4 border-blue-500 pl-4 italic my-3 text-gray-800 bg-blue-50 py-2 pr-3 rounded-r text-sm"
              {...props}
            />
          ),
          code: ({ node, inline, ...props }: any) => {
            if (inline) {
              return (
                <code
                  className="bg-gray-100 text-red-600 px-2 py-0.5 rounded font-mono text-xs font-semibold border border-gray-300"
                  {...props}
                />
              );
            }
            return (
              <code className="block bg-gray-50 text-gray-800 p-4 rounded-lg overflow-x-auto my-3 font-mono text-xs leading-relaxed border border-gray-300" {...props} />
            );
          },
          pre: ({ node, ...props }) => (
            <pre className="bg-gray-50 text-gray-800 p-4 rounded-lg overflow-x-auto my-3 border border-gray-300 text-xs leading-relaxed" {...props} />
          ),
          table: ({ node, ...props }) => (
            <div className="overflow-x-auto my-4 rounded-lg border border-gray-300">
              <table className="border-collapse w-full" {...props} />
            </div>
          ),
          thead: ({ node, ...props }) => (
            <thead className="bg-blue-600 text-white" {...props} />
          ),
          tbody: ({ node, ...props }) => (
            <tbody {...props} />
          ),
          tr: ({ node, ...props }) => (
            <tr className="border-b border-gray-300 hover:bg-blue-50" {...props} />
          ),
          th: ({ node, ...props }) => (
            <th className="border-r border-blue-400 px-4 py-3 text-left font-bold text-sm" {...props} />
          ),
          td: ({ node, ...props }) => (
            <td className="border-r border-gray-300 px-4 py-2 text-gray-900 text-sm" {...props} />
          ),
          hr: ({ node, ...props }) => (
            <hr className="my-4 border-t-2 border-blue-400" {...props} />
          ),
          strong: ({ node, ...props }) => (
            <strong className="font-bold text-gray-900" {...props} />
          ),
          em: ({ node, ...props }) => (
            <em className="italic text-gray-800" {...props} />
          ),
        }}
      >
        {content}
      </ReactMarkdown>
    </div>
  );
}
