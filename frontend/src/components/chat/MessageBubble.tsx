import React from 'react';

interface MessageBubbleProps {
  message: string;
  sender: 'user' | 'agent';
}

/**
 * Message bubble component for chat display
 */
export default function MessageBubble({ message, sender }: MessageBubbleProps) {
  const isUser = sender === 'user';

  return (
    <div className={`flex ${isUser ? 'justify-end' : 'justify-start'}`}>
      <div
        className={`max-w-xs lg:max-w-md xl:max-w-lg px-4 py-2 rounded-lg ${
          isUser
            ? 'bg-primary-600 text-white rounded-br-none'
            : 'bg-gray-200 text-gray-800 rounded-bl-none'
        }`}
      >
        <p className="break-words text-sm">{message}</p>
      </div>
    </div>
  );
}
