<script setup lang="ts">
import { useSlots } from 'vue';

    type Props = {
        headline: string,
        counter?: {
            current: number,
            max: number
        }
    }

    const props = withDefaults(defineProps<Props>(), {
        
    })

    const slots = useSlots();
</script>

<template>
    <div class="configpanel">
        <div class="configpanel__content">
            <div class="configpanel__content-header">
                <h2 class="configpanel__content-headline">{{ props.headline }}</h2>
                <div class="configpanel__content-counter" v-if="props.counter">
                    {{ props.counter?.current }}/{{ props.counter?.max }}
                </div>
            </div>
            <div class="configpanel__content-body" v-if="slots.content">
                <slot name="content"></slot>
            </div>
            <div class="configpanel__content-footer" v-if="slots.footer">
                <slot name="footer"></slot>
            </div>
        </div>
    </div>
</template>

<style scoped>
    .configpanel {
        width: 100%;
        height: 100%;
        background-color: var(--colorLight);
        border-radius: 3px;
        box-shadow: 10px 10px 0 rgba(0,0,0,0.2);
    }

    .configpanel__content {
        width: 100%;
        display: grid;  
        grid-template-columns: 100%;
        grid-template-rows: auto 1fr auto;
        padding-bottom: 15px;
    }

    .configpanel__content-header {
        width: 100%;
        position: relative;
        box-sizing: border-box;
        padding: 25px 30px 30px;
        display: flex;
        justify-content: space-between;
        flex-wrap: nowrap;
        gap: 0 15px;
    }

    .configpanel__content-header::after {
        position: absolute;
        content: '';
        width: calc(100% - 60px);
        height: 0;
        border-top: 3px dashed var(--colorSecondary);
        inset: auto 0 16.5px;
        margin: 0 auto;
    }

    .configpanel__content-headline {
        font-weight: normal;
        font-size: var(--fontSizeDefault);
        margin: -1px 0;
    }

    .configpanel__content-counter {
        white-space: nowrap;
        position: relative;
        padding-left: calc(18px + 4px);
    }

    .configpanel__content-counter::before {
        position: absolute;
        content: '';
        width: 18px;
        height: 18px;
        left: 0;
        top: calc(50% - 1px);
        transform: translateY(-50%);
        background-image: url('../../assets/images/icons/user_icon.svg');
        background-size: contain;
        background-repeat: no-repeat;
        background-position: center;
    }

    .configpanel__content-body {
        width: 100%;
        padding: 30px;
        box-sizing: border-box;
        overflow: hidden;
    }

    .configpanel__content-footer {
        width: 100%;
        padding: 15px 30px;
        box-sizing: border-box;
        display: flex;
        justify-content: center;
        gap: 10px;
    }
</style>