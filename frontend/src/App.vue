<script setup lang="ts">
import Scene from './components/Scene.vue'
import Username from './components/Username.vue'
import FetchMap from './components/FetchMap.vue'
import ThreeDModelViewer from './components/ThreeDModelViewer.vue'
</script>

<template>
  <!--
  <ThreeDModelViewer />
  -->
  <FetchMap />
  <Username />
  <Scene />
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
