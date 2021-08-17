package com.example.userservice.client;

import com.example.userservice.vo.ResponseOrder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

// order-service -> http://127.0.0.1:50002/{userId}/orders
@FeignClient(name = "order-service")
public interface OrderServiceClient {

    @GetMapping(value = "/{userId}/orders")
    public List<ResponseOrder> getOrders(@PathVariable String userId);

    @GetMapping(value = "/{userId}/orders_wrong")
    public List<ResponseOrder> getOrdersWrong(@PathVariable String userId);
}
