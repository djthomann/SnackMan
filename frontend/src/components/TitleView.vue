<template>
    <div class="titleview">
      <div class="titleview__titleimage"></div>
      <div class="titleview__panel">
        <label for="usernameInputField">Player-Username:</label>
        <input type="text" v-model="usernameInputField" class="usernameInputField"/>
        <button @click="submitForm">Submit</button>
      </div>
    </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/userStore';
import useWebSocket from '@/services/socketService';

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
  console.log('Handling... ')
  serverMessage.value = message;
  if (message.startsWith('CLIENT_ID')) {
    const information = JSON.parse(message.split(';')[1]);
    userStore.setId(information.id);
    router.push({ path: '/home'});
  }
}
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
  width: 50dvw;
  height: 50dvh;
  display: flex;
  justify-content: center;
  align-content: center;
  background-image: url('../assets/images/decorations/skateboard.png');
  background-size: 100%;
  background-repeat: no-repeat;
  background-position: center;
}

.titleview__panel {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background-image: url('../assets/images/backgrounds/titleview_backgroundpanel.png');
  background-size: 100%;
  background-position: center;
  background-repeat: no-repeat;
  padding: 5% 10% 5% 10%;
}

label {
  align-self: flex-start;
  padding: 0 5% 5% 0;
}

input {
  border: none;
  width: 100%;
  border-radius: 5px;
}


</style>
