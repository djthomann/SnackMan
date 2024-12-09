<script setup lang="ts">
import { onMounted, onUnmounted } from 'vue';
import useWebSocket from '@/services/socketService';

const { connect, closeConnection } = useWebSocket();

onMounted(() => {
  connect();
});

onUnmounted(() => {
  closeConnection();
});
</script>

<template>
  <router-view></router-view>
</template>

<style scoped>
html {
  margin: 0;
  padding: 0;
}

body {
  margin: 0;
  padding: 0;
}
</style>
