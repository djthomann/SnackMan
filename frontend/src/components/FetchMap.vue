<template>
  <input
    v-show="!mapLoaded"
    class="my-cool-style"
    accept=".csv"
    type="file"
    @change="handleFileUpload($event)"
  />
  <button v-show="!mapLoaded" class="my-cool-style" @click="uploadMap">Map hochladen</button>
  <button v-show="!mapLoaded" class="my-cool-style" @click="requestMap">Map anfordern</button>
</template>

<script setup lang="ts">
import useWebSocket from '@/services/socketService';
import { ref } from 'vue';

const { sendMessage } = useWebSocket();

const mapLoaded = ref<boolean>(false);
const file = ref<File | null>();

const requestMap = () => {
  if (!mapLoaded.value) {
    console.log('loading map');
    const map = {
      type: 'MAPREQUEST',
    };
    sendMessage(JSON.stringify(map));
    mapLoaded.value = true;
  } else {
    console.log('loaded map already');
  }
};

const uploadMap = () => {
  if (!mapLoaded.value) {
    if (file.value != null) {
      const reader = new FileReader();

      reader.readAsText(file.value);

      reader.onload = () => {
        const fileContent = reader.result as string;

        const map = {
          type: 'MAPUPLOAD',
          content: fileContent,
        };
        sendMessage(JSON.stringify(map));
        mapLoaded.value = true;
      };
    }
  } else {
    console.log('loaded map already');
  }
};

const handleFileUpload = (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (target && target.files) {
    file.value = target.files[0];
    console.log('file loaded');
  }
};
</script>

<style scoped>
.my-cool-style {
  float: right;
  background-color: black;
  color: white;
  border: 2px solid turquoise;
  float: right;
  padding: 5px;
  margin-top: 12px;
  margin-right: 20px;
  font-size: 12pt;
}
h1 {
  color: blue;
}
button {
  margin-top: 12px;
  margin-right: 20px;
  font-size: 12pt;
}
h1 {
  color: blue;
}
button {
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
