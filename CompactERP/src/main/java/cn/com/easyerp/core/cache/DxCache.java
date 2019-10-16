package cn.com.easyerp.core.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.easyerp.auth.AuthConfigDescribe;
import cn.com.easyerp.core.approve.FlowBlock;
import cn.com.easyerp.core.approve.FlowConditionDetail;
import cn.com.easyerp.core.approve.FlowEvent;
import cn.com.easyerp.core.approve.FlowLine;
import cn.com.easyerp.core.authGroup.AuthDataGroup;
import cn.com.easyerp.core.authGroup.AuthGroup;
import cn.com.easyerp.core.data.SystemParameter;
import cn.com.easyerp.core.ftp.FtpService;
import cn.com.easyerp.core.ftp.SftpService;
import cn.com.easyerp.core.mail.MailServer;
import cn.com.easyerp.core.widget.menu.MenuModel;
import cn.com.easyerp.core.widget.menu.PageModel;

@SuppressWarnings({ "rawtypes" })
public class DxCache {
    Map<String, TableDescribe> tableDescCache;
    Map<String, Map<String, I18nDescribe>> dictCache;
    Map<String, I18nDescribe> dictNameI18NCache;
    Map<String, I18nDescribe> tableI18nCache;
    Map<String, I18nDescribe> columnI18nCache;
    Map<String, I18nDescribe> msgI18nCache;
    Map<String, I18nDescribe> groupI18nCache;
    Map<String, I18nDescribe> reportI18nCache;
    Map<String, I18nDescribe> menuI18nCache;
    Map<String, I18nDescribe> batchI18nCache;
    Map<String, I18nDescribe> actionI18nCache;
    Map<String, I18nDescribe> tableShortcutI18nCache;
    Map<String, I18nDescribe> tableTabI18nCache;
    Map<String, I18nDescribe> authGroupI18nCache;
    SystemParameter systemParam;
    Map<String, Map<String, Object>> bizParam;
    Map<String, List<OrderByDescribe>> orderBy;
    Map<String, Map> data = new HashMap<>();
    Map<String, PageModel> pages;
    List<MenuModel> menu;
    Map<String, MenuModel> menuMap;
    List<MenuModel> shortcut;
    Map<String, OperationDescribe> operations;
    Map<String, BatchDescribe> batches;
    Map<String, TableShortcutDescribe> tableShortcuts;
    Map<String, Map<String, FlowBlock>> flowBlocks;
    Map<String, List<FlowBlock>> tableFlowBlock;
    Map<String, List<FlowLine>> tableFlowLines;
    Map<String, List<FlowConditionDetail>> tableFlowConditionDetails;
    Map<String, ComplexColumnDescribe> complexColumns;
    Map<AuthConfigDescribe.Target, Map<String, AuthConfigDescribe>> authConfigs;
    Map<String, List<CalenderEventConfigDescribe>> calendarEventConfigs;
    MailServer mailServer;
    FtpService ftpServer;
    SftpService sftpServer;
    Map<String, TableViewStyle> tableViewStyle;
    Map<String, List<ActionEventDescribe>> actionEvent;
    Map<String, ActionEventDescribe> actionEventByEventId = new HashMap<>();
    Map<String, FlowEvent> flowEventMap = new HashMap<>();
    Map<String, TriggerDescribe> triggers;
    List<AuthGroup> menuAuth;
    Map<String, List<AuthGroup>> optionAuth;
    Map<String, List<AuthGroup>> columnAuth;
    Map<String, AuthDataGroup> authDataGroup;
    Map<String, UrlInterfaceDescribe> urlInterface;
    String domainLanguage = "cn";

    String decryptKey;
    Map<String, Map<String, Object>> dashboards;
    Map<String, Map<String, List<AuthGroup>>> approveBlockUsers;
    public Map<String, SqlMap> sqlMap;
    public List<String> loadCacheErrorMsg = new ArrayList<>();
}
