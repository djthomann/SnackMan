import type { Ghost, Snackman, SnackManMap, Chicken } from "@/types/SceneTypes";
import { defineStore } from "pinia";
import { ref } from "vue";
import useWebSocket from '@/services/socketService';
import eventBus from '@/services/eventBus';
import { update } from "three/examples/jsm/libs/tween.module.js";

export const useEntityStore = defineStore('entityStore', () => {

  const { sendMessage } = useWebSocket();
  const serverMessage = ref<string>('');

  const handleServerMessage = (message: string) => {
    serverMessage.value = message;
    if (message.startsWith('GAME_START')) {
      let data = JSON.parse(message);
      ghosts.value = data.ghosts;
      snackMen.value = data.snackMen;
      chicken.value = data.foods;
      map.value = data.map;
    } else if (message.startsWith('GAME_STATE')) {

    }
  }
  eventBus.on('serverMessage', handleServerMessage);

  const ghosts = ref<Ghost[]>([]);
  const snackMen = ref<Snackman[]>([]);
  const chicken = ref<Chicken[]>([]);
  const map = ref<SnackManMap>();

  return {
    snackMen,
    ghosts,
    chicken,
    map
  }
})