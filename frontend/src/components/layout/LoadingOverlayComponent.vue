<script setup lang="ts">
import { onMounted, ref } from "vue"

    const texts = [
        'Food is being scattered ...',
        'Chicken are charging up ...',
        'Marvin is preparing Onigiri ...',
        'Brokkoli is being washed ...',
        '17 cakes are baking ...',
        'The tea is being spilled ...'

    ]
    const currentText = ref<string>();

    onMounted(() => {
        setInterval(() => {
            selectText();
        }, 2000)
        
    })

    function selectText() {
        currentText.value = texts[Math.floor(Math.random() * texts.length)];
    }
</script>

<template>
    <div class="loading-overlay">
        <div class="loading-overlay__background-image"></div>
        <div class="loading-overlay__container">
            <div class="loading-overlay__image">
                <img class="loading-overlay__image-gif" src="@/assets/images/decorations/chicken.gif"/>
            </div>
            <div class="loading-overlay__content">
                <span class="loading-overlay__text">{{ currentText }}</span>
            </div>
        </div>
    </div>
</template>

<style scoped lang="css">
    .loading-overlay {
        position: fixed;
        inset: 0;
        width: 100dvw;
        height: 100dvh;
        z-index: 1000;
    }

    .loading-overlay__background-image {
        width: 100%;
        height: 100%;
        position: absolute;
        z-index: 1;
        background-color: var(--colorSecondary);
        background-image: url('../../assets/images/backgrounds/backgroundcontainer_image.jpg');
        background-size: 100%;
        background-repeat: no-repeat;
        background-position: center;
        user-select: none;
        pointer-events: none;
        animation-name: zoom;
        animation-duration: 10s;
        animation-iteration-count: infinite;
        animation-direction: alternate-reverse;
        animation-timing-function: ease-in-out ;
    }

    @keyframes zoom {
        0% {
            background-size: 100%;
        }

        100% {
            background-size: 120%;
        }
    }

    .loading-overlay__container {
        position: absolute;
        z-index: 2;
        width: 300px;
        height: 342px;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        background-image: url('../../assets/images/backgrounds/loadingPanel.png');
        background-size: 100% 100%;
        background-repeat: no-repeat;
        background-position: center;
        padding: 30px;
        box-sizing: border-box;
        filter: drop-shadow(10px 10px 0 rgba(0,0,0,0.1));
    }

    .loading-overlay__image {
        width: 100%;
        height: 200px;
        position: relative;
    }

    .loading-overlay__image::before {
        position: absolute;
        content: '';
        width: 85px;
        height: 25px;
        left: 48%;
        bottom: 15px;
        transform: translateX(-50%);
        border-radius: 50%;
        background-color: var(--colorShadow);
        opacity: 0.4;
        filter: blur(5px);
        animation-name: shadow;
        animation-duration: 0.86s;
        animation-iteration-count: infinite;
        animation-timing-function: ease-in-out;
    }

    @keyframes shadow {
        0% {
            transform: translateX(-50%) scale(1.15);
        }

        50% {
            transform: translateX(-50%) scale(1);
        }

        100% {
            transform: translateX(-50%) scale(1.15);
        }
    }

    .loading-overlay__image-gif {
        position: absolute;
        width: 500px;
        height: 500px;
        object-fit: contain;
        object-position: center;
        top: 50%;
        left: 53%;
        transform: translate(-50%, -50%);
        filter: brightness(1.33);
    }

    .loading-overlay__content {
        width: 100%;
        text-align: center;
        padding-top: 20px;
    }
</style>