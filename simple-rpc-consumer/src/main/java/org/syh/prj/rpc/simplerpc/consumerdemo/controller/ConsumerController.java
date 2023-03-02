package org.syh.prj.rpc.simplerpc.consumerdemo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.syh.prj.rpc.simplerpc.interfaces.OrderService;
import org.syh.prj.rpc.simplerpc.interfaces.UserService;
import org.syh.prj.rpc.simplerpc.springstarter.common.SimpleRpcReference;

import java.util.List;

@RestController
@RequestMapping("/rpc-consumer")
public class ConsumerController {
    @SimpleRpcReference
    private UserService userService;

    @SimpleRpcReference
    private OrderService orderService;

    @GetMapping("/users")
    public ResponseEntity<List<String>> getUsers() {
        List<String> users = userService.getUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping("/orders")
    public ResponseEntity<String> placeOrder(@RequestBody String order) {
        String orderId = orderService.placeOrder(order);
        return new ResponseEntity<>(orderId, HttpStatus.OK);
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<String> getOrder(@PathVariable String orderId) {
        String order = orderService.getOrder(orderId);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }
}
