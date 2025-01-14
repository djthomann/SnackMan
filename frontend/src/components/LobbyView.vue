<template>
  <div>
    <BackgroundComponent :title="`LOBBY #${lobbyCode}`">
      <div class="lobby-grid">
        <div class="lobby-grid__column">
          <PlayerPanelComponent avatar="ghost">
            <template #counter>{{ ghostPlayers.length }}/4</template>
            <template #button
              ><button class="chooseRole" type="button" @click="decide(false)">
                Ghost
              </button></template
            >
            <template #content>
              <li v-for="player in ghostPlayers" :key="player.id">
                {{ player.username }}
              </li>
            </template>
          </PlayerPanelComponent>
        </div>
        <div class="lobby-grid__column">
          <ConfigPanelComponent
            headline="Game Settings:"
            :counter="{ current: undecidedPlayers.length, max: 8 }"
          >
            <template #content>
              <div class="config-panel-grid">
                <FieldsetComponent height-behaviour="stretch">
                  <span>Slay</span>
                  <span>Slay</span>
                </FieldsetComponent>
                <FieldsetComponent height-behaviour="stretch">
                  <form
                    @submit.prevent="submitForm"
                    id="gameConfig"
                    :action="`/lobby/${lobbyCode}`"
                    method="POST"
                  >
                    <span>
                      <label for="scoreToWin">Score to Win:</label>
                      <input type="number" id="scoreToWin" v-model="gameConfig.scoreToWin" />
                    </span>
                    <span>
                      <label for="speedModifier">Movement-Speed:</label>
                      <input type="number" id="speedModifier" v-model="gameConfig.speedModifier" />
                    </span>
                    <span>
                      <label for="snackmanSpeed">Snackman Speed:</label>
                      <input type="number" id="snackmanSpeed" v-model="gameConfig.snackManSpeed" />
                    </span>
                    <span>
                      <label for="ghostSpeed">Ghost Speed:</label>
                      <input type="number" id="ghostSpeed" v-model="gameConfig.ghostSpeed" />
                    </span>
                    <span>
                      <label for="chickenSpeed">Chicken Speed:</label>
                      <input type="number" id="chickenSpeed" v-model="gameConfig.chickenSpeed" />
                    </span>
                    <span>
                      <label for="mapWidth">Map Width in tiles:</label>
                      <input type="number" id="mapWidth" v-model="gameConfig.mapWidth" />
                    </span>
                    <span>
                      <label for="mapHeight">Map Height in tiles:</label>
                      <input type="number" id="mapHeight" v-model="gameConfig.mapHeight" />
                    </span>
                    <span>
                      <label for="gameTime">Game Time in seconds:</label>
                      <input type="number" id="gameTime" v-model="gameConfig.gameTime" />
                    </span>
                    <span>
                      <label for="chickenCount">Number of Chicken:</label>
                      <input type="number" id="chickenCount" v-model="gameConfig.chickenCount" />
                    </span>
                    <span>
                      <label for="jumpCalories">Calories Burned on Jump:</label>
                      <input type="number" id="jumpCalories" v-model="gameConfig.jumpCalories" />
                    </span>
                    <button type="submit">Apply</button>
                    <button type="button" @click="resetForm">Reset</button>
                  </form>
                </FieldsetComponent>
              </div>
            </template>
          </ConfigPanelComponent>
        </div>
        <div class="lobby-grid__column">
          <PlayerPanelComponent avatar="snackman" selected>
            <template #counter>{{ snackManPlayers.length }}/4</template>
            <template #button
              ><button class="chooseRole" type="button" @click="decide(true)">
                SnackMan
              </button></template
            >
            <template #content>
              <li v-for="player in snackManPlayers" :key="player.id">
                {{ player.username }}
              </li>
            </template>
          </PlayerPanelComponent>
        </div>
        <div class="lobby-grid__column lobby-grid__column--span-all">
          <button type="button" @click="startGame">Start Game</button>
        </div>
      </div>
    </BackgroundComponent>
  </div>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router';
import { computed, onMounted, ref } from 'vue';
import useWebSocket from '@/services/socketService';
import eventBus from '@/services/eventBus';
import { useUserStore } from '@/stores/userStore';
import type { Player } from '@/types/SceneTypes';
import BackgroundComponent from './layout/BackgroundComponent.vue';
import PlayerPanelComponent from './layout/PlayerPanelComponent.vue';
import { Logger } from '../util/logger';
import ConfigPanelComponent from './layout/ConfigPanelComponent.vue';
import FieldsetComponent from './layout/FieldsetComponent.vue';

const logger = new Logger();

const { sendMessage } = useWebSocket();
const route = useRoute();
const router = useRouter();
const lobbyCode = ref(Number(route.params.code));
const serverMessage = ref<string>('');
const userStore = useUserStore();
const name = computed(() => userStore.username);
const clientID = computed(() => userStore.id);
const snackManPlayers = ref<Array<Player>>([]);
const ghostPlayers = ref<Array<Player>>([]);
const undecidedPlayers = ref<Array<Player>>([
  { id: 1, username: 'Alice' },
  { id: 2, username: 'Bob' },
]);

// Type definition of GameConfig interface
interface GameConfig {
  scoreToWin: number | null;
  speedModifier: number | null;
  snackManSpeed: number | null;
  ghostSpeed: number | null;
  chickenSpeed: number | null;
  mapWidth: number | null;
  mapHeight: number | null;
  gameTime: number | null;
  chickenCount: number | null;
  jumpCalories: number | null;
}
// Reactive variable to store game config
const gameConfig = ref<GameConfig>({
  scoreToWin: null,
  speedModifier: null,
  snackManSpeed: null,
  ghostSpeed: null,
  chickenSpeed: null,
  mapWidth: null,
  mapHeight: null,
  gameTime: null,
  chickenCount: null,
  jumpCalories: null,
});

// Method, to get GameConfig from BE
const handleServerMessage = (message: string) => {
  serverMessage.value = message;
  if (message.startsWith('GAME_CONFIG')) {
    gameConfig.value = JSON.parse(message.split(';')[1]);
  } else if (message.startsWith('GAME_START')) {
    //TODO: get this event into scene
    router.push('/game/' + lobbyCode.value);
  } else if (message.startsWith('PLAYERS')) {
    buildPlayerArrays(message);
  } else if (message.startsWith('FOREIGN_GAMESTART')) {
    router.push('/game/' + lobbyCode.value);
  }
};

const buildPlayerArrays = (message: string) => {
  const parsedData = JSON.parse(message.split(';')[1]);
  const snackmanArray: Array<Player> = [];
  const ghostArray: Array<Player> = [];
  const undecidedArray: Array<Player> = [];
  parsedData.players.forEach((player: string[]) => {
    console.log(`Username: ${player[0]}, Client ID: ${player[1]}`);
    if (player[2] === 'SNACKMAN') {
      snackmanArray.push({ id: Number(player[1]), username: player[0] });
    } else if (player[2] === 'GHOST') {
      ghostArray.push({ id: Number(player[1]), username: player[0] });
    } else {
      undecidedArray.push({ id: Number(player[1]), username: player[0] });
    }
  });
  snackManPlayers.value = snackmanArray;
  undecidedPlayers.value = undecidedArray;
  ghostPlayers.value = ghostArray;
};

// Method, to send GameConfig to BE as JSON
const submitForm = async () => {
  const data = JSON.stringify({
    type: 'SET_GAME_CONFIG',
    gameID: lobbyCode.value,
    objectID: 0,
    gameConfig: gameConfig.value,
  });
  sendMessage(data);
};

// On Reset-Button press, send message to BE with gameID: 0, to signify default value request
const resetForm = async () => {
  const reset = JSON.stringify({
    type: 'GET_GAME_CONFIG',
    gameID: 0,
  });
  sendMessage(reset);
};

const decide = (snackman: boolean) => {
  const data = JSON.stringify({
    type: 'ROLE',
    snackman: snackman,
    id: userStore.id,
  });
  sendMessage(data);

  fetchPlayers();
};

// Automatic call on load
onMounted(async () => {
  // Method, to wait until Connection is established
  const awaitConnection = () => {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve('Connected');
      }, 1000);
    });
  };

  // Registration of EventBus
  eventBus.on('serverMessage', handleServerMessage);

  // Sending request for GameConfig to BE
  try {
    await awaitConnection();
    const requestData = JSON.stringify({
      type: 'GET_GAME_CONFIG',
      gameID: lobbyCode.value,
    });
    sendMessage(requestData);
  } catch (e) {
    logger.info('Failed to fetch Data on load: ', e);
  }

  fetchPlayers();
});

const fetchPlayers = () => {
  const requestData = JSON.stringify({
    type: 'GET_PLAYERS',
    lobbyCode: lobbyCode.value,
  });
  sendMessage(requestData);
};

// Method, to start the game
const startGame = () => {
  const requestData = JSON.stringify({
    type: 'START_GAME',
    gameID: lobbyCode.value,
  });
  sendMessage(requestData);
  router.push('/game/' + lobbyCode.value);
};

const choose = (role: string) => {
  const message = JSON.stringify({
    type: 'CHOOSEROLE',
    clientID: clientID.value,
    username: name.value,
    gameID: lobbyCode.value,
    isSnackMan: role === 'SnackMan' ? true : false,
  });
  sendMessage(message);
};
</script>

<style scoped>
.lobby-grid {
  width: 100%;
  height: 100%;
  display: grid;
  grid-template-columns: auto 1fr auto;
  grid-template-rows: 1fr auto;
  gap: 40px 60px;
  padding: 4dvw;
  box-sizing: border-box;
}

.lobby-grid__column {
  min-height: 0;
}

.lobby-grid__column--span-all {
  grid-column: 1/4;
  text-align: center;
}

.config-panel-grid {
  width: 100%;
  height: 100%;
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-template-rows: 100%;
  gap: 30px;
}

.chooseRole {
  width: 100%;
  padding: 10px;
  background-color: white;
  color: #ff8c00;
  font-size: 1.5rem;
  font-weight: bold;
  border: none;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.chooseRole:hover {
  color: black;
  transform: scale(1.05);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
}
</style>
