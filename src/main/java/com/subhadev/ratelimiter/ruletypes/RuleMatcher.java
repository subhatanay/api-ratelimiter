package com.subhadev.ratelimiter.ruletypes;



import javax.servlet.http.HttpServletRequest;

public interface RuleMatcher {

    String matchesAndReturnFilterKey(HttpServletRequest httpServletRequest);

}
