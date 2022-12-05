package org.syh.prj.rpc.simplerpc.core.server;

import org.syh.prj.rpc.simplerpc.interfaces.DataService;

import java.util.ArrayList;
import java.util.List;

public class DataServiceImpl implements DataService {
    @Override
    public String sendData(String body) {
        System.out.println(String.format("Received %d length of content: %s", body.length(), body));
        return "success";
    }

    @Override
    public List<String> getList() {
        System.out.println(String.format("Call from client for list"));
        ArrayList arrayList = new ArrayList();
        arrayList.add("alpha");
        arrayList.add("beta");
        arrayList.add("gamma");
        return arrayList;
    }
}
