<template>
  <div>
    <!-- 顶部导航栏：显示标题 -->
    <navbar title="预约信息管理" :left="false"></navbar>

    <div v-for="item in rows" @click="clickItem(item)">
      <el-card class="box-card" style="margin: 3px 0">
        <div slot="header" class="clearfix">
          <span style="margin-left: 1em">{{ item.username }}</span>
          <el-button style="float: right; padding: 3px 0;color: #757575" type="text">{{ getTime(item.startTime) }}
          </el-button>
        </div>
        <van-cell title="座位号" :value="item.subName + item.row + item.column"/>
        <van-cell title="状态">
          <div style="color: #e6a23c">{{ getState(item.state) }}</div>
        </van-cell>
        <TimeSlider :disable="true" :start-time="item.startTime" :end-time="item.endTime"></TimeSlider>
        <div style="margin-top: 10px; text-align: right; padding: 0 10px">
          <el-button type="danger" size="mini" @click.stop="deleteReservation(item.rid, item.username)">删除</el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
import request from "@/req";
import TimeSlider from "@/components/TimeSlider";
import { MessageBox, Message } from 'element-ui';
import Navbar from "@/components/navbar";

export default {
  name: "ReservationMess",
  components: {TimeSlider, Navbar},
  props: {},
  data() {
    return {
      rows: null
    }
  },
  methods: {
    clickItem(item) {
      localStorage.setItem("student", JSON.stringify(item))
      this.$router.push('/LookStudentMess')
    },
    deleteReservation(rid, username) {
      MessageBox.confirm(`确定要删除 ${username} 的预约记录吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request.post('/teacher/deleteReservation', { rid }).then(() => {
          Message.success('删除成功');
          this.loadData();
        });
      }).catch(() => {});
    },
    loadData() {
      request.get('/teacher/getReservation').then(res => {
        this.rows = res.rows;
      });
    },
    getState(state) {
      switch (state) {
        case 0:
          return '待签到'
        case 1:
          return '使用中'
        case 2:
          return '预约未签到'
        case 3:
          return '暂离'
        case 4:
          return '暂离超时未回'
        case 5:
          return '使用完成'
        default:
          return '未知状态'
      }
    },
    getTime(d) {
      // 如果时间为空，返回空字符串
      if (!d) return '';

      // 将秒级时间戳转换为毫秒
      let timestamp = typeof d === 'number' ? d * 1000 : d;
      let date = new Date(timestamp);

      // 检查日期是否有效（在合理范围内）
      if (date.getFullYear() < 2020 || date.getFullYear() > 2030) {
        // 时间戳异常，显示当前日期
        let now = new Date();
        let year = now.getFullYear();
        let month = String(now.getMonth() + 1).padStart(2, '0');
        let day = String(now.getDate()).padStart(2, '0');
        return `${year}-${month}-${day} `;
      }

      // 格式化日期时间
      let year = date.getFullYear();
      let month = String(date.getMonth() + 1).padStart(2, '0');
      let day = String(date.getDate()).padStart(2, '0');

      return `${year}-${month}-${day}`;
    }
  },
  created() {
    this.loadData();
  }
}
</script>
