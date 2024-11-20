<template>
  <button @click="loadMap">Map laden</button>
</template>

<script setup lang="ts">
import eventBus from '@/services/eventBus'
import useWebSocket from '@/services/socketService'

const { serverResponse, connect, sendMessage, closeConnection } = useWebSocket()

const loadMap = () => {
  console.log('loading map')
  sendMessage('Das ist meine Anfrage')
}
</script>

<style scoped>
h1 {
  color: blue;
}
button {
  background-color: black;
  color: white;
  border: none;
  float: right;
  margin-top: 20px;
  margin-right: 20px;
  font-size: 14pt;
}
</style>
