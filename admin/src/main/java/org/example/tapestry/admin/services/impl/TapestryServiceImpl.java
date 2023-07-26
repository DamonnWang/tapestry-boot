package org.example.tapestry.admin.services.impl;

import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.tapestry5.ioc.annotations.Inject;
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
    @Inject
    private UserService userService;

    @Override
    public User getUser() {
        return userService.getUser();
    }
}
