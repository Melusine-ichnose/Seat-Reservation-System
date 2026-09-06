package com.example.spring_seatreservation.controller;

import com.example.spring_seatreservation.Bean.CreditLog;
import com.example.spring_seatreservation.Bean.MyTask;
import com.example.spring_seatreservation.Bean.R;
import com.example.spring_seatreservation.Bean.ReservationCode;
import com.example.spring_seatreservation.Bean.SeatCode;
import com.example.spring_seatreservation.Bean.SignRecord;
import com.example.spring_seatreservation.Bean.Violation;
import com.example.spring_seatreservation.Mapper.CreditLogMapper;
import com.example.spring_seatreservation.Mapper.SignRecordMapper;
import com.example.spring_seatreservation.Mapper.ViolationMapper;
import com.example.spring_seatreservation.Other.DynamicTaskService;
import com.example.spring_seatreservation.Other.SignedNumber;
import com.example.spring_seatreservation.mapper.PublicMapper;
import com.example.spring_seatreservation.mapper.UserMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/user")
public class UserController {

    private final DynamicTaskService dynamicTask;


    public UserController(DynamicTaskService dynamicTask) {
        this.dynamicTask = dynamicTask;
    }

    @Resource
    UserMapper userMapper;
    @Resource
    PublicMapper publicMapper;
    @Resource
    ViolationMapper violationMapper;
    @Resource
    SignRecordMapper signRecordMapper;
    @Resource
    CreditLogMapper creditLogMapper;


    /**
     * startTime,endTime,sid,rid,uid
     *
     * @param map
     */
    @PostMapping("/addReservation")
    public Map<String, Object> addReservation(@RequestBody Map<String, Object> map) {

        // 检查当前用户是否已有活跃预约（待签到、使用中、暂离）
        List<Map<String, Object>> curReservation = userMapper.getActiveReservation(map.get("uid"));
        if (curReservation.size() > 0) {
            return new R().bad().add("msg", "您当前已有预约，无法重复预约").builder();
        }
        long startTime = ((long) map.get("startTime"));
        long endTime = ((long) map.get("endTime"));

        String finishTaskName = ReservationCode.FINISH
                + "-" + map.get("sid");
        String unSignedTaskName = ReservationCode.UNSIGNED
                + "-" + map.get("sid");
        //插入预约记录
        userMapper.addReservation(map);
        userMapper.updateSeat(SeatCode.BE_RESERVATION, map.get("sid"));
        int rid = Integer.parseInt(map.get("rid").toString());

        //预约开始超过 30 分钟将设置预约状态为违约未签到，
        //并释放座椅
        long finalUid = Long.parseLong(map.get("uid").toString());
        long finalSid = Long.parseLong(map.get("sid").toString());
        dynamicTask.add(new MyTask(unSignedTaskName,
                startTime + 30 * 60 * 1000L, () -> {
            dynamicTask.stop(finishTaskName);
            userMapper.updateReservation(ReservationCode.UNSIGNED, rid);
            userMapper.updateSeat(SeatCode.CAN_USE, finalSid);
            //写入违规记录
            Violation violation = new Violation((long) rid, finalUid, 1, 10, System.currentTimeMillis());
            violationMapper.insert(violation);
            //写入信用分日志
            CreditLog creditLog = new CreditLog(finalUid, 1, 10, "预约超时未签到", "系统", System.currentTimeMillis());
            creditLogMapper.insert(creditLog);
        }));

        //预约结束时自动恢复座位状态
        dynamicTask.add(new MyTask(finishTaskName, endTime, () -> {
            userMapper.updateReservation(ReservationCode.FINISH, rid);
            userMapper.updateSeat(SeatCode.CAN_USE, map.get("sid"));
        }));

        return new R().ok().builder();
    }


    /**
     * 通过rid签到
     * 预约开始的时间前后半小时内有效(暂离1小时内有效)
     *
     * @param map
     * @return
     */
    @PostMapping("/toSigned")
    public Map<String, Object> toSigned(@RequestBody Map<String, Object> map) {
        long number = new Long(map.get("number").toString());
        long currentTimeMillis = System.currentTimeMillis();
        
        synchronized (this) {
            //获取预约记录
            Map<String, Object> reservation = userMapper.getReservationByRid(map.get("rid"));

            Object stateObj = reservation.get("state");
            int state = stateObj != null ? ((Number) stateObj).intValue() : -999;
            long startTime = (long) reservation.get("startTime");
            
            // 打印调试信息
            System.out.println("=== 签到请求 ===");
            System.out.println("预约ID: " + map.get("rid"));
            System.out.println("签到码: " + number);
            System.out.println("当前时间戳: " + currentTimeMillis);
            System.out.println("当前时间: " + new java.util.Date(currentTimeMillis));
            System.out.println("预约状态: " + state + " (TIME_BEGAN=" + ReservationCode.TIME_BEGAN + ", SIGNED_BE_USE=" + ReservationCode.SIGNED_BE_USE + ")");
            System.out.println("预约开始时间戳: " + startTime);
            System.out.println("预约开始时间: " + new java.util.Date(startTime));
            System.out.println("允许签到范围: [" + new java.util.Date(startTime - 30*60*1000L) + "] ~ [" + new java.util.Date(startTime + 30*60*1000L) + "]");
            
            //预约签到
            if (state == ReservationCode.TIME_BEGAN) {
                // 在开始时间前后半小时内可以签到
                if (currentTimeMillis > startTime + 30 * 60 * 1000L
                        || currentTimeMillis < startTime - 30 * 60 * 1000L) {
                    //不在前后半小时内
                    System.out.println("签到时间检查失败: 当前时间=" + currentTimeMillis 
                        + ", 预约开始时间=" + startTime 
                        + ", 允许范围=" + (startTime - 30*60*1000L) + " ~ " + (startTime + 30*60*1000L));
                    return new R().bad().add("msg", "时间超时，签到失败").builder();
                }
                boolean flag = number == SignedNumber.getSignedNumber(reservation);
                if (flag) {
                    userMapper.updateReservation(ReservationCode.SIGNED_BE_USE, reservation.get("rid"));
                    userMapper.updateSeat(SeatCode.BE_USE, reservation.get("sid"));
                    //预约签到成功，关闭签到倒计时任务
                    dynamicTask.stop(ReservationCode.UNSIGNED + "-" + reservation.get("sid").toString());
                    //写入签到记录
                    SignRecord signRecord = new SignRecord(
                            (Long) reservation.get("rid"),
                            (Long) reservation.get("uid"),
                            1,
                            String.valueOf(number),
                            System.currentTimeMillis()
                    );
                    signRecordMapper.insert(signRecord);
                }
                return new R().ok().add("verify", flag).builder();

            }
            //暂离签到
            else if (state == ReservationCode.LEAVE) {
                if (currentTimeMillis > ((long) reservation.get("leaveTime")) + 60 * 60 * 1000L) {
                    //暂离超过1小时
                    return new R().bad().builder();
                }
                boolean flag = number == SignedNumber.getLeaveSignedNumber(reservation);
                if (flag) {
                    userMapper.updateReservation(ReservationCode.SIGNED_BE_USE, reservation.get("rid"));
                    userMapper.updateSeat(SeatCode.BE_USE, reservation.get("sid"));
                    //暂离回来签到成功，关闭签到倒计时任务
                    dynamicTask.stop(ReservationCode.LEAVE_UNSIGNED + "-" + reservation.get("sid").toString());
                    //写入签到记录
                    SignRecord signRecord = new SignRecord(
                            (Long) reservation.get("rid"),
                            (Long) reservation.get("uid"),
                            2,
                            String.valueOf(number),
                            System.currentTimeMillis()
                    );
                    signRecordMapper.insert(signRecord);
                }
                return new R().ok().add("verify", flag).builder();
            } else {
                // 状态不正确，可能已经签到过或状态已变更
                return new R().bad().add("msg", "状态已变更，请刷新页面").builder();
            }
        }
    }


    /**
     * 暂离，rid,sid
     *
     * @param map
     * @return
     */
    @PostMapping("/toLeave")
    public Map<String, Object> toLeave(@RequestBody Map<String, Object> map) {
        long leaveTime = System.currentTimeMillis();
        //更新数据库中预约状态和座位状态
        userMapper.leaveReservation(leaveTime, map.get("rid"));
        userMapper.leaveSeat(map.get("sid"));
        //超过 60 分钟未签到将设置状态为暂离超时未签到，同时释放座位
        long leaveRid = Long.parseLong(map.get("rid").toString());
        long leaveUid = Long.parseLong(map.get("uid").toString());
        long leaveSid = Long.parseLong(map.get("sid").toString());
        dynamicTask.add(new MyTask(ReservationCode.LEAVE_UNSIGNED + "-" + map.get("sid"),
                leaveTime + 60 * 60 * 1000L, () -> {
            dynamicTask.stop(ReservationCode.FINISH + "-" + leaveSid);
            userMapper.updateReservation(ReservationCode.LEAVE_UNSIGNED, leaveRid);
            userMapper.updateSeat(SeatCode.CAN_USE, leaveSid);
            //写入违规记录
            Violation violation = new Violation(leaveRid, leaveUid, 2, 10, System.currentTimeMillis());
            violationMapper.insert(violation);
            //写入信用分日志
            CreditLog creditLog = new CreditLog(leaveUid, 1, 10, "暂离超时未返回", "系统", System.currentTimeMillis());
            creditLogMapper.insert(creditLog);
        }));
        return new R().builder();
    }


//    /**
//     * 获取当前其他用户的预约，得到自己可以预约的时间范围
//     * curTime,sid,
//     * @param map
//     * @return
//     */
//    @PostMapping("/getTimeRange")
//    public Map<String, Object> getTimeRange(@RequestBody Map<String, Object> map) {
//        List<Map<String, Object>> list = userMapper.getTimeRange(map);
//        return new R().ok().add("rows", ).builder();
//    }


    @PostMapping("/getReservation")
    public Map<String, Object> getReservationNeedSub(@RequestBody Map<String, Object> map) {
        return new R().ok().add("rows", userMapper.getReservation(map)).builder();
    }

    @PostMapping("/getReservationByUid")
    public Map<String, Object> getReservationByUid(@RequestBody Map<String, Object> map) {
        List<Map<String, Object>> list = userMapper.getReservationByUid(map.get("uid"));
        return new R().add("rows", list).builder();
    }

    @PostMapping("/getScore")
    public Map<String, Object> getScore(@RequestBody Map<String, Object> map) {
        return new R().add("score", userMapper.getScore(map.get("uid"))).builder();
    }

    /**
     * 退座/取消预约，rid,sid
     * 取消正在使用或已预约的座位
     * @param map
     * @return
     */
    @PostMapping("/cancelReservation")
    public Map<String, Object> cancelReservation(@RequestBody Map<String, Object> map) {
        try {
            // 更新预约状态为取消
            userMapper.cancelReservation(map.get("rid"));
            // 恢复座位状态为可用
            userMapper.updateSeat(SeatCode.CAN_USE, map.get("sid"));
            
            // 取消相关的定时任务
            dynamicTask.stop(ReservationCode.FINISH + "-" + map.get("sid").toString());
            dynamicTask.stop(ReservationCode.UNSIGNED + "-" + map.get("sid").toString());
            dynamicTask.stop(ReservationCode.LEAVE_UNSIGNED + "-" + map.get("sid").toString());
            
            return new R().ok().builder();
        } catch (Exception e) {
            e.printStackTrace();
            return new R().bad().builder();
        }
    }

}
