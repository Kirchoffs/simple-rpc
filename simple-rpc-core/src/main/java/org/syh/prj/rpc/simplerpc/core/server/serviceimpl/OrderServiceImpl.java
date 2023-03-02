package org.syh.prj.rpc.simplerpc.core.server.serviceimpl;

import org.syh.prj.rpc.simplerpc.interfaces.OrderService;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class OrderServiceImpl implements OrderService {
    private static final String UPPERCASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int UPPERCASE_LENGTH = 5;
    private static final String LOWERCASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final int LOWERCASE_LENGTH = 5;
    private static final String DIGITS = "0123456789";
    private static final int DIGITS_LENGTH = 5;

    private Map<String, String> orderMap = new HashMap<>();

    private String generateRandomId() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < UPPERCASE_LENGTH; i++) {
            int index = random.nextInt(UPPERCASE_LETTERS.length());
            char randomChar = UPPERCASE_LETTERS.charAt(index);
            sb.append(randomChar);
        }

        for (int i = 0; i < LOWERCASE_LENGTH; i++) {
            int index = random.nextInt(LOWERCASE_LETTERS.length());
            char randomChar = LOWERCASE_LETTERS.charAt(index);
            sb.append(randomChar);
        }

        for (int i = 0; i < DIGITS_LENGTH; i++) {
            int index = random.nextInt(DIGITS.length());
            char randomChar = DIGITS.charAt(index);
            sb.append(randomChar);
        }

        return sb.toString();
    }

    @Override
    public String placeOrder(String order) {
        String orderId;
        do {
            orderId = generateRandomId();
        } while (orderMap.containsKey(orderId));

        orderMap.put(orderId, order);
        return orderId;
    }

    @Override
    public String getOrder(String orderId) {
        return orderMap.getOrDefault(orderId, generateRandomId());
    }
}
