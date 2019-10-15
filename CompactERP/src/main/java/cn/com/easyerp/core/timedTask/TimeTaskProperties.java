package cn.com.easyerp.core.timedTask;

import java.util.HashMap;
import java.util.Map;

public class TimeTaskProperties {
    private boolean isExec;
    private String domain;
    private Map<String, Object> domains;

    public boolean getIsExec() {
        return this.isExec;
    }

    public void setIsExec(boolean isExec) {
        this.isExec = isExec;
    }

    public String getDomain() {
        return this.domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
        if (null == domain)
            return;
        String[] split = domain.split(",");
        Map<String, Object> domainMap = new HashMap<String, Object>();
        for (int i = 0; i < split.length; i++) {
            domainMap.put(split[i].trim(), domain);
        }
        this.domains = domainMap;
    }

    public Map<String, Object> getDomains() {
        return this.domains;
    }

    public void setDomains(Map<String, Object> domains) {
        this.domains = domains;
    }
}
