<template>
  <div class="lobby-grid-wrapper">

    <!--Content for when one of the Snackmen wins-->
    <div v-if="gameData.winningTeam == 'SNACKMAN'">
      <div class="background-component">
        <BackgroundComponent :title="`Congrats! ${gameData.winnerName} Won!`"></BackgroundComponent>
      </div>
      <div class="lobby-grid">
        <!--Shows the Player that won-->
        <div class="player-panel">
          <PlayerPanelComponent avatar="snackman" variant="white">
            <template #button>
              <h3>Winner</h3>
              <p>Cheese Thief of the Month</p>
            </template>
            <template #content>
              <p>{{ gameData.winnerName }}</p>
              <h3>{{ gameData.winnerCaloryCount }} CAL</h3>
            </template>
          </PlayerPanelComponent>
          <div>
            <button @click="saveMap">Save Map</button>
          </div>
          <div>
            <button @click="toHome()">Back to Home</button>
          </div>
        </div>

        <!--Shows the Ghost Team-->
        <div class="ghost-panel">
          <GhostPanelComponent>
            <template #ghost-list>
              <li
                v-for="(player, index) in gameData.players?.filter(
                  (player) => player.role === 'GHOST',
                  )"
                :key="index"
                class="ghostpanel__item"
              >
                <img src="@/assets/images/avatars/avatar_ghost.svg" />
                <h3>{{ player.username }}</h3>
              </li>
            </template>
          </GhostPanelComponent>
        </div>
      </div>

      <!--Shows the Snackman Team with Scores-->
      <div class="recipe-grid">
        <ReceiptComponent>
          <template #receipt-list>
            <li
              v-for="(player, index) in gameData.players?.filter(
                (player) => player.role === 'SNACKMAN',
              )"
              :key="index"
              class="receiptpanel__item"
            >
              <span class="player-name">{{ player.username }}</span>
              <div class="seperation-line"></div>
              <span>{{ player.score }} Kalorien</span>
            </li>
          </template>
        </ReceiptComponent>
      </div>
    </div>

    <!--Content for when the Ghost Team wins-->
    <div v-if="gameData.winningTeam == 'GHOST'">
      <div class="background-component">
        <BackgroundComponent :title="`Oh No! Ghosts Won!`"></BackgroundComponent>
      </div>
      <div class="lobby-grid">
        <!--Content for the Snackman that didn't win-->
        <div class="player-panel">
          <PlayerPanelComponent avatar="snackman" variant="white">
            <template #button>
              <h3>Winner</h3>
              <p>Cheese Thief of the Month</p>
            </template>
            <template #content>
              <p>{{ gameData.winnerName }}</p>
              <h3>{{ gameData.winnerCaloryCount }} CAL</h3>
            </template>

            <!--Graffiti Overlay-->
            <template #avatar-overlay>
              <div class="avatar-overlay">
                <img src="@/assets/images/avatars/ghost_graffiti.png" />
              </div>
            </template>
          </PlayerPanelComponent>
          <div>
            <button @click="saveMap">Save Map</button>
          </div>
          <div>
            <button @click="toHome()">Back to Home Screen</button>
          </div>
        </div>

        <div class="ghost-panel__ghostWon">
          <GhostPanelComponent>
            <template #ghost-list>
              <li
                v-for="(player, index) in gameData.players?.filter(
                  (player) => player.role === 'GHOST',
                )"
                :key="index"
                class="ghostpanel__item"
              >
                <img src="@/assets/images/avatars/avatar_ghost.svg" />
                <h3>{{ player.username }}</h3>
              </li>
            </template>
          </GhostPanelComponent>
        </div>
      </div>

      <div class="recipe-grid__ghostWon">
        <ReceiptComponent>
          <template #receipt-list>
            <li
              v-for="(player, index) in gameData.players?.filter(
                (player) => player.role === 'SNACKMAN',
              )"
              :key="index"
              class="receiptpanel__item"
            >
              <span class="player-name">{{ player.username }}</span>
              <div class="seperation-line"></div>
              <span>{{ player.score }} Calories</span>
            </li>
          </template>
        </ReceiptComponent>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router';
import { onMounted, ref } from 'vue';
import useWebSocket from '@/services/socketService';
import eventBus from '@/services/eventBus';
import BackgroundComponent from './layout/BackgroundComponent.vue';
import ReceiptComponent from './layout/ReceiptComponent.vue';
import PlayerPanelComponent from './layout/PlayerPanelComponent.vue';
import GhostPanelComponent from './layout/GhostPanelComponent.vue';
import { Logger } from '@/util/logger';

const { sendMessage } = useWebSocket();
const route = useRoute();
const router = useRouter();
const lobbyCode = ref(Number(route.params.code));
const serverMessage = ref<string>('');
const mapData = ref<string>('')
const logger = new Logger();

interface Player {
  username: string;
  role: string;
  score: number;
}

interface GameData {
  winningTeam: String | null;
  winnerName: String | null;
  winnerCaloryCount: number | null;
  players: Player[] | null;
}

const gameData = ref<GameData>({
  winningTeam: null,
  winnerName: null,
  winnerCaloryCount: null,
  players: null,
});

const handleServerMessage = (message: string) => {
  serverMessage.value = message;
  if (message.startsWith('GAME_END')) {
    gameData.value = JSON.parse(message.split(';')[1]);
  }
  if (message.startsWith('MAP_DATA')) {
    mapData.value = message.split(';')[1];
  }
};

onMounted(async () => {
  // Method, to wait until Connection is established
  const awaitConnection = () => {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve('Connected');
      }, 1000);
    });
  };

  eventBus.on('serverMessage', handleServerMessage);

  try {
    await awaitConnection();
    const requestData = JSON.stringify({
      type: 'END_GAME',
      gameID: lobbyCode.value,
    });
    sendMessage(requestData);
  } catch (e) {
    console.log('Failed to fetch Data on load: ', e);
  }
});

const toHome = () => {
  router.push('/home/');
};

const getFormattedTimestamp = (): string => {
  const now = new Date();
  const year = now.getFullYear();
  const month = String(now.getMonth() + 1).padStart(2, "0");
  const day = String(now.getDate()).padStart(2, "0");
  const hours = String(now.getHours()).padStart(2, "0");
  const minutes = String(now.getMinutes()).padStart(2, "0");
  const seconds = String(now.getSeconds()).padStart(2, "0");

  return `${year}-${month}-${day}_${hours}-${minutes}-${seconds}`;
};

const saveMap = () => {
  const timestamp = getFormattedTimestamp();
  const filename = `snackmanMap_${timestamp}.csv`;
  const blob = new Blob([mapData.value], { type: "text/plain" });
  const link = document.createElement("a");

  link.href = URL.createObjectURL(blob);
  link.download = filename;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
}

// TODO: Save Map Button
</script>

<style scoped>
html,
body {
  margin: 0;
  padding: 0;
  height: 100%;
  overflow: hidden;
}
.lobby-grid-wrapper {
  position: relative;
  display: flex;
  flex-direction: column;
  height: 100vh;
}

.background-component {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
}

.background-component :deep(.background__title) {
  text-align: center;
  width: 1000px;
  left: 50%;
  transform: translateX(-50%);
}

.lobby-grid {
  position: relative;
  display: grid;
  grid-template-columns: auto 1fr auto;
  grid-template-rows: 1fr;
  gap: 40px 60px;
  padding: calc(11%) calc(10%);
  z-index: 1;
}

.recipe-grid {
  position: fixed;
  bottom: 0;
  left: calc(40%);
  width: 400px;
  height: 80%;
  display: flex;
  flex-direction: column;
  gap: 20px;
  box-sizing: border-box;
  z-index: 10;
  overflow-y: auto;
  margin-bottom: -15px;
}

.recipe-grid__ghostWon {
  position: fixed;
  bottom: 0;
  left: calc(60%);
  width: 400px;
  height: 80%;
  display: flex;
  flex-direction: column;
  gap: 20px;
  box-sizing: border-box;
  z-index: 10;
  overflow-y: auto;
  margin-bottom: -15px;
}

.receiptpanel__item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
}

.seperation-line {
  flex-grow: 1;
  border-bottom: 3px dotted;
  margin: 0 5px 5px;
}

.player-panel {
  padding-left: 200px;
  height: 80%;
  grid-column: 1;
  z-index: 2;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 10;
}

.avatar-overlay img {
  position: absolute;
  top: 110px;
  z-index: 1;
  width: 225px;
  height: 310px;
}

.ghost-panel {
  padding-right: 100px;
  grid-column: 3;
  z-index: 2;
}

.ghost-panel__ghostWon {
  padding-right: 100px;
  grid-column: 2;
  z-index: 2;
}

.ghostpanel__item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 8px 0;
}

.player-name {
  color: #ff8c00;
  font-weight: 500;
}

h3 {
  text-align: center;
}

p {
  text-align: center;
  font-size: 12pt;
}

button {
  color: var(--colorTextDark);
  background-color: var(--colorTertiary);
  width: 100%;
  border-style: solid;
  border-width: 5px;
  border-color: white;
  border-radius: 10px;
  font-family: 'Lilita One', 'Helvetica Neue', Helvetica, Arial, 'Segoe UI', sans-serif;
  padding: 5%;
  margin-top: 15px;
  transition-duration: 0.4s;
}

button:hover {
  background-color: var(--colorTertiary);
  box-shadow: 10px 10px var(--colorShadow);
}
</style>
