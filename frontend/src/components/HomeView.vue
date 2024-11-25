<template>
  <div>
    <h1>Home</h1>
    <p>Welcome, {{ name }}</p>

    <h2>Lobbies</h2>
    <ul>
      <li v-for="lobby in lobbies" :key="lobby.code">
        <p>Lobby Code: {{ lobby.code }}</p>
        <p>{{ lobby.players }}/{{ maxPlayers }} Players</p>
        <button @click="joinLobby(lobby.code)">Enter Lobby</button>
      </li>
    </ul>

    <button @click="createLobby">Create Lobby</button>

    <h2>Enter Lobby Code</h2>
    <input type="text" v-model="lobbyCode" placeholder="Enter Lobby Code" />
    <button @click="joinLobby(lobbyCode)">Join Lobby</button>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();
const name = computed(() => route.query.name || 'Guest');
const maxPlayers = 8;

const lobbies = ref([
  { code: 'D8FXKA', players: 5 }, // TODO: generate random lobby codes 1#
  { code: '065LOM', players: 3 },
]);

const lobbyCode = ref('');

const createLobby = () => {
  console.log('Creating a new lobby');
  const newLobbyCode = 'NEW759'; // TODO: generate random lobby codes #2
  router.push({ path: `/lobby/${newLobbyCode}` });
};

const joinLobby = (code: string) => {
  console.log(`Joining lobby with code: ${code}`);
  router.push({ path: `/lobby/${code}` });
};
</script>

<style scoped></style>
