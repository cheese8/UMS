package top.dcenter.security.core.auth.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import top.dcenter.security.core.enums.ErrorCodeEnum;
import top.dcenter.security.core.exception.IllegalAccessUrlException;
import top.dcenter.security.core.properties.ClientProperties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static top.dcenter.security.core.consts.SecurityConstants.DEFAULT_SESSION_INVALID_URL;
import static top.dcenter.security.core.util.AuthenticationUtil.redirectProcessingByLoginProcessType;

/**
 * session 失效后处理的控制器.<br><br>
 * 如果要自定义控制器，请继承 {@link InvalidSessionController} 类，并注入 IOC 容器即可
 *
 * @author zyw
 * @version V1.0  Created by 2020/5/3 17:43
 */
@Slf4j
@ResponseBody
public class InvalidSessionController {

    private final RedirectStrategy redirectStrategy;
    private final ClientProperties clientProperties;
    private final ObjectMapper objectMapper;

    public InvalidSessionController(ClientProperties clientProperties, ObjectMapper objectMapper) {
        this.clientProperties = clientProperties;
        this.objectMapper = objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.redirectStrategy = new DefaultRedirectStrategy();
    }


    @GetMapping(DEFAULT_SESSION_INVALID_URL)
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    @ConditionalOnProperty(prefix = "security.client", name = "invalid-session-url", havingValue = DEFAULT_SESSION_INVALID_URL)
    public void invalidSessionHandler(HttpServletRequest request, HttpServletResponse response) {

        try
        {
            redirectProcessingByLoginProcessType(request, response, clientProperties, objectMapper,
                                                 redirectStrategy, ErrorCodeEnum.INVALID_SESSION,
                                                 clientProperties.getLoginPage());
        }
        catch (Exception e)
        {
            String requestURI = request.getRequestURI();
            String ip = request.getRemoteAddr();
            log.error(String.format("IllegalAccessUrlException: ip={}, uri={}, sid={}, error={}",
                                    ip,
                                    requestURI,
                                    request.getSession(true).getId(),
                                    e.getMessage()), e);
            throw new IllegalAccessUrlException(ErrorCodeEnum.SERVER_ERROR, requestURI, ip);
        }
    }
}
