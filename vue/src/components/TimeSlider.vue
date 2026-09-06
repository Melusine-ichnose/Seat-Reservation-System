<template>
  <div>
    <van-cell title="起止时间">
      <div style="display: flex;justify-content: end">
        <van-button size="mini" plain type="primary">{{ startText }}</van-button>
        <div>--</div>
        <van-button size="mini" plain type="info">{{ endText }}</van-button>
      </div>
    </van-cell>
    <div class="block" style="margin: 3px 0 1em 0;">
      <el-slider
        v-model="value"
        @change="updateTime"
        :disabled="disable"
        :marks="marks"
        range
        :min="min"
        :max="max"
        :format-tooltip="formatToolTip"
        show-stops
      />
    </div>
  </div>
</template>

<script>
export default {
  name: "timeSlider",
  props: {
    disable: false,
    startTime: null,
    endTime: null
  },
  data() {
    return {
      value: [0, 1],
      showLabelSize: 5,
      min: null,
      max: 22 * 2 + 1,
      marks: {},
      startText: '',
      endText: ''
    }
  },
  methods: {
    normalizeTimestamp(value) {
      if (typeof value !== 'number') {
        return new Date(value).getTime()
      }
      // 项目里接口返回的是 13 位毫秒时间戳，只有 10 位秒级时间戳才需要乘 1000
      return value < 100000000000 ? value * 1000 : value
    },
    updateTime() {
      this.$nextTick(() => {
        let date = new Date(this.getStartTime())
        this.startText = date.getHours() + ':' + (date.getMinutes() === 0 ? date.getMinutes() + '0' : date.getMinutes())
        let date2 = new Date(this.getEndTime())
        this.endText = date2.getHours() + ':' + (date2.getMinutes() === 0 ? date2.getMinutes() + '0' : date2.getMinutes())
      })
    },
    getStartTime() {
      let time = new Date(new Date().setHours(0, 0, 0, 0)).getTime()
      return time + this.value[0] * 30 * 60 * 1000
    },
    getEndTime() {
      let time = new Date(new Date().setHours(0, 0, 0, 0)).getTime()
      return time + this.value[1] * 30 * 60 * 1000
    },
    formatToolTip(v) {
      let pre = Math.trunc(v / 2)
      let suf = v % 2 === 0 ? ':00' : ':30'
      return pre + suf
    },
    getMarks() {
      let labels = []
      let values = []
      for (let i = this.min; i <= this.max; i++) {
        values.push(i)
        labels.push((Math.trunc(i / 2)) + (i % 2 === 0 ? ':00' : ':30'))
      }

      let div = Math.ceil(labels.length / this.showLabelSize)
      for (let i = 0; i < this.showLabelSize; i++) {
        if (i * div < labels.length) {
          let value = values[i * div] + ''
          this.marks[value] = labels[i * div]
        }
      }
    }
  },
  created() {
    if (this.disable) {
      this.min = 8 * 2
      this.max = 22 * 2 + 1

      let startTime = this.normalizeTimestamp(this.startTime)
      let endTime = this.normalizeTimestamp(this.endTime)

      let date1 = new Date(startTime)
      let hours1 = date1.getHours()
      let date2 = new Date(endTime)
      let hours2 = date2.getHours()

      this.value[0] = date1.getMinutes() > 0 ? hours1 * 2 + 1 : hours1 * 2
      this.value[1] = date2.getMinutes() > 0 ? hours2 * 2 + 1 : hours2 * 2
    } else {
      let date = new Date()
      let hour = date.getHours()

      if (hour < 8) {
        this.min = 8 * 2
      } else {
        this.min = date.getMinutes() > 30 ? hour * 2 + 2 : hour * 2 + 1
      }

      this.value[0] = this.min
      this.value[1] = this.max
    }
    this.getMarks()
    this.updateTime()
  }
}
</script>

<style scoped>

</style>
