export type Snackman = {
    gameId: number;
    objectId: number;
    username: string;
    x: number;
    y: number;
    z: number;
    gainedCalories: number;
  }

  export type Ghost = {
    gameId: number;
    objectId: number;
    username: string;
    collisions: number;
    x: number;
    y: number;
    z: number;
  }

  export type Chicken = {
    gameId: number;
    objectId: number;
    x: number;
    y: number;
    z: number;
    gainedCalories: number;
    radius: number;
    state: StateOfObject;
  }

  export type StateOfObject = 'NEUTRAL'|'SCARED'|'FULL'|'INVINCIBLE'|'POWERED_UP';

  export type FoodType = 'UNHEALTHY'|'OKAY'|'HEALTHY'|'EGG';

  export type Food = {
    gameId: number;
    objectId: number;
    x: number;
    y: number;
    z: number,
    calories: number;
    foodType: FoodType;
  }

  export type OccupationType = 'WALL'|'ITEM'|'FREE';

  export type Tile = {
    x: number;
    y: number;
    occupationType: OccupationType;
    food: Food;
  }

  export type SnackManMap = {
    w: number;
    h: number;
    allTiles: Tile[][];
  }

  export type Player = {
    id: number,
    username: string
  }
