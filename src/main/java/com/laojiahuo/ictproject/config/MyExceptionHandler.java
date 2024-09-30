package com.laojiahuo.ictproject.config;

import com.laojiahuo.ictproject.utils.JsonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @description: 自定义异常处理
 */
@ControllerAdvice
public class MyExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(MyExceptionHandler.class);

    @ExceptionHandler(value = CustomException.class)
    @ResponseBody
    public JsonResult<Object> handleCustomException(CustomException e) {
        logger.error("自定义异常捕获: ", e);
        return JsonResult.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(value = NullPointerException.class)
    @ResponseBody
    public JsonResult<Object> handleNullPointerException(NullPointerException e) {
        logger.error("NullPointerException caught: ", e);
        return JsonResult.error(500, "Null pointer exception occurred");
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    @ResponseBody
    public JsonResult<Object> handleIllegalArgumentException(IllegalArgumentException e) {
        logger.error("IllegalArgumentException caught: ", e);
        return JsonResult.error(400, e.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public JsonResult<Object> handleException(Exception e) {
        logger.error("Exception caught: ", e);
        return JsonResult.error(500, "An unexpected error occurred: " + e.getMessage());
    }
}
