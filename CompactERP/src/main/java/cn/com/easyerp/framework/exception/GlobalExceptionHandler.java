package cn.com.easyerp.framework.exception;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import cn.com.easyerp.core.logger.LogService;
import cn.com.easyerp.core.logger.Loggable;
import cn.com.easyerp.framework.common.ActionErrorModel;
import cn.com.easyerp.framework.common.ActionResult;
import cn.com.easyerp.storage.StorageService;

@ControllerAdvice
public class GlobalExceptionHandler {
    @Loggable
    private Logger logger;
    @Autowired
    private StorageService storageService;
    @Autowired
    private LogService logService;

    @ResponseBody
    @ExceptionHandler({ Exception.class })
    public ActionResult handler(Exception e) throws Exception {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        String logId = (String) request.getAttribute("logId");
        String target_id = (String) request.getAttribute("tableId");
        String context = (String) request.getAttribute("data");

        if (AnnotationUtils.findAnnotation(e.getClass(),
                org.springframework.web.bind.annotation.ResponseStatus.class) != null) {
            throw e;
        }
        this.storageService.rollback();
        this.logger.error("Exception:", e);
        String message = e.getMessage();
        if (message == null) {
            message = e.toString();
        }
        this.logService.updateLogException(logId, target_id, context, message);

        if (e instanceof ApplicationException) {
            return new ActionErrorModel(message,
                    DatabaseException.class.isInstance(e) ? new ApplicationException(e.getMessage()) : e);
        }
        return new ActionErrorModel("application error",
                DatabaseException.class.isInstance(e) ? new ApplicationException(e.getMessage()) : e);
    }
}
