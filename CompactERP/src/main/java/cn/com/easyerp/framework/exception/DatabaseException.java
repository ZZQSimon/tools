package cn.com.easyerp.framework.exception;

import org.springframework.dao.DataAccessException;

public class DatabaseException extends DataAccessException {
    private static final long serialVersionUID = 938436006977682098L;
    private static final String EXCEPTION_MSG = "DB access exception";

    public DatabaseException(String msg) {
        super(msg);
    }

    public DatabaseException(Throwable cause) {
        super(EXCEPTION_MSG, cause);
    }
}
