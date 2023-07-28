package org.example.tapestry.in.services.impl;

import org.example.tapestry.in.services.TapestryService;
import org.springframework.stereotype.Service;

/**
 * 一个示例
 */
@Service
public class TapestryServiceImpl implements TapestryService {
    @Override
    public String getType() {
        return "TapestryServiceImpl";
    }
}
