<template>
    <div class="titleview">
      <LoadingOverlay></LoadingOverlay>
      <div class="titleview__titleimage"></div>
      <div class="titleview__panel">
        <label for="username__inputfield">Player-Username:</label>
        <input type="text" v-model="usernameInputField" class="username__inputfield"/>
        <button @click="submitForm">START</button>
      </div>
    </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/userStore';
import useWebSocket from '@/services/socketService';
import { Logger } from '../util/logger';
import LoadingOverlay from './layout/LoadingOverlayComponent.vue';

const logger = new Logger();

const router = useRouter();
const { sendMessage, onMessage } = useWebSocket();
const serverMessage = ref<string>('');
const userStore = useUserStore();
const usernameInputField = ref('');

const submitForm = () => {
  const message = JSON.stringify({ type: 'REGISTERUSERNAME', username: usernameInputField.value });
  sendMessage(message);
  userStore.setUsername(usernameInputField.value);
};

// Method, to get ClientID from BE
const handleServerMessage = (message: string) => {
  logger.info('Handling server message');
  serverMessage.value = message;
  if (message.startsWith('CLIENT_ID')) {
    const information = JSON.parse(message.split(';')[1]);
    userStore.setId(information.id);
    router.push({ path: '/home' });
  }
};

onMessage(handleServerMessage);
</script>

<style scoped>

.titleview {
  width: 100dvw;
  height: 100dvh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-content: center;
  align-items: center;
  background-image: url('../assets/images/backgrounds/backgroundcontainer_image.jpg');
  background-color: var(--colorSecondary);
  background-size: cover;
  background-repeat: no-repeat;
  background-position: center;
}

.titleview__titleimage {
  width: 60svw;
  height: 20svw;
  max-height: 50%;
  background-image: url('../assets/images/decorations/skateboard.png');
  background-size: 100%;
  background-repeat: no-repeat;
  background-position: center;
}

.titleview__panel {
  width: 20svw;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background-image: url('../assets/images/backgrounds/titleview_backgroundpanel.png');
  background-size: 100%;
  background-position: center;
  background-repeat: no-repeat;
  padding: 7%;
}

label {
  align-self: flex-start;
  color: var(--colorTextDark);
}

input {
  border: none;
  width: 100%;
  border-radius: 5px;
  margin: 5%;
  padding: 3% 2%;
}

button {
  color: var(--colorTextDark);
  background-color: var(--colorTertiary);
  width: 70%;
  border-style: solid;
  border-width: 5px;
  border-color: white;
  border-radius: 10px;
  font-family: "Lilita One","Helvetica Neue", Helvetica, Arial, "Segoe UI", sans-serif;
  padding: 3%;
  transition-duration: 0.4s;
}

button:hover {
  background-color: var(--colorTertiary);
  box-shadow: 10px 10px var(--colorShadow);
}

</style>
