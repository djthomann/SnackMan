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
  <div>
    <BackgroundComponent>
    <div class="background__container">
      <div class="lobby-grid receiptpanel--full-height">
        <ReceiptComponent></ReceiptComponent>
      </div>
    </div>
    </BackgroundComponent>
  </div>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router';
import { onMounted, ref } from 'vue';
import useWebSocket from '@/services/socketService';
import eventBus from '@/services/eventBus';
import BackgroundComponent from './layout/BackgroundComponent.vue';
import ReceiptComponent from './layout/ReceiptComponent.vue';
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

.lobby-grid {
  width: 100%;
  height: 90vh;
  display: grid;
  grid-template-columns: auto 1fr auto;
  grid-template-rows: 1fr auto;
  gap: 40px 60px;
  padding: 4dvw;
  box-sizing: border-box;
  overflow: hidden;
}

.lobby-grid__column--span-all {
  grid-column: 1/4;
  text-align: center;
}
</style>
