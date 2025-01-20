<template>
  <div class="common-layout">
    <router-view/>
  </div>
</template>

<script lang="ts" setup>
</script>

<style lang="scss">
::-webkit-scrollbar {
  width: 2px; /* 垂直滚动条宽度 */
  height: 2px; /* 水平滚动条高度 */
}
::-webkit-scrollbar-track {
  background: #f1f1f1; /* 轨道背景色 */
  border-radius: 4px; /* 圆角 */
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1; /* 滑块背景色 */
  border-radius: 4px; /* 圆角 */
}

::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8; /* 悬停时滑块背景色 */
}
</style>
