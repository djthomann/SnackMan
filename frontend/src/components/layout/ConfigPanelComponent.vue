<script setup lang="ts">
import { log } from 'three/webgpu';
import { onMounted, ref, useSlots } from 'vue';

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

    /* Dynamic computed body height */

    const container = ref()
    const header = ref()
    const body = ref()
    const footer = ref()

    const calcPrefBodyHeight = () => {
        // Prevent calculations when important elements are missing to prevent errors
        if(!container.value || !header.value || !body.value) return false

        // Get respective sizing values of container and children from viewport
        const containerHeight = Math.round(container.value?.getBoundingClientRect().height ?? 0)
        const headerHeight = Math.round(header.value?.getBoundingClientRect().height ?? 0)
        const bodyHeight = Math.round(body.value?.getBoundingClientRect().height ?? 0)
        const footerHeight = Math.round(footer.value?.getBoundingClientRect().height ?? 0)

        // Calculate cumulative height of configpanel body element from container and sibling sizes
        const prefComputedBodyHeight = containerHeight - headerHeight - footerHeight;

        // Only apply computed height to body element if element height is greater than the computed
        if(bodyHeight > prefComputedBodyHeight) body.value.style.height = `${prefComputedBodyHeight}px`
    }

    // Start body height when mounted lifecycle has finished
    onMounted(() => {
        calcPrefBodyHeight()
    })
</script>

<template>
    <div class="configpanel">
        <div class="configpanel__content" ref="container">
            <div class="configpanel__content-header" ref="header">
                <h2 class="configpanel__content-headline">{{ props.headline }}</h2>
                <div class="configpanel__content-counter" v-if="props.counter">
                    {{ props.counter?.current }}/{{ props.counter?.max }}
                </div>
            </div>
            <div class="configpanel__content-body" v-if="slots.content" ref="body">
                <slot name="content"></slot>
            </div>
            <div class="configpanel__content-footer" v-if="slots.footer" ref="footer">
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
        height: 100%;
        display: grid;  
        grid-template-columns: 100%;
        grid-template-rows: auto 1fr auto;
        padding-bottom: 15px;
        box-sizing: border-box;
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
        padding: 0 30px 15px;
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