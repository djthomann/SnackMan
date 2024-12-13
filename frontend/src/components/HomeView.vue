<template>
  <div>
    <h1>Home</h1>
    <p>Welcome, {{ name }} with the ID {{  id  }}</p>

    <h2>Lobbies</h2>
    <ul>
      <li v-for="lobby in lobbies" :key="lobby.id ?? 'default-key'">
        <p>Lobby Code: {{ lobby.id }}</p>
        <p>0/{{ maxPlayers }} Players</p>
        <button @click="joinLobby(lobby.id?.toString() ?? '')">Enter Lobby</button>
      </li>
    </ul>

    <button @click="createLobby">Create Lobby</button>
    <button @click="fetchLobbies">Refresh Lobbies</button>

    <h2>Enter Lobby Code</h2>
    <input type="text" v-model="lobbyCode" placeholder="Enter Lobby Code" />
    <button @click="joinLobby(lobbyCode)">Join Lobby</button>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/userStore';
import useWebSocket from '@/services/socketService';

const { sendMessage, onMessage } = useWebSocket();
const router = useRouter();
const lobbyCode = ref('');
const serverMessage = ref<string>('');
const name = computed(() => userStore.username || 'Guest');
const id = computed(() => userStore.id || 0 );
const maxPlayers = 8;
const userStore = useUserStore();

interface Lobby {
  id: number | null;
}

const lobbies = ref<Lobby[]>([]);

const handleServerMessage = (message: string) => {
  serverMessage.value = message;
  if (message.startsWith('ALL_LOBBIES')) {
    lobbies.value = JSON.parse(message.split(';')[1]);
    console.log("ALL LOBIES"+lobbies.value);
  }
};

onMessage(handleServerMessage);

const createLobby = () => {
  const message = JSON.stringify({ type: 'LOBBY_CREATE_EVENT', id: 0 });
  sendMessage(message);

  /* How do I attain the new lobby id
  const newLobbyCode = 'NEW759';
  router.push({ path: `/lobby/${newLobbyCode}` }); */
};

const joinLobby = (code: string) => {
  console.log(`Joining lobby with code: ${code}`);
  router.push({ path: `/lobby/${code}` });
};

const fetchLobbies = () => {
  const message = JSON.stringify({ type: 'LOBBY_SHOW_EVENT' });
  sendMessage(message);
};
</script>

<style scoped></style>