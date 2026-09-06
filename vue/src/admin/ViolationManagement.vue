<template>
  <div>
    <el-form style="padding: 1em" :inline="true" class="demo-form-inline">
      <el-form-item label="学工号">
        <el-input v-model="number" placeholder="学工号" clearable></el-input>
      </el-form-item>
      <el-form-item label="用户名">
        <el-input v-model="username" placeholder="用户名" clearable></el-input>
      </el-form-item>
      <el-form-item label="违规类型">
        <el-select v-model="type" placeholder="请选择" clearable>
          <el-option label="未签到" :value="1"></el-option>
          <el-option label="暂离超时" :value="2"></el-option>
        </el-select>
      </el-form-item>
    </el-form>
    <el-table :data="filteredRows" style="width: 100%" max-height="600">
      <el-table-column prop="vid" label="编号"></el-table-column>
      <el-table-column prop="number" label="学工号"></el-table-column>
      <el-table-column prop="username" label="用户名"></el-table-column>
      <el-table-column label="违规类型">
        <template slot-scope="scope">
          <el-tag :type="scope.row.type === 1 ? 'danger' : 'warning'">
            {{ scope.row.type === 1 ? '未签到' : '暂离超时' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="deductScore" label="扣除分数"></el-table-column>
      <el-table-column label="违规原因">
        <template slot-scope="scope">
          {{ scope.row.type === 1 ? '预约超时未签到' : '暂离超时未返回' }}
        </template>
      </el-table-column>
      <el-table-column label="违规时间">
        <template slot-scope="scope">
          {{ formatDate(scope.row.createTime) }}
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import request from "@/req";

export default {
  name: "ViolationManagement",
  data() {
    return {
      rows: [],
      number: '',
      username: '',
      type: null
    }
  },
  computed: {
    filteredRows() {
      return this.rows.filter(row => {
        if (this.number && String(row.number).indexOf(this.number) === -1) return false
        if (this.username && row.username.indexOf(this.username) === -1) return false
        if (this.type !== null && row.type !== this.type) return false
        return true
      })
    }
  },
  methods: {
    update() {
      request.get('/admin/getViolationList').then(res => {
        this.rows = res.rows || []
      })
    },
    formatDate(timestamp) {
      if (!timestamp) return '-'
      const date = new Date(timestamp)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      return `${year}-${month}-${day} ${hours}:${minutes}`
    }
  },
  created() {
    this.update()
  }
}
</script>

<style scoped>
.el-form {
  background: #fff;
  border-radius: 4px;
}
</style>