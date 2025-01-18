<template>
  <div class="overlay-container">
    <div v-if="menuVisible" class="menu-container">
      <div class="menu">
        <h2>Das ist unser Menü!</h2>
        <div class="slider-container">
          <!-- Insert another icon here pls -->
          <p>Volume</p>
          <input type="range" min="0" max="100" value="50" class="slider" id="mySlider" />
        </div>
      </div>
    </div>
    <div class="info-box timer">
      <img class="icon" src="@/assets/icons/clock.svg" alt="Clock Icon" />
      <p v-text="seconds" class="overlay-info timer-info"></p>
    </div>
    
    <div v-if="userStore.isSnackman" class="info-box calories">
      <img class="icon" src="@/assets/icons/calories.svg" alt="Clock Icon" />
      <p class="overlay-info calories-info">{{ calories }}</p>
    </div>

    <div v-if="!userStore.isSnackman" class="info-box calories">
      <img class="icon" src="@/assets/icons/ghost_icon.svg" alt="Clock Icon" />
      <p class="overlay-info calories-info">{{ collisions }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useGameStore } from '@/stores/gameStore';
import { useUserStore } from '@/stores/userStore';
import { computed, ref } from 'vue';
import { onMounted, onUnmounted } from 'vue';

const gameStore = useGameStore();
const userStore = useUserStore();

const menuVisible = ref<boolean>(false);
const seconds = computed(() => gameStore.remainingTime);
const calories = computed(() => gameStore.calories);
const collisions = computed(() => gameStore.collisions);

const toggleVisibility = () => {
  menuVisible.value = !menuVisible.value;
};

const handleKeydown = (event: KeyboardEvent) => {
  if (event.key == 'Escape') {
    toggleVisibility();
  }
};

const props = defineProps({
  childRef: Object,
});

onMounted(() => {
  window.addEventListener('keydown', handleKeydown);
});

onUnmounted(() => {
  window.removeEventListener('keydown', handleKeydown);
});
</script>

<style scoped>
.menu-container {
  position: absolute;
  width: 100%;
  height: 100%;
  z-index: 2;
  display: flex;
  align-items: center;
  justify-content: center;
}
.menu {
  width: 30%;
  height: 50%;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-direction: column;
  background-color: white;
}
.slider-container {
  display: flex;
  flex-direction: row;
  align-items: center;
}
.overlay-container {
  position: absolute;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.overlay-info {
  user-select: none;
}

.info-box {
  margin: 2%;
  height: 5vh;
  display: flex;
  align-items: center;
  color: white;
  -webkit-text-stroke: 2px black;
}
.timer {
  justify-content: flex-end;
  float: right;
}
.timer-info {
  font-size: 5vh;
  margin-left: 10%;
}
.icon {
  position: relative;
  height: 100%;
}
.calories {
  justify-content: flex-start;
  float: left;
}
.calories-info {
  font-size: 5vh;
  margin-left: 10%;
}
</style>
