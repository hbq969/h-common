<template>
  <div class="time-component">
    当前时间: {{ formattedTime }}
  </div>
</template>

<script lang="ts" setup>
import {ref, computed, onMounted, onUnmounted} from 'vue';

// 定义 props
const props = defineProps({
  showYear: {
    type: Boolean,
    default: true,
  },
  showMonth: {
    type: Boolean,
    default: true,
  },
  showDay: {
    type: Boolean,
    default: true,
  },
  showHour: {
    type: Boolean,
    default: true,
  },
  showMinute: {
    type: Boolean,
    default: true,
  },
  showSecond: {
    type: Boolean,
    default: true,
  },
});

const currentTime = ref(new Date());

// 更新时间函数
const updateTime = () => {
  currentTime.value = new Date();
};

// 格式化时间
const formattedTime = computed(() => {
  const date = currentTime.value;
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  const seconds = String(date.getSeconds()).padStart(2, '0');

  // 根据 props 动态生成时间格式
  const ymd = [];
  const hms = [];
  if (props.showYear) ymd.push(year);
  if (props.showMonth) ymd.push(month);
  if (props.showDay) ymd.push(day);
  if (props.showHour) hms.push(hours);
  if (props.showMinute) hms.push(minutes);
  if (props.showSecond) hms.push(seconds);

  return ymd.join('-') + (hms.length > 0 ? ' '+hms.join(':') : ''); // 用 "-" 连接各部分
});

// 组件挂载时启动定时器
onMounted(() => {
  updateTime();
  const timer = setInterval(updateTime, 1000); // 每秒更新一次

  // 组件卸载时清除定时器
  onUnmounted(() => {
    clearInterval(timer);
  });
});
</script>

<style scoped>
.time-component {
  font-size: 0.6em;
  font-family: Arial, sans-serif;
  color: orange;
  margin-right: 5px;
}
</style>