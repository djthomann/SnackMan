<template>
  <div>
    <!--<h1>Home</h1>
    <p>Welcome, {{ name }} with the ID {{ id }}</p>
    <h2>Lobbies</h2>
    <ul>
      <li v-for="lobby in lobbies" :key="lobby.lobbyCode ?? 'default-key'">
        <p>Lobby Code: {{ lobby.lobbyCode }}</p>
        <p>{{ lobby.numPlayers }}/{{ maxPlayers }} Players</p>
        <button @click="joinLobby(lobby.lobbyCode?.toString() ?? '')">Enter Lobby</button>
      </li>
    </ul>

    <button @click="fetchLobbies">Refresh Lobbies</button>
    
    <button @click="createLobby">Create Lobby</button>
    <h2>Enter Lobby Code</h2>-->
  </div>
  <BackgroundComponent :title="`JOIN A LOBBY`">
    <div class="home-grid">
      <div class="player-box">

        <UserPanelComponent :name="name" :id="id"></UserPanelComponent>
      </div>
      <div class="lobbies-headline">
          <div>
            <span v-if="info != ''" class="error-text">{{ info }}</span>
            <input class="custom-lobby" type="text" v-model="lobbyCode" placeholder="Enter Lobby Code" />
          </div>
          <div class="icon-button join-button">
            <button @click="joinLobby(lobbyCode)">
              <img src="@/assets/icons/join.svg" />
            </button>
          </div>
          <div class="icon-button refresh-button button">
            <button @click="fetchLobbies">
              <img src="@/assets/icons/refresh.svg" />
            </button>
          </div>
          <div class="icon-button add-button button">
            <button @click="createLobby">
              <img src="@/assets/icons/add.svg" />
            </button>
          </div>


        </div>
        <div class="lobbies">

<LobbyPanelComponent @click="joinLobby(lobby.lobbyCode?.toString() ?? '')" v-for="lobby in lobbies"
  :lobby="lobby" height-behaviour="stretch"></LobbyPanelComponent>
</div>
    </div>
  </BackgroundComponent>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/userStore';
import useWebSocket from '@/services/socketService';
import BackgroundComponent from './layout/BackgroundComponent.vue';

import { Logger } from '../util/logger';
import LobbyPanelComponent from './layout/LobbyPanelComponent.vue';
import type { Lobby } from '@/types/lobby';
import UserPanelComponent from './layout/UserPanelComponent.vue';

const logger = new Logger();

const { sendMessage, onMessage } = useWebSocket();
const router = useRouter();
const lobbyCode = ref('');
const serverMessage = ref<string>('');
const name = computed(() => userStore.username || 'Guest');
const id = computed(() => userStore.id || 0);
const maxPlayers = 8;
const userStore = useUserStore();
const info = ref('');

const lobbies = ref<Lobby[]>([]);

const handleServerMessage = (message: string) => {
  serverMessage.value = message;
  if (message.startsWith('ALL_LOBBIES')) {
    lobbies.value = JSON.parse(message.split(';')[1]);
    logger.info('ALL LOBBIES' + lobbies.value.toString());
  } else if (message.startsWith('NEW_LOBBY_CREATE') || message.startsWith('NEW_LOBBY_JOIN')) {
    fetchLobbies();
  } else if (message.startsWith('LOBBY_ID')) {
    joinLobby(message.split(';')[1]);
  }
}

onMounted(() => {
  fetchLobbies();
})

onMessage(handleServerMessage);

const createLobby = () => {
  const message = JSON.stringify({ type: 'LOBBY_CREATE_EVENT', id: 0 });
  sendMessage(message);
};

const joinLobby = (code: string) => {
  if (findLobby(parseInt(code))) {
    info.value = ""
    logger.info(`Joining lobby with code: ${code}`);
    const data = JSON.stringify({
      type: 'JOIN_LOBBY',
      lobbyCode: code
    });
    sendMessage(data);
    router.push({ path: `/lobby/${code}` });
  } else {
    info.value = "Lobby not found!"
  }
};

function findLobby(code: number) {
  for (let lobby of lobbies.value) {
    if (lobby.lobbyCode == code) {
      logger.info('Found lobby with code: ${code}');
      return true;
    }
  }
  logger.info(`Could not find lobby with code: ${code}`);
  return false;
};

const fetchLobbies = () => {
  const message = JSON.stringify({ type: 'LOBBY_SHOW_EVENT' });
  sendMessage(message);
};
</script>

<style scoped>
* {
  user-select: none;
}

.home-grid {
  position: absolute;
  width: 90%;
  height: 90%;
  grid-template-columns: 1fr;
  grid-template-rows: 1fr 1fr;

  gap: 40px 60px;
  margin: 5%;
  box-sizing: border-box;
}

.player-box {
  position: absolute;
  width: auto;
  top: 0.5%;
  right: 4%;
  padding: 2%;
  display: flex;
  flex-direction: column;
  align-items: center;
  transform: rotate(10deg)
}

.pin {
  width: 20px;
}

.home-view-row {
  display: flex;
  flex-direction: column;
}

.background__title {
  width: max-content;
  height: 90px;
  background-color: var(--colorPrimary);
  border: 9px solid var(--colorLight);
  border-radius: 2px;
  box-sizing: border-box;
  box-shadow: 10px 10px 0 rgba(0, 0, 0, 0.2);
  padding: 10px 25px;
}

.background__headline {
  color: var(--colorLight);
  margin: 0;
  font-size: 45px;
  font-weight: normal;
}

.bottom {
  grid-row: 2/3;
}

.lobbies {
  position: relative;
  height: 60%;
  width: 80%;
  display: flex;
  flex-wrap: wrap;
  overflow: scroll;
}

.lobbies-headline {
  display: flex;
  align-items: center;
  margin: 2% 0;
}

.button {
  user-select: none;
  margin-left: 1%;
}

.icon-button img {
  height: 60px;
  transition: transform 0.5s ease;
  user-select: none;
}

.icon-button button {
  background: 0;
  border: 0;
  user-select: none;
}

.icon-button button:hover {
  cursor: pointer;
}

.refresh-button img:hover {
  transform: rotate(360deg);
}

.add-button img {
  transition: transform 0.2s ease;
}

.add-button img:hover {
  transform: scale(1.3);
}

.join-button img {

  transform: translateX(-10px);
  transition: transform 0.2s ease;
}

.join-button:hover img {
  transform: translateX(0);
}

.custom-lobby {
  background-color: var(--colorTextMuted);
  border: 8px solid var(--colorLight);
  color: white;
  padding: 7px;
  margin-left: 1%;
  height: 40px;
  font-size: 18pt;
  font-weight: normal;
  font-family: "Lilita One";
  transition: background-color 200ms;
}

.custom-lobby:focus {
  background-color: var(--colorPrimary);
  outline: none;
}
</style>
