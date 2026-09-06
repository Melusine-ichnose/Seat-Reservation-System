<template>
  <div>
    <!-- 顶部导航栏：紫色背景，显示标题、返回按钮和退出按钮 -->
    <div class="top-navbar">
      <div class="nav-left">
        <van-icon name="arrow-left" size="1.2em" color="white" @click="backToReservation"></van-icon>
      </div>
      <div class="nav-title">信用分管理</div>
      <div class="nav-right">
        <el-button type="danger" size="small" @click="logout" icon="el-icon-switch-button">退出</el-button>
      </div>
    </div>

    <div v-for="item in rows">
      <el-card class="box-card" style="margin: 3px 0">
        <div slot="header" class="clearfix">
          <span style="margin-left: 1em">{{ item.username }}</span>
          <el-button style="float: right; padding: 3px 0;color: #757575" type="text">{{ getTime(item.startTime) }}
          </el-button>
        </div>
        <TimeSlider :disable="true" :start-time="item.startTime" :end-time="item.endTime"></TimeSlider>
        <van-cell title="状态">
          <div style="color: #e6a23c">{{ getState(item.state) }}</div>
        </van-cell>
        <van-cell title="详情">
          <van-tag type="danger" size="large" color="gray">已扣{{ item.score }}分</van-tag>
        </van-cell>
      </el-card>
    </div>
  </div>
</template>

<script>
import request from "@/req";
import TimeSlider from "@/components/TimeSlider";
import {Toast} from "vant";

export default {
  name: "ScoreMess",
  components: {TimeSlider},
  props: {},
  data() {
    return {
      rows: null
    }
  },
  methods: {
    backToReservation() {
      this.$router.push('/student/seat/reservation');
    },
    logout() {
      this.$confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        localStorage.removeItem('user');
        localStorage.removeItem('student_user');
        localStorage.removeItem('reservation');
        localStorage.removeItem('selectedArea');
        this.$router.replace('/login');
        Toast.success('已退出登录');
      }).catch(() => {});
    },
    getState(state) {
      switch (state) {
        case 2:
          return '预约未签到'
        case 4:
          return '暂离超时未回'
      }
    },
    getTime(d) {
      let date = new Date(d);
      return date.getFullYear() + '/' + (date.getMonth() + 1) + '/' + date.getDate() + ' ' + date.getHours() + ':' + (date.getMinutes() === 0 ? "00" : "30")
    },
    update() {
      request.post('/user/getReservation',{
        uid:this.$getUser().uid
      }).then(res => {
        this.rows = res.rows
        console.log(this.rows);
      })
    }
  },
  created() {
    this.update()
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
  cursor: pointer;
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
</style>
