package com.example.spring_seatreservation.Mapper;

import com.example.spring_seatreservation.Bean.CreditLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CreditLogMapper {
    void insert(CreditLog log);
}