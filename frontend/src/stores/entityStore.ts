import type { Ghost, Snackman, Chicken } from "@/types/SceneTypes";
import { defineStore } from "pinia";
import { ref } from "vue";
import eventBus from '@/services/eventBus';

import { Logger } from '../util/logger';

export const useEntityStore = defineStore('entityStore', () => {

  const serverMessage = ref<string>('');
  const logger = new Logger();

  const handleServerMessage = (message: string) => {
    serverMessage.value = message;
    if (message.startsWith('GAME_START')) {
      const parsedData = JSON.parse(message.split(';')[1]);
      snackMen.value.clear();
      ghosts.value.clear();
      chickens.value.clear();
      map.value = parsedData.map;

      parsedData.snackMen.forEach((snackman: Snackman) => {
        logger.info(`Snackman ${snackman.username} found`)
        snackMen.value.set(Number(snackman.objectId), {
          gameId: Number(snackman.gameId),
          objectId: Number(snackman.objectId),
          username: snackman.username,
          x: Number(snackman.x),
          y: Number(snackman.y),
          z: Number(snackman.z),
          gainedCalories: Number(snackman.gainedCalories),
        })
      })
      parsedData.ghosts.forEach((ghost: Ghost) => {
        logger.info(`ghost ${ghost.username} found`)
        ghosts.value.set(Number(ghost.objectId), {
          gameId: Number(ghost.gameId),
          objectId: Number(ghost.objectId),
          username: ghost.username,
          collisions: ghost.collisions,
          x: Number(ghost.x),
          y: Number(ghost.y),
          z: Number(ghost.z)
        })
      })
      parsedData.chickens.forEach((chicken: Chicken) => {
        chickens.value.set(Number(chicken.objectId), {
          gameId: Number(chicken.gameId),
          objectId: Number(chicken.objectId),
          x: Number(chicken.x),
          y: Number(chicken.y),
          z: Number(chicken.z),
          gainedCalories: Number(chicken.gainedCalories),
          radius: chicken.radius,
          state: chicken.state
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

  const ghosts = ref<Map<number, Ghost>>(new Map([
    [101, { gameId: 1, objectId: 101, username: "Ghosty1", x: 108, y: 0, z: 102, collisions: 2 }],
    [102, { gameId: 1, objectId: 102, username: "Ghosty2", x: 104, y: 0, z: 102, collisions: 2 }]
  ]));
  const snackMen = ref<Map<number, Snackman>>(new Map([
    [201, { gameId: 1, objectId: 201, username: "SnackMan1", x: 110, y: 0, z: 102, gainedCalories: 0 }],
    [202, { gameId: 1, objectId: 202, username: "SnackMan2", x: 117, y: 0, z: 102, gainedCalories: 100 }],
  ]));
  const chickens = ref<Map<number, Chicken>>(new Map([]));
  const map = ref();

  return {
    snackMen,
    ghosts,
    chickens,
    map
  }
})
