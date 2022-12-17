package com.subhadev.ratelimiter;

import com.subhadev.ratelimiter.config.ConfigLoader;
import com.subhadev.ratelimiter.config.RuleYAMLConfigLoader;
import com.subhadev.ratelimiter.parser.RuleConfigParser;
import com.subhadev.ratelimiter.parser.RuleManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;

public class RateLimiterBasicTest {
    @Test
    void basicRateLimiterTestWithAnyIPAddress() throws Exception {
        ConfigLoader configLoader = new RuleYAMLConfigLoader();
        RuleConfigParser configParser = new RuleConfigParser(configLoader);
        RuleManager ruleManager = new RuleManager(configParser);
        RateLimiter rateLimiter = new RateLimiter(ruleManager);

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn("/users/12121/follow");
        Mockito.when(request.getRemoteAddr()).thenReturn("22.33.22.33.22");

        for (int i=0;i<200;i++) {
            Assertions.assertEquals(true,rateLimiter.isRequestAllowed(request));
        }
        Assertions.assertEquals(false,rateLimiter.isRequestAllowed(request));
        Thread.sleep(10010);
        Assertions.assertEquals(true,rateLimiter.isRequestAllowed(request));
    }

    @Test
    void basicRateLimiterTestWithRestrictSpecificIP() throws Exception {
        ConfigLoader configLoader = new RuleYAMLConfigLoader();
        RuleConfigParser configParser = new RuleConfigParser(configLoader);
        RuleManager ruleManager = new RuleManager(configParser);
        RateLimiter rateLimiter = new RateLimiter(ruleManager);

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn("/users/12121/follow");
        Mockito.when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        Assertions.assertEquals(false,rateLimiter.isRequestAllowed(request));
    }

    @Test
    void basicRateLimiterTestWithComplexRule() throws Exception {
        ConfigLoader configLoader = new RuleYAMLConfigLoader();
        RuleConfigParser configParser = new RuleConfigParser(configLoader);
        RuleManager ruleManager = new RuleManager(configParser);
        RateLimiter rateLimiter = new RateLimiter(ruleManager);

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn("/users/12121/follow");
        Mockito.when(request.getRemoteAddr()).thenReturn("66.99.55.78");
        Mockito.when(request.getHeader(Mockito.anyString())).thenReturn("Token");
        Assertions.assertEquals(true,rateLimiter.isRequestAllowed(request));
    }
}
