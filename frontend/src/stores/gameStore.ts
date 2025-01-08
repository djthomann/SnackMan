import { defineStore } from 'pinia';

export const useGameStore = defineStore('game', {
  state: () => ({
    remainingTime: 0,
    calories: 0,
  }),
  actions: {
    setRemainingTime(time: number) {
      this.remainingTime = time;
    },
    addCalories(amount: number) {
      this.calories += amount;
      console.log(this.calories);
    },
    removeCalories(amount: number) {
      this.calories = Math.max(0, this.calories - amount);
    },
  },
});
