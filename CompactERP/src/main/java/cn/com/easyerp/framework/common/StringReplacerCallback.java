package cn.com.easyerp.framework.common;

import java.util.regex.Matcher;

public interface StringReplacerCallback {
    String replace(Matcher paramMatcher);
}
