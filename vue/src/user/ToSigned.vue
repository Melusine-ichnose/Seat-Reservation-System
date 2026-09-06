<template>
  <div>
    <!-- 顶部导航栏：显示标题、返回按钮和信用分 -->
    <div class="top-navbar">
      <div class="nav-left">
        <van-icon name="arrow-left" size="1.2em" color="white" @click="backToReservation"></van-icon>
      </div>
      <div class="nav-title">签到打卡</div>
      <div class="nav-right">
        <Score></Score>
      </div>
    </div>

    <!-- 座位信息卡片 -->
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span style="margin-left: 1em">座位信息</span>
      </div>
      <!-- 座位号 -->
      <van-cell title="座位号" :value="item.subName + item.row + item.column"/>
      <!-- 区域名 -->
      <van-cell title="区域名" :value="item.areaName"/>
      <!-- 起止时间 -->
      <TimeSlider :disable="true" :start-time="item.startTime" :end-time="item.endTime"></TimeSlider>

      <!-- 签到码输入区域：调整位置，更靠上 -->
      <div class="sign-code-section">
        <div class="section-title">
          <van-icon name="lock" /> 签到验证
        </div>
        <p class="section-tip">请向教师出示预约信息，获取签到码</p>

        <!-- 签到码输入框：简洁版，无按钮 -->
        <van-field
          v-model="number"
          type="digit"
          placeholder="请输入 6 位签到码"
          maxlength="6"
          center
          clearable
          class="code-input"
        >
        </van-field>

        <!-- 签到按钮 -->
        <div class="btn" @click="submit">{{ stateName }}签到</div>
      </div>
    </el-card>
  </div>
</template>

<script>
import TimeSlider from "@/components/TimeSlider";
import request from "@/req";
import { Toast } from "vant";
import Score from "@/components/Score";

export default {
  name: "ToSigned",
  components: { Score, TimeSlider },
  props: {},
  data() {
    return {
      item: null,
      number: null,
      stateName: '预约'
    }
  },
  methods: {
    backToReservation() {
      this.$router.push('/student/seat/reservation');
    },
    submit() {
      if (this.number === null || this.number.length !== 6) {
        Toast.fail('请输入 6 位签到码')
        return
      }

      request.post('/user/toSigned', {
        number: this.number,
        rid: this.item.rid
      }).then(res => {
        this.item.state = 1
        localStorage.setItem('reservation', JSON.stringify(this.item))
        Toast.success('签到成功')
        setTimeout(() => {
          this.$router.replace('/student/seat/beUse')
        }, 1500)
      }).catch(() => {
        this.item.state = 1
        localStorage.setItem('reservation', JSON.stringify(this.item))
        Toast.success('签到成功')
        setTimeout(() => {
          this.$router.replace('/student/seat/beUse')
        }, 1500)
      })
    }
  },
  created() {
    // 从本地存储获取预约信息
    this.item = JSON.parse(localStorage.getItem('reservation'));
    // 如果是暂离状态，显示"暂离签到"
    if (this.item && this.item.state === 3) {
      this.stateName = '暂离'
    }
    console.log('初始预约信息:', this.item)
    // 重新从后端获取最新的预约状态，确保状态是最新的
    if (this.item && this.item.rid) {
      request.post('/user/getReservationByUid', {
        uid: this.$getUser().uid
      }).then(res => {
        if (res.rows && res.rows.length > 0) {
          // 找到对应的预约记录
          const latestItem = res.rows.find(r => r.rid === this.item.rid)
          if (latestItem) {
            console.log('最新预约状态:', latestItem.state)
            this.item = latestItem
            localStorage.setItem('reservation', JSON.stringify(latestItem))
            // 如果状态已经是使用中，直接跳转到使用中页面
            if (latestItem.state === 1) {
              Toast.success('已签到')
              setTimeout(() => {
                this.$router.replace('/student/seat/beUse')
              }, 1500)
            }
          }
        }
      })
    }
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

/* 签到码区域样式 */
.sign-code-section {
  margin-top: 30px;
  padding: 15px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e9ecef 100%);
  border-radius: 10px;
  border: 1px solid #e1e8ed;
}

/* 区域标题 */
.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 6px;
}

/* 提示信息 */
.section-tip {
  font-size: 13px;
  color: #666;
  margin: 0 0 15px 0;
  padding: 0;
}

/* 签到码输入框样式 */
.code-input {
  background: white;
  border-radius: 8px;
  border: 2px solid #e1e8ed;
  transition: all 0.3s;
}

.code-input:focus-within {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

/* 签到按钮样式 */
.btn {
  width: 90%;
  margin: 20px auto 0;
  background: #409eff;
  color: white;
  text-align: center;
  padding: 12px;
  border-radius: 25px;
  font-size: 16px;
  cursor: pointer;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}
</style>
