<script setup lang="ts">
import { computed, useSlots } from 'vue';

    type Props = {
        variant?: 'orange' | 'white'
        heightBehaviour?: 'auto' | 'stretch'
    }

    const props = withDefaults(defineProps<Props>(), {
        variant: 'orange', 
        heightBehaviour: 'stretch'
    })

    const classes = computed(() => {
        return {
            [`playerpanel--variant-${props.variant}`]: true,
            [`playerpanel--height-${props.heightBehaviour}`]: true

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
        </div>
        <div class="playerpanel__content">
            <div class="playerpanel__content-header">
                <div class="playerpanel__content-headline" v-if="slots.counter">
                    <div class="playerpanel__content-counter" v-if="slots.counter">
                        <slot name="counter"></slot>
                    </div>
                </div>
                <div class="playerpanel__content-image" v-if="slots.image">
                    <slot name="image"></slot>
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

.playerpanel__sheet {
    width: 100%;
    height: 100%;
    position: absolute;
    display: grid;
    grid-template-rows: auto 1fr auto;
    grid-template-columns: 100%;
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
}

.playerpanel--height-auto .playerpanel__content {
    height: auto;
    grid-template-rows: auto auto;
}

.playerpanel--height-stretch .playerpanel__content {
    height: 100%;
    grid-template-rows: auto 1fr;
}
</style>