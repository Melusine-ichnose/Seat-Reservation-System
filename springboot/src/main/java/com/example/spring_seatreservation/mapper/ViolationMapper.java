package com.example.spring_seatreservation.Mapper;

import com.example.spring_seatreservation.Bean.Violation;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ViolationMapper {
    void insert(Violation violation);
}