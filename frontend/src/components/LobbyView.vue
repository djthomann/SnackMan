<template>
  <div>
    <!--
    <h1>Lobby {{ lobbyCode }}</h1>
      <button @click="choose('SnackMan')">Choose SnackMan</button>
      <button @click="choose('Ghost')">Choose Ghost</button>
      <h3>SnackMan</h3>
      <ul>
        <li v-for="player in snackManPlayers" :key="player.id">
          {{ player.username }}
        </li>
      </ul>
      <h3>Ghosts</h3>
      <ul>
        <li v-for="player in ghostPlayers" :key="player.id">
          {{ player.username }}
        </li>
      </ul>
      <h3>Undecided Players</h3>
      <ul>
        <li v-for="player in undecidedPlayers" :key="player.id">
          {{ player.username }}
        </li>
      </ul>
      <form @submit.prevent="submitForm" id="gameConfig" :action="`/lobby/${lobbyCode}`" method="POST">
        <label for="scoreToWin">Score to Win:</label><br />
        <input type="number" id="scoreToWin" v-model="gameConfig.scoreToWin" /><br /><br />

        <label for="speedModifier">Movement-Speed:</label><br />
        <input type="number" id="speedModifier" v-model="gameConfig.speedModifier" /><br /><br />

        <label for="snackmanSpeed">Snackman Speed:</label><br />
        <input type="number" id="snackmanSpeed" v-model="gameConfig.snackManSpeed" /><br /><br />

        <label for="ghostSpeed">Ghost Speed:</label><br />
        <input type="number" id="ghostSpeed" v-model="gameConfig.ghostSpeed" /><br /><br />

        <label for="chickenSpeed">Chicken Speed:</label><br />
        <input type="number" id="chickenSpeed" v-model="gameConfig.chickenSpeed" /><br /><br />

        <label for="mapWidth">Map Width in tiles:</label><br />
        <input type="number" id="mapWidth" v-model="gameConfig.mapWidth" /><br /><br />

        <label for="mapHeight">Map Height in tiles:</label><br />
        <input type="number" id="mapHeight" v-model="gameConfig.mapHeight" /><br /><br />

        <label for="gameTime">Game Time in seconds:</label><br />
        <input type="number" id="gameTime" v-model="gameConfig.gameTime" /><br /><br />

        <label for="chickenCount">Number of Chicken:</label><br />
        <input type="number" id="chickenCount" v-model="gameConfig.chickenCount" /><br /><br />

        <label for="jumpCalories">Calories Burned on Jump:</label><br />
        <input type="number" id="jumpCalories" v-model="gameConfig.jumpCalories" /><br /><br />

        <button type="submit">Apply</button>
        <button type="button" @click="resetForm">Reset</button>
        <button type="button" @click="startGame">Start Game</button>
      </form>

    -->


    <BackgroundComponent>
      <div class="lobby-grid">
        <div class="lobby-grid__column">
          <PlayerPanelComponent  avatar="ghost">
            <template #counter>{{ ghostPlayers.length }}/4</template>
            <template #button><button type="button" @click="decide(false)"></button>be Ghost</template>
            <template #content>
              <li v-for="player in ghostPlayers" :key="player.id">
                {{ player.username }}
              </li>
            </template>
          </PlayerPanelComponent>
        </div>
        <div class="lobby-grid__column">
          <PlayerPanelComponent avatar="ghost">
            <template #counter>{{ undecidedPlayers.length }}/8</template>
            <template #content>
              Undecided Players
              <li v-for="player in undecidedPlayers" :key="player.id">
                {{ player.username }}
              </li>
            </template>
          </PlayerPanelComponent>
        </div>
        <div class="lobby-grid__column">
          <PlayerPanelComponent  avatar="snackman" selected>
            <template #counter>{{ snackManPlayers.length }}/4</template>
            <template #button><button type="button" @click="decide(true)"></button>be Snackman</template>
            <template #content>
              <li v-for="player in snackManPlayers" :key="player.id">
                {{ player.username }}
              </li>
            </template>
          </PlayerPanelComponent>
        </div>
        <div class="lobby-grid__column lobby-grid__column--span-all">
          Merry Crisis
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
import type {Player} from "@/types/SceneTypes";
import BackgroundComponent from './layout/BackgroundComponent.vue';
import PlayerPanelComponent from './layout/PlayerPanelComponent.vue';
import { Logger } from '../util/logger';

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
  } else if (message.startsWith("PLAYERS")) {
    buildPlayerArrays(message);
  } else if (message.startsWith("FOREIGN_GAMESTART")) {
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
    if (player[2] === "SNACKMAN") {
      snackmanArray.push({id: Number(player[1]), username: player[0]})
    } else if (player[2] === "GHOST") {
      ghostArray.push({id: Number(player[1]), username: player[0]})
    } else {
      undecidedArray.push({id: Number(player[1]), username: player[0]})
    }
  })
  snackManPlayers.value = snackmanArray;
  undecidedPlayers.value = undecidedArray;
  ghostPlayers.value = ghostArray;
}

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
    id: userStore.id
  });
  sendMessage(data);

  fetchPlayers();
}

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
  const requestData = JSON.stringify( {
    type: "GET_PLAYERS",
    lobbyCode: lobbyCode.value
  })
  sendMessage(requestData);
}

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
      type: "CHOOSEROLE",
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
  box-sizing:border-box;
}

.lobby-grid__column--span-all {
  grid-column: 1/4;
  text-align: center;
}
</style>
