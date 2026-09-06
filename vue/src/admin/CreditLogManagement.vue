<template>
  <div>
    <el-form style="padding: 1em" :inline="true" class="demo-form-inline">
      <el-form-item label="学工号">
        <el-input v-model="number" placeholder="学工号" clearable></el-input>
      </el-form-item>
      <el-form-item label="用户名">
        <el-input v-model="username" placeholder="用户名" clearable></el-input>
      </el-form-item>
      <el-form-item label="变动类型">
        <el-select v-model="changeType" placeholder="请选择" clearable>
          <el-option label="扣分" :value="1"></el-option>
          <el-option label="加分" :value="2"></el-option>
        </el-select>
      </el-form-item>
    </el-form>
    <el-table :data="filteredRows" style="width: 100%" max-height="600">
      <el-table-column prop="clid" label="编号"></el-table-column>
      <el-table-column prop="number" label="学工号"></el-table-column>
      <el-table-column prop="username" label="用户名"></el-table-column>
      <el-table-column label="变动类型">
        <template slot-scope="scope">
          <el-tag :type="scope.row.changeType === 1 ? 'danger' : 'success'">
            {{ scope.row.changeType === 1 ? '扣分' : '加分' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="变动分数">
        <template slot-scope="scope">
          <span :style="{color: scope.row.changeType === 1 ? '#F56C6C' : '#67C23A'}">
            {{ scope.row.changeType === 1 ? '-' : '+' }}{{ scope.row.changeScore }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="reason" label="变动原因"></el-table-column>
    </el-table>
  </div>
</template>

<script>
import request from "@/req";

export default {
  name: "CreditLogManagement",
  data() {
    return {
      rows: [],
      number: '',
      username: '',
      changeType: null
    }
  },
  computed: {
    filteredRows() {
      return this.rows.filter(row => {
        if (this.number && String(row.number).indexOf(this.number) === -1) return false
        if (this.username && row.username.indexOf(this.username) === -1) return false
        if (this.changeType !== null && row.changeType !== this.changeType) return false
        return true
      })
    }
  },
  methods: {
    update() {
      request.get('/admin/getCreditLogList').then(res => {
        this.rows = res.rows || []
      })
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