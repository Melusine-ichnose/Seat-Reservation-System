<template>
  <div>
    <!-- 顶部导航栏：紫色背景，显示标题和退出按钮 -->
    <div class="top-navbar">
      <div class="nav-left"></div>
      <div class="nav-title">论坛</div>
      <div class="nav-right">
        <el-button type="danger" size="small" @click="logout" icon="el-icon-switch-button">退出</el-button>
      </div>
    </div>

    <van-swipe class="my-swipe" indicator-color="white" :autoplay="3000" :loop="true">
      <van-swipe-item v-for="item in announceRows">
        <div style="font-weight: bold;margin-top: -6px;margin-bottom: 6px">{{ item.title }}</div>
        <div class="an_content">{{ item.content }}</div>
      </van-swipe-item>
    </van-swipe>
    <el-divider content-position="right"><van-tag @click="$router.push('/PushArticle')" plain type="primary"  size="medium">发帖</van-tag></el-divider>
    <div class="item" v-for="item in rows" @click="forumInfo(item)">
      <div class="title">{{ item.title }}</div>
      <div class="content">{{ item.content }}</div>
      <div class="time">{{ $formatTime(item.datetime) }}</div>
    </div>
  </div>
</template>

<script>
import request from "@/req";
import {Toast} from "vant";

export default {
  name: "Forum",
  data() {
    return {
      rows: null,
      announceRows: null
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
    forumInfo(item) {
      localStorage.setItem('forum', JSON.stringify(item))
      this.$router.push('/ForumInfo')
    }
  },
  created() {
    request.get('/forum/getArticle').then(res => {
      console.log(res);
      this.rows = res.rows
    })
    request.get('/forum/getAnnounce').then(res => {
      console.log(res);
      this.announceRows = res.rows
    })

  }
}
</script>

<style scoped>
.top-navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.nav-left {
  display: flex;
  align-items: center;
  width: 40px;
}

.nav-title {
  flex: 1;
  text-align: center;
  color: white;
  font-size: 16px;
  font-weight: bold;
}

.nav-right {
  display: flex;
  align-items: center;
}

.my-swipe .van-swipe-item {
  color: #fff;
  padding: 12px;
  box-sizing: border-box;
  height: 150px;
  text-align: center;
  background-color: #39a9ed;
}

.an_content {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 5;
  -webkit-box-orient: vertical;
}

.title {
  border-left: 6px solid #409eff;
  padding: 0 6px;
  font-size: 1em;
  font-weight: bold;
  margin: 1em 0 1em 6px;
}

.item {
  margin: 0 12px 12px 12px;
}

.time {
  text-align: right;
  margin: 6px;
  color: #757575;
  font-size: 0.8em;
}

.content {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
</style>
