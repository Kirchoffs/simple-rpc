package org.syh.prj.rpc.simplerpc.interfaces;

import java.util.List;

public interface DataService {
    String sendData(String body);
    List<String> getList();
}
