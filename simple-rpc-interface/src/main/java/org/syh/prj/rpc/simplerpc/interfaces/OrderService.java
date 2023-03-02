package org.syh.prj.rpc.simplerpc.interfaces;

import java.util.List;

public interface OrderService {
    String placeOrder(String body);
    String getOrder(String id);
}
