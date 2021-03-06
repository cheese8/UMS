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
package demo.jwt.service;

import org.springframework.stereotype.Service;
import top.dcenter.ums.security.jwt.api.validator.service.CustomClaimValidateService;
import top.dcenter.ums.security.jwt.enums.JwtCustomClaimNames;

/**
 * 示例添加自定义的 JWT 校验服务
 * @author YongWu zheng
 * @version V2.0  Created by 2020.12.20 21:47
 */
@Service
public class UmsCustomClaimValidateServiceImpl implements CustomClaimValidateService {

    @Override
    public boolean validate(Object claimObject) {
        // claimObject 实际的对象可以通过 UmsJwtClaimTypeConverterSupplier 与
        // MappedJwtClaimSetConverter#withDefaults(Map) 查看.
        // 业务逻辑
        // ...
        return true;
    }

    @Override
    public String getClaimName() {
        // return JwtClaimNames.SUB
        return JwtCustomClaimNames.USER_ID.getClaimName();
    }

}
