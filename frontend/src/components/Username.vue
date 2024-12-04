<template>
  <div>
    <label for="usernameinputField">Username:</label>
    <input
      id="usernameInputField"
      type="text"
      v-on:keyup.enter="sendUsernameEvent"
      v-model="usernameInputField"
    />
    <label for="userRoleButton">Role:</label>
    <button v-on:click="sendGhostEvent">ghost</button>
    <button v-on:click="sendSnackmanEvent">snackman</button>

    <label>You:</label>
    <p>Hi {{ playerUsername }}! You play as a {{ playerRole }}...</p>
    <label>Other Players Online:</label>
    <li v-for="value in usernamesList">{{ value }}</li>
  </div>
</template>

<script setup lang="ts">
import { onUnmounted, ref, onMounted } from 'vue';
import eventBus from '@/services/eventBus';
import useWebSocket from '@/services/socketService';

const { sendMessage } = useWebSocket();

const serverMessage = ref<string>('');
const usernameInputField = ref<string>('');
const playerUsername = ref<string>('');
const playerRole = ref<string>('');
const usernamesList = ref<string[]>([]);

// TODO: gameID anc role etc. has to be generated properly.
function sendUsernameEvent() {
  const data = JSON.stringify({
    type: 'REGISTERUSERNAME',
    gameID: 17,
    username: usernameInputField.value,
  });
  sendMessage(data);
  playerUsername.value = usernameInputField.value;
  usernameInputField.value = '';
}
document.addEventListener('username', sendUsernameEvent);

// TODO: gameID etc. has to be generated properly.
function sendGhostEvent() {
  const data = JSON.stringify({
    type: 'REGISTERGHOST',
    gameID: 17,
    username: usernameInputField.value,
    role: {
      id: 2,
      x: 3,
      y: 3,
      z: 7,
    },
  });
  sendMessage(data);
  playerRole.value = 'Ghost :)';
}
document.addEventListener('ghost', sendGhostEvent);

// TODO: gameID etc. has to be generated properly.
function sendSnackmanEvent() {
  const data = JSON.stringify({
    type: 'REGISTERSNACKMAN',
    gameID: 17,
    username: usernameInputField.value,
    role: {
      id: 2,
      x: 3,
      y: 3,
      z: 7,
    },
  });
  sendMessage(data);
  playerRole.value = 'Snackman :)';
}
document.addEventListener('snackman', sendSnackmanEvent);

onMounted(() => {
  eventBus.on('serverMessage', handleServerMessage);
});

onUnmounted(() => {
  eventBus.off('serverMessage', handleServerMessage);
  usernamesList.value = [];
});

// TODO: Backend Events should arrive here.
const handleServerMessage = (message: string) => {
  serverMessage.value = message;
  console.log('Received server message: ' + message);

  if (message.startsWith('USERNAME')) {
    playerUsername.value = message.split(':')[1];
  } else if (message.startsWith('OTHERPLAYERINFO')) {
    usernamesList.value = [];
    const usernames: string[] = message.split(':');
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
