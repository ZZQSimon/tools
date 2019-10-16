package cn.com.easyerp.core.module;

import cn.com.easyerp.framework.enums.LogLevel;

public class LogConfig {
    private ModuleConfig module;
    LogLevel logLevel;

    LogConfig(ModuleConfig module) {
        this.module = module;
    }

    public ModuleConfig level(LogLevel l) {
        this.logLevel = l;
        return this.module;
    }
}
