<template>
  <div>
    <!-- 顶部导航栏：紫色背景，显示用户名和退出按钮 -->
    <div class="top-navbar">
      <div class="nav-left">
        <van-icon name="arrow-left" size="1.2em" color="white" @click="backToArea"></van-icon>
      </div>
      <div class="nav-title">预约座位</div>
      <div class="nav-right">
        <Score></Score>
        <el-button type="danger" size="small" @click="logout" icon="el-icon-switch-button" style="margin-left: 10px;">退出</el-button>
      </div>
    </div>

    <div class="page-container">
      <el-card style="margin: 10px;">
        <div slot="header" class="clearfix">
          <ToggleArea @changeArea="getSeatRows" ref="toggleArea" :area-rows="areaRows" v-if="areaRows"></ToggleArea>
          <HeadTip></HeadTip>
        </div>
        <Area ref="room" v-if="seatRows" :seat-rows="seatRows" @seatClick="seatClick">
          <div slot="seatMenu" class="blankMenu">
            <div @click="clickPop">
              <i class="el-icon-s-flag"></i>预约
            </div>
          </div>
        </Area>
        <div v-else style="text-align: center; padding: 40px; color: #999;">
          <i class="el-icon-loading" style="font-size: 24px;"></i>
          <p>正在加载座位...</p>
        </div>
      </el-card>
      <el-card style="margin: 10px; margin-bottom: 70px;">
        <van-cell title="座位" :value="seatName"/>
        <TimeSlider ref="timeSlider"></TimeSlider>
        <div class="btn" @click="submit">预约</div>
      </el-card>
    </div>
  </div>
</template>

<script>

import Area from "@/components/Area";
import request from "@/req";
import HeadTip from "@/components/HeadTip";
import ToggleArea from "@/components/ToggleArea";
import TimeSlider from "@/components/TimeSlider";
import {Toast} from "vant";
import Score from "@/components/Score";

export default {
  name: "Reservation",
  components: {Score, TimeSlider, ToggleArea, HeadTip, Area},
  data() {
    return {
      areaRows: null,
      seatRows: null,
      seatCurIndex: 0,
      seatName: '请选择座位',
      username: ''
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
        localStorage.removeItem('student_user');
        localStorage.removeItem('reservation');
        localStorage.removeItem('selectedArea');
        this.$router.replace('/login');
        Toast.success('已退出登录');
      }).catch(() => {});
    },
    backToArea() {
      this.$router.push('/student/seat/area-selection');
    },
    clickPop() {
      this.$nextTick(() => {
        let area = this.$refs.toggleArea.getArea();
        this.seatName = area.subName +
            this.seatRows[this.seatCurIndex].row + this.seatRows[this.seatCurIndex].column
      })
    },
    getSeatRows() {
      if (this.$refs.toggleArea) {
        const selectedArea = this.$refs.toggleArea.getArea();
        console.log('当前选择的区域:', selectedArea);
        if (selectedArea) {
          request.post('/public/getAreaSeats', {
            area: selectedArea.aid
          }).then(res => {
            console.log('座位数据:', res);
            if (res.code === 200 && res.rows) {
              this.seatRows = res.rows;
            } else {
              this.seatRows = [];
            }
          }).catch(err => {
            console.error('获取座位失败:', err);
            this.seatRows = [];
          });
        }
      }
    },

    seatClick(index) {
      if (this.seatRows[index].type === 1 || this.seatRows[index].state === 2 || this.seatRows[index].state === 1) {
        this.seatRows[index].show = false;
        return
      }
      this.seatCurIndex = index
    },
    changeArea(index) {
      this.getSeatRows()
    },
    submit() {
      if (this.seatName === '请选择座位') {
        Toast.fail('请选择座位')
        return
      }
      if (new Date().getHours()>=22) {
        Toast.fail('22 点之后无法预约')
        return
      }
      this.$nextTick(() => {
        let body={
          startTime: this.$refs.timeSlider.getStartTime(),
          endTime: this.$refs.timeSlider.getEndTime(),
          uid: this.$getUser().uid,
          sid: this.seatRows[this.seatCurIndex].sid
        }
        if (body.startTime===body.endTime){
          Toast.fail('时间非法！')
          return
        }
        request.post('/user/addReservation',body ).then(res => {
          if (res.code === 200) {
            Toast.success('预约成功')
            this.togglePage()
          } else {
            Toast.fail('预约失败，当前已有预约')
          }
        })
      })
    },
    togglePage() {
      //当前有待操作的预约则跳转到操作页
      request.post('/user/getReservationByUid', {
        uid: this.$getUser().uid
      }).then(res => {
        console.log(res)
        // 过滤出活跃状态的预约（待签到、使用中、暂离）
        const activeReservations = res.rows.filter(item => 
          item.state === 0 || item.state === 1 || item.state === 3
        )
        // 如果没有活跃预约，直接返回
        if (activeReservations.length === 0) {
          return
        }
        // 按预约ID降序排序，取最新的预约
        activeReservations.sort((a, b) => b.rid - a.rid)
        let item = activeReservations[0]
        localStorage.setItem('reservation', JSON.stringify(item))
        switch (item.state) {
          case 0:
          case 3:
            if (this.$route.path !== '/student/seat/toSigned') {
              this.$router.replace('/student/seat/toSigned')
            }
            break
          case 1:
            if (this.$route.path !== '/student/seat/beUse') {
              this.$router.replace('/student/seat/beUse')
            }
            break
        }
      })
    },
  },
  created() {
    // 获取用户名
    const user = this.$getUser();
    if (user) {
      this.username = user.username || '用户';
    }

    // 检查是否从区域选择页面进入
    const areaId = this.$route.query.areaId;
    console.log('URL 中的 areaId:', areaId);

    request.get('/public/getArea').then(res => {
      console.log('区域数据:', res);
      this.areaRows = res.rows;

      // 如果指定了区域 ID，则默认选择该区域
      if (areaId && this.areaRows) {
        this.$nextTick(() => {
          const targetArea = this.areaRows.find(area => area.aid === parseInt(areaId));
          console.log('目标区域:', targetArea);
          if (targetArea && this.$refs.toggleArea) {
            this.$refs.toggleArea.selectArea(targetArea);
          }
          this.getSeatRows();
        });
      } else {
        this.$nextTick(() => {
          this.getSeatRows();
        });
      }
    }).catch(err => {
      console.error('加载区域失败:', err);
    });
    this.togglePage()
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

.page-container {
  background: #f5f7fa;
  min-height: calc(100vh - 66px);
}

.btn {
  width: 90%;
  background: white;
  color: #409eff;
  text-align: center;
  padding: 10px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: bold;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
  margin: 15px auto;
  display: block;
  border: 2px solid #409eff;
}

.btn:hover {
  background: #409eff;
  color: white;
}
</style>
