<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-header">
        <div class="icon">📚</div>
        <h2>注册账号</h2>
        <p>Register Account</p>
      </div>

      <form>
        <div class="input-group">
          <div class="input-icon">🔖</div>
          <input
            type="text"
            v-model="number"
            @keyup.enter="$refs.nameInput.focus()"
            placeholder="请输入学工号"
            autocomplete="off"
          >
        </div>

        <div class="input-group">
          <div class="input-icon">👤</div>
          <input
            type="text"
            v-model="username"
            @keyup.enter="$refs.passwordInput.focus()"
            ref="nameInput"
            placeholder="请输入姓名"
            autocomplete="off"
          >
        </div>

        <div class="input-group">
          <div class="input-icon">🔒</div>
          <input
            type="password"
            v-model="password"
            ref="passwordInput"
            placeholder="请输入密码"
          >
        </div>

        <div class="role-group">
          <el-radio-group v-model="type">
            <el-radio-button label="学生"></el-radio-button>
            <el-radio-button label="老师"></el-radio-button>
          </el-radio-group>
        </div>

        <div class="btn-group">
          <button type="button" class="btn-register" @click="handleSubmit">
            <span class="btn-text">注 册</span>
          </button>
          <button type="button" class="btn-login" @click="$router.replace('/login')">
            返回登录
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
  name: "Register",
  data() {
    return {
      type: '学生',
      number: '',
      username: '',
      password: '',
    }
  },
  methods: {
    handleSubmit() {
      if (this.number === '' || this.username === '' || this.password === '') {
        Toast.fail('请完整输入')
        return
      }
      request.post('/public/register', {
        number: this.number,
        username: this.username,
        password: this.password,
        type: this.type === '学生' ? 0 : 1
      }).then(res => {
        Toast('注册成功')
        this.$router.replace('/login')
      })
    },
  },
}
;
</script>

<style scoped>
.register-container {
  width: 100vw;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
}

.register-box {
  width: 420px;
  padding: 50px 40px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(10px);
}

.register-header {
  text-align: center;
  margin-bottom: 40px;
}

.register-header .icon {
  font-size: 64px;
  margin-bottom: 10px;
}

.register-header h2 {
  margin: 0;
  padding: 0;
  color: #333;
  font-size: 28px;
  font-weight: 600;
}

.register-header p {
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

.role-group {
  display: flex;
  justify-content: center;
  margin-bottom: 30px;
}

.role-group .el-radio-group {
  background: #f5f7fa;
  border-radius: 10px;
  padding: 5px;
}

.role-group .el-radio-button {
  margin-right: 5px;
}

.role-group .el-radio-button:last-child {
  margin-right: 0;
}

.role-group .el-radio-button__inner {
  border: none !important;
  background: transparent !important;
  color: #666;
  padding: 10px 30px !important;
  border-radius: 8px !important;
}

.role-group .el-radio-button__inner:hover {
  color: #667eea !important;
}

.role-group .el-radio-button.is-active .el-radio-button__inner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  color: white !important;
}

.btn-group {
  margin-top: 10px;
}

.btn-register {
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

.btn-register:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
}

.btn-register:active {
  transform: translateY(0);
}

.btn-login {
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

.btn-login:hover {
  background: #667eea;
  color: white;
}
</style>