package com.example.orderservice.controller;

import com.example.orderservice.client.CatalogServiceClient;
import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.entity.OrderEntity;
import com.example.orderservice.mq.KafkaProducer;
import com.example.orderservice.mq.OrderProducer;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.vo.RequestOrder;
import com.example.orderservice.vo.ResponseCatalog;
import com.example.orderservice.vo.ResponseOrder;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/")
public class OrderController {
    OrderService orderService;
    Environment env;
    KafkaProducer kafkaProducer;
    CatalogServiceClient catalogServiceClient;
    OrderProducer orderProducer;

    @Autowired
    public OrderController(OrderService orderService, Environment env, KafkaProducer kafkaProducer,
                           CatalogServiceClient catalogServiceClient, OrderProducer orderProducer) {
        this.orderService = orderService;
        this.env = env;
        this.kafkaProducer = kafkaProducer;
        this.catalogServiceClient = catalogServiceClient;
        this.orderProducer = orderProducer;
    }

    @GetMapping("/health_check")
    public String status(HttpServletRequest request){
        return String.format("It's Working in User Service," +
                        "port(local.server.port)=%s, port(server.port)=%s," +
                        "token_secret=%s, token_expiration_time=%s," +
                        "gateway_ip=%s",
                env.getProperty("local.server.port"), env.getProperty("server.port"),
                env.getProperty("token.secret"), env.getProperty("token.expiration_time"), env.getProperty("gateway.ip"));
    }

    @PostMapping(value = "/{userId}/orders")
    public ResponseEntity<ResponseOrder> createOrder(@PathVariable("userId") String userId, @RequestBody RequestOrder orderDetails){
        log.info("Before add orders data");

        // check how much stock is left
        // order-service -> catalog-service
        // resttemplate or openfegin
        ResponseCatalog responseCatalog = null;

//        try{
//            responseCatalog = catalogServiceClient.getCatalog(orderDetails.getProductId());
//        } catch (FeignException ex) {
//            log.error(ex.getMessage());
//        }

//        // 재고 및 주문수량
//        int stock = responseCatalog.getStock();
//        int orderQty = orderDetails.getQty();
//
//        boolean isAvailable = false;
//
//        log.info(String.format("재고 : %d / 주문수량 : %d", stock, orderQty));
//
//        // 재고가 0이 아니고, 재고가 주문수량보다 크거나 같으면 주문처리
//        if (stock != 0 && stock >= orderQty){
//            isAvailable = true;
//        } else {
//            log.info("잘못된 주문입니다.");
//        }

        boolean isAvailable = true;
        responseCatalog = catalogServiceClient.getCatalog(orderDetails.getProductId());
        if (responseCatalog != null &&
                responseCatalog.getStock() - orderDetails.getQty() < 0)
            isAvailable = false;

        if (isAvailable){
            ModelMapper modelMapper = new ModelMapper();
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

            OrderDto orderDto = modelMapper.map(orderDetails, OrderDto.class);
            orderDto.setUserId(userId);

            /* jpa */
            OrderDto createDto = orderService.createOrder(orderDto);
            ResponseOrder returnValue = modelMapper.map(createDto, ResponseOrder.class);

            /* kafka */
/*
            orderDto.setOrderId(UUID.randomUUID().toString());
            orderDto.setTotalPrice(orderDetails.getQty() * orderDetails.getUnitPrice());
            orderDto.setInstanceId(env.getProperty("eureka.instance.instance-id"));
            ResponseOrder returnValue = modelMapper.map(orderDto, ResponseOrder.class);
*/

            /* send message to kafka topic */
            kafkaProducer.send("example-catalog-topic", orderDto);
            //orderProducer.send("orders", orderDto);

            /* store a json file with orderDto */

            log.info("After added orders data");
            return ResponseEntity.status(HttpStatus.CREATED).body(returnValue);
        } else {
            log.info("After added orders data");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping(value = "/{userId}/orders")
    public ResponseEntity<List<ResponseOrder>> getOrder(@PathVariable("userId") String userId) throws Exception {
        log.info("Before retrieve orders data");
        Iterable<OrderEntity> orderList = orderService.getOrdersByUserId(userId);

        List<ResponseOrder> result = new ArrayList<>();
        orderList.forEach(v->result.add(new ModelMapper().map(v, ResponseOrder.class)));

//        throw new Exception("Server not working");

        Random rnd = new Random(System.currentTimeMillis());
        int time = rnd.nextInt(3);
        if(time % 2 == 0){
            try {
                Thread.sleep(10000);
                throw new Exception();
            } catch (Exception ex) {
                log.warn(ex.getMessage());
            }
        }

        log.info("After retrieve orders data");

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /*@PutMapping(value = "/orders/{order_id}")
    public ResponseEntity<ResponseOrder> updateOrder(@PathVariable String order_id){
        OrderDto updateDto = orderService.getOrderByOrderId(order_id);

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        orderProducer.send("orders", updateDto);

        ResponseOrder returnValue = modelMapper.map(updateDto, ResponseOrder.class);

        return ResponseEntity.status(HttpStatus.OK).body(returnValue);
    }*/
 }
