import type { Ghost, Snackman, SnackManMap, Chicken } from "@/types/SceneTypes";
import { defineStore } from "pinia";
import { ref } from "vue";
import useWebSocket from '@/services/socketService';
import eventBus from '@/services/eventBus';
import * as THREE from 'three';

export const useEntityStore = defineStore('entityStore', () => {

  const { sendMessage } = useWebSocket();
  const serverMessage = ref<string>('');

  const handleServerMessage = (message: string) => {
    serverMessage.value = message;
    if (message.startsWith('GAME_START')) {
      const parsedData = JSON.parse(message.split(';')[1]);
      snackMen.value.clear();
      ghosts.value.clear();
      map.value = parsedData.map;

      parsedData.snackMen.forEach((snackman: Snackman) => {
        console.log(`Snackman ${snackman.username} found`)
        snackMen.value.set(Number(snackman.objectId), {
          gameId: Number(snackman.gameId),
          objectId: Number(snackman.objectId),
          username: snackman.username,
          x: Number(snackman.x),
          y: Number(snackman.y),
          z: Number(snackman.z),
          gainedCalories: Number(snackman.gainedCalories)
        })
      })
      parsedData.ghosts.forEach((ghost: Ghost) => {
        console.log(`Snackman ${ghost.username} found`)
        ghosts.value.set(Number(ghost.objectId), {
          gameId: Number(ghost.gameId),
          objectId: Number(ghost.objectId),
          username: ghost.username,
          x: Number(ghost.x),
          y: Number(ghost.y),
          z: Number(ghost.z)
        })
      })
    } else if (message.startsWith('GAME_STATE')) {

    }
  }

  eventBus.on('serverMessage', handleServerMessage);

  /*
  const ghosts = ref<Ghost[]>([]);
  const snackMen = ref<Snackman[]>([]);
  */

  const ghosts = ref<Map<Number, Ghost>>(new Map([
    [101, { gameId: 1, objectId: 101, username: "Ghosty1", x: 108, y: 0, z: 102 }],
    [102, { gameId: 1, objectId: 102, username: "Ghosty2", x: 104, y: 0, z: 102 }]
  ]));
  const snackMen = ref<Map<Number, Snackman>>(new Map([
    [201, { gameId: 1, objectId: 201, username: "SnackMan1", x: 110, y: 0, z: 102, gainedCalories: 0 }],
    [202, { gameId: 1, objectId: 202, username: "SnackMan2", x: 117, y: 0, z: 102, gainedCalories: 100 }],
  ]));
  const chicken = ref<Map<Number, Chicken>>(new Map([]));
  const map = ref();

  return {
    snackMen,
    ghosts,
    chicken,
    map
  }
})
