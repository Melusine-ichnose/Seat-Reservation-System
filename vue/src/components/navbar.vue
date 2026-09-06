<template>
  <!-- 顶部导航栏：显示标题、返回按钮、退出登录按钮 -->
  <div class="navbar" @click="$emit('click')">
    <!-- 左侧返回按钮：点击返回上一页 -->
    <div class="left" @click="leftClick" :style="left?'':'display:none'">
      <van-icon name="arrow-left" size="1.2em"/>
    </div>
    <div class="left">
      <slot name="left"></slot>
    </div>
    <!-- 中间标题 -->
    <div class="center">{{ title }}</div>
    <!-- 右侧区域：显示退出登录按钮 -->
    <div class="right">
      <!-- 退出登录按钮：点击清除用户信息并跳转到登录页 -->
      <div class="logout-btn" @click="logout" v-if="showLogout">
        <van-icon name="logout" size="1.1em"/>
        <span>退出</span>
      </div>
      <slot name="right"></slot>
    </div>
  </div>
</template>

<script>
import { Toast } from 'vant';

export default {
  name: "navbar",
  data() {
    return {};
  },
  props: {
    // 是否显示左侧返回按钮
    left: {
      default: true
    },
    // 导航栏标题
    title: '',
    // 是否显示退出登录按钮（默认显示）
    showLogout: {
      default: true
    }
  },
  components: {},
  methods: {
    // 返回上一页
    leftClick() {
      this.$router.go(-1)
    },
    // 退出登录：清除本地存储的用户信息，跳转到登录页
    logout() {
      // 清除本地存储的用户信息
      localStorage.removeItem('user');
      // 显示退出成功提示
      Toast.success('已退出登录');
      // 跳转到登录页面
      this.$router.replace('/login');
    }
  }
};
</script>

<style scoped>
.navbar {
  position: sticky;
  background: #ffffff;
  color: black;
  z-index: 100;
  top: 0;
  height: 46px;
  line-height: 46px;
  width: 100%;
  box-sizing: border-box;
  padding: 16px;
}

.right {
  right: 16px;
  top: 0;
  bottom: 0;
  position: absolute;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 退出登录按钮样式 */
.logout-btn {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #e74c3c;
  font-size: 14px;
  cursor: pointer;
  padding: 0 10px;
  transition: all 0.3s;
}

.logout-btn:hover {
  opacity: 0.8;
  background: rgba(231, 76, 60, 0.1);
  border-radius: 5px;
}

.center {
  font-size: 16px;
  top: 0;
  bottom: 0;
  position: absolute;
  left: 0;
  right: 0;
  font-weight: bold;
  text-align: center;
}

.left {
  left: 16px;
  top: 0;
  bottom: 0;
  z-index: 101;
  position: absolute;
  display: flex;
  align-items: center;
}

img {
  width: 1.3em;
  height: 1.3em;
}
</style>
