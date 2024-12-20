<script setup lang="ts">
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
}

.background__sheet-tile {
    height: 100%;

}

.background__sheet-tile--left,
.background__sheet-tile--center,
.background__sheet-tile--right {
    width: auto;
}

.background__sheet-tile--stretch {
    width:calc(100% + 2px);
    margin-left: -1px;
    margin-right: -1px;
}

.background__sheet-tile--left .background__sheet-image,
.background__sheet-tile--center .background__sheet-image,
.background__sheet-tile--right .background__sheet-image {
    width: auto;
    height: 100%;
}

.background__sheet-tile--stretch .background__sheet-image {
    width:100%;
    height: 100%;
}

.background__content {
    position: relative;
    width: 100%;
    height: 100%;
}

</style>