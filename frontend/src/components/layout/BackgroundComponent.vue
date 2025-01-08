<script setup lang="ts">
type Props = {
    title?: string
}

const props = defineProps<Props>();
</script>

<template>
    <div class="background">
        <div class="background__image"></div>
        <div class="background__container">
            <div class="background__sheet">
                <div class="background__sheet-tile background__sheet-tile--left">
                    <img class="background__sheet-image" src="@/assets/images/backgrounds/backgroundcontainer_left.png">
                </div>
                <div class="background__sheet-tile background__sheet-tile--stretch">
                    <img class="background__sheet-image" src="@/assets/images/backgrounds/backgroundcontainer_stretch.png">
                </div>
                <div class="background__sheet-tile background__sheet-tile--center">
                    <img class="background__sheet-image" src="@/assets/images/backgrounds/backgroundcontainer_center.png">
                </div>
                <div class="background__sheet-tile background__sheet-tile--stretch">
                    <img class="background__sheet-image" src="@/assets/images/backgrounds/backgroundcontainer_stretch.png">
                </div>
                <div class="background__sheet-tile background__sheet-tile--right">
                    <img class="background__sheet-image" src="@/assets/images/backgrounds/backgroundcontainer_right.png">
                </div>
                
            </div>
            <div class="background__title" v-if="props.title">
                <h1 class="background__headline">{{ props.title }}</h1>
            </div>
            <div class="background__content">
                <slot></slot>
            </div>
        </div>
    </div>
</template>

<style scoped lang="css">
.background {
    width: 100dvw;
    height: 100dvh;
    position: relative;
}

.background__image {
    width: 100%;
    height: 100%;
    position: absolute;
    z-index: 1;
    background-color: var(--colorSecondary);
    background-image: url('../../assets/images/backgrounds/backgroundcontainer_image.jpg');
    background-size: cover;
    background-repeat: no-repeat;
    background-position: center;
    user-select: none;
    pointer-events: none;
}

.background__container {
    width: 92dvw;
    max-width: 1440px;
    height: calc(100% - 8dvw);
    max-height: 770px;
    position: relative;
    z-index: 2;
    /* position element centered */ 
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    /*background-color: var(--colorPrimary);*/
}

.background__sheet {
    width: 100%;
    height: 100%;
    position: absolute;
    display: grid;
    grid-template-columns: auto 1fr auto 1fr auto;
    grid-template-rows: 100%;
    filter: drop-shadow(35px 35px 4px rgba(0,0,0,0.2));
    user-select: none;
    pointer-events: none;
}

.background__sheet-tile {
    height: 100%;

}

.background__sheet-tile--left,
.background__sheet-tile--center,
.background__sheet-tile--right {
    width: max-content;
}

.background__sheet-tile--stretch {
    width:calc(100% + 2px);
    margin-left: -1px;
    margin-right: -1px;
}

.background__sheet-tile--left .background__sheet-image,
.background__sheet-tile--center .background__sheet-image,
.background__sheet-tile--right .background__sheet-image {
    width: max-content;
    height: 100%;
}

.background__sheet-tile--stretch .background__sheet-image {
    width:100%;
    height: 100%;
}

.background__title {
    position: absolute;
    width: max-content;
    height: 90px;
    left: 4dvw;
    top: -32px;
    z-index: 2;
    background-color: var(--colorPrimary);
    border: 9px solid var(--colorLight);
    border-radius: 2px;
    box-sizing: border-box;
    box-shadow: 10px 10px 0 rgba(0,0,0,0.2);
    padding: 10px 25px;
}

.background__headline {
    color: var(--colorLight);
    margin: 0;
    font-size: 45px;
    font-weight: normal;
}

.background__content {
    position: relative;
    width: 100%;
    height: 100%;
    padding-top: 2dvw;
}

</style>