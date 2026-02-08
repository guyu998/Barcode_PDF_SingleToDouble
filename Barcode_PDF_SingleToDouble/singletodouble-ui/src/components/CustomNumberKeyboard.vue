<template>
  <div class="numeric-keyboard-container">
    <!-- 虚拟数字键盘 -->
    <div class="numeric-keyboard">
      <div v-for="(row,index1) in keys" :key="index1" class="key-row">
        <button v-for="(key,index) in row" :key="index" @click="handleKeyClick(key)">
          {{ key }}
        </button>
      </div>
      
      <!-- 添加退格和清空按钮 -->
      <div class="key-row">
        <button style="font-size:14px" @click="backspace">退格</button>
        <button @click="handleKeyClick('0')">0</button>
        <button style="font-size:14px" @click="clear">清空</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "VirtualNumericKeyboard",
  props: {
    value: {
      type: String,
      default: ""
    }
  },
  data() {
    return {
      keys: [
        ['1', '2', '3'],
        ['4', '5', '6'],
        ['7', '8', '9'],
      ]
    };
  },
  methods: {
    handleKeyClick(key) {
      this.$emit("input", String(this.value) + key); // 直接更新父组件中的值
    },
    backspace() {
      this.$emit("input", this.value.slice(0, -1)); // 更新父组件值，删除最后一个字符
    },
    clear() {
      this.$emit("input", ""); // 更新父组件值，清空
    }
  }
};
</script>

<style scoped>
.numeric-keyboard-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  /* padding: 20px; */
  background-color: #f0f0f0;
  border-radius: 8px;
}

.numeric-keyboard {
  display: grid;
  grid-gap: 1px;
  background-color: #eee;
  padding: 2px;
  border-radius: 10px;
}

.key-row, .function-row {
  display: flex;
  justify-content: center;
}

button {
  width: 50px;
  height: 50px;
  font-size: 18px;
  margin: 5px;
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 5px;
  cursor: pointer;
}

button:active {
  background-color: #ddd;
}

.function-row button {
  background-color: #4CAF50;
  color: #fff;
  border: none;
  border-radius: 5px;
  padding: 10px;
}
</style>
