import type { Food, Ghost, Snackman, Tile } from "@/types/SceneTypes";
import { defineStore } from "pinia";
import { ref } from "vue";

export const useEntityStore = defineStore('entityStore', () => {

    const ghosts = ref<Ghost[]>([
        {
          id: 3,
          username: 'Booo',
          x: 1,
          y: 2,
          z: 0,
        } as Ghost,
  ]);
    const snackmen = ref<Snackman[]>([
        {
            id: 1,
            username: 'Snacko',
            x: 1,
            y: 1,
            z: 0,
            gainedCalories: 10,
          } as Snackman,
          {
            id: 2,
            username: 'Snackish',
            x: 2,
            y: 2,
            z: 0,
            gainedCalories: 10,
          } as Snackman,
    ]);

    const foods = ref<Food[]>([]);

    // TODO: add floor once it's type etc is known

    const tiles = ref<Tile[]>([]);


    return {
        snackmen,
        ghosts,
        foods,
        tiles
    }
})