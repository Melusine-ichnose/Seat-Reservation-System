<template>
  <div>
    <!-- 顶部导航栏：显示标题 -->
    <navbar title="座位信息管理" :showLogout="false">
    </navbar>

    <!-- 座位信息卡片：距离顶部适当距离 -->
    <el-card style="margin-top: 10px; margin-left: 10px; margin-right: 10px; overflow: scroll">

      <div slot="header" class="clearfix">
        <ToggleArea @changeArea="getSeatRows" ref="toggleArea" :area-rows="areaRows" v-if="areaRows"></ToggleArea>
        <HeadTip></HeadTip>
      </div>
      <Area ref="room" v-if="seatRows" :seat-rows="seatRows" @seatClick="seatClick">
        <div slot="seatMenu" class="blankMenu">
          <div>签到码：{{formatNumber(number)}} </div>
        </div>
      </Area>
    </el-card>
  </div>
</template>

<script>
import Area from "@/components/Area";
import request from "@/req";
import HeadTip from "@/components/HeadTip";
import ToggleArea from "@/components/ToggleArea";
import Navbar from "@/components/navbar";
import {Toast} from "vant";

export default {
  name: "SeatMess",
  // 注册 navbar 组件
  components: {ToggleArea, HeadTip, Area, Navbar},
  data() {
    return {
      areaRows: null,
      seatRows: null,
      number:'000000'
    }
  },
  methods: {
    formatNumber(num) {
      return String(num).padStart(6, '0')
    },
    seatClick(index) {
      //正在使用的座位无法操作
      if (this.seatRows[index].state !== 1) {
        this.seatRows[index].show = false
        return
      }
      request.post('/public/getSignedNumber',{
        sid:this.seatRows[index].sid
      }).then(res=>{
        this.number=res.number
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
    }
  },
  created() {
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
          if (this.$refs.toggleArea) {
            this.getSeatRows();
          }
        });
      }
    })
  }
}
</script>

<style scoped>


</style>
