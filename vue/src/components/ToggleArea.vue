<template>
  <el-select v-if="areaRows" size="mini" v-model="selectedAreaId" @change="changeArea">
    <el-option
        v-for="item in areaRows"
        :key="item.aid"
        :label="item.areaName"
        :value="item.aid">
    </el-option>
  </el-select>
</template>

<script>
export default {
  name: "ToggleArea",
  props: {
    areaRows: {
      type: Array
    }
  },
  data() {
    return {
      selectedAreaId: null,
      currentArea: null
    }
  },
  methods: {
    getArea() {
      return this.currentArea
    },
    changeArea(areaId) {
      const area = this.areaRows.find(a => a.aid === areaId);
      if (area) {
        this.currentArea = area;
        this.$emit('changeArea', area);
      }
    },
    selectArea(area) {
      this.selectedAreaId = area.aid;
      this.currentArea = area;
    }
  },
  created() {
    if (this.areaRows && this.areaRows.length > 0) {
      this.selectArea(this.areaRows[0]);
    }
  }
}
</script>

<style scoped>

</style>
