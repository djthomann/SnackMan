<template>
  <button @click="loadMap">Map laden</button>
</template>

<script setup lang="ts">
import eventBus from '@/services/eventBus'
import useWebSocket from '@/services/socketService'

const { serverResponse, connect, sendMessage, closeConnection } = useWebSocket()

const loadMap = () => {
  console.log('loading map')
  sendMessage('MAP')
}
</script>

<style scoped>
h1 {
  color: blue;
}
button {
  background-color: black;
  color: white;
  border: 2px solid turquoise;
  float: right;
  padding: 5px;
  margin-top: 12px;
  margin-right: 20px;
  font-size: 14pt;
  transition: background-color 200ms;
}
button:hover {
  background-color: white;
  color: black;
  cursor: pointer;
}
</style>
