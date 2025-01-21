<script setup lang="ts">

    type Props = {
        name: string
        id: number,
    }

    const props = withDefaults(defineProps<Props>(), {
        name: "Default",
        id: 0
    })

</script>

<template>
    <div class="playerpanel">
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

                <img class="playerpanel__content-pin" src="@/assets/icons/pin.svg">

                        <img class="playerpanel__content-image" src="@/assets/images/avatars/avatar_snackman.svg">
                <div class="playerpanel__content-body">
                    <p>{{ name }}(#{{ id }})</p>
                </div>
            </div>
        </div>
        
    </div>
</template>

<style scoped lang="css">

.playerpanel--height-auto,
.playerpanel--height-auto .playerpanel__container {
    height: auto;
}

.playerpanel--height-stretch, 
.playerpanel--height-stretch .playerpanel__container {
    height: 100%;
}

.playerpanel__container {
    width: 200px;
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
    display: flex;
    flex-direction: column;
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
    align-items: center;
    justify-content: center;
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

.playerpanel__content-pin {
    width: 30%;
    height: 100%;
    transform: translateY(-30px);
    padding-left: 20px;
}

.playerpanel__content-body {
    padding: 15px 30px 30px;
    box-sizing: border-box;
    position: relative;
}

.playerpanel__content-body p {
    color: white;
    margin: 0;
}
</style>