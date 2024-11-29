<template>
  <h1>Lobby {{ lobbyCode }}</h1>
  <form id="gameConfig" action="/api/game-config" method="POST">
    <label for="scoreToWin">Score to Win:</label><br>
    <input type="number" id="scoreToWin" name="scoreToWin" required><br><br>

    <label for="speedModifier">Movement-Speed:</label><br>
    <input type="number" id="speedModifier" name="speedModifier" step="0.01" required><br><br>

    <label for="snackmanSpeed">Snackman Speed:</label><br>
    <input type="number" id="snackmanSpeed" name="snackmanSpeed" required><br><br>

    <label for="ghostSpeed">Ghost Speed:</label><br>
    <input type="number" id="ghostSpeed" name="ghostSpeed" required><br><br>

    <label for="chickenSpeed">Chicken Speed:</label><br>
    <input type="number" id="chickenSpeed" name="chickenSpeed" required><br><br>

    <label for="mapWidth">Map Width in tiles:</label><br>
    <input type="number" id="mapWidth" name="mapWidth" required><br><br>

    <label for="mapHeight">Map Height in tiles:</label><br>
    <input type="number" id="mapHeight" name="mapHeight" required><br><br>

    <label for="gameTime">Game Time in seconds:</label><br>
    <input type="number" id="gameTime" name="gameTime" required><br><br>

    <label for="chickenCount">Number of Chicken:</label><br>
    <input type="number" id="chickenCount" name="chickenCount" required><br><br>

    <label for="jumpCalories">Calories Burned on Jump:</label><br>
    <input type="number" id="jumpCalories" name="jumpCalories" required><br><br>

    <button type="submit" @click="startGame">Start Game</button>
  </form>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router';
import { computed } from 'vue';

const route = useRoute();
const router = useRouter();
const lobbyCode = computed(() => route.params.code);

const startGame = () => {
  router.push('/game/' + lobbyCode.value);
};
</script>

<style scoped></style>
