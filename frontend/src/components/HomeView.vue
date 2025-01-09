<template>
  <div>
    <h1>Home</h1>
    <p>Welcome, {{ name }} with the ID {{ id }}</p>

    <h2>Lobbies</h2>
    <ul>
      <li v-for="lobby in lobbies" :key="lobby.lobbyCode ?? 'default-key'">
        <p>Lobby Code: {{ lobby.lobbyCode }}</p>
        <p>{{ lobby.numPlayers }}/{{ maxPlayers }} Players</p>
        <button @click="joinLobby(lobby.lobbyCode?.toString() ?? '')">Enter Lobby</button>
      </li>
    </ul>

    <button @click="createLobby">Create Lobby</button>
    <button @click="fetchLobbies">Refresh Lobbies</button>

    <h2>Enter Lobby Code</h2>
    <input type="text" v-model="lobbyCode" placeholder="Enter Lobby Code" />
    <button @click="joinLobby(lobbyCode)">Join Lobby</button>
  </div>
  <BackgroundComponent :title="`JOIN A LOBBY`">
    <div class="home-grid">
      <div class="player-box">
        <img class="player-picture" src="@/assets/icons/player_icon.png"/>
        <img class="pin" src="@/assets/icons/pin.png"/>
          <p>{{ name }}</p>
      </div>
      <div class="home-view-row lobbies">
       
        <LobbyPanelComponent height-behaviour="stretch"></LobbyPanelComponent>
      </div>
    </div>
  </BackgroundComponent>
</template>

<script setup lang="ts">
import {ref, computed, onMounted} from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/userStore';
import useWebSocket from '@/services/socketService';
import BackgroundComponent from './layout/BackgroundComponent.vue';

import { Logger } from '../util/logger';
import LobbyPanelComponent from './layout/LobbyPanelComponent.vue';

const logger = new Logger();

const { sendMessage, onMessage } = useWebSocket();
const router = useRouter();
const lobbyCode = ref('');
const serverMessage = ref<string>('');
const name = computed(() => userStore.username || 'Guest');
const id = computed(() => userStore.id || 0);
const maxPlayers = 8;
const userStore = useUserStore();

interface Lobby {
  lobbyCode: number | null;
  numPlayers: number;
}

const lobbies = ref<Lobby[]>([]);

const handleServerMessage = (message: string) => {
  serverMessage.value = message;
  if (message.startsWith('ALL_LOBBIES')) {
    lobbies.value = JSON.parse(message.split(';')[1]);
    logger.info('ALL LOBBIES' + lobbies.value);
  }
};

onMounted(() => {
  fetchLobbies();
})

onMessage(handleServerMessage);

const createLobby = () => {
  const message = JSON.stringify({ type: 'LOBBY_CREATE_EVENT', id: 0 });
  sendMessage(message);
  fetchLobbies();

  /* How do I attain the new lobby id
  const newLobbyCode = 'NEW759';
  router.push({ path: `/lobby/${newLobbyCode}` }); */
};

const joinLobby = (code: string) => {
  logger.info(`Joining lobby with code: ${code}`);
  const data = JSON.stringify({
    type: 'JOIN_LOBBY',
    lobbyCode: code
  });
  sendMessage(data);
  router.push({ path: `/lobby/${code}` });
};

const fetchLobbies = () => {
  const message = JSON.stringify({ type: 'LOBBY_SHOW_EVENT' });
  sendMessage(message);
};
</script>

<style scoped>
.home-grid {
  position: relative;
  width: 100%;
  height: 100%;
  display: grid; 
  grid-template-columns: 1fr; 
  grid-template-rows: 1fr 1fr 1fr; 
  
  gap: 40px 60px;
  padding: 2dvw 4dvw 7dvw 4dvw;
  box-sizing:border-box;
}

.player-box {
  position: absolute;
  width: auto;
  top: 5%;
  right: 10%;
  padding: 2%;
  background-color: blue;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.pin {
  width: 20px;
}

.home-view-row {
  

}

.lobbies {
  grid-row: 3/4;
}
</style>
