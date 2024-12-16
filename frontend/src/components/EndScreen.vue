<template>
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
</template>

<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router';
import { onMounted, ref } from 'vue';
import useWebSocket from '@/services/socketService';
import eventBus from '@/services/eventBus';
// TODO: Testdata, delete later
import testData from '@/assets/testData.json';


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
  eventBus.on('serverMessage', handleServerMessage);

  // TODO: Testing purposes, delete this line later
  gameData.value = testData;

  try {
    const requestData = JSON.stringify({
      type: 'GET_RESULTS',
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

<style scoped></style>
