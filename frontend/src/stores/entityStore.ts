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
      let data = JSON.parse(message.split(';')[1]);
      ghosts.value = data.ghosts;
      snackMen.value = data.snackMen;
      chicken.value = data.foods;
      map.value = data.map;
    } else if (message.startsWith('GAME_STATE')) {

    }
  }

  eventBus.on('serverMessage', handleServerMessage);

  /*
  const ghosts = ref<Ghost[]>([]);
  const snackMen = ref<Snackman[]>([]);
  */

  const ghosts = ref<Ghost[]>([
    { gameId: 1, objectId: 101, username: "Ghosty1", x: 108, y: 0, z: 102 },
    { gameId: 1, objectId: 102, username: "Ghosty2", x: 104, y: 0, z: 102 },
  ]);
  const snackMen = ref<Snackman[]>([
    { gameId: 1, objectId: 201, username: "SnackMan1", x: 110, y: 0, z: 102, gainedCalories: 0 },
    { gameId: 1, objectId: 202, username: "SnackMan2", x: 117, y: 0, z: 102, gainedCalories: 100 },
  ]);
  const chicken = ref<Chicken[]>([]);
  const map = ref<SnackManMap>();

  return {
    snackMen,
    ghosts,
    chicken,
    map
  }
})