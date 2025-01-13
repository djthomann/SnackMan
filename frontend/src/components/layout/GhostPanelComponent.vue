<script setup lang="ts">
import { useSlots } from 'vue';


const slots = useSlots();

// platzhalter spielerdaten
const players = [
    { name: 'Spieler 1', score: 150 },
    { name: 'Spieler 2', score: 200 },
    { name: 'Spieler 3', score: 120 },
    { name: 'Spieler 4', score: 120 },
];
</script>

<template>
    <div class="ghostpanel">
        <div class="ghostpanel__container">
            <div class="ghostpanel__box">
                <li v-for="(player, index) in players" :key="index" class="ghostpanel__item">
                    <img src="@/assets/images/avatars/avatar_ghost.svg">
                    <h3>{{ player.name }}</h3>
                </li>
            </div>
        </div>
    </div>
</template>

<style scoped lang="css">
.ghostpanel {
    width: 350px;
    border-radius: 8px;
    padding: 16px;
    display: flex;
    flex-direction: column;
    justify-content: flex-start;
    min-height: 100vh;
    overflow: hidden;

}

.ghostpanel__container {
    position: relative;
    width: 100%;
    max-width: 340px;
    z-index: 3;
    height: 100%;
}

.ghostpanel__box {
    text-align: center;
    padding: 16px;
    height: 100%;
    overflow: hidden;
}

.ghostpanel__list {
    list-style: none;
    padding: 0;
}

.ghostpanel__item {
    display: flex;
    padding: 8px 0;
}

.ghostpanel__item:last-child {
    border-bottom: none;
}

</style>
