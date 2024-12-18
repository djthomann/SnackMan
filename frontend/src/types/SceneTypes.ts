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
  }

  export type FoodType = 'UNHEALTHY'|'OKAY'|'HEALTHY';
  
  export type Food = {
    gameId: number;
    objectId: number;
    x: number;
    y: number;
    calories: number;
    foodType: FoodType;
  }

  export type OccupationType = 'WALL'|'ITEM'|'FREE';

  export type Tile = {
    x: number;
    y: number;
    occupationType: OccupationType;
  }

  export type SnackManMap = {
    w: number;
    h: number;
    allTiles: Tile[][];
  }