package com.ssafy.petpal.control.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.petpal.control.dto.ApplianceContainer;
import com.ssafy.petpal.control.dto.ControlDto;
import com.ssafy.petpal.map.dto.MapDto;
import com.ssafy.petpal.map.service.MapService;
import com.ssafy.petpal.object.service.ApplianceService;
import com.ssafy.petpal.route.dto.RouteDto;
import com.ssafy.petpal.route.service.RouteService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
//@RequiredArgsConstructor
@AllArgsConstructor
public class ControlController {
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final StringRedisTemplate redisTemplate;
    private final ApplianceService applianceService;
    private final MapService mapService;
    private final RouteService routeService;
    private static final String CONTROL_QUEUE_NAME = "control.queue";
    private static final String CONTROL_EXCHANGE_NAME = "control.exchange";



    @MessageMapping("control.message.{homeId}")
    public void sendMessage(@Payload String rawMessage, @DestinationVariable String homeId) throws JsonProcessingException {
//        logger.info("Received message: {}", rawMessage);
        ControlDto controlDto = objectMapper.readValue(rawMessage, ControlDto.class);
        String type = controlDto.getType();
        switch (type){
            case "COMPLETE":
                // ROS에서 입증한 실제 가전상태 데이터를 redis에 올린다.
//                controlDto.getMessage() //parsing
                ApplianceContainer.Complete complete = objectMapper.readValue(controlDto.getMessage(),ApplianceContainer.Complete.class);
                if(complete.getIsSuccess()){
                    applianceService.updateApplianceStatus(homeId,complete.getApplianceId(),complete.getCurrentStatus());
                }else{
                    // 다시 발행
                }
//              fcm 호출.
                break;
            case "ON":
                break;
            case "OFF":
                break;
        }
        rabbitTemplate.convertAndSend(CONTROL_EXCHANGE_NAME, "home." + homeId, controlDto);
    }

    @MessageMapping("images.stream.{homeId}.images")
    public void sendImagesData(@Payload String rawMessage, @DestinationVariable String homeId) throws JsonProcessingException {
        ControlDto controlDto = objectMapper.readValue(rawMessage, ControlDto.class);
        //type : IMAGE
        rabbitTemplate.convertAndSend(CONTROL_EXCHANGE_NAME, "home." + homeId + ".images", controlDto);
    }

    @MessageMapping("scan.map.{homeId}")
    public void sendMapData(@Payload String rawMessage, @DestinationVariable String homeId) throws JsonProcessingException {
        ControlDto controlDto = objectMapper.readValue(rawMessage, ControlDto.class);
        String type = controlDto.getType();
        switch (type){
            case "SCAN":
                rabbitTemplate.convertAndSend(CONTROL_EXCHANGE_NAME, "home." + homeId, controlDto);
                break;
            case "COMPLETE":
                // 날것의 맵
                // dtoMapper로 만들어서
                // mapService.createMap(dto)
                // 메세지 발행(깎은 맵이 들어가있다)
                MapDto mapDto = mapService.createMap(homeId, controlDto.getMessage());
                rabbitTemplate.convertAndSend(CONTROL_EXCHANGE_NAME, "home." + homeId, mapDto);
                break;
            case "ROUTE":
                // 경로 저장 repository
                RouteDto routeDto = routeService.saveRoute(homeId, controlDto.getMessage());
                break;
        }
    }

    @RabbitListener(queues = CONTROL_QUEUE_NAME)
    public void receive(ControlDto controlDto) {
//        logger.info(" log : " + controlDto);
    }
}
