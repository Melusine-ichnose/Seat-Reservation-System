<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <div class="icon">📚</div><!--emoji-->
        <h2>图书馆座位预约</h2>
        <p>Library Seat Reservation</p>
      </div>

      <form>
        <div class="input-group">
          <div class="input-icon">👤</div>
          <input
            type="text"
            v-model="number"
            @keyup.enter="$refs.passwordInput.focus()"
            placeholder="请输入学工号"
            autocomplete="off"
          >
        </div>

        <div class="input-group">
          <div class="input-icon">🔒</div>
          <input
            type="password"
            v-model="password"
            @keyup.enter="handleSubmit()"
            ref="passwordInput"
            placeholder="请输入密码"
          >
        </div>

        <div class="btn-group">
          <button type="button" class="btn-login" @click="handleSubmit">
            <span class="btn-text">登 录</span>
          </button>
          <button type="button" class="btn-register" @click="signUp_asd">
            注册账号
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import request from "@/req";
import {Toast} from "vant";

export default {
  name: "Login",
  data() {
    return {
      number: '',
      password: '',
    }
  },
  methods: {
    signUp_asd() {
      this.$router.replace({path: '/register'});
    },
    handleSubmit() {
      if(this.password===''||this.number===''){
        Toast.fail('请完整输入')
        return
      }
      request.post('/public/login', {
        number: this.number,
        password: this.password
      }).then(res => {
        if (res.code === 200) {
          console.log(res);
          Toast.success('登录成功')
          // 清除之前用户的预约数据，防止数据混淆
          localStorage.removeItem('reservation');
          localStorage.removeItem('selectedArea');
          // 根据用户类型存储到不同的 key
          const storageKey = this.$getUserStorageKey ? this.$getUserStorageKey(res.user.type) : 'user';
          localStorage.setItem(storageKey, JSON.stringify(res.user));
          localStorage.setItem('user', JSON.stringify(res.user)); // 保持兼容

          // 学生用户和教师用户登录后跳转到区域选择页面
          if (res.user.type === 0) {
            this.$router.replace('/student/area-selection')
          } else if (res.user.type === 1) {
            this.$router.replace('/teacher/area-selection')
          } else if (res.user.type === 2) {
            this.$router.replace('/admin/statistics')
          }
        } else {
          Toast.fail('账户或密码错误')
        }
      })
    },

  },
}
</script>

<style scoped>
.login-container {
  width: 100vw;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
}

.login-box {
  width: 420px;
  padding: 50px 40px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(10px);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-header .icon {
  font-size: 64px;
  margin-bottom: 10px;
}

.login-header h2 {
  margin: 0;
  padding: 0;
  color: #333;
  font-size: 28px;
  font-weight: 600;
}

.login-header p {
  margin: 8px 0 0;
  color: #999;
  font-size: 14px;
}

.input-group {
  position: relative;
  margin-bottom: 25px;
  background: #f5f7fa;
  border-radius: 10px;
  transition: all 0.3s;
}

.input-group:hover {
  background: #ecf5ff;
}

.input-group:focus-within {
  background: #e6f1ff;
  box-shadow: 0 0 0 2px #667eea;
}

.input-icon {
  position: absolute;
  left: 15px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 20px;
  color: #999;
  transition: all 0.3s;
}

.input-group:focus-within .input-icon {
  color: #667eea;
}

.input-group input {
  width: 100%;
  padding: 16px 15px 16px 50px;
  font-size: 15px;
  color: #333;
  border: none;
  background: transparent;
  outline: none;
  box-sizing: border-box;
}

.input-group input::placeholder {
  color: #bbb;
}

.btn-group {
  margin-top: 35px;
}

.btn-login {
  width: 100%;
  padding: 15px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.btn-login:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
}

.btn-login:active {
  transform: translateY(0);
}

.btn-register {
  width: 100%;
  padding: 15px;
  margin-top: 12px;
  background: white;
  color: #667eea;
  border: 2px solid #667eea;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-register:hover {
  background: #667eea;
  color: white;
}
</style>
