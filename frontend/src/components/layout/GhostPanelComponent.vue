<script setup lang="ts">
import { useSlots } from 'vue';

const slots = useSlots();

</script>

<template>
  <div class="ghostpanel">
    <div class="ghostpanel__container">
      <div class="ghostpanel__box">
        <ul>
          <slot name="ghost-list">
            <li>
              <img src="@/assets/images/avatars/avatar_ghost.svg" />
              <h3>No players available</h3>
            </li>
          </slot>
        </ul>
      </div>
    </div>
  </div>
</template>

<style scoped lang="css">
.ghostpanel {
  width: 350px;
  border-radius: 8px;
  padding: 16px;
  overflow: hidden;
}

.ghostpanel__container {
  position: relative;
  width: 100%;
  max-width: 340px;
  z-index: 3;
  height: 100%;
}

.ghostpanel__box {
  text-align: center;
  padding: 16px;
  height: 100%;
  overflow: hidden;
}

.ghostpanel__list {
  list-style: none;
  padding: 0;
}

ul {
  columns: 2;
}
</style>
