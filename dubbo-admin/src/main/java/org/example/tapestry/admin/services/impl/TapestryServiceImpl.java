package org.example.tapestry.admin.services.impl;

import org.apache.dubbo.config.annotation.DubboReference;
import org.example.inter.data.User;
import org.example.inter.service.UserService;
import org.example.tapestry.admin.services.TapestryService;
import org.springframework.stereotype.Service;

/**
 * 一个示例
 */
@Service
public class TapestryServiceImpl implements TapestryService {

    @DubboReference(url = "dubbo://localhost:20888")
    // @Inject (使用了Dubbo注入，不再需要使用tapestry注入就可以使用了)
    private UserService userService;

    @Override
    public User getUser() {
        System.out.println(userService);
        return userService.getUser();
    }
}
