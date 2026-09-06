package com.example.spring_seatreservation.controller;

import com.example.spring_seatreservation.Bean.R;
import com.example.spring_seatreservation.mapper.TeacherMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/teacher")
public class TeacherController {

    @Resource
    TeacherMapper teacherMapper;


    @GetMapping("/getReservation")
    public Map<String, Object> getReservation() {
        return new R().ok().add("rows", teacherMapper.getReservation()).builder();
    }

    @GetMapping("/getReservationNeedSub")
    public Map<String, Object> getReservationNeedSub() {
        return new R().ok().add("rows", teacherMapper.getReservationNeedSub()).builder();
    }

    @PostMapping("/subScore")
    public Map<String, Object> subScore(@RequestBody Map<String, Object> map) {
        teacherMapper.subScore(map);
        teacherMapper.subReservationScore(map);
        return new R().ok().builder();
    }

    @PostMapping("/addScore")
    public Map<String, Object> addScore(@RequestBody Map<String, Object> map) {
        teacherMapper.addScore(map.get("uid"));
        return new R().ok().builder();
    }

    @PostMapping("/deleteReservation")
    public Map<String, Object> deleteReservation(@RequestBody Map<String, Object> map) {
        try {
            // 先获取预约记录，得到 sid
            Map<String, Object> reservation = teacherMapper.getReservationByRid(map.get("rid"));
            if (reservation != null) {
                Object sid = reservation.get("sid");
                // 删除预约记录
                teacherMapper.deleteReservation(map.get("rid"));
                // 释放座位，将座位状态恢复为可用
                teacherMapper.releaseSeat(sid);
                return new R().ok().add("msg", "删除成功，座位已释放").builder();
            } else {
                return new R().bad().add("msg", "预约记录不存在").builder();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new R().bad().add("msg", "删除失败").builder();
        }
    }

}
