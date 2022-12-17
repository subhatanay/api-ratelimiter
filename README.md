# API-RateLimiter

## Description
A Distributed API-RateLimiter Java library that helps to throttle http requests based on defined rules. Easily integratable to any Java EE based or Springboot based application.
Supports both InMemeory and Redis based caching for creating buckets for each type of request. Currentry supports Token bucket and Sliding Windowlog based ratelimiting strategy.

## MVP

## Class Diagram
![image](https://user-images.githubusercontent.com/22850961/208228133-ab93b178-4803-4f6c-afa8-8218779189e8.png)

## Flow Diagram
![image](https://user-images.githubusercontent.com/22850961/208235701-3a083753-48be-4271-a80b-535c375f4154.png)

## Documentation

## Setup Guide

### Prerequisites
1. Java 8
2. Maven
3. A REST API Application where RateLimiter need to be plugged.

### Build 
1. Clone this library using -> git clone https://github.com/subhatanay/api-ratelimiter.git
2. cd api-ratelimiter & mvn clean install
3. library should be ready to use in the REST API Application. 

## Integrate Steps
#### Include following dependency in your pom.xml 
```
 <dependency>
            <groupId>com.subhadev</groupId>
            <artifactId>rate-limiter</artifactId>
            <version>1.0</version>
</dependency>
```
#### Include Ratelimiting Rules Config file
Add `rate-limit-rule.yaml` file under src/main/resources folder. See Documentation for more Info.
Here is a sample YAML file supporting sliding window log ratelimiting algorithm
```
strategy: sliding # token-bucket | sliding
in-memory: false
rules:
  - name: ip based filter for any request
    type: ip-address
    ipaddress: '*'
    windowPeriodInSecs: '100'
    maxRequests: '5'
  - name: specific ip will not be allowed to make any request
    type: ip-address
    ipaddress: 127.0.0.1
    windowPeriodInSecs: '10'
    maxRequests: '0'
  - name: authenticated user request rate-limit filter
    type: request-header
    header: Authorization
    windowPeriodInSecs: '600'
    maxRequests: '100'
  - name: individual user can add 20 followers per hour
    type: request-url-regex,request-header
    urlRegex: '/api/users/(\d+)/follow'
    header: Authorization
    windowPeriodInSecs: '600'
    maxRequests: '20'
```
Above file is a sample for ratelimiting rule, rules needs to be changed based on applications usage.
#### Library Usage :: 
##### Ratelimter Initalization : 
```java
ConfigLoader configLoader = new RuleYAMLConfigLoader();
RuleConfigParser configParser = new RuleConfigParser(configLoader);
RuleManager ruleManager = new RuleManager(configParser);
RateLimiter rateLimiter = new RateLimiter(ruleManager);
```

##### Ratelimter Usage for http request ratelimitig inside a servlet filter :
```
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        logger.warn("Got request " + request.getRequestURI());
        if (rateLimiter.isRequestAllowed(request)) {
            filterChain.doFilter(request,response);
        } else {
            logger.warn("Request got rate limited for " + request.getRequestURI());
            response.sendError(429);
        }
}
```



 
