package cn.com.easyerp.core.api;

import java.util.List;

public class ApiResult {
    private int result;
    private List<String> messages;

    public ApiResult(int result, List<String> messages) {
        this.result = result;
        this.messages = messages;
    }

    public ApiResult() {
    }

    public void setResult(int result) {
        this.result = result;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public int getResult() {
        return this.result;
    }

    public List<String> getMessages() {
        return this.messages;
    }

    public boolean isSuccess() {
        return (this.result == 0);
    }
}
