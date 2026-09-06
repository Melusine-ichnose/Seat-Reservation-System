package com.example.spring_seatreservation.mapper;


import com.example.spring_seatreservation.Bean.R;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

public interface TeacherMapper {

    @Select("SELECT a.rid, a.uid, a.startTime, a.endTime, a.sid, a.state, a.leaveTime, a.score, " +
            "b.row, b.column, " +
            "c.subName, c.areaName, " +
            "d.username, d.number, d.score as uScore " +
            "FROM reservation AS a " +
            "LEFT JOIN seat AS b ON b.sid=a.sid " +
            "LEFT JOIN area AS c ON c.aid=b.area " +
            "LEFT JOIN user AS d ON d.uid=a.uid " +
            "ORDER BY rid DESC")
    List<Map<String, Object>> getReservation();

    @Select("SELECT a.rid, a.uid, a.startTime, a.endTime, a.sid, a.state, a.leaveTime, a.score, " +
            "b.row, b.column, " +
            "c.subName, c.areaName, " +
            "d.username, d.number, d.score as uScore " +
            "FROM reservation AS a " +
            "LEFT JOIN seat AS b ON b.sid=a.sid " +
            "LEFT JOIN area AS c ON c.aid=b.area " +
            "LEFT JOIN user AS d ON d.uid=a.uid " +
            "WHERE (a.state=2 OR a.state=4 OR a.score IS NOT NULL) " +
            "ORDER BY a.rid DESC")
    List<Map<String, Object>> getReservationNeedSub();

    @Update("update user set `score`=score-10 where  uid=${uid}")
    void subScore(Object uid);

    @Update("update reservation set `score`=10 where rid=${rid}")
    void subReservationScore(Object rid);

    @Update("update user set `score`=LEAST(score+10, 100) where uid=${uid}")
    void addScore(Object uid);

    @Delete("delete from reservation where rid=${rid}")
    void deleteReservation(Object rid);

    @Select("select * from reservation where rid=${rid}")
    Map<String, Object> getReservationByRid(Object rid);

    @Update("update seat set state=0 where sid=${sid}")
    void releaseSeat(Object sid);

}
