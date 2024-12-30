<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router';
import { onMounted, ref } from 'vue';
import useWebSocket from '@/services/socketService';
import eventBus from '@/services/eventBus';
import BackgroundComponent from './layout/BackgroundComponent.vue';
import PlayerPanelComponent from './layout/PlayerPanelComponent.vue';

const { sendMessage } = useWebSocket();
const route = useRoute();
const router = useRouter();
const lobbyCode = ref(Number(route.params.code));
const serverMessage = ref<string>('');

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
  }
}

// Method, to send GameConfig to BE as JSON
const submitForm = async () => {
  const data = JSON.stringify({
    type: "SET_GAME_CONFIG",
    gameID: lobbyCode.value,
    objectID: 0,
    gameConfig: gameConfig.value
  });
  sendMessage(data);
};

// On Reset-Button press, send message to BE with gameID: 0, to signify default value request
const resetForm = async () => {
  const reset = JSON.stringify({
    type: "GET_GAME_CONFIG",
    gameID: 0,
  })
  sendMessage(reset);
}

// Automatic call on load
onMounted(async () => {

  // Method, to wait until Connection is established
  const awaitConnection = () => {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve("Connected");
      }, 1000);
    });
  };

  // Registration of EventBus
  eventBus.on('serverMessage', handleServerMessage);

  // Sending request for GameConfig to BE
  try {
    await awaitConnection();
    const requestData = JSON.stringify({
      type: "GET_GAME_CONFIG",
      gameID: lobbyCode.value,
    });
    sendMessage(requestData);
  } catch (e) {
    console.log("Failed to fetch Data on load: ", e);
  }
});

// Method, to start the game
const startGame = () => {
  router.push('/game/' + lobbyCode.value);
};
</script>

<template>
  <div>
    <BackgroundComponent>
      <div class="lobby-grid">
        <div class="lobby-grid__column">
          <PlayerPanelComponent  avatar="ghost">
            <template #counter>2/4</template>
            <template #button>Insert Button Component</template>
            <template #content>David Snackham</template>
          </PlayerPanelComponent>
        </div>
        <div class="lobby-grid__column">
          <PlayerPanelComponent avatar="ghost"></PlayerPanelComponent>
        </div>
        <div class="lobby-grid__column">
          <PlayerPanelComponent  avatar="snackman" selected>
            <template #counter>3/4</template>
            <template #button>Insert Button Component</template>
            <template #content>David Snackham</template>
          </PlayerPanelComponent>
        </div>
        <div class="lobby-grid__column lobby-grid__column--span-all">
          Merry Crisis
        </div>
      </div>
    </BackgroundComponent>
    
    <!--
    <h1>Lobby {{ lobbyCode }}</h1>
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
  </div>
</template>

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
