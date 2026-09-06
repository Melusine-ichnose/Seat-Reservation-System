<template>
  <div class="score-management-page">
    <!-- 顶部导航栏：显示标题 -->
    <navbar title="信用分管理" :showLogout="false">
    </navbar>

    <el-divider></el-divider>

    <!-- 加载中 -->
    <div v-if="rows === null" style="text-align: center; padding: 50px; color: #909399">
      <van-loading color="#1989fa" vertical>
        <span style="font-size: 14px">加载中...</span>
      </van-loading>
    </div>

    <!-- 空数据 -->
    <div v-else-if="rows.length === 0" style="text-align: center; padding: 50px; color: #909399">
      <i class="el-icon-info" style="font-size: 48px; margin-bottom: 10px"></i>
      <p>暂无需要扣分的预约记录</p>
    </div>

    <!-- 数据列表 -->
    <div v-else>
      <div v-for="item in rows" :key="item.rid">
        <el-card class="box-card" style="margin: 10px">
          <div slot="header" class="clearfix">
            <span style="margin-left: 1em; font-weight: bold">{{ item.username }}</span>
            <el-button style="float: right; padding: 3px 0;color: #757575" type="text">{{ formatDateTime(item.startTime) }}
            </el-button>
          </div>
          <TimeSlider :disable="true" :start-time="item.startTime" :end-time="item.endTime"></TimeSlider>
          <van-cell title="座位号" :value="item.subName + item.row + item.column"/>
          <van-cell title="状态">
            <div style="color: #e6a23c">{{ getState(item.state) }}</div>
          </van-cell>
          <van-cell title="当前信用分">
            <div style="color: #67c23a; font-weight: bold">{{ item.uScore }}分</div>
          </van-cell>
          <div style="margin-top: 10px; text-align: right; padding: 0 10px">
            <el-button type="success" size="mini" @click="addScore(item)">加分</el-button>
            <el-button type="warning" size="mini" @click="subScore(item)" :disabled="item.score">扣分</el-button>
            <el-button type="danger" size="mini" @click="deleteReservation(item)">删除</el-button>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script>
import request from "@/req";
import TimeSlider from "@/components/TimeSlider";
import { MessageBox, Message } from 'element-ui';
import Navbar from "@/components/navbar";
import { Loading } from 'vant';

export default {
  name: "ScoreManagement",
  components: {TimeSlider, Navbar, Loading},
  props: {},
  data() {
    return {
      rows: null
    }
  },
  methods: {
    addScore(item) {
      MessageBox.confirm(`确定要给 ${item.username} 加 10 分吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'success'
      }).then(() => {
        request.post('/teacher/addScore', { uid: item.uid }).then(() => {
          Message.success('加分成功');
          this.update();
        });
      }).catch(() => {});
    },
    subScore(item) {
      if (item.score) {
        Message.warning('该记录已扣过分，不能重复扣分');
        return;
      }
      MessageBox.confirm(`确定要给 ${item.username} 扣 10 分吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request.post('/teacher/subScore', {
          uid: item.uid,
          rid: item.rid
        }).then(() => {
          Message.success('扣分成功');
          this.update();
        });
      }).catch(() => {});
    },
    deleteReservation(item) {
      MessageBox.confirm(`确定要删除 ${item.username} 的这条预约记录吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request.post('/teacher/deleteReservation', { rid: item.rid }).then(() => {
          Message.success('删除成功');
          this.update();
        });
      }).catch(() => {});
    },
    getState(state) {
      switch (state) {
        case 2:
          return '预约未签到'
        case 4:
          return '暂离超时未回'
        default:
          return '已完成'
      }
    },
    formatDateTime(timestamp) {
      let date;
      
      // 如果时间为空或无效，使用当前日期
      if (!timestamp || timestamp === null || timestamp === undefined || isNaN(timestamp) || timestamp <= 0) {
        date = new Date();
      } else {
        // 确保是数字类型
        let ms = typeof timestamp === 'number' ? timestamp : (typeof timestamp === 'string' && !isNaN(timestamp) ? parseInt(timestamp) : new Date(timestamp).getTime());

        // 如果是 10 位时间戳（秒级），转换为毫秒级
        if (ms < 1000000000000) {
          ms = ms * 1000;
        }

        date = new Date(ms);

        // 检查日期是否在合理范围内，否则使用当前日期
        if (date.getFullYear() < 2020 || date.getFullYear() > 2030) {
          date = new Date();
        }
      }

      // 格式化日期时间
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      const hours = String(date.getHours()).padStart(2, '0');
      const minutes = String(date.getMinutes()).padStart(2, '0');

      return `${year}-${month}-${day} ${hours}:${minutes}`;
    },
    update() {
      request.get('/teacher/getReservationNeedSub').then(res => {
        console.log('信用分管理数据:', res);
        console.log('rows:', res.rows);
        this.rows = res.rows || [];
        console.log('最终 rows:', this.rows);
      }).catch(err => {
        console.error('加载失败:', err);
        this.rows = [];
      })
    }
  },
  created() {
    this.update()
  }
}
</script>

<style scoped>
.score-management-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 60px;
}
</style>
