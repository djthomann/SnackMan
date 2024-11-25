<template>
  <h1>Lobby {{ lobbyCode }}</h1>
  <button @click="startGame">Start Game</button>
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
