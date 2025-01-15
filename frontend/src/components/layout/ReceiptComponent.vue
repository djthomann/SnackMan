<script setup lang="ts">
import { useSlots } from 'vue';


const slots = useSlots();

// platzhalter spielerdaten
const players = [
  { name: 'Spieler 1', score: 150 },
  { name: 'Spieler 2', score: 200 },
  { name: 'Spieler 3', score: 120 },
];
</script>

<template>
  <div class="receiptpanel">
    <div class="receiptpanel__container">
      <div class="receiptpanel__box">
        <h3>Receipt</h3>
        <hr style="" />
        <ul class="receiptpanel__list">
          <li v-for="(player, index) in players" :key="index" class="receiptpanel__item">
            <span class="player-name">{{ player.name }}</span>
            <div class="seperation-line"></div>
            <span>{{ player.score }} Kalorien</span>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<style scoped lang="css">
h3 {
  font-weight: 500;
  margin-bottom: 24px;
}

hr {
  border: none;
  border-top: 2px dashed #4f9fde;
  margin: 24px auto;
  width: 140px
}

.receiptpanel {
  width: 350px;
  border-radius: 8px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  overflow: hidden;

}

.receiptpanel__container {
  position: relative;
  width: 100%;
  height: 100%;
  max-width: 340px;
  z-index: 3;
  display: flex;
  flex-direction: column;
}

.receiptpanel__box {
  background-color: white;
  box-shadow: 15px 15px 0px rgba(0, 0, 0, 0.25);
  text-align: center;
  padding: 16px;
  height: 100%;
  overflow: hidden;
}

.receiptpanel__list {
  list-style: none;
  padding: 0;
}

.receiptpanel__item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
}

.receiptpanel__item:last-child {
  border-bottom: none;
}

.player-name {
  color: #ff8c00;
  font-weight: 500;
}

.seperation-line {
  flex-grow: 1;
  border-bottom: 3px dotted;
  margin: 0 5px 5px;
}
</style>
