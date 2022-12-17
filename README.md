# API-RateLimiter Library

## Description
A Distributed API-RateLimiter Java library that helps to throttle http requests based on defined rules. Easily integratable to any Java EE based or Springboot based application.
Supports both InMemeory and Redis based caching for creating buckets for each type of request. Currently supports Token bucket and Sliding Windowlog based ratelimiting strategy. 

## Class Diagram
![image](https://user-images.githubusercontent.com/22850961/208228133-ab93b178-4803-4f6c-afa8-8218779189e8.png)

## Flow Diagram
![image](https://user-images.githubusercontent.com/22850961/208235701-3a083753-48be-4271-a80b-535c375f4154.png)

## Documentation
1. Supports two types of ratelimiting strategy. Token Bucket and Sliding Window Log.
2. Supports in-memeory based and redis based.
3. Supports configuring different rules in yaml file based on application need. Default file rate-limit-rule.yaml.
4. Supports auto reload of the config file when the application is running.
5. While running in In-Memeory setup, rate limiting filter keys will be created in application memory in memory for tracking each request usage.
6. While running in Redis based setup, rate limiting filter keys will be created in REDIS system, this feature can be used for Loadbalancers or appservers with multiple nodes which can connect to shared redis system for the distributed ratelimiting.

Rate limiting rule YAML configuartion details :: 
| Key | Value   | Description   |
| :---:   | :---: | :---: |
| strategy | sliding or token-bucket   | Rate limiting algorithm to be use for request throttle   |
| in-memory | true or false   | true - In-memory based , false - Redis based   |
| in-memory | true or false   | true - In-memory based , false - Redis based   |
| redis.ip |    | Redis ip address   |
| redis.port |    | Redis port    |
| rules.name |    | Array of rules for request   |
| rules[i].name | <name of the rule name>   | Name of rule  |
| rules[i].type | ip-address or request-address or request-url-regex   | Rule type  |
| rules[i].windowPeriodInSecs | <Number>   | when strategy= sliding , Request allowed window period in Secs |
| rules[i].maxRequests | <Number>   | when strategy= sliding , Max request allowed in the window |
| rules[i].bucket-size | <Number>   | when strategy= token-bucket , token bucket capacity |
| rules[i].refill-rate | <Number>   | when strategy= token-bucket , refill rate of the bucket per secs | 

For each rule type seperate keys needs to be included in the config file. Below is the details ::
1. For type = ip-address -> ipaddress (Mandatory)
2. For type = request-header -> header (Mandatory)
3. For type = request-url-regex -> urlRegex (Mandatory) , pathparamindex (Optional)


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
##### Ratelimter Initalization with in-memory ratelimiting : 
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



 
