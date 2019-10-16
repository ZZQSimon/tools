package cn.com.easyerp.core.module;

import java.util.LinkedHashSet;
import java.util.Set;

import cn.com.easyerp.framework.common.Common;

public class ModuleConfig {
    Set<String> js = new LinkedHashSet<>();
    Set<String> css = new LinkedHashSet<>();
    LogConfig log = new LogConfig(this);

    public ModuleConfig js(String jsFile) {
        if (Common.notBlank(jsFile))
            this.js.add(jsFile);
        return this;
    }

    public ModuleConfig css(String cssFile) {
        if (Common.notBlank(cssFile))
            this.css.add(cssFile);
        return this;
    }

    public LogConfig log() {
        return this.log;
    }
}
