<template>
  <h1>Home</h1>
  <p>Welcolme, {{ name }}</p>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router';
import { computed } from 'vue';

const route = useRoute();
const name = computed(() => route.query.name || 'Guest');
</script>

<style scoped></style>
