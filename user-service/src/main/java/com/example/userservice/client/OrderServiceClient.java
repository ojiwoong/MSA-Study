package com.example.userservice.client;

import com.example.userservice.error.FeignErrorDecoder;
import com.example.userservice.vo.ResponseOrder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

// order-service -> http://127.0.0.1:50002/{userId}/orders
// @FeignClient 어노테이션에 configuration를 명시적으로 사용하여 ErrorDecoder class 셋팅 가능
// configuration 속성 사용이유 : 여러클라이언트 사용 시 중복 메서드명이 있을 경우 methodKey로 구분이 되지 않기 때문에 FeignErrorDecoder를 분리
@FeignClient(name = "order-service", configuration = FeignErrorDecoder.class)
public interface OrderServiceClient {

    @GetMapping(value = "/{userId}/orders")
    public List<ResponseOrder> getOrders(@PathVariable String userId);

    @GetMapping(value = "/{userId}/orders_wrong")
    public List<ResponseOrder> getOrdersWrong(@PathVariable String userId);
}
