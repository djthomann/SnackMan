<template>
  <div>
    <label for="usernameinputField">Username:</label>
    <input
      id="usernameInputField"
      type="text"
      v-on:keyup.enter="handleUsername"
      v-model="usernameInputField"
    />
    <label>Your username:</label>
    <p>{{ playerUsername }}</p>
    <label>Other Players Online:</label>
    <li v-for="value in usernamesList">{{ value }}</li>
  </div>
</template>

<script setup lang="ts">
import { defineComponent, onUnmounted, ref, onMounted, reactive } from 'vue';
import eventBus from '@/services/eventBus';
import useWebSocket from '@/services/socketService';

const { serverResponse, connect, sendMessage, closeConnection } = useWebSocket();

const serverMessage = ref<string>('');
const usernameInputField = ref<string>('');
const playerUsername = ref<string>('');
const usernamesList = ref<string[]>([]);

function handleUsername() {
  sendMessage('USERNAME:' + usernameInputField.value);
  usernameInputField.value = '';
  console.log('In handle Usernames: ' + usernamesList.value);
}

document.addEventListener('username', handleUsername);

onMounted(() => {
  eventBus.on('serverMessage', handleServerMessage);
  connect();
});

onUnmounted(() => {
  eventBus.off('serverMessage', handleServerMessage);
  usernamesList.value = [];
  closeConnection();
});

// react to servermessage
const handleServerMessage = (message: string) => {
  serverMessage.value = message;
  console.log('Received server message: ' + message);

  if (message.startsWith('USERNAME')) {
    playerUsername.value = message.split(':')[1];
  } else if (message.startsWith('OTHERPLAYERINFO')) {
    usernamesList.value = [];
    let usernames: string[] = message.split(':');
    usernames.forEach((username) => {
      if (username != 'OTHERPLAYERINFO' && username != '') {
        usernamesList.value.push(username);
        console.log('New user:' + username + 'All Users: ' + usernamesList.value);
      }
    });
  }
};
</script>

<style></style>
