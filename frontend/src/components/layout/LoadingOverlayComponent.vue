<script setup lang="ts">
</script>

<template>
    <div class="loadingoverlay">
        <div class="loadingoverlay__content">
            <img class="loadingoverlay__content-gif" src="@/assets/images/decorations/chicken.gif"/>
        </div>
    </div>
</template>

<style scoped lang="css">
    .loadingoverlay {
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        display: flex;
        justify-content: center;
        align-items: center;
        background-color: var(--colorShadow);
        z-index: 1000;
    }


    .loadingoverlay__content-gif {
        width: 700px;
        height: 700px;
        object-fit: contain;
    }
</style>