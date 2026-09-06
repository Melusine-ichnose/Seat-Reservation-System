<template>
  <div>
    <el-form style="padding: 1em" :inline="true" class="demo-form-inline">
      <el-form-item label="学工号">
        <el-input v-model="number" placeholder="学工号" clearable></el-input>
      </el-form-item>
      <el-form-item label="用户名">
        <el-input v-model="username" placeholder="用户名" clearable></el-input>
      </el-form-item>
      <el-form-item label="签到类型">
        <el-select v-model="type" placeholder="请选择" clearable>
          <el-option label="预约签到" :value="1"></el-option>
          <el-option label="暂离返回" :value="2"></el-option>
        </el-select>
      </el-form-item>
    </el-form>
    <el-table :data="filteredRows" style="width: 100%" max-height="600">
      <el-table-column prop="sid" label="编号"></el-table-column>
      <el-table-column prop="number" label="学工号"></el-table-column>
      <el-table-column prop="username" label="用户名"></el-table-column>
      <el-table-column label="签到类型">
        <template slot-scope="scope">
          <el-tag :type="scope.row.type === 1 ? 'success' : 'primary'">
            {{ scope.row.type === 1 ? '预约签到' : '暂离返回' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="signCode" label="签到码"></el-table-column>
      <el-table-column label="签到状态">
        <template slot-scope="scope">
          <el-tag type="success">签到成功</el-tag>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import request from "@/req";

export default {
  name: "SignRecordManagement",
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
      request.get('/admin/getSignRecordList').then(res => {
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