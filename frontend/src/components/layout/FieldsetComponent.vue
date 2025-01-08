<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';

    type Props = {
        heightBehaviour?: 'auto' | 'stretch'
    }

    const props = withDefaults(defineProps<Props>(), {
        heightBehaviour: 'auto'
    })

    const classes = computed(() => {
        return {
            [`fieldset--height-${props.heightBehaviour}`]: true
        }
    })

    const scrollContainer = ref()

    onMounted(() => {
        if(!scrollContainer.value) return false

        console.log(scrollContainer.value)
        scrollContainer.value.classList.toggle('fieldset__scroll-container--has-overflow', scrollContainer.value.scrollHeight > scrollContainer.value.clientHeight)
    })
</script>

<template>
    <div class="fieldset" :class="classes">
        <div class="fieldset__scroll-container" ref="scrollContainer">
            <div class="fieldset__content">
                <slot></slot>
            </div>
        </div>
    </div>
</template>

<style scoped>
    .fieldset {
        width: 100%;
        background-color: var(--colorSecondary);
        border-radius: 9px;
        padding: 10px;
        box-sizing: border-box;
    }

    .fieldset--height-stretch {
        height: 100%;
    }

    .fieldset--height-auto {
        height: max-content;
    }

    .fieldset__scroll-container {
        width: 100%;
        height: 100%;
        overflow-y: scroll;
        overflow-x: hidden;
    }

    .fieldset__scroll-container:not(.fieldset__scroll-container--has-overflow)::-webkit-scrollbar {
        display: none;
    }

    .fieldset__scroll-container:not(.fieldset__scroll-container--has-overflow) .fieldset__content {
        padding-right: 0;
    }

    .fieldset__scroll-container--has-overflow::-webkit-scrollbar {
        width: 12px;
    }

    .fieldset__scroll-container--has-overflow::-webkit-scrollbar-track {
        background-color: rgba(255, 255, 255, 0.3);
        border-radius: 6px;
    }

    .fieldset__scroll-container--has-overflow::-webkit-scrollbar-thumb {
        background-color: var(--colorLight);
        border-radius: 6px;
    }

    .fieldset__content {
        width: 100%;
        height: max-content;
        display: flex;
        flex-direction: column;
        gap: 10px;
        padding-right: 10px;
        box-sizing: border-box;
    }

    .fieldset__content:deep(span) {
        height: 120px;
        display: block;
        background-color: #fff3;
    }
</style>