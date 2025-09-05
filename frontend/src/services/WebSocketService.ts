export interface WebSocketMessage {
  type: string;
  message: string;
  data: any;
  timestamp: number;
}

class WebSocketService {
  private connected = false;
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 5;
  private reconnectInterval = 5000;

  connect(): Promise<void> {
    return new Promise((resolve, reject) => {
      try {
        // For now, simulate WebSocket connection
        // TODO: Implement actual WebSocket connection when backend is ready
        console.log('WebSocket connection simulated');
        this.connected = true;
        this.reconnectAttempts = 0;
        resolve();
      } catch (error) {
        console.error('Failed to create WebSocket connection:', error);
        reject(error);
      }
    });
  }

  disconnect(): void {
    if (this.connected) {
      console.log('WebSocket disconnected');
      this.connected = false;
    }
  }

  subscribe(topic: string, callback: (message: WebSocketMessage) => void): void {
    if (this.connected) {
      console.log(`Subscribed to topic: ${topic}`);
      // TODO: Implement actual subscription when WebSocket is ready
    }
  }

  subscribeToUser(userId: string, callback: (message: WebSocketMessage) => void): void {
    if (this.connected) {
      console.log(`Subscribed to user: ${userId}`);
      // TODO: Implement actual user subscription when WebSocket is ready
    }
  }

  send(destination: string, body: any): void {
    if (this.connected) {
      console.log(`Sending message to ${destination}:`, body);
      // TODO: Implement actual message sending when WebSocket is ready
    }
  }

  private handleReconnect(): void {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++;
      console.log(`Attempting to reconnect... (${this.reconnectAttempts}/${this.maxReconnectAttempts})`);
      
      setTimeout(() => {
        this.connect().catch(() => {
          // Reconnection failed, will try again
        });
      }, this.reconnectInterval);
    } else {
      console.error('Max reconnection attempts reached');
    }
  }

  isConnected(): boolean {
    return this.connected;
  }
}

export const webSocketService = new WebSocketService();
