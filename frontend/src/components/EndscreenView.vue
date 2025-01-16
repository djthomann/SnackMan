<template>
  <!--
  <div v-if="gameData">
    <h1>Congrats! {{ gameData.winningTeam }} Won!</h1>
    <div>
      <div>
        <h2>Winner</h2>
        <p>Cheese Thief of the Month</p>
        <p>{{ gameData.winnerName }}</p>
        <p>{{ gameData.winnerCaloryCount }}</p>
      </div>
      <div>
        <button type="button">Save Map</button>
        <button type="button" @click="toLobby">Back To Lobby</button>
      </div>
    </div>
    <div>
      <h2>Receipt</h2>
      <ul>
        <li v-for="player in gameData.players">
          <b>{{ player.username }}</b>
          <p>{{ player.score }}</p>
        </li>
      </ul>
    </div>
  </div>

  <div v-else>
    <p>Loading Results...</p>
  </div>
  -->
  <div class="lobby-grid-wrapper">

    <div class="background-component">
    <BackgroundComponent :title="`Congrats! Snackman Won!`"></BackgroundComponent>
    </div>

      <div class="lobby-grid">

        <div class="player-panel">
          <PlayerPanelComponent avatar="snackman" variant="white">
            <template #button>Insert Button Component</template>
            <template #content>Snacko<br><strong>123456789 CAL</strong></template>
          </PlayerPanelComponent>
          <div>
            <button>Save Map</button>
          </div>
          <div>
            <button>Back to Lobby</button>
          </div>
        </div>

        <div class="ghost-panel">
          <GhostPanelComponent></GhostPanelComponent>
        </div>

      </div>

      <div class="recipe-grid">
        <ReceiptComponent></ReceiptComponent>
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
// TODO: Testdata, delete later
// import testData from '@/assets/testData.json';

const { sendMessage } = useWebSocket();
const route = useRoute();
const router = useRouter();
const lobbyCode = ref(Number(route.params.code));
const serverMessage = ref<string>('');

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
  if (message.startsWith('RESULTS')) {
    gameData.value = JSON.parse(message.split(';')[1]);
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

  // TODO: Testing purposes, delete this line later
  // gameData.value = testData;

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

const toLobby = () => {
  router.push('/lobby/' + lobbyCode.value);
};

// TODO: Save Map Button
</script>

<style scoped>
html, body {
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

.player-panel {
  padding-left: 200px;
  grid-column: 1;
  z-index: 2;
}
.ghost-panel{
  padding-right: 100px;
  grid-column: 3;
  z-index: 2;
}

button {
  color: var(--colorTextDark);
  background-color: var(--colorTertiary);
  width: 100%;
  border-style: solid;
  border-width: 5px;
  border-color: white;
  border-radius: 10px;
  font-family: "Lilita One", "Helvetica Neue", Helvetica, Arial, "Segoe UI", sans-serif;
  padding: 5%;
  margin-top: 15px;
  transition-duration: 0.4s;
}

button:hover {
  background-color: var(--colorTertiary);
  box-shadow: 10px 10px var(--colorShadow);
}

</style>
