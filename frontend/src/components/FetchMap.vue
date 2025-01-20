<template>
  <div class="fileInputContainer" :class="{ animateGreen: isGreen }">
    <label for="fileInput" class="dropZone" @dragover="handleDragOver" @drop="handleDrop">
      <svg class="uploadIcon" viewBox="0 0 24 24" fill="currentColor" width="48" height="48">
        <path
          d="M19.35 10.04C18.67 6.59 15.64 4 12 4 9.11 4 6.6 5.64 5.35 8.04 2.34 8.36 0 10.91 0 14c0 3.31 2.69 6 6 6h13c2.76 0 5-2.24 5-5 0-2.64-2.05-4.78-4.65-4.96zM14 13v4h-4v-4H7l5-5 5 5h-3z"
        />
      </svg>
      <p class="dropZoneText">Drag & drop your map here</p>
      <p class="fileTypes">or click to browse (CSV)</p>
    </label>
    <input
      id="fileInput"
      type="file"
      accept=".csv"
      @change="handleFileUpload($event)"
      class="fileInput"
    />

    <div v-if="file" class="fileNameContainer">
      <p class="fileName">{{ file.name }}</p>
    </div>

    <button v-if="file" @click="uploadMap" class="uploadButton">Upload Map</button>
  </div>
</template>

<script setup lang="ts">
import useWebSocket from '@/services/socketService';
import { ref } from 'vue';
import { Logger } from '../util/logger';

const { sendMessage } = useWebSocket();
const file = ref<File | null>();
const logger = new Logger();
const isGreen = ref(false); /* for animation */

const uploadMap = () => {
  /* Animation */
  isGreen.value = true;
  setTimeout(() => {
    isGreen.value = false;
  }, 400);

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
    };
  }
  file.value = null;
  const inputElement = document.getElementById('fileInput') as HTMLInputElement;
  inputElement.value = '';
};

const handleFileUpload = (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (target && target.files) {
    file.value = target.files[0];
    logger.info('file loaded');
  }
};
</script>

<style scoped>
.fileInputContainer.animateGreen {
  background-color: #9cffff;
}

.fileInputContainer {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: transparent;
  border-radius: 2%;
  align-items: center;
  justify-content: center;
  transition: background-color 200ms ease-in-out;
}

.fileInput {
  display: none;
}

.dropZone {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  text-align: center;
  transition: all 0.3s ease;
  cursor: pointer;
}

.dropZone:hover {
  transform: translateY(-3px);
}

.dropZone:hover .uploadIcon {
  opacity: 1;
}

.uploadIcon {
  color: white;
  margin-bottom: 20px;
  opacity: 0.8;
}

.dropZoneText {
  font-size: 18px;
  font-weight: 500;
  color: white;
  margin: 0 0 8px 0;
}

.fileTypes {
  font-size: 14px;
  color: white;
}

.fileNameContainer {
  padding: 12px;
}

.fileName {
  color: white;
  font-size: 16px;
  font-weight: 500;
  margin: 0;
  text-align: center;
}

.uploadButton {
  cursor: pointer;
  background-color: white;
  color: black;
  border: none;
  border-radius: 4px;
  padding: 8px 16px;
}

.uploadButton:hover {
  background-color: #f1f1f1;
}
</style>
