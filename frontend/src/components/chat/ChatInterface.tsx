import React, { useState, useRef, useEffect } from 'react';
import { Send, Loader } from 'lucide-react';
import { useAgentStore } from '@store';
import apiService from '@services/api.service';
import MessageBubble from './MessageBubble';
import ChatInput from './ChatInput';
import { ChatMessage } from '@types';

/**
 * Chat interface component for interacting with AI agent
 */
export default function ChatInterface() {
  const { messages, addMessage, isLoading, setLoading } = useAgentStore();
  const [sessionId, setSessionId] = useState<string>('');
  const messagesEndRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    // Generate session ID on mount
    setSessionId(Date.now().toString());
  }, []);

  useEffect(() => {
    // Auto-scroll to bottom
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const handleSendMessage = async (content: string) => {
    if (!content.trim() || !sessionId) return;

    // Add user message
    const userMessage: ChatMessage = {
      id: Date.now().toString(),
      content,
      sender: 'user',
      timestamp: Date.now(),
      sessionId,
    };

    addMessage(userMessage);
    setLoading(true);

    try {
      const response = await apiService.chat({
        message: content,
        sessionId,
        useTools: true,
      });

      // Add agent response
      const agentMessage: ChatMessage = {
        id: Date.now().toString(),
        content: response.response,
        sender: 'agent',
        timestamp: Date.now(),
        sessionId,
      };

      addMessage(agentMessage);
    } catch (error) {
      console.error('Error sending message:', error);

      const errorMessage: ChatMessage = {
        id: Date.now().toString(),
        content: 'Sorry, an error occurred while processing your request.',
        sender: 'agent',
        timestamp: Date.now(),
        sessionId,
      };

      addMessage(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex flex-col h-full bg-white rounded-lg shadow-lg">
      {/* Messages area */}
      <div className="flex-1 overflow-auto p-6 space-y-4">
        {messages.length === 0 ? (
          <div className="h-full flex items-center justify-center text-center">
            <div>
              <h2 className="text-2xl font-bold text-gray-800 mb-2">
                Welcome to Security AI Agent
              </h2>
              <p className="text-gray-600">
                Start a conversation to perform security scans and vulnerability analysis
              </p>
            </div>
          </div>
        ) : (
          <>
            {messages.map((message) => (
              <MessageBubble
                key={message.id}
                message={message.content}
                sender={message.sender}
              />
            ))}

            {isLoading && (
              <div className="flex items-center gap-2 text-gray-500">
                <Loader className="w-4 h-4 animate-spin" />
                <span>Thinking...</span>
              </div>
            )}

            <div ref={messagesEndRef} />
          </>
        )}
      </div>

      {/* Input area */}
      <div className="border-t border-gray-200 p-6">
        <ChatInput onSend={handleSendMessage} disabled={isLoading} />
      </div>
    </div>
  );
}
