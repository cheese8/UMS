package top.dcenter.ums.security.core.auth.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import top.dcenter.ums.security.core.api.validate.code.ValidateCodeProcessor;
import top.dcenter.ums.security.core.auth.validate.codes.ValidateCodeProcessorHolder;
import top.dcenter.ums.security.core.exception.ValidateCodeException;
import top.dcenter.ums.security.core.exception.ValidateCodeProcessException;
import top.dcenter.ums.security.core.properties.ValidateCodeProperties;
import top.dcenter.ums.security.core.util.MvcUtil;
import top.dcenter.ums.security.core.vo.ResponseResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static top.dcenter.ums.security.core.enums.ErrorCodeEnum.GET_VALIDATE_CODE_FAILURE;
import static top.dcenter.ums.security.core.enums.ErrorCodeEnum.ILLEGAL_VALIDATE_CODE_TYPE;


/**
 * 验证码 控制器
 * @author zhailiang
 * @author  zyw
 * @version V1.0  Created by 2020/5/3 23:41
 */
@Slf4j
@ResponseBody
public class ValidateCodeController implements InitializingBean {

    private ValidateCodeProcessorHolder validateCodeProcessorHolder;

    @Autowired
    private ValidateCodeProperties validateCodeProperties;

    @Autowired
    private GenericApplicationContext applicationContext;

    /**
     * 获取图片验证码, 根据验证码类型不同，调用不同的 {@link ValidateCodeProcessor} 接口实现
     * @param request request 中的 width 的值如果小于 height * 45 / 10, 则 width = height * 45 / 10
     * @param response  {@link HttpServletResponse}
     */
    @GetMapping("/code/{type}")
    public void createCode(@PathVariable("type") String type,
                                     HttpServletRequest request, HttpServletResponse response) {

        ValidateCodeProcessor validateCodeProcessor;
        if (validateCodeProcessorHolder != null)
        {
            validateCodeProcessor = validateCodeProcessorHolder.findValidateCodeProcessor(type);
        } else {
            validateCodeProcessor = null;
        }

        String ip = request.getRemoteAddr();
        String errorMsg = ILLEGAL_VALIDATE_CODE_TYPE.getMsg();
        if (validateCodeProcessor == null)
        {
            log.warn("创建验证码错误: error={}, ip={}, type={}", errorMsg, ip, type);
            throw new ValidateCodeException(ILLEGAL_VALIDATE_CODE_TYPE, ip, type);
        }

        boolean validateStatus = validateCodeProcessor.produce(new ServletWebRequest(request, response));

        if (!validateStatus)
        {
            log.warn("发送验证码失败: error={}, ip={}, type={}", errorMsg, ip, type);
            throw new ValidateCodeProcessException(GET_VALIDATE_CODE_FAILURE, ip, type);
        }

    }

    /**
     * 验证方法, 所有验证逻辑都通过 {@link top.dcenter.ums.security.core.auth.validate.codes.ValidateCodeFilter} 处理:<br>
     *     1. 验证不通过, 过滤器直接抛出 {@link top.dcenter.ums.security.core.exception.ValidateCodeException } ,
     *        再通过 {@link top.dcenter.ums.security.core.api.advice.SecurityControllerExceptionHandler} 处理返回.
     *     2. 验证通过, 通过此方法返回.
     *
     * @return  ResponseResult
     */
    @RequestMapping(value = {"${security.codes.slider.sliderCheckUrl}"}, method = RequestMethod.POST)
    public ResponseResult sliderCheck() {
        return ResponseResult.success();
    }



    @Override
    public void afterPropertiesSet() throws Exception {

        // 1. 解决循环应用问题
        this.validateCodeProcessorHolder = applicationContext.getBean(ValidateCodeProcessorHolder.class);

        // 2. 动态注入 sliderCheck() PostMapping 的映射 uri
        String methodName = "sliderCheck";
        MvcUtil.setRequestMappingUri(methodName,
                                     validateCodeProperties.getSlider().getSliderCheckUrl(),
                                     this.getClass());

        // 3. 在 mvc 中做 Uri 映射等动作
        MvcUtil.registerController("validateCodeController", applicationContext, null);


    }
}
