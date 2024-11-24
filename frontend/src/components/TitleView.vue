<template>
  <h1>Title</h1>
  <p>Please enter your username</p>
  <input type="text" v-model="usernameInputField" />
  <button @click="submitForm">Submit</button>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const usernameInputField = ref('');
const router = useRouter();

const submitForm = () => {
  router.push({ path: '/home', query: { name: usernameInputField.value } });
};
</script>

<style scoped></style>
