package org.example.provider.service.impl;

import org.apache.dubbo.config.annotation.DubboService;
import org.example.inter.data.User;
import org.example.inter.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author Damon
 * @date 2023/6/25
 **/

@Service
@DubboService(interfaceClass = UserService.class)
public class UserServiceImpl implements UserService {
    @Override
    public User getUser() {
        System.out.println("received request now");
        return new User("zhangsan", 18);
    }
}
