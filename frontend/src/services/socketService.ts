import { ref } from 'vue';
import eventBus from './eventBus';
import { Logger } from '../util/logger';

/**
 * Service that establishes a socket connection with backend, sends messages
 * to eventBus and allows to send messages to backend
 */
const logger = new Logger();
const prefix = window.location.protocol === 'https:' ? 'wss' : 'ws';

const url = `${prefix}://${window.location.hostname}:${window.location.port}/ws-endpoint`;

logger.info(url);

const serverResponse = ref<string>('');
const websocket = ref<WebSocket | null>(null);
const messageCallbacks = ref<((message: string) => void)[]>([]);

export default function useWebSocket() {
  const connect = () => {
    if (websocket.value && websocket.value.readyState !== WebSocket.CLOSED) {
      logger.info('Connection already opened');
      return;
    }

    websocket.value = new WebSocket(url);
    websocket.value.onopen = () => {
      logger.info('Connection established');
    };

    websocket.value.onmessage = (event: MessageEvent) => {
      // logger.info('Received message from server:', event.data);
      serverResponse.value = event.data;
      eventBus.emit('serverMessage', event.data);
      messageCallbacks.value.forEach((callback) => callback(event.data));
    };

    websocket.value.onclose = () => {
      logger.info('Connection closed');
    };
  };

  const sendTestMessage = () => {
    if (websocket.value && websocket.value.readyState === WebSocket.OPEN) {
      websocket.value.send('Hello from Vue with TypeScript!');
    }
  };

  const sendMessage = (message: string) => {
    logger.info('Trying to send... ' + message);
    if (websocket.value && websocket.value.readyState === WebSocket.OPEN) {
      logger.info('sending: ' + message);
      websocket.value.send(message);
    }
  };

  const closeConnection = () => {
    if (websocket.value) {
      websocket.value.close();
    }
  };

  const onMessage = (callback: (message: string) => void) => {
    messageCallbacks.value.push(callback);
  };

  return {
    serverResponse,
    connect,
    sendTestMessage,
    sendMessage,
    closeConnection,
    onMessage,
  };
}
