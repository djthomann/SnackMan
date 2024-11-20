import { ref } from 'vue';
import eventBus from './eventBus';

/**
 * Service that establishes a socket connection with backend, sends messages
 * to eventBus and allows to send messages to backend
 */

let url = "ws://localhost:8080/ws-endpoint"

const serverResponse = ref<string>('');
const websocket = ref<WebSocket | null>(null);

export default function useWebSocket() {
  

  const connect = () => {

    if (websocket.value && websocket.value.readyState !== WebSocket.CLOSED) {
        console.log("Connection already opened");
        return;
    }

    websocket.value = new WebSocket(url);
    websocket.value.onopen = () => {
        console.log("Connection established");
    };

    websocket.value.onmessage = (event: MessageEvent) => {
        console.log("Received message from server:", event.data);
        serverResponse.value = event.data;
        eventBus.emit('serverMessage', event.data);
    };

    websocket.value.onclose = () => {
      console.log("Connection closed");
    };
  };

  const sendTestMessage = () => {
    if (websocket.value && websocket.value.readyState === WebSocket.OPEN) {
      websocket.value.send("Hello from Vue with TypeScript!");
    }
  };

  const sendMessage = (message: string) => {
    console.log("Trying to send... " + message)
    if (websocket.value && websocket.value.readyState === WebSocket.OPEN) {
        console.log("sending: " + message)
      websocket.value.send(message);
    }
  };

  const closeConnection = () => {
    if (websocket.value) {
      websocket.value.close();
    }
  };

  return {
    serverResponse,
    connect,
    sendTestMessage,
    sendMessage,
    closeConnection
  };
}
