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
const { sendMessage } = useWebSocket();
const userStore = useUserStore();

const submitForm = () => {
  const message = JSON.stringify({ type: 'REGISTERUSERNAME', username: usernameInputField.value });
  sendMessage(message);
  userStore.setUsername(usernameInputField.value);
  router.push({ path: '/home'});
};
</script>

<style scoped></style>
