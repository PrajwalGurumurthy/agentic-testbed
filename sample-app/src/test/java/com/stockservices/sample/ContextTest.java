package com.stockservices.sample;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(com.stockservices.common.chaos.lib.ChaosMonkeyFacade.class)
public class ContextTest {

    @Test
    public void contextLoads() {
    }
}
