<script setup lang="ts">
import SceneView from './SceneView.vue';
import FetchMap from './FetchMap.vue';
</script>

<template>
  <RouterLink to="/title">To The Title Screen </RouterLink>
  <FetchMap />
  <SceneView />
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
