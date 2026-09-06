<template>
  <div class="area-selection-container">
    <!-- 顶部导航栏 -->
    <div class="top-navbar">
      <div class="nav-left">
        <div class="user-info">
          <el-avatar :size="36" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png"></el-avatar>
          <span class="username">{{ username }}</span>
        </div>
      </div>
      <div class="nav-right">
        <el-button type="danger" size="small" @click="logout" icon="el-icon-switch-button">退出登录</el-button>
      </div>
    </div>

    <div class="header">
      <h2>📚 选择阅览区域</h2>
      <p>Select Library Area</p>
      <div class="tips">
        <el-tag size="mini" type="success" effect="plain">💡 点击卡片选择区域</el-tag>
        <el-tag size="mini" type="primary" effect="plain">🪑 查看实时座位数</el-tag>
      </div>
    </div>
    
    <div class="area-grid">
      <div 
        v-for="area in areas" 
        :key="area.aid" 
        class="area-card"
        @click="selectArea(area)"
      >
        <div class="area-image">
          <img :src="getAreaImage(area.subName)" :alt="area.areaName" />
        </div>
        <div class="area-info">
          <h3>{{ area.areaName }}</h3>
          <p class="sub-name">{{ area.subName }}</p>
          <div class="seat-count">
            <span>🪑</span> {{ getSeatCount(area.aid) }} 个座位
          </div>
          <div class="area-tags">
            <el-tag size="mini" v-if="getSeatCount(area.aid) > 80" type="success">热门</el-tag>
            <el-tag size="mini" v-if="getSeatCount(area.aid) < 50" type="warning">人少</el-tag>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import request from "@/req";
import {Toast} from "vant";

export default {
  name: "AreaSelection",
  data() {
    return {
      areas: [],
      seatCounts: {},
      username: ''
    }
  },
  methods: {
    getAreaImage(subName) {
      const imageMap = {
        'AW': require('@/assets/area/AW.jpg'),
        'AE': require('@/assets/area/AE.jpg'),
        'BW': require('@/assets/area/BW.jpg'),
        'BE': require('@/assets/area/BE.jpg'),
        'CW': require('@/assets/area/CW.jpg'),
        'CE': require('@/assets/area/CE.jpg')
      };
      
      try {
        return imageMap[subName] || require('@/assets/area/default.jpg');
      } catch (e) {
        return 'https://via.placeholder.com/300x200?text=' + subName;
      }
    },
    
    async loadAreas() {
      try {
        const res = await request.get('/public/getArea');
        if (res.code === 200) {
          this.areas = res.rows;
          this.areas.forEach(area => {
            this.loadSeatCount(area);
          });
        }
      } catch (error) {
        Toast.fail('加载区域失败');
      }
    },
    
    async loadSeatCount(area) {
      try {
        const res = await request.post('/public/getAreaSeats', { area: area.aid });
        if (res.code === 200) {
          this.$set(this.seatCounts, area.aid, res.rows.length);
        }
      } catch (error) {
        console.error('加载座位数失败:', error);
      }
    },
    
    getSeatCount(aid) {
      return this.seatCounts[aid] || 0;
    },
    
    selectArea(area) {
      localStorage.setItem('selectedArea', JSON.stringify(area));
      this.$router.push({
        path: '/student/seat/reservation',
        query: { areaId: area.aid }
      });
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
    }
  },
  created() {
    const user = this.$getUser();
    if (user) {
      this.username = user.username || '用户';
    }
    this.loadAreas();
  }
}
</script>

<style scoped>
.area-selection-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 0;
  position: relative;
}

.top-navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.1);
}

.nav-left {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  color: white;
  gap: 10px;
}

.username {
  font-size: 16px;
  font-weight: 500;
}

.nav-right {
  display: flex;
  align-items: center;
}

.header {
  text-align: center;
  color: white;
  padding: 30px 20px 20px;
}

.header h2 {
  font-size: 32px;
  margin: 0 0 10px 0;
  font-weight: 600;
}

.header p {
  font-size: 14px;
  opacity: 0.9;
  margin: 0 0 20px 0;
}

.tips {
  display: flex;
  justify-content: center;
  gap: 10px;
  flex-wrap: wrap;
}

.area-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.area-card {
  background: white;
  border-radius: 15px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.area-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.3);
}

.area-card:active {
  transform: scale(0.98);
}

.area-image {
  width: 100%;
  height: 200px;
  overflow: hidden;
  background: #f0f0f0;
}

.area-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.area-card:hover .area-image img {
  transform: scale(1.05);
}

.area-info {
  padding: 20px;
}

.area-info h3 {
  margin: 0 0 8px 0;
  color: #333;
  font-size: 18px;
  font-weight: 600;
}

.area-info .sub-name {
  margin: 0 0 12px 0;
  color: #999;
  font-size: 14px;
  font-weight: 500;
}

.seat-count {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #667eea;
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 10px;
}

.area-tags {
  display: flex;
  gap: 5px;
  flex-wrap: wrap;
}

/* 移动端响应式 */
@media (max-width: 768px) {
  .area-grid {
    grid-template-columns: 1fr;
  }
  
  .header h2 {
    font-size: 24px;
  }

  .top-navbar {
    padding: 10px 15px;
  }

  .username {
    font-size: 14px;
  }
}
</style>
