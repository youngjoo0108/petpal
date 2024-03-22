package com.ssafy.petpal.schedule.controller;

import com.ssafy.petpal.schedule.dto.ScheduleDto;
import com.ssafy.petpal.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/schedules")

public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<List<ScheduleDto>> getSchedule(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        List<ScheduleDto> response = new ArrayList<>();
        ScheduleDto sd = new ScheduleDto();
        sd.setRoom("RoomName?"); sd.setAppliance("Schedule Api Test DTO");
        response.add(sd);
        return ResponseEntity.ok(response);
    }
}
