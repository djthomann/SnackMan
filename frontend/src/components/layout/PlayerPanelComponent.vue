<script setup lang="ts">
import { computed, useSlots } from 'vue';

    type Props = {
        variant?: 'orange' | 'white'
        heightBehaviour?: 'auto' | 'stretch'
        avatar: 'snackman' | 'ghost'
        selected?: boolean
    }

    const props = withDefaults(defineProps<Props>(), {
        variant: 'orange', 
        heightBehaviour: 'stretch',
        selected: false
    })

    const classes = computed(() => {
        return {
            [`playerpanel--variant-${props.variant}`]: true,
            [`playerpanel--height-${props.heightBehaviour}`]: true,
            ['playerpanel--selected']: props.selected

        }
    })
    
    const slots = useSlots()
</script>

<template>
    <div class="playerpanel" :class="classes">
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
            <div class="playerpanel__content">
                <div class="playerpanel__content-header">
                    <div class="playerpanel__content-headline" v-if="slots.counter">
                        <div class="playerpanel__content-counter" v-if="slots.counter">
                            <slot name="counter"></slot>
                        </div>
                    </div>
                    <div class="playerpanel__content-image">
                        <img v-if="props.avatar === 'ghost'" src="@/assets/images/avatars/avatar_ghost.svg">
                        <img v-else src="@/assets/images/avatars/avatar_snackman.svg">

                        <slot name="avatar-overlay"></slot>
                    </div>
                    <div class="playerpanel__content-button" v-if="slots.button">
                        <slot name="button"></slot>
                    </div>
                </div>
                <div class="playerpanel__content-body" v-if="slots.content">
                    <slot name="content"></slot>
                </div>
            </div>
        </div>
        
    </div>
</template>

<style scoped lang="css">

.playerpanel {
    width:250px;
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


.playerpanel__sheet {
    width: 100%;
    height: 100%;
    position: absolute;
    display: grid;
    grid-template-rows: auto 1fr auto;
    grid-template-columns: 100%;
    filter: drop-shadow(10px 10px 0 rgba(0,0,0,0.2));
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

.playerpanel__content {
    position: relative;
    width: 100%;
    display: grid;
    grid-template-columns: 100%;
    overflow: hidden;
}

.playerpanel--height-auto .playerpanel__content {
    height: auto;
    grid-template-rows: auto auto;
}

.playerpanel--height-stretch .playerpanel__content {
    height: 100%;
    grid-template-rows: auto 1fr;
}

.playerpanel__content-header {
    padding: 25px 30px 15px;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
    gap: 15px;
}

.playerpanel__content-counter {
    width: 100%;
    text-align: right;
    color: var(--colorLight);
    user-select: none;
}

.playerpanel__content-image {
    width: 100%;
    height: 100px;
    user-select: none;
    pointer-events: none;
}

.playerpanel__content-image img {
    width: 100%;
    height: 100%;
    object-fit: contain;
    object-position: center;
}

.playerpanel__content-body {
    padding: 15px 30px 30px;
    box-sizing: border-box;
    position: relative;
}

.playerpanel__content-header + .playerpanel__content-body::before {
    position: absolute;
    content: '';
    width: calc(100% - 60px);
    height: 0px;
    border-top: 3px dashed var(--colorLight);
    inset: -1.5px 0 auto;
    margin: 0 auto;
}
</style>