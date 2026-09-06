<template>
  <div>
    <!-- 顶部导航栏：显示标题、返回按钮和信用分 -->
    <div class="top-navbar">
      <div class="nav-left">
        <van-icon name="arrow-left" size="1.2em" color="white" @click="backToReservation"></van-icon>
      </div>
      <div class="nav-title">使用中</div>
      <div class="nav-right">
        <Score></Score>
      </div>
    </div>

    <!-- 分隔线 -->
    <el-divider></el-divider>
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span style="margin-left: 1em">座位信息</span>
      </div>
      <van-cell title="座位号" :value="item.subName + item.row + item.column"/>
      <van-cell title="区域名" :value="item.areaName"/>
      <TimeSlider :disable="true" :start-time="item.startTime" :end-time="item.endTime"></TimeSlider>

    </el-card>
    <div class="button-container">
      <div class="action-btn cancel-btn" @click="cancelReservation">退座</div>
      <div class="action-btn leave-btn" @click="submit">暂离</div>
    </div>

  </div>
</template>

<script>
import TimeSlider from "@/components/TimeSlider";
import request from "@/req";
import {Toast} from "vant";
import Score from "@/components/Score";

export default {
  name: "BeUse",
  components: {Score, TimeSlider},
  props: {},
  data() {
    return {
      item: null,
      number: null
    }
  },
  methods: {
    backToReservation() {
      this.$router.push('/student/seat/reservation');
    },
    cancelReservation() {
      if (this.item === null) {
        Toast.fail('请完整输入')
        return
      }
      request.post('/user/cancelReservation', {
        rid: this.item.rid,
        sid: this.item.sid
      }).then(res => {
        if (res.code === 200) {
          Toast.success('退座成功')
          localStorage.removeItem('reservation')
          // 跳转到座位选择页面
          this.$router.replace('/student/seat/reservation')
        } else {
          Toast.fail('退座失败')
        }
      })
    },
    submit() {
      if (this.item===null){
        Toast.fail('请完整输入')
        return
      }
      request.post('/user/toLeave', {
        sid: this.item.sid,
        rid: this.item.rid
      }).then(res => {
        this.item.state = 3
        localStorage.setItem('reservation', JSON.stringify(this.item))
        Toast.success('操作成功，请回馆后及时签到')
        this.$router.replace('/student/seat/toSigned')
      })
    }
  },
  created() {
    this.item = JSON.parse(localStorage.getItem('reservation'));
    console.log(this.item)
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

.button-container {
  display: flex;
  justify-content: space-between;
  gap: 10px;
  padding: 15px 30px;
  position: fixed;
  bottom: 54px;
  left: 0;
  right: 0;
  background: transparent;
}

.action-btn {
  flex: 1;
  height: 40px;
  line-height: 40px;
  border-radius: 20px;
  text-align: center;
  font-size: 14px;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  color: white;
}

.cancel-btn {
  background: #f56c6c;
}

.leave-btn {
  background: #67c23a;
}
</style>
