# 使用 JWT 注意事项:

1\. 通过 ajax 认证与刷新 jwt, 推荐 `ums.client.login-process-type` 设置 `LoginProcessType.JSON`, 这样通过 form POST 提交认证与刷新 jwt, 不需要处理因跳转目标 `url` 时处理 `jwt` 与 `refreshToken`. 减少复杂度.

2\. 认证后默认 `JWT` 通过 `header` 返回, `Authorization: bearer xxx.xxx.xxx` . 可以通过 `ums.jwt.bearer` 下的属性配置更改请求头, 也可以通过 `json
` 返回.

3\. 认证后默认 `JWT refresh token` 通过 `header` 返回, `refresh_token: xxxx.xxxx.xxxx` . 可以通过 `ums.jwt.bearer
` 下的属性配置更改请求头, 也可以通过 `json` 返回.
      
4\. 认证成功后请求需要认证 `API` 接口, 默认要求 `JWT` 设置到 `header`, `Authorization: bearer xxx.xxx.xxx` . `可以通过 ums.jwt.bearer` 下的属性配置更改 `JWT` 传递方式, 如设置到 `request parameter` 中传递.
    
5\. 需要跨域时, 需配置 `ums.client.cors` 下属性, 注意设置 `JWT` 需要曝露的 `header` 头, 例如: `Access-Control-Expose-Headers: Authorization`.

# [jwt-example-github](https://github.com/ZeroOrInfinity/UMS/tree/master/demo/jwt-example) / [jwt-example-gitee](https://gitee.com/pcore/UMS/tree/master/demo/jwt-example);

## 配置文件

```yaml
server:
  port: 9090

spring:
  profiles:
    active: dev
  # mysql
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://127.0.0.1:3306/ums?useSSL=false&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai
    username: root
    password: 123456
  # thymeleaf
  thymeleaf:
    encoding: utf-8
    prefix: classpath:/templates/
    suffix: .htm
    servlet:
      content-type: text/html;charset=UTF-8
  # jackson
  jackson:
    date-format: yyyy-MM-dd HH:mm:ss
    time-zone: GMT+8

  # session 简单配置
  session:
    # session 存储模式设置, 要导入相应的 spring-session 类的依赖, 默认为 none, 分布式服务应用把 session 放入 redis 等中间件
    store-type: none
    # session 过期时间
    timeout: PT300s

  # ===============start session 详细设置 ===============
  redis:
    host: 192.168.88.88
    port: 6379
    password:
    database: 0
    # 连接超时的时间
    timeout: 10000
    # redis-lettuce-pool
    lettuce:
      # 会影响应用关闭是时间, dev 模式设置为 0
      shutdown-timeout: PT500S
      pool:
        max-active: 8
        max-wait: PT10S
        max-idle: 8
        min-idle: 1
        
#  security:
#    oauth2:
#      resourceserver:
#        jwt:
#          public-key-location: classpath:key.pub
#          jws-algorithm: RS256


ums:
  oauth:
    # 是否支持第三方授权登录功能, 默认: 空, 必须明确配置是否支持
    enabled: false
  # RBAC 权限访问控制
  rbac:
    # ========= 放行权限 ==========
    # 权限表达式, 当 enableRestfulApi=true 且没有 @EnableGlobalMethodSecurity 注释时生效, 默认为 hasPermission(request, authentication).
    # hasPermission 表达式默认实现为 UriAuthoritiesPermissionEvaluator, 想自定义逻辑, 实现 PermissionEvaluator 即可替换.
    # String accessExp = "hasPermission(request, authentication)";
    # // 配置等效与
    # httpSecurity.authorizeRequests().anyRequest().access(hasPermission(request, authentication));
    restful-access-exp: permitAll()
    # 是否支持 restful Api (前后端交互接口的风格; 如: 查询(GET),添加(POST),修改(PUT),删除(DELETE)), 默认: true.
    # 当 {@code enableRestfulApi=false} 时 {@code accessExp} 权限表达式生效,
    # 当 {@code enableRestfulApi=true} 时 {@code restfulAccessExp} 权限表达式生效.
    enable-restful-api: true
  client:
    # 设置登录后返回格式(REDIRECT 与 JSON): 默认 JSON
    login-process-type: json
    # 一级域名(不包括二级域名) 例如:
    #       domain: www.example.com -> topDomain: example.com
    #       domain: www.example.com.cn -> topDomain: example.com.cn
    #       domain: aaa.bbb.example.net -> topDomain: example.net
    # 测试时用的 IP 或 localhost 直接原样设置就行.
    # 在应用启动时通过 SecurityAutoConfiguration 自动注入 MvcUtil 字段 topDomain 中. 如在设置跨域 cookie 时可以通过 MvcUtil.getTopDomain() 方法获取.
    topDomain: localhost
    # 登录页(必须自己实现)
    login-page: /login
    # 登录失败页(必须自己实现)
    failure-url: /login
    # 登录成功页(必须自己实现)
    success-url: /me
    # 设置登出 url, 默认为 /logout
    logout-url: /logout
    # 设置登出后跳转的 url(必须自己实现), 默认为 /login
    logout-success-url: /login
    # 不需要认证的静态资源 urls, 例如: /resources/**, /static/**
    ignoring-urls:
      - /static/**
    # 设置登录时用户名的 request 参数名称, 默认为 username
    usernameParameter: username
    # 设置登录时用户密码的 request 参数名称, 默认为 password
    passwordParameter: password
    permit-urls:

  # ========= JWT 功能 =========
  jwt:
    # 是否支持 jwt, 默认: false
    enable: true
    # JWT 的有效期, 默认: 1 小时
    timeout: PT3600S
    # 授权服务器的时钟与资源服务器的时钟可能存在偏差, 设置时钟偏移量以消除不同服务器间的时钟偏差的影响, 默认: 0 秒.
    # 注意: 此默认值适合 "单服务器" 应用, "多服务器" 应用请更改此值
    clock-skew: PT0S
    # 通过 refreshToken 获取 JWT uri, 默认: /jwt/refreshToken
    jwt-by-refresh-token-uri: /jwt/refreshToken
    # JWT 剩余的有效期间隔小于此值后自动刷新 JWT, 此配置在 {@link JwtRefreshHandlerPolicy#AUTO_RENEW} 时有效, 默认: 600 秒
    remaining-refresh-interval: PT600S
    # ========= JWK 相关 =========
    # 是否曝露 jwk-set-uri, 默认: false.  如果为 true, 需要实现 JwkEndpointPermissionService 权限服务接口
    expose-jwk-set-uri: true
    # 密钥键值对(KeyPair)的别名
    jks-alias: zyw
    # 用于 JWT 的密钥键值对(KeyPair)的文件位置; 当设置此属性时, 属性 jwsAlgorithms 必须是 RS256, RS384, RS512 中的一个.
    # 注意: 属性 macsSecret 与 jksKeyPairLocation 同时配置, jksKeyPairLocation 属性优于 macsSecret.
    jks-key-pair-location: classpath:zyw.jks
    # 密钥键值对(KeyPair)的密码
    jks-password: 123456
    # Use the given signing algorithm . The value should be one of RS256, RS384, RS512, HS256, HS384, or HS512 . 默认: RS256
    jws-algorithms: RS256
    # 该JWT的签发者, 必须是 URL, 根据是否需要设置
    iss: http://localhost:9090/demo/
    bearer:
      # 是否支持从 form encoded body parameter 传递参数名称为 bearerTokenParameterName 的 bearer token 或传递参数名称为
      # refreshTokenParameterName 的 refresh token, 默认: false
      # 注意:
      # 1. 属性 allowFormEncodedBodyParameter, allowUriQueryParameter 只能是其中一个值为 true,
      #    表示使用 bearerTokenParameterName 的值, bearerTokenHeaderName 失效;
      # 2. 两个值为 false 时表示使用 bearerTokenHeaderName 的值, bearerTokenParameterName 失效.
      # 3. 此属性也控制认证成功后 jwt 与 refresh token 返回的方式, false 表示从 header 中返回, true 表示 json 返回.
      # 4. 当启用通过 request 的 form 来传递 JWT 时会带来很多局限性, 前端只能通过 org.springframework.http.HttpMethod.POST
      #    来访问需要权限的 API; 一般情况下请保持此默认值, 通过请求头传递.
      allow-form-encoded-body-parameter: false
      # bearer token header name. 默认: Authorization . 不能为 null 值.
      # 注意:
      # 1. 属性 bearerTokenHeaderName 只有在 allowFormEncodedBodyParameter, allowUriQueryParameter 都为 false 时生效.
      # 2. 如果是跨域的场景, 需要设置 ums.client.cors.accessControlExposeHeaders.
      # 3. 当 usm.jwt.refreshHandlerPolicy=AUTO_RENEW 时, 不管此字段是否生效, 刷新的 jwt 直接设置到此 header 中,
      #    前端可以从相应的 header 中获取.
      bearer-token-header-name: Authorization
      # request parameter name, 默认为: access_token. 不能为 null 值.
      # 注意:
      # 属性 bearerTokenParameterName 只有在 allowFormEncodedBodyParameter 或 allowUriQueryParameter 其中一方为 true 时生效.
      bearer-token-parameter-name: access_token
      # bearer token header name. 默认: refresh_token . 不能为 null 值.
      # 注意:
      # 1. 属性 refreshTokenHeaderName 只有在 allowFormEncodedBodyParameter, allowUriQueryParameter 都为 false 时生效.
      # 2. 如果是跨域的场景, 需要设置 ums.client.cors.accessControlExposeHeaders
      refresh-token-header-name: refresh_token
      # request parameter name, 默认为: refresh_token . 不能为 null 值.
      # 注意:
      # 1. 属性 refreshTokenParameterName 只有在 allowFormEncodedBodyParameter 或 allowUriQueryParameter 其中一方为 true 时生效.
      # 2. 目前 allowFormEncodedBodyParameter, allowUriQueryParameter 属性还不能配置, 默认都为 false, refreshTokenParameterName 总是失效.
      refresh-token-parameter-name: refresh_token
    cache:
      # JWT 黑名单缓存前缀, 默认: JWT:BLACKLIST:
      blacklist-prefix: 'JWT:BLACKLIST:'
      # JWT refresh token 缓存前缀, 默认: JWT:refreshToken:
      refresh-token-prefix: 'JWT:refreshToken:'
      # JWT refresh token ttl(有效期), 默认: 30 天
      refresh-token-ttl: PT720H




  # 验证码配置
  # 同一个 uri 由多种验证码同时配置, **优先级**如下:
  #  `SMS > CUSTOMIZE > SELECTION > TRACK > SLIDER > IMAGE`
  codes:
    # 图片验证码
    image:
      # 设置需要图片验证码认证的 uri(必须是非 GET 请求)，多个 uri 用 “-” 或 ","号分开支持通配符，如：/hello,/user/*；默认为 /authentication/form
      auth-urls:
        - /authentication/form
      request-param-image-code-name: imageCode

---
spring:
  profiles: dev
  mvc:
    throw-exception-if-no-handler-found: true

server:
  servlet:
    context-path: /demo

logging:
  config: classpath:logback-spring.xml
#debug: true
```