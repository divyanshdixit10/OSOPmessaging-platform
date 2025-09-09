import SockJS from 'sockjs-client';
import { Client, IMessage, Stomp } from '@stomp/stompjs';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080';

export interface WebSocketMessage {
  type: string;
  message: string;
  data: any;
  timestamp: number;
}

class WebSocketService {
  private client: Client | null = null;
  private connected = false;
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 5;
  private reconnectInterval = 5000;
  private subscriptions: { [key: string]: { id: string, callback: (message: WebSocketMessage) => void } } = {};

  connect(): Promise<void> {
    return new Promise((resolve, reject) => {
      try {
        // Create a new STOMP client
        this.client = new Client({
          webSocketFactory: () => new SockJS(`${API_BASE_URL}/ws`),
          debug: function (str) {
            console.log('STOMP: ' + str);
          },
          reconnectDelay: 5000,
          heartbeatIncoming: 4000,
          heartbeatOutgoing: 4000
        });

        // On connect event
        this.client.onConnect = (frame) => {
          console.log('Connected to WebSocket');
          this.connected = true;
          this.reconnectAttempts = 0;
          
          // Resubscribe to all topics
          Object.keys(this.subscriptions).forEach(topic => {
            this.resubscribe(topic);
          });
          
          resolve();
        };

        // On error event
        this.client.onStompError = (frame) => {
          console.error('STOMP error:', frame.headers['message']);
          this.handleReconnect();
          reject(new Error(frame.headers['message']));
        };

        // Connect
        this.client.activate();
      } catch (error) {
        console.error('Failed to create WebSocket connection:', error);
        this.handleReconnect();
        reject(error);
      }
    });
  }

  disconnect(): void {
    if (this.client && this.connected) {
      this.client.deactivate();
      this.connected = false;
      console.log('WebSocket disconnected');
    }
  }

  subscribe(topic: string, callback: (message: WebSocketMessage) => void): void {
    if (!this.client) {
      console.error('WebSocket client not initialized');
      return;
    }

    // Store the subscription info
    this.subscriptions[topic] = { id: '', callback };

    if (this.connected) {
      this.resubscribe(topic);
    }
  }

  private resubscribe(topic: string): void {
    if (!this.client || !this.connected) return;

    const subscription = this.client.subscribe(`/topic/${topic}`, (message: IMessage) => {
      try {
        const parsedMessage = JSON.parse(message.body) as WebSocketMessage;
        this.subscriptions[topic].callback(parsedMessage);
      } catch (error) {
        console.error('Error parsing WebSocket message:', error);
      }
    });

    this.subscriptions[topic].id = subscription.id;
    console.log(`Subscribed to topic: ${topic}`);
  }

  subscribeToUser(userId: string, callback: (message: WebSocketMessage) => void): void {
    if (!this.client) {
      console.error('WebSocket client not initialized');
      return;
    }

    const topic = `user/${userId}`;
    this.subscriptions[topic] = { id: '', callback };

    if (this.connected) {
      const subscription = this.client.subscribe(`/user/${userId}/queue/messages`, (message: IMessage) => {
        try {
          const parsedMessage = JSON.parse(message.body) as WebSocketMessage;
          callback(parsedMessage);
        } catch (error) {
          console.error('Error parsing WebSocket message:', error);
        }
      });
      
      this.subscriptions[topic].id = subscription.id;
      console.log(`Subscribed to user: ${userId}`);
    }
  }

  send(destination: string, body: any): void {
    if (!this.client || !this.connected) {
      console.error('WebSocket not connected');
      return;
    }

    this.client.publish({
      destination: `/app/${destination}`,
      body: JSON.stringify(body)
    });
    
    console.log(`Sent message to ${destination}:`, body);
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

  unsubscribe(topic: string): void {
    if (!this.client || !this.connected) return;
    
    const subscription = this.subscriptions[topic];
    if (subscription && subscription.id) {
      this.client.unsubscribe(subscription.id);
      delete this.subscriptions[topic];
      console.log(`Unsubscribed from topic: ${topic}`);
    }
  }

  unsubscribeAll(): void {
    if (!this.client || !this.connected) return;
    
    Object.keys(this.subscriptions).forEach(topic => {
      this.unsubscribe(topic);
    });
    
    console.log('Unsubscribed from all topics');
  }
}

export const webSocketService = new WebSocketService();
