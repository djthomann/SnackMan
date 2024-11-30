<template>
  <h1>Lobby {{ lobbyCode }}</h1>
  <form @submit.prevent="submitForm" id="gameConfig" :action="`/lobby/${lobbyCode}`" method="POST">
    <label for="scoreToWin">Score to Win:</label><br />
    <input type="number" id="scoreToWin" v-model="gameConfig.scoreToWin" required /><br /><br />

    <label for="speedModifier">Movement-Speed:</label><br />
    <input type="number" id="speedModifier" v-model="gameConfig.speedModifier" required /><br /><br />

    <label for="snackmanSpeed">Snackman Speed:</label><br />
    <input type="number" id="snackmanSpeed"  v-model="gameConfig.snackManSpeed" required /><br /><br />

    <label for="ghostSpeed">Ghost Speed:</label><br />
    <input type="number" id="ghostSpeed" v-model="gameConfig.ghostSpeed" required /><br /><br />

    <label for="chickenSpeed">Chicken Speed:</label><br />
    <input type="number" id="chickenSpeed" v-model="gameConfig.chickenSpeed" required /><br /><br />

    <label for="mapWidth">Map Width in tiles:</label><br />
    <input type="number" id="mapWidth" v-model="gameConfig.mapWidth" required /><br /><br />

    <label for="mapHeight">Map Height in tiles:</label><br />
    <input type="number" id="mapHeight" v-model="gameConfig.mapHeight" required /><br /><br />

    <label for="gameTime">Game Time in seconds:</label><br />
    <input type="number" id="gameTime" v-model="gameConfig.gameTime" required /><br /><br />

    <label for="chickenCount">Number of Chicken:</label><br />
    <input type="number" id="chickenCount" v-model="gameConfig.chickenCount" required /><br /><br />

    <label for="jumpCalories">Calories Burned on Jump:</label><br />
    <input type="number" id="jumpCalories" v-model="gameConfig.jumpCalories" required /><br /><br />

    <button type="submit">Apply</button>
    <button @click="startGame">Start Game</button>
  </form>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router';
import { onMounted, ref } from 'vue';

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

const route = useRoute();
const router = useRouter();
const lobbyCode = ref(Number(route.params.code));

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
const submitForm = async () => {
  try {
    const response = await fetch(`http://localhost:8080/lobby/${lobbyCode.value}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(gameConfig.value),
    });

    if (response.ok) {
      console.log('GameConfig successfully updated.');
    } else {
      throw new Error('Error saving GameConfig.');
    }
  } catch (error) {
    console.error('Failed to submit GameConfig: ', error);
    alert('Could not apply game-settings.');
  }
};

const fetchGameConfig = async () => {
  try {
    const response = await fetch(`http://localhost:8080/lobby/${lobbyCode.value}`, {
      method: 'GET',
      headers: { 'Content-Type': 'application/json' },
    });

    if (response.ok) {
      const config = await response.json();
      gameConfig.value = config;
    } else if (response.status === 404) {
      console.warn('No GameConfig found for this lobby.');
    } else {
      throw new Error('Error fetching GameConfig.');
    }
  } catch (error) {
    console.error('Failed to submit GameConfig: ', error);
    alert('Could not get game-settings.');
  }
};

onMounted(() => {
  fetchGameConfig();
});

const startGame = () => {
  router.push('/game/' + lobbyCode.value);
};
</script>

<style scoped></style>
