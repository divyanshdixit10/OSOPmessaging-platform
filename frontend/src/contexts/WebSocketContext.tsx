import React, { createContext, useContext, useEffect, useState, ReactNode } from 'react';
import { webSocketService, WebSocketMessage } from '../services/WebSocketService';

interface WebSocketContextType {
  connected: boolean;
  subscribe: (topic: string, callback: (message: WebSocketMessage) => void) => void;
  subscribeToUser: (userId: string, callback: (message: WebSocketMessage) => void) => void;
  send: (destination: string, body: any) => void;
  lastMessage: WebSocketMessage | null;
}

const WebSocketContext = createContext<WebSocketContextType | undefined>(undefined);

export const WebSocketProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [connected, setConnected] = useState(false);
  const [lastMessage, setLastMessage] = useState<WebSocketMessage | null>(null);

  useEffect(() => {
    const connect = async () => {
      try {
        await webSocketService.connect();
        setConnected(true);
      } catch (error) {
        console.error('Failed to connect to WebSocket:', error);
        setConnected(false);
      }
    };

    connect();

    return () => {
      webSocketService.disconnect();
      setConnected(false);
    };
  }, []);

  const subscribe = (topic: string, callback: (message: WebSocketMessage) => void) => {
    webSocketService.subscribe(topic, (message) => {
      setLastMessage(message);
      callback(message);
    });
  };

  const subscribeToUser = (userId: string, callback: (message: WebSocketMessage) => void) => {
    webSocketService.subscribeToUser(userId, (message) => {
      setLastMessage(message);
      callback(message);
    });
  };

  const send = (destination: string, body: any) => {
    webSocketService.send(destination, body);
  };

  const value: WebSocketContextType = {
    connected,
    subscribe,
    subscribeToUser,
    send,
    lastMessage
  };

  return (
    <WebSocketContext.Provider value={value}>
      {children}
    </WebSocketContext.Provider>
  );
};

export const useWebSocket = () => {
  const context = useContext(WebSocketContext);
  if (context === undefined) {
    throw new Error('useWebSocket must be used within a WebSocketProvider');
  }
  return context;
};
