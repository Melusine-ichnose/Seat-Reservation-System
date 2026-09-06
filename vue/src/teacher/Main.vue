<template>
  <div>
    <!-- 顶部导航栏 -->
    <div class="teacher-header">
      <div class="header-left">
        <el-avatar :size="32" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png"></el-avatar>
        <span class="teacher-name">{{ teacherName }}</span>
      </div>
      <el-button size="small" type="danger" @click="logout" icon="el-icon-switch-button">退出登录</el-button>
    </div>

    <router-view style="margin-bottom: 60px; overflow-x: hidden;"></router-view>
    <TabLayout>
      <div @click="()=>{active=index;if($route.path!==tab.url)$router.replace(tab.url)}"
           :style="index===active?'color:#409eff;font-weight:bold;':''" v-for="(tab,index) in tabs">{{ tab.name }}
      </div>
    </TabLayout>
  </div>
</template>

<script>
import TabLayout from "@/components/TabLayout";
import {Toast} from "vant";

export default {
  name: "Main",
  components: {TabLayout},
  props: {},
  data() {
    return {
      active: 0,
      teacherName: '',
      tabs: [
        {name: '🪑 座位信息', url: '/Teacher/seat'},
        {name: '📋 预约信息', url: '/Teacher/reservation'},
        {name: '⭐ 信用分管理', url: '/Teacher/score'}]
    }
  },

  methods: {
    logout() {
      this.$confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        localStorage.removeItem('user');
        this.$router.replace('/login');
        Toast.success('已退出登录');
      }).catch(() => {});
    },
    getCurIndex() {
      for (let i = 0; i < this.tabs.length; i++) {
        if (this.$route.path === this.tabs[i].url) {
          return i
        }
      }
    }
  },
  created() {
    const user = this.$getUser();
    if (user) {
      this.teacherName = user.username || '教师';
    }
    this.active = this.getCurIndex()
  }
}
</script>

<style scoped>
.teacher-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
  color: white;
}

.teacher-name {
  font-size: 16px;
  font-weight: 500;
}
</style>
