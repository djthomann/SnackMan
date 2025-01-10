<script setup lang="ts">
import { computed, useSlots } from 'vue';
import type { Lobby } from '@/types/lobby';

const maxPlayers = 8;

    type Props = {
        lobby: Lobby,
        heightBehaviour?: 'auto' | 'stretch'
    }

    const props = withDefaults(defineProps<Props>(), {
        
    })

    const classes = computed(() => {
        
    })
    
    const slots = useSlots()
</script>

<template>
    <div class="lobbypanel" :class="classes">
        <div class="playerpanel__container">
            <div class="playerpanel__sheet">
                <div class="playerpanel__sheet-tile playerpanel__sheet-tile--top">
                    <img class="playerpanel__sheet-image" src="@/assets/images/backgrounds/playerpanel_top.png">
                </div>
                <div class="playerpanel__sheet-tile playerpanel__sheet-tile--stretch">
                    <img class="playerpanel__sheet-image" src="@/assets/images/backgrounds/playerpanel_stretch.png">
                </div>
                <div class="playerpanel__sheet-tile playerpanel__sheet-tile--bottom">
                    <img class="playerpanel__sheet-image" src="@/assets/images/backgrounds/playerpanel_bottom.png">
                </div>
            </div>
            <div class="lobbypanel__content">
                <div class="lobbypanel__content-header">
                    <p>LOBBY #{{ lobby.lobbyCode }}</p>
                    
                </div>
                <div class="lobbypanel__content-body">
                    <img class="num-player-icon" src="@/assets/icons/person.svg" />
                    <p>{{ lobby.numPlayers }}/{{ maxPlayers }}</p>
                </div>
            </div>
        </div>
        
    </div>
</template>

<style scoped lang="css">
p {
    color: white;
}
.lobbypanel {
    width:200px;
    margin: 10px;
    user-select: none;
    transition: transform 200ms;
}

.lobbypanel:hover {
    cursor: pointer;
    transform: translate(10px, -10px);
    z-index: 10;
}

.lobbypanel:hover .playerpanel__sheet {
    filter: drop-shadow(-10px 10px 0 rgba(0, 0, 0, 0.2));
}

.playerpanel--height-auto,
.playerpanel--height-auto .playerpanel__container {
    height: auto;
}

.playerpanel--height-stretch, 
.playerpanel--height-stretch .playerpanel__container {
    height: 100%;
}

.playerpanel__container {
    width: 100%;
    height: 100%;
    max-width: 340px;
    position: relative;
    z-index: 3;
}

.playerpanel--selected .playerpanel__container::before {
    position: absolute;
    content: '';
    height: 130px;
    width: 100%;
    right: 0;
    top: -87px;
    background-image: url(../../assets/images/decorations/knife.svg);
    background-size: contain;
    background-position: right;
    background-repeat: no-repeat;
    pointer-events: none;
    z-index: 1;
}

.num-player-icon {
    height: 50px;
}

.playerpanel__sheet {
    width: 100%;
    height: 100%;
    position: absolute;
    display: grid;
    grid-template-rows: auto 1fr auto;
    grid-template-columns: 100%;
    filter: none;
    transition: filter 0.2s ease;
    user-select: none;
    pointer-events: none;
}

.playerpanel__sheet-tile {
    width: 100%;
}

.playerpanel__sheet-tile--top,
.playerpanel__sheet-tile--bottom {
    height: auto;
}

.playerpanel__sheet-tile--stretch {
    height: calc(100% + 2px);
    margin-top: -1px;
    margin-bottom: -1px;
}

.playerpanel__sheet-tile--top .playerpanel__sheet-image,
.playerpanel__sheet-tile--bottom .playerpanel__sheet-image {
    height: auto;
    width: 100%;
    margin-bottom: -5px;
}

.playerpanel__sheet-tile--stretch 
.playerpanel__sheet-image {
    width: 100%;
    height: 100%;
}

.lobbypanel__content {
    position: relative;
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
}

.playerpanel--height-auto .lobbypanel__content {
    height: auto;
    grid-template-rows: auto auto;
}

.playerpanel--height-stretch .lobbypanel__content {
    height: 100%;
    grid-template-rows: auto 1fr;
}

.lobbypanel__content-header {
    color: white;
    font-size: 16pt;
    text-align: center;
    display: flex;
    align-items: center;
    justify-content: center;
    
}

.lobbypanel__content-header p {
    margin: 0;
}

.lobbypanel__content-counter {
    width: 100%;
    text-align: right;
    color: var(--colorLight);
    user-select: none;
}

.lobbypanel__content-image {
    width: 100%;
    height: 100px;
    user-select: none;
    pointer-events: none;
}

.lobbypanel__content-image img {
    width: 100%;
    height: 100%;
    object-fit: contain;
    object-position: center;
}

.lobbypanel__content-body {
    margin: 0 10%;
    display: flex;
    align-items: center;
    justify-content: center;
    box-sizing: border-box;
    position: relative;
}
</style>