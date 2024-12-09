export type Snackman = {
    id: number;
    username: string;
    x: number;
    y: number;
    z: number;
    gainedCalories: number;
  }

  export type Ghost = {
    id: number;
    username: string;
    x: number;
    y: number;
    z: number;
  }

  export type Chicken = {
    id: number;
    x: number;
    y: number;
    z: number;
    gainedCalories: number;
  }

  export type FoodType = 'UNHEALTHY'|'OKAY'|'HEALTHY';
  

  export type Food = {
    id: number;
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