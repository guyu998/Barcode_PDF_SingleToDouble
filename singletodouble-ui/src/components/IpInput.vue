<template>
    <div class="ip-input-container">
      <input v-for="(part, index) in ipParts" :key="index" ref="ipInput" v-model="ipParts[index]"
              @keydown="handleKeyDown(index, $event)" @input="handleInput(index, $event)" class="ip-input"
              :class="{ 'error': showError && !validateIP() }">
      <span v-if="showError" class="error-message">请输入有效的 IP 地址</span>
    </div>
  </template>
  
  <script>
  export default {
      props: {
          value: String
      },
      data() {
          return {
              ipParts: ['', '', '', ''],
              inputValue: '',
              showError: false
          };
      },
      watch: {
          value(newVal) {
              console.log(newVal)
              this.inputValue = newVal;
              this.ipParts = ['', '', '', '']
              if (newVal) {
                  this.ipParts = newVal.split('.');
              }
          },
          ipParts: {
              handler(newVal) {
                  const ipString = newVal.join('.');
                  if (ipString !== this.inputValue) {
                      this.inputValue = ipString;
                      this.$emit('input', ipString);
                  }
              },
              deep: true
          }
      },
      mounted() {
          if (this.value) {
              this.inputValue = this.value;
              this.ipParts = this.value.split('.');
          }
      },
      methods: {
          handleKeyDown(index, event) {
              const input = event.target;
              const inputValue = input.value;
  
              if (event.key === "." && inputValue !== "") {
                  const nextIndex = index + 1;
                  if (nextIndex < this.ipParts.length) {
                      this.$refs.ipInput[nextIndex].focus();
                  }
                  event.preventDefault();
              } else if (
                  event.key === "Backspace" &&
                  inputValue === "" &&
                  index > 0 &&
                  input.selectionStart === 0
              ) {
                  const prevIndex = index - 1;
                  const prevInput = this.$refs.ipInput[prevIndex];
                  prevInput.focus();
                  prevInput.setSelectionRange(prevInput.value.length, prevInput.value.length);
                  event.preventDefault();
              } else if (
                  (event.key === "ArrowRight" ||
                      event.key === "Tab" ||
                      event.key === "Enter") &&
                  index < this.ipParts.length - 1 &&
                  input.selectionEnd === inputValue.length
              ) {
                  const nextIndex = index + 1;
                  setTimeout(() => {
                      this.$refs.ipInput[nextIndex].focus();
                  }, 0);
                  event.preventDefault();
              } else if (
                  (event.key === "ArrowLeft" ||
                      event.key === "Shift" ||
                      (event.key === "Tab" && event.shiftKey)) &&
                  index > 0 &&
                  input.selectionStart === 0
              ) {
                  const prevIndex = index - 1;
                  const prevInput = this.$refs.ipInput[prevIndex];
                  setTimeout(() => {
                      prevInput.focus();
                      prevInput.setSelectionRange(prevInput.value.length, prevInput.value.length);
                  }, 0);
                  event.preventDefault();
              }
          },
          handleInput(index, event) {
              let value = event.target.value;
              // 格式校验: 只允许输入数字
              value = value.replace(/[^\d]/g, '');
              // 截断超过3位的数字
              value = value.slice(0, 3);
              this.ipParts[index] = value;
  
              // 如果输入满了3个数字，自动跳转到下一个输入框
              if (value.length === 3 && index < this.ipParts.length - 1) {
                  this.$refs.ipInput[index + 1].focus();
              }
  
              // 校验IP地址格式，并显示/隐藏错误提示
              this.showError = !this.validateIP();
              const ipString = this.ipParts.join('.');
              if (value || index === 3) {
                  this.inputValue = ipString;
                  this.$emit('input', ipString);
              }
          },
          validateIP() {
              const ipRegex = /^(\d{1,2}|1\d{2}|2[0-4]\d|25[0-5])(\.(\d{1,2}|1\d{2}|2[0-4]\d|25[0-5])){3}$/;
              const ipString = this.ipParts.join('.');
              return ipRegex.test(ipString);
          }
      }
  };
  </script>
  
  <style scoped>
  .ip-input-container {
      display: flex;
      height: 40px;
      padding-bottom: 10px;
  }
  
  .ip-input {
      width: 40px;
      text-align: center;
      border: none;
      border-bottom: 1px solid #ccc;
      margin-right: 5px;
      outline: none;
  }
  
  .ip-input.error {
      border-bottom-color: red;
  }
  
  .error-message {
      color: red;
  }
  </style>./IpInput.vue