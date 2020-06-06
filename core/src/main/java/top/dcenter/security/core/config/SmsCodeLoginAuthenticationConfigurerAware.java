package top.dcenter.security.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import top.dcenter.security.core.api.config.WebSecurityConfigurerAware;
import top.dcenter.security.core.properties.SmsCodeLoginAuthenticationProperties;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 手机登录配置
 * @author zyw
 * @version V1.0  Created by 2020/5/15 21:51
 */
@Configuration
@ConditionalOnProperty(prefix = "security..mobile.login", name = "sms-code-login-is-open", havingValue = "true")
@AutoConfigureAfter({SmsCodeLoginAuthenticationConfig.class, ValidateCodeBeanConfiguration.class})
@Slf4j
public class SmsCodeLoginAuthenticationConfigurerAware implements WebSecurityConfigurerAware {

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired(required = false)
    private SmsCodeLoginAuthenticationConfig smsCodeLoginAuthenticationConfig;
    private final SmsCodeLoginAuthenticationProperties smsCodeLoginAuthenticationProperties;

    public SmsCodeLoginAuthenticationConfigurerAware(SmsCodeLoginAuthenticationProperties smsCodeLoginAuthenticationProperties) {
        this.smsCodeLoginAuthenticationProperties = smsCodeLoginAuthenticationProperties;
    }

    @Override
    public void postConfigure(HttpSecurity http) throws Exception {
        // do nothing
    }

    @Override
    public void preConfigure(HttpSecurity http) throws Exception {
        // 短信验证码登录配置
        if (smsCodeLoginAuthenticationConfig != null)
        {
            http.apply(smsCodeLoginAuthenticationConfig);
        }
    }

    @Override
    public Map<String, Set<String>> getAuthorizeRequestMap() {
        Set<String> permitAllSet = new HashSet<>();
        permitAllSet.add(smsCodeLoginAuthenticationProperties.getLoginProcessingUrlMobile());

        Map<String, Set<String>> permitAllMap = new HashMap<>(1);
        permitAllMap.put(permitAll, permitAllSet);
        return permitAllMap;
    }
}

