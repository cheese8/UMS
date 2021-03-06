/*
 * MIT License
 * Copyright (c) 2020-2029 YongWu zheng (dcenter.top and gitee.com/pcore and github.com/ZeroOrInfinity)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package top.dcenter.ums.security.jwt.properties;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

/**
 * jti 缓存(用于 refreshToken), 以及 jwt 黑名单缓存属性(用于旧 jwt 失效引发的并发访问问题).
 * @author YongWu zheng
 * @version V2.0  Created by 2020.12.5 14:28
 */
@Getter
@Setter
public class JwtBlacklistProperties {

    /**
     * 是否支持 jwt 黑名单功能, 默认: true.
     * 如果为 false, jwtToken 与 refreshToken 会保存到 redis
     */
    private Boolean enable = Boolean.TRUE;

    /**
     * 是否需要重新登录认证的 redis key 前缀, 默认: "JWT:REAUTH:"
     */
    private String reAuthPrefix = "JWT:REAUTH:";

    /**
     * 用于存储用户 Token 信息的前缀, 也可以理解为用户信息, 后面回添加 Jwt 的 jti, 默认: "JWT:tokenInfo:"
     */
    private String tokenInfoPrefix = "JWT:tokenInfo:";

    /**
     * JWT refresh token 缓存前缀, 默认: JWT:refreshToken:
     */
    private String refreshTokenPrefix = "JWT:refreshToken:";

    /**
     * JWT refresh token ttl(有效期), 默认: 30 天
     */
    private Duration refreshTokenTtl = Duration.ofDays(30);

    /**
     * JWT 黑名单缓存前缀, 默认: JWT:BLACKLIST:
     */
    private String blacklistPrefix = "JWT:BLACKLIST:";

}
