<template>
  <h1>Title</h1>
  <p>Please enter your username</p>
  <input type="text" v-model="usernameInputField" />
  <button @click="submitForm">Submit</button>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/userStore';
import useWebSocket from '@/services/socketService';

const usernameInputField = ref('');
const router = useRouter();
const { sendMessage, onMessage } = useWebSocket();
const serverMessage = ref<string>('');
const userStore = useUserStore();

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
    let information = JSON.parse(message.split(';')[1]);
    userStore.setId(information.id);
    router.push({ path: '/home'});
  }
}

onMessage(handleServerMessage);

</script>

<style scoped></style>
