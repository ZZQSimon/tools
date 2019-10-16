package cn.com.easyerp.core;

import java.util.Locale;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;

import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.logger.Loggable;

public class TextResolver implements MessageSource {
    @Loggable
    private Logger logger;
    @Autowired
    private DataService dataService;

    public String getMessage(String code, Object[] objects, String s2, Locale locale) {
        String ret = null;
        return (ret == null) ? s2 : ret;
    }

    public String getMessage(String code, Object[] objects, Locale locale) throws NoSuchMessageException {
        String ret = this.dataService.getMessageText(code, objects);
        if (ret == null) {
            if (this.logger.isDebugEnabled()) {
                this.logger.warn(String.format("not defined message resource:%s", new Object[] { code }));
            }
            return code;
        }

        return ret;
    }

    public String getMessage(MessageSourceResolvable messageSourceResolvable, Locale locale)
            throws NoSuchMessageException {
        return "not defined";
    }
}
