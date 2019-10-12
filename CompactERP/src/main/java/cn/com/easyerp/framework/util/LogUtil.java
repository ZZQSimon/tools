package cn.com.easyerp.framework.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.com.easyerp.framework.enums.Language;
import cn.com.easyerp.framework.i18n.I18NBundle;

/**
 * log统一工具类
 * 
 * @author Simon Zhang
 */
public class LogUtil {
    private static final Log log = LogFactory.getLog(LogUtil.class);

    /**
     * Debug with message.
     * 
     * @param log
     *            Log
     * @param message
     *            log message
     */
    public static void debug(final String message) {
        if (log.isDebugEnabled()) {
            log.debug(getCodeLocation(Thread.currentThread().getStackTrace()) + message);
        }
    }

    /**
     * Debug with formatter.
     * 
     * @param log
     *            Log
     * @param formatter
     *            String formatter
     * @param args
     *            use by formatter
     */
    public static void debug(final String formatter, final Object... args) {
        if (log.isDebugEnabled()) {
            log.debug(
                    formatMessage(Thread.currentThread().getStackTrace(), MessageUtil.formmatString(formatter, args)));
        }
    }

    /**
     * Debug with exception message.
     * 
     * @param log
     *            Log
     * @param e
     *            Exception
     */
    public static void debug(final Throwable e) {
        if (log.isDebugEnabled()) {
            log.debug(formatMessage(Thread.currentThread().getStackTrace(), e.getMessage()), e);
        }
    }

    /**
     * Debug with exception and formatter message.
     * 
     * @param log
     *            Log
     * @param e
     *            Exception
     * @param formatter
     *            String formatter
     * @param args
     *            use by formatter
     */
    public static void debug(final Throwable e, final String formatter, final Object... args) {
        if (log.isDebugEnabled()) {
            log.debug(formatMessage(Thread.currentThread().getStackTrace(), MessageUtil.formmatString(formatter, args)),
                    e);
        }
    }

    /**
     * Error with message.
     * 
     * @param log
     *            Log
     * @param message
     *            log message
     */
    public static void error(final String message) {
        if (log.isErrorEnabled()) {
            log.error(formatMessage(Thread.currentThread().getStackTrace(), message));
        }
    }

    /**
     * Error with formatter.
     * 
     * @param log
     *            Log
     * @param formatter
     *            String formatter
     * @param args
     *            use by formatter
     */
    public static void error(final String formatter, final Object... args) {
        if (log.isErrorEnabled()) {
            log.error(
                    formatMessage(Thread.currentThread().getStackTrace(), MessageUtil.formmatString(formatter, args)));
        }
    }

    /**
     * Error with exception message.
     * 
     * @param log
     *            Log
     * @param e
     *            Exception
     */
    public static void error(final Throwable e) {
        if (log.isErrorEnabled()) {
            log.error(formatMessage(Thread.currentThread().getStackTrace(), e.getMessage()), e);
        }
    }

    /**
     * Error with exception and formatter message.
     * 
     * @param log
     *            Log
     * @param e
     *            Exception
     * @param formatter
     *            String formatter
     * @param args
     *            use by formatter
     */
    public static void error(final Throwable e, final String formatter, final Object... args) {
        if (log.isErrorEnabled()) {
            log.error(formatMessage(Thread.currentThread().getStackTrace(), MessageUtil.formmatString(formatter, args)),
                    e);
        }
    }

    /**
     * Fatal with message.
     * 
     * @param log
     *            Log
     * @param message
     *            log message
     */
    public static void fatal(final String message) {
        if (log.isFatalEnabled()) {
            log.fatal(formatMessage(Thread.currentThread().getStackTrace(), message));
        }
    }

    /**
     * Fatal with formatter.
     * 
     * @param log
     *            Log
     * @param formatter
     *            String formatter
     * @param args
     *            use by formatter
     */
    public static void fatal(final String formatter, final Object... args) {
        if (log.isFatalEnabled()) {
            log.fatal(
                    formatMessage(Thread.currentThread().getStackTrace(), MessageUtil.formmatString(formatter, args)));
        }
    }

    /**
     * Fatal with exception message.
     * 
     * @param log
     *            Log
     * @param e
     *            Exception
     */
    public static void fatal(final Throwable e) {
        if (log.isFatalEnabled()) {
            log.fatal(formatMessage(Thread.currentThread().getStackTrace(), e.getMessage()), e);
        }
    }

    /**
     * fatal with exception and formatter message.
     * 
     * @param log
     *            Log
     * @param e
     *            Exception
     * @param formatter
     *            String formatter
     * @param args
     *            use by formatter
     */
    public static void fatal(final Throwable e, final String formatter, final Object... args) {
        if (log.isFatalEnabled()) {
            log.fatal(formatMessage(Thread.currentThread().getStackTrace(), MessageUtil.formmatString(formatter, args)),
                    e);
        }
    }

    /**
     * Info with message.
     * 
     * @param log
     *            Log
     * @param message
     *            log message
     */
    public static void info(final String message) {
        if (log.isInfoEnabled()) {
            log.info(formatMessage(Thread.currentThread().getStackTrace(), message));
        }
    }

    /**
     * Info with formatter.
     * 
     * @param log
     *            Log
     * @param formatter
     *            String formatter
     * @param args
     *            use by formatter
     */
    public static void info(final String formatter, final Object... args) {
        if (log.isInfoEnabled()) {
            log.info(formatMessage(Thread.currentThread().getStackTrace(), MessageUtil.formmatString(formatter, args)));
        }
    }

    /**
     * Info with exception message.
     * 
     * @param log
     *            Log
     * @param e
     *            Exception
     */
    public static void info(final Throwable e) {
        if (log.isInfoEnabled()) {
            log.info(formatMessage(Thread.currentThread().getStackTrace(), e.getMessage()), e);
        }
    }

    /**
     * Info with exception and formatter message.
     * 
     * @param log
     *            Log
     * @param e
     *            Exception
     * @param formatter
     *            String formatter
     * @param args
     *            use by formatter
     */
    public static void info(final Throwable e, final String formatter, final Object... args) {
        if (log.isInfoEnabled()) {
            log.info(formatMessage(Thread.currentThread().getStackTrace(), MessageUtil.formmatString(formatter, args)),
                    e);
        }
    }

    /**
     * Warn with message.
     * 
     * @param log
     *            Log
     * @param message
     *            log message
     */
    public static void warn(final String message) {
        if (log.isWarnEnabled()) {
            log.warn(formatMessage(Thread.currentThread().getStackTrace(), message));
        }
    }

    /**
     * Warn with formatter.
     * 
     * @param log
     *            Log
     * @param formatter
     *            String formatter
     * @param args
     *            use by formatter
     */
    public static void warn(final String formatter, final Object... args) {
        if (log.isWarnEnabled()) {
            log.warn(formatMessage(Thread.currentThread().getStackTrace(), MessageUtil.formmatString(formatter, args)));
        }
    }

    /**
     * Warn with exception message.]
     * 
     * @param log
     *            Log
     * @param e
     *            Exception
     */
    public static void warn(final Throwable e) {
        if (log.isWarnEnabled()) {
            log.warn(formatMessage(Thread.currentThread().getStackTrace(), e.getMessage()), e);
        }
    }

    /**
     * Warn with exception and formatter message.
     * 
     * @param log
     *            Log
     * @param e
     *            Exception
     * @param formatter
     * @param args
     *            use by formatter
     */
    public static void warn(final Throwable e, final String formatter, final Object... args) {
        if (log.isWarnEnabled()) {
            log.warn(formatMessage(Thread.currentThread().getStackTrace(), MessageUtil.formmatString(formatter, args)),
                    e);
        }
    }

    /**
     * 操作文件时候的异常
     * 
     * @param filename
     *            文件名
     * @param locale
     *            国际化地区，使用LocaleUtil.locale
     * @param e
     *            异常
     * @param formatter
     *            格式化消息
     * @param args
     *            格式化消息参数
     */
    public static void fileOperateError(final String filename, final Language locale, final Throwable e,
            final String formatter, final Object... args) {
        String fPattern = I18NBundle.getString("fileoperate", locale);
        String prefixLogMsg = MessageUtil.formmatString(fPattern, filename);
        if (log.isErrorEnabled()) {
            log.error(formatMessage(Thread.currentThread().getStackTrace(),
                    MessageUtil.formmatString(prefixLogMsg + formatter, args)), e);
        }
    }

    /**
     * 获取调用此函数的代码的位置
     * 
     * @return 包名.类名.方法名(行数)
     */
    private static String getCodeLocation(StackTraceElement[] stacks) {
        try {
            /* 获取输出信息的代码的位置 */
            StringBuffer location = new StringBuffer();
            location.append(stacks[2].getClassName());
            location.append(".");
            location.append(stacks[2].getMethodName());
            location.append("(");
            location.append(stacks[2].getLineNumber());
            location.append(") | ");
            return location.toString();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 任务线日志
     * 
     * @param pattern
     * @param arguments
     */
    public static void taskTrail(final String pattern, Object... arguments) {
        if (log.isInfoEnabled()) {
            log.info(formatMessage(Thread.currentThread().getStackTrace(),
                    MessageUtil.formmatString(pattern, arguments)));
        }
    }

    /**
     * 业务线日志
     * 
     * @param code
     * @param description
     * @param pattern
     * @param arguments
     */
    public static void businessTrail(final String code, final String description, final String pattern,
            Object... arguments) {
        if (log.isInfoEnabled()) {
            String logString = MessageUtil.formmatString("[{0}], 业务详细描述[{1}]  ", code, description);
            logString = formatMessage(Thread.currentThread().getStackTrace(),
                    MessageUtil.formmatString(logString + pattern, arguments));
            log.info(logString);
        }
    }

    /**
     * 业务线日志
     * 
     * @param code
     * @param description
     * @param pattern
     * @param arguments
     */
    public static void businessTrail(final String code, final String description) {
        // BusinessCode[ {0} ], Description[{1}]
        if (log.isInfoEnabled()) {
            log.info(formatMessage(Thread.currentThread().getStackTrace(),
                    MessageUtil.formmatString("[{0}], 业务详细描述：[{1}]  ", code, description)));
        }
    }

    private static String formatMessage(StackTraceElement[] stacks, String message) {
        return getCodeLocation(stacks) + message;
    }
}
