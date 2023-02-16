package org.syh.prj.rpc.simplerpc.core.server;

import org.checkerframework.checker.units.qual.A;
import org.syh.prj.rpc.simplerpc.interfaces.UserService;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class UserServiceImpl implements UserService {
    Random random = new Random();

    @Override
    public List<String> getUsers() {
        String[] database = {"Ben", "Tom", "Jason", "Jim", "Leo"};
        return Arrays.asList(
            database[random.nextInt(database.length) % database.length],
            database[random.nextInt(database.length) % database.length]
        );
    }
}
