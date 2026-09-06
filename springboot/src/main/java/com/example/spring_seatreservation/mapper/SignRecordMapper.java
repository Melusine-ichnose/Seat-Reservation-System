package com.example.spring_seatreservation.Mapper;

import com.example.spring_seatreservation.Bean.SignRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SignRecordMapper {
    void insert(SignRecord record);
}