package top.dcenter.security.core.api.validate.code;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.web.context.request.ServletWebRequest;
import top.dcenter.security.core.auth.validate.codes.ValidateCode;
import top.dcenter.security.core.enums.ValidateCodeType;
import top.dcenter.security.core.exception.ValidateCodeException;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static top.dcenter.security.core.enums.ErrorCodeEnum.GET_VALIDATE_CODE_FAILURE;
import static top.dcenter.security.core.enums.ErrorCodeEnum.ILLEGAL_VALIDATE_CODE_TYPE;
import static top.dcenter.security.core.enums.ErrorCodeEnum.VALIDATE_CODE_ERROR;
import static top.dcenter.security.core.enums.ErrorCodeEnum.VALIDATE_CODE_EXPIRED;
import static top.dcenter.security.core.enums.ErrorCodeEnum.VALIDATE_CODE_NOT_EMPTY;

/**
 * 验证码处理逻辑的默认实现抽象类
 *
 * @author zhailiang
 * @version V1.0  Created by 2020/5/6 10:14
 * @author zyw
 */
@Slf4j
public abstract class AbstractValidateCodeProcessor implements ValidateCodeProcessor {
    /**
     * 操作 session 的工具类
     */
    protected final SessionStrategy sessionStrategy;

    private final Map<String, ValidateCodeGenerator<?>> validateCodeGenerators;

    /**
     * 验证码处理逻辑的默认实现抽象类.<br><br>
     *
     * @param validateCodeGenerators 子类继承时对此参数不需要操作，在子类注入 IOC 容器时，spring自动注入此参数
     */
    public AbstractValidateCodeProcessor(Map<String, ValidateCodeGenerator<?>> validateCodeGenerators) {

        this.sessionStrategy = new HttpSessionSessionStrategy();
        if (validateCodeGenerators == null)
        {
            this.validateCodeGenerators = new HashMap<>(0);
            return;
        }
        Collection<ValidateCodeGenerator<?>> values = validateCodeGenerators.values();
        this.validateCodeGenerators =
                values.stream().collect(Collectors.toMap((ValidateCodeGenerator::getValidateCodeType),
                                                         validateCodeGenerator -> validateCodeGenerator));
    }

    @Override
    public final boolean produce(ServletWebRequest request) throws ValidateCodeException {

        ValidateCode validateCode;
        HttpServletRequest req = request.getRequest();
        String ip = req.getRemoteAddr();
        String sid = request.getSessionId();
        String uri = req.getRequestURI();
        try
        {
            validateCode = generate(request);
            saveSession(request, validateCode);
            boolean validateStatus = sent(request, validateCode);
            if (!validateStatus)
            {
                this.sessionStrategy.removeAttribute(request, getValidateCodeType().getSessionKey());
                log.warn("发送验证码失败: ip={}, sid={}, uri={}, validateCode={}",
                         ip, sid, uri, validateCode.toString());
                return false;
            }
        }
        catch (Exception e)
        {
            this.sessionStrategy.removeAttribute(request, getValidateCodeType().getSessionKey());
            if (e instanceof ValidateCodeException)
            {
                ValidateCodeException exception = (ValidateCodeException) e;
                log.warn(String.format("生成验证码失败: error={}, ip={}, uid={}, sid={}, uri={}, data={}",
                                       exception.getMessage(), ip, exception.getUid(), sid, uri, exception.getData()), exception);
                throw exception;
            }
            else
            {
                log.warn(String.format("生成验证码失败: error={}, ip={}, sid={}, uri={}",
                                       e.getMessage(), ip, sid, uri), e);
                throw new ValidateCodeException(GET_VALIDATE_CODE_FAILURE, e, ip, uri);
            }
        }
        return true;
    }

    @Override
    public final ValidateCode generate(ServletWebRequest request) {
        try
        {
            ValidateCodeGenerator<?> validateCodeGenerator = getValidateCodeGenerator(getValidateCodeType());
            return (ValidateCode) validateCodeGenerator.generate(request.getRequest());
        }
        catch (ValidateCodeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new ValidateCodeException(GET_VALIDATE_CODE_FAILURE, e, request.getRequest().getRemoteAddr(), request.getRequest().getRequestURI());
        }
    }

    @Override
    public boolean saveSession(ServletWebRequest request, ValidateCode validateCode) {

        try
        {
            ValidateCodeType validateCodeType = getValidateCodeType();
            if (validateCodeType == null)
            {
                return false;
            }
            this.sessionStrategy.setAttribute(request, validateCodeType.getSessionKey(), validateCode);
        }
        catch (Exception e)
        {
            log.error(String.format("验证码保存到Session失败: error={}, ip={}, code={}",
                                    e.getMessage(),
                                    request.getRequest().getRemoteAddr(),
                                    validateCode), e);
            return false;
        }
        return true;
    }

    /**
     * 发送验证码，由子类实现,
     * 发送失败，必须清除 sessionKey 的缓存，示例代码: <br><br>
     * <p>&nbsp;&nbsp;&nbsp;&nbsp;sessionStrategy.removeAttribute(request, sessionKey); </p>
     * <p>&nbsp;&nbsp;&nbsp;&nbsp;sessionKey 获取方式：</p>
     * <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * 如果不清楚是哪种类型 sessionKey，用如下方式：</p>
     * <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * ValidateCodeType validateCodeType = getValidateCodeType();</p>
     * <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * String sessionKey = validateCodeType.getSessionKey();</p>
     *
     * @param request      ServletWebRequest
     * @param validateCode 验证码对象
     * @return 是否发送成功的状态
     */
    @Override
    public abstract boolean sent(ServletWebRequest request, ValidateCode validateCode);

    @Override
    public void validate(ServletWebRequest request) throws ValidateCodeException {
        ValidateCodeType validateCodeType = getValidateCodeType();
        String sessionKey = validateCodeType.getSessionKey();

        String requestParamValidateCodeName = getValidateCodeGenerator(validateCodeType).getRequestParamValidateCodeName();

        ValidateCode codeInSession = (ValidateCode) this.sessionStrategy.getAttribute(request, sessionKey);
        String codeInRequest = request.getParameter(requestParamValidateCodeName).trim();

        HttpServletRequest req = request.getRequest();

        if (!StringUtils.isNotBlank(codeInRequest))
        {
            throw new ValidateCodeException(VALIDATE_CODE_NOT_EMPTY, req.getRemoteAddr(), validateCodeType.name());
        }

        if (codeInSession == null)
        {
            throw new ValidateCodeException(VALIDATE_CODE_EXPIRED, req.getRemoteAddr(), codeInRequest);
        }

        if (codeInSession.isExpired())
        {
            sessionStrategy.removeAttribute(request, sessionKey);
            throw new ValidateCodeException(VALIDATE_CODE_EXPIRED, req.getRemoteAddr(), codeInRequest);
        }

        if (!StringUtils.equalsIgnoreCase(codeInSession.getCode(), codeInRequest))
        {
            throw new ValidateCodeException(VALIDATE_CODE_ERROR, req.getRemoteAddr(), codeInRequest);
        }
        sessionStrategy.removeAttribute(request, sessionKey);

    }



    /**
     * 获取验证码类型
     * @return {@link ValidateCodeType}
     */
    @Override
    public abstract ValidateCodeType getValidateCodeType();

    /**
     * 获取验证码生成器
     * @param type 验证码类型
     * @return 验证码生成器
     */
    private ValidateCodeGenerator<?> getValidateCodeGenerator(ValidateCodeType type) throws ValidateCodeException {
        try
        {
            if (validateCodeGenerators != null)
            {
                ValidateCodeGenerator<?> validateCodeGenerator = validateCodeGenerators.get(type.name().toLowerCase());
                if (validateCodeGenerator != null)
                {
                    return validateCodeGenerator;
                }
            }
            throw new ValidateCodeException(ILLEGAL_VALIDATE_CODE_TYPE, null, type.name());
        }
        catch (Exception e)
        {
            throw new ValidateCodeException(ILLEGAL_VALIDATE_CODE_TYPE, e, null, type.name());
        }
    }

}
