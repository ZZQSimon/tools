package cn.com.easyerp.auth;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.AuthenticationException;
import javax.naming.CommunicationException;
import javax.naming.directory.InitialDirContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.addCompany.AddCompanyService;
import cn.com.easyerp.auth.weixin.CompanyWXData;
import cn.com.easyerp.auth.weixin.WeiXinAPI;
import cn.com.easyerp.core.ViewService;
import cn.com.easyerp.core.cache.CacheService;
import cn.com.easyerp.core.cache.DictionaryDescribe;
import cn.com.easyerp.core.cache.TriggerDescribe;
import cn.com.easyerp.core.dao.AuthDao;
import cn.com.easyerp.core.dao.SubjectDao;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.logger.Loggable;
import cn.com.easyerp.core.master.DxRoutingDataSource;
import cn.com.easyerp.core.view.FormController;
import cn.com.easyerp.core.view.form.detail.DetailFormModel;
import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.core.widget.WidgetModelBase;
import cn.com.easyerp.core.widget.grid.RecordModel;
import cn.com.easyerp.email.RegisterValidateService;
import cn.com.easyerp.email.ServiceException;
import cn.com.easyerp.framework.common.ActionResult;
import cn.com.easyerp.framework.common.Common;
import cn.com.easyerp.framework.exception.ApplicationException;
import cn.com.easyerp.verification.CCPRestSmsSDK;
import cn.com.easyerp.weixin.WeChatService;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Controller
@RequestMapping({ "/auth" })
public class AuthController extends FormController {
    @Autowired
    private AuthDao authDao;
    @Autowired
    private DataService dataService;
    @Autowired
    private AuthService authService;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private SubjectDao subjectDao;
    @Autowired
    private DxRoutingDataSource dxRoutingDataSource;
    @Autowired
    private RegisterValidateService service;
    @Autowired
    private WeiXinAPI weiXinAPI;
    @Autowired
    private AddCompanyService addCompanyService;
    @Autowired
    @Qualifier("serviceNumber")
    private WeChatService weChatService;
    @Autowired
    private PwdService pwdService;
    // @Autowired
    // private Index index;
    @Loggable
    private Logger logger;
    private static final Base64.Decoder decoder = Base64.getDecoder();
    private static final String TMP_ROOT = "/tmp";
    private static final String inputPassword = "inputPassword";
    private static final String inputPhoneNum = "inputPhoneNum";
    private static final String input_security_code = "input_security_code";
    private static final String forget_the_password = "forget_the_password";
    private static final String Remember_password = "Remember_password";
    private static final String free_registration = "free_registration";
    private static final String Login = "Login";
    private static final String password_Login = "password_Login";
    private static final String companyName = "BusinessProcessManagementPlatform";
    private static final String PleaseSelectTheNameOfTheCompany = "PleaseSelectTheNameOfTheCompany";
    private static final String login_back = "login_back";
    private static final String Can_not_see = "Can_not_see";
    private static final String p_parameter_default_language = "p_parameter_default_language";

    @RequestMapping({ "/login.view" })
    public ModelAndView login(@RequestParam(value = "error", required = false) String error, String language,
            HttpSession session, HttpServletRequest request) {
        if (language == null) {
            language = (String) session.getAttribute("login_language");
        }
        session.setAttribute("login_language", language);
        ModelAndView mv = new ModelAndView("login");
        String authorization = request.getHeader("Authorization");
        String sapId = "";
        if (authorization != null) {
            String authorizationUser = authorization.split(" ")[1];
            try {
                String userMsg = new String(decoder.decode(authorizationUser), "utf-8");
                System.out.println("sapid----------------------------------" + userMsg);
                this.logger.error(new Date() + "sapid----------------------------------" + userMsg);
                // String userAccont = userMsg.split(":")[0];
                sapId = userMsg.split(":")[1];
                if (sapId != null) {
                    session.setAttribute("sapId", sapId);
                    return mv;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        session.removeAttribute("sapId");
        if (error != null) {
            Exception e = (Exception) RequestContextHolder.getRequestAttributes()
                    .getAttribute("SPRING_SECURITY_LAST_EXCEPTION", 1);

            if (e != null) {
                mv.addObject("error", e);
                session.setAttribute("error", "用户名或密码错误！");
            }
        }
        if (error == null) {
            session.removeAttribute("error");
        }
        return mv;
    }

    private AuthGroupFormModel buildAuthForm(boolean notMenu) {
        List<AuthControlDescribe> controls = this.authDao.selectAuthEditCtrl(notMenu);
        AuthGroupFormModel form = notMenu ? new AuthGroupFormModel(controls) : new AuthMenuFormModel(controls);
        form.setRoles(this.authDao.selectOptions("m_role"));
        form.setDepts(this.authDao.selectOptions("m_department"));
        List<AuthOption> users = this.authDao.selectOptions("m_user");
        users.add(new AuthOption("_SELF", this.dataService.getMessageText("Self", new Object[0])));
        form.setUsers(users);
        return form;
    }

    @RequestMapping({ "/edit.view" })
    public ModelAndView edit() {
        return buildModelAndView(buildAuthForm(true));
    }

    @Transactional
    @ResponseBody
    @RequestMapping({ "/group.do" })
    public ActionResult group(@RequestBody AuthEditRequestModel request) {
        List<AuthControlDescribe> controls = request.getControls();
        for (AuthControlDescribe control : controls)
            this.authDao.updateAuthControl(control.getId(), Common.toJson(control.getEntries()));
        this.authService.reload();
        return Common.ActionOk;
    }

    @RequestMapping({ "/menu.view" })
    public ModelAndView menu() {
        AuthMenuFormModel form = (AuthMenuFormModel) buildAuthForm(false);
        form.setMenu(this.cacheService.getMenu());
        form.setShortcut(this.cacheService.getShortcut());
        form.setConfigs(this.authDao.selectAuthMenuConfig());
        return buildModelAndView(form);
    }

    @Transactional
    @ResponseBody
    @RequestMapping({ "/menu.do" })
    public ActionResult menu(@RequestBody AuthEditRequestModel request) {
        List<AuthControlDescribe> controls = request.getControls();
        this.authDao.deleteAuthMenuConfig(controls);

        Map<String, List<String>> configMap = request.getConfigMap();
        for (Map.Entry<String, List<String>> entry : configMap.entrySet()) {
            String controlId = (String) entry.getKey();
            for (String menuId : entry.getValue()) {
                this.authDao.insertAuthMenuConfig(controlId, menuId);
            }
        }
        return group(request);
    }

    @ResponseBody
    @RequestMapping({ "/opAuth.do" })
    public ActionResult opAuth(@RequestBody AuthRequestModel request, AuthDetails user) {
        List<Map<String, Boolean>> noAuth = new ArrayList<Map<String, Boolean>>();
        Map<String, Object> noAuthRows = request.getNoAuthRows();
        if (noAuthRows == null || noAuthRows.size() == 0)
            return new ActionResult(true);
        String apiId = noAuthRows.get("id").toString();
        TriggerDescribe triggerDescribe = (TriggerDescribe) this.cacheService.getTriggers().get(apiId);
        String table_id = triggerDescribe.getTable_id();
        List<Map<String, Object>> owners = (List) noAuthRows.get("owners");
        for (Map<String, Object> rows : owners) {
            Object rowId = rows.get("rowId");
            if (rowId == null) {
                Map<String, Boolean> row = new HashMap<String, Boolean>();
                row.put(rows.get("rowId").toString(), Boolean.valueOf(false));
                noAuth.add(row);
                continue;
            }
            Map<String, FieldModelBase> fieldMap = null;
            WidgetModelBase widgetModelBase = ViewService.fetchWidgetModel(rowId.toString());
            if (widgetModelBase instanceof RecordModel) {
                fieldMap = ((RecordModel) widgetModelBase).getFieldMap();
            } else if (widgetModelBase instanceof DetailFormModel) {
                fieldMap = ((DetailFormModel) widgetModelBase).getFieldMap();
            }
            Object owner = null;
            if (fieldMap != null && fieldMap.containsKey("owner")) {
                owner = ((FieldModelBase) fieldMap.get("owner")).getValue();
            } else if (fieldMap != null) {
                owner = ((FieldModelBase) fieldMap.get("cre_user")).getValue();
            }
            if (!this.authService.optionAuth(user, table_id, apiId, owner)) {
                Map<String, Boolean> row = new HashMap<String, Boolean>();
                row.put(rows.get("rowId").toString(), Boolean.valueOf(false));
                noAuth.add(row);
            }
        }
        return new ActionResult(true, noAuth);
    }

    @ResponseBody
    @RequestMapping({ "/entries.do" })
    public ActionResult auth(@RequestBody AuthRequestModel request, AuthDetails user) {
        Map<String, Boolean> ret = new HashMap<String, Boolean>(request.getEntries().size());
        for (AuthRequestModel.AuthEntry entry : request.getEntries()) {
            boolean passed = true;
            ret.put(entry.getId(), Boolean.valueOf(passed));
            // for (String owner : entry.getOwners()) {
            // }
        }
        return new ActionResult(true, ret);
    }

    @ResponseBody
    @RequestMapping({ "/logoutElse.do" })
    public ActionResult auth(AuthDetails user, HttpSession session) {
        this.authService.logoutElse(user, session);
        user.setLogged(false);
        return Common.ActionOk;
    }

    @RequestMapping({ "/loginVerification.do" })
    public void loginVerification(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0L);
        response.setContentType("image/jpeg");
        String verifyCode = Verification.generateVerifyCode(4);
        session.removeAttribute("verifyCode");
        session.setAttribute("verifyCode", verifyCode);
        int w = 100, h = 40;
        Verification.outputImage(w, h, response.getOutputStream(), verifyCode);
    }

    @ResponseBody
    @RequestMapping({ "/login2.view" })
    public ModelAndView login2(@RequestParam(value = "error", required = false) String error, String subjectID,
            String backgroundID, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        ModelAndView mv = new ModelAndView("login2");
        String companyLanguage = (String) session.getAttribute("login_language");
        String SysCompanyName = "";
        String SysLogo = "SysLogo";
        String interior_logo = "interior_logo";
        if (null != session.getAttribute("error")) {
            mv.addObject("error", session.getAttribute("error"));
        }
        Object domain = session.getAttribute("domain");

        domain = "PRDHRIDB";
        Object emailDetail = session.getAttribute("emailDetail");
        if (emailDetail != null && !"".equals(emailDetail.toString())) {
            Map<String, Object> param = null;
            param = Common.paseEventParam(this.dataService.getDecode(emailDetail.toString()));
            domain = param.get("domain");
            mv.addObject("isEmailDetail", "emailDetail");
            mv.addObject("emailDetail", param);
            session.removeAttribute("emailDetail");
            session.setAttribute("emailDetailParam", param);
        }
        if (domain != null) {
            this.dxRoutingDataSource.setDomainKey(domain.toString());
            if ("".equals(companyLanguage) || companyLanguage == null) {
                companyLanguage = this.authDao.selectCompanyDefaultLanguage();
            }
            mv.addObject("companyLanguage", companyLanguage);
            String backgroud = "backgroud";
            mv.addObject("backgroud", backgroud);
            mv.addObject("logo", SysLogo);
            String name = this.authDao.selectLoginCss("companyName");
            SysCompanyName = this.authDao.selectLoginInternational(companyLanguage, name);
            if (SysCompanyName == null) {
                mv.addObject("companyName", "");
            } else {
                mv.addObject("companyName", SysCompanyName);
            }
            mv.addObject("freeRegistration", "");
        } else {
            this.dxRoutingDataSource.setDomainKey(DxRoutingDataSource.DX_DEFAULT_DATASOURCE);
            if ("".equals(companyLanguage) || companyLanguage == null) {
                companyLanguage = this.authDao.selectCompanyLanguage(DxRoutingDataSource.DX_DEFAULT_DATASOURCE);
            }
            SysCompanyName = this.authDao.selectLoginInternational(companyLanguage, companyName);
            mv.addObject("companyName", SysCompanyName);
            mv.addObject("backgroud", "");
            mv.addObject("logo", SysLogo);
            mv.addObject("freeRegistration", this.authDao.selectLoginInternational(companyLanguage, free_registration));
        }
        session.setAttribute("SysCompanyName", SysCompanyName);
        session.setAttribute("SysLogo", SysLogo);
        session.setAttribute("interior_logo", interior_logo);
        mv.addObject(inputPassword, this.authDao.selectLoginInternational(companyLanguage, inputPassword));
        mv.addObject(inputPhoneNum, this.authDao.selectLoginInternational(companyLanguage, inputPhoneNum));
        mv.addObject("inputSecurityCode", this.authDao.selectLoginInternational(companyLanguage, input_security_code));
        mv.addObject("forgetThePassword", this.authDao.selectLoginInternational(companyLanguage, forget_the_password));
        mv.addObject("rememberPassword", this.authDao.selectLoginInternational(companyLanguage, Remember_password));
        mv.addObject("login", this.authDao.selectLoginInternational(companyLanguage, Login));
        mv.addObject("passwordLogin", this.authDao.selectLoginInternational(companyLanguage, password_Login));
        mv.addObject(PleaseSelectTheNameOfTheCompany,
                this.authDao.selectLoginInternational(companyLanguage, PleaseSelectTheNameOfTheCompany));
        mv.addObject(login_back, this.authDao.selectLoginInternational(companyLanguage, login_back));
        mv.addObject(Can_not_see, this.authDao.selectLoginInternational(companyLanguage, Can_not_see));

        List<DictionaryDescribe> languageSelect = this.authDao.selectLanguage(companyLanguage);
        mv.addObject("languageSelect", languageSelect);
        this.authDao.selectLoginInternational(companyLanguage, p_parameter_default_language);
        mv.addObject("defaultLanguage",
                this.authDao.selectLoginInternational(companyLanguage, p_parameter_default_language));
        String login_language = (String) session.getAttribute("login_language");
        if ("".equals(login_language) || login_language == null) {
            mv.addObject("login_language", "");
        } else {
            mv.addObject("login_language", companyLanguage);
        }
        if (error != null) {
            Exception e = (Exception) RequestContextHolder.getRequestAttributes()
                    .getAttribute("SPRING_SECURITY_LAST_EXCEPTION", 1);
            if (e != null)
                mv.addObject("error", e);
        }
        if (subjectID.equals("undefined") || subjectID.equals("")) {
            subjectID = "yellow";
            List<Map<String, Object>> list = this.subjectDao.selectSubject(subjectID);
            mv.addObject("list", list);
            mv.addObject("subjectID", subjectID);
        } else {
            List<Map<String, Object>> list = this.subjectDao.selectSubject(subjectID);
            mv.addObject("list", list);
            mv.addObject("subjectID", subjectID);
        }
        mv.addObject("backgroundID", backgroundID);
        this.dxRoutingDataSource.setDomainKey(DxRoutingDataSource.DX_DEFAULT_DATASOURCE);
        return mv;
    }

    @RequestMapping({ "/loginImgDownload.do" })
    public void loginImgDownload(String path, String id, HttpServletResponse response, HttpSession session) {
        try {
            String filePath = "";
            if (path != null) {
                Object domain = session.getAttribute("domain");
                if (domain == null) {
                    domain = DxRoutingDataSource.DX_DEFAULT_DATASOURCE;
                }
                this.dxRoutingDataSource.setDomainKey(domain.toString());
                if (path.equals("SysLogo")) {
                    filePath = this.authDao.selectLoginCss("logo");
                }
                if (path.equals("backgroud")) {
                    filePath = this.authDao.selectLoginCss("backgroud");
                }
                if (path.equals("interior_logo")) {
                    filePath = this.authDao.selectLoginCss("interior_logo");
                }
                /*
                 * switch (path) { case "SysLogo": filePath =
                 * this.authDao.selectLoginCss("logo"); break; case "backgroud": filePath =
                 * this.authDao.selectLoginCss("backgroud"); break; case "interior_logo":
                 * filePath = this.authDao.selectLoginCss("interior_logo"); break; }
                 */
            }
            File file = new File(filePath);
            String filename = file.getName();
            // String ext = filename.substring(filename.lastIndexOf(".") + 1).toUpperCase();
            InputStream fis = new BufferedInputStream(new FileInputStream(filePath));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            response.reset();
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes()));
            response.addHeader("Content-Length", "" + file.length());
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            toClient.write(buffer);
            toClient.flush();
            toClient.close();
        } catch (Exception ex) {

        }
    }

    public String sendPhoneCord(String telephone) {
        HashMap<String, Object> result = null;
        CCPRestSmsSDK restAPI = new CCPRestSmsSDK();
        restAPI.init("app.cloopen.com", "8883");
        restAPI.setAccount("aaf98f8947b461870147be822bcb1221", "29e60cd2e80948aa8dc28ad8c8d27883");
        restAPI.setAppId("8a48b55147b467900147bf0b7c3412b2");
        int verificationCoad_int = (int) ((Math.random() * 9.0D + 1.0D) * 100000.0D);
        String verificationCoad = String.valueOf(verificationCoad_int);
        result = restAPI.sendTemplateSMS(telephone, "1", new String[] { verificationCoad, "5" });
        System.out.println("SDKTestGetSubAccounts result=" + result);
        if ("000000".equals(result.get("statusCode"))) {
            HashMap<String, Object> data = (HashMap) result.get("data");
            Set<String> keySet = data.keySet();
            for (String key : keySet) {
                Object object = data.get(key);
                System.out.println(key + " = " + object);
            }
            return verificationCoad;
        }
        System.out.println("错误码=" + result.get("statusCode") + " 错误信息= " + result.get("statusMsg"));
        return null;
    }

    @RequestMapping({ "/changSubject.do" })
    public ModelAndView changSubject(@RequestParam("select-pic") MultipartFile file, String subjectID) {
        ModelAndView mv = new ModelAndView("login2");
        String tmppath = this.dataService.getSystemParam().getUpload_root() + TMP_ROOT + "/";
        tmppath = tmppath.replace("/", "\\");
        String imgpath = tmppath + (new Date()).getTime() + file.getOriginalFilename();
        File newFile = new File(imgpath);
        try {
            file.transferTo(newFile);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (subjectID.equals("undefined") || subjectID.equals("")) {
            subjectID = "yellow";
            List<Map<String, Object>> list = this.subjectDao.selectSubject(subjectID);
            mv.addObject("list", list);
            mv.addObject("subjectID", subjectID);
        } else {
            List<Map<String, Object>> list = this.subjectDao.selectSubject(subjectID);
            mv.addObject("list", list);
            mv.addObject("subjectID", subjectID);
        }
        mv.addObject("subjectID", subjectID);
        int num = this.subjectDao.addBackground(imgpath);
        if (num > 0) {
            mv.addObject("backgroundID", imgpath);
        }
        return mv;
    }

    @RequestMapping({ "/findpassword.view" })
    public ModelAndView findpassword() {
        return new ModelAndView("findpassword");
    }

    @ResponseBody
    @RequestMapping({ "/sendValidCode.do" })
    public ActionResult sendValidCode(String type, String address, HttpSession session) {
        int cnt = this.authDao.selectSysUserCnt(address, false,
                this.cacheService.getDecryptKey(DxRoutingDataSource.DX_DEFAULT_DATASOURCE));
        if (cnt > 1) {
            return new ActionResult(false, "存在多个用户！");
        }
        AuthDetails user = this.authDao.selectSysUser(address, false,
                this.cacheService.getDecryptKey(DxRoutingDataSource.DX_DEFAULT_DATASOURCE));
        if (user == null) {
            return new ActionResult(false, "没有该用户！");
        }
        return this.pwdService.sendValidCode(type, user, session) ? new ActionResult(true)
                : new ActionResult(false, "验证码发送失败，稍后请重试！");
    }

    @ResponseBody
    @RequestMapping({ "/checkVaildCode.do" })
    public ActionResult checkVaildCode(String code, HttpSession session) {
        String session_code = (String) session.getAttribute("login_valid_code");
        if (Common.isBlank(code)) {
            return new ActionResult(false, "请输入验证码！");
        }
        return code.equals(session_code) ? new ActionResult(true) : new ActionResult(false, "验证码错误!");
    }

    @ResponseBody
    @RequestMapping({ "/updatePassword.do" })
    public ActionResult updatePassword(String password, HttpSession session) {
        int pwd_strong_level = this.cacheService.getSystemParam().getPwd_strong_level();
        if (!this.dataService.checkPassword(password, pwd_strong_level)) {
            return new ActionResult(false, this.dataService.getMessageText("PASS_NEW_INVALID", new Object[0]));
        }
        boolean flag = this.pwdService.updatePassword(session, password);
        return flag ? new ActionResult(false, "更改密码失败！") : new ActionResult(true);
    }

    @ResponseBody
    @RequestMapping({ "/checkUserName.do" })
    public ActionResult checkUserName(String username, String password, String verifyCode, HttpServletRequest request,
            HttpServletResponse response, HttpSession session) {
        AuthDetails authDetails = null;
        List<Map<String, Object>> userCompanyAdidas = new ArrayList<Map<String, Object>>();
        Map<String, Object> adidas = new HashMap<String, Object>();
        adidas.put("user_id", (authDetails == null) ? username : authDetails.getId());
        adidas.put("master_id", DxRoutingDataSource.DX_DEFAULT_DATASOURCE);
        adidas.put("company_id", "");
        adidas.put("app_id", "");
        userCompanyAdidas.add(adidas);
        return new ActionResult(true, userCompanyAdidas);
    }

    @ResponseBody
    @RequestMapping({ "/sapLogin.do" })
    public Map<String, Object> sapLogin(String sapId, HttpSession session) {
        String domain = this.dxRoutingDataSource.getDomainKey();
        if (domain == null) {
            domain = DxRoutingDataSource.DX_DEFAULT_DATASOURCE;
        }
        this.dxRoutingDataSource.setDomainKey(domain);
        List<AuthDetails> users = this.authDao.selectUserBySapid(sapId, this.cacheService.getDecryptKey(domain));
        if (users.size() == 1) {
            Map<String, Object> userMap = new HashMap<String, Object>();
            userMap.put("username", ((AuthDetails) users.get(0)).getId());
            userMap.put("_pwd", ((AuthDetails) users.get(0)).get_password());
            userMap.put("domain", domain);
            String language_id = ((AuthDetails) users.get(0)).getLanguage_id();
            if (language_id == null || "".equals(language_id)) {
                language_id = "cn";
            }
            userMap.put("languageId", language_id);
            return userMap;
        }
        session.removeAttribute("sapId");
        return null;
    }

    @SuppressWarnings("unused")
    private boolean AdLogin(String userName, String password) {
        Map<String, Object> ldapParam = this.authDao.ldapParam();
        String host = (ldapParam.get("ldap_ip") == null) ? "" : ldapParam.get("ldap_ip").toString();
        String domain = (ldapParam.get("ladp_domain") == null) ? "" : ldapParam.get("ladp_domain").toString();
        String port = (ldapParam.get("ldap_port") == null) ? "" : ldapParam.get("ldap_port").toString();
        String url = new String("ldap://" + host + ":" + port);
        String user = userName + domain;
        Hashtable env = new Hashtable();
        InitialDirContext ctx = null;
        env.put("java.naming.security.authentication", "simple");
        env.put("java.naming.security.principal", user);
        env.put("java.naming.security.credentials", password);
        env.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
        env.put("java.naming.provider.url", url);
        try {
            ctx = new InitialDirContext(env);
            System.out.println("身份验证成功!aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            return true;
        } catch (AuthenticationException e) {
            System.out.println("身份验证失败!aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            return false;
        } catch (CommunicationException e) {
            System.out.println("AD域连接失败!aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            return false;
        } catch (Exception e) {
            System.out.println("身份验证未知异常!aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            return false;
        } finally {
            if (null != ctx) {
                try {
                    ctx.close();
                    ctx = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @RequestMapping({ "/register.view" })
    public ModelAndView register(String isleader, String companyId, HttpSession session, HttpServletRequest request) {
        session.setAttribute("isleader_reg", isleader);
        session.setAttribute("regCompanyId", companyId);
        ModelAndView mv = new ModelAndView("register");
        if (isleader != null && !"".equals(isleader)) {
            mv.addObject("isleader", isleader);
        } else {
            mv.addObject("isleader", "0");
        }
        return mv;
    }

    @RequestMapping({ "/phoneCode.view" })
    public ModelAndView phoneCode(String telephone, HttpSession session) {
        ModelAndView mv = new ModelAndView("register");
        String verificationCoad = sendPhoneCord(telephone);
        session.setAttribute("phoneCode_reg", verificationCoad);
        session.setAttribute("telephone_reg", telephone);
        return mv;
    }

    @ResponseBody
    @RequestMapping({ "/phoneReg_emp.do" })
    public ActionResult phoneReg_emp(String regphone, String phoneCode, HttpSession session) {
        if (phoneCode.equals(session.getAttribute("phoneCode_reg"))) {
            if (!regphone.equals(session.getAttribute("telephone_reg"))) {
                return new ActionResult(false, "3");
            }
            session.setAttribute("telephone_reg", regphone);
            session.setAttribute("phoneCode_reg_emp", phoneCode);
            List<String> companyNameList = this.authDao.getUserCompanyName(regphone);
            String cname = this.authDao.getCompanyName(session.getAttribute("regCompanyId").toString());
            String companyName = "";
            for (String string : companyNameList) {
                companyName = companyName + string + ",";
            }
            if (!"".equals(companyName)) {
                if (companyName.indexOf(cname) != -1) {
                    return new ActionResult(false, "2");
                }
                AuthDetails authDetail = this.authDao.selectSysUser(regphone, false,
                        this.cacheService.getDecryptKey(DxRoutingDataSource.DX_DEFAULT_DATASOURCE));
                this.dxRoutingDataSource.setDomainKey(session.getAttribute("regCompanyId").toString());
                this.authDao.saveCompanyUser(authDetail, new Date());
                this.dxRoutingDataSource.setDomainKey(DxRoutingDataSource.DX_DEFAULT_DATASOURCE);
                this.authDao.saveUserCompany(regphone, session.getAttribute("regCompanyId").toString());
                session.setAttribute("userName_reg", authDetail.getName());
                return new ActionResult(true, companyName);
            }

            return new ActionResult(true);
        }

        return new ActionResult(false, "1");
    }

    @ResponseBody
    @RequestMapping({ "/phoneReg.do" })
    public ActionResult phoneReg(String regphone, String password, String phoneCode, String regname,
            HttpSession session) {
        AuthDetails authDetail = new AuthDetails();
        if ("".equals(regphone) || regphone == null) {
            regphone = session.getAttribute("telephone_reg").toString();
        }
        if ("".equals(phoneCode) || phoneCode == null) {
            phoneCode = session.getAttribute("phoneCode_reg_emp").toString();
        }
        authDetail.setMobile(regphone);
        authDetail.setName(regname);
        authDetail.set_password(password);
        if (phoneCode.equals(session.getAttribute("phoneCode_reg"))) {
            if (!regphone.equals(session.getAttribute("telephone_reg"))) {
                return new ActionResult(false, "3");
            }
            String isleader = (String) session.getAttribute("isleader_reg");
            if (isleader == null || "".equals(isleader)) {
                if (this.authDao.selectSysUser(regphone, false,
                        this.cacheService.getDecryptKey(DxRoutingDataSource.DX_DEFAULT_DATASOURCE)) != null) {
                    return new ActionResult(false, "2");
                }
                authDetail.setRole_id("admin");
                this.authDao.saveUser(authDetail, new Date());
                session.setAttribute("adminRegInfo", authDetail);
                session.removeAttribute("phoneCode_reg");
            } else if (session.getAttribute("regCompanyId") != null
                    && !"".equals(session.getAttribute("regCompanyId"))) {
                if (this.authDao.findUserCompany(regphone, session.getAttribute("regCompanyId").toString()) > 0) {
                    return new ActionResult(false, "2");
                }
                this.authDao.saveUser(authDetail, new Date());
                this.authDao.saveUserCompany(regphone, session.getAttribute("regCompanyId").toString());
                this.dxRoutingDataSource.setDomainKey(session.getAttribute("regCompanyId").toString());
                this.authDao.saveCompanyUser(authDetail, new Date());
                this.dxRoutingDataSource.setDomainKey(DxRoutingDataSource.DX_DEFAULT_DATASOURCE);
                session.removeAttribute("phoneCode_reg");
                session.setAttribute("userName_reg", regname);
            }

            session.setAttribute("telephone_reg", regphone);
        } else {
            return new ActionResult(false, "1");
        }
        return new ActionResult(true);
    }

    @ResponseBody
    @RequestMapping({ "/emailReg.view" })
    public ActionResult emailReg(String email, String password, String validateCode, HttpServletRequest request,
            HttpServletResponse response) throws ParseException {
        AuthDetails authDetail = new AuthDetails();
        authDetail.set_password(password);
        authDetail.setEmail(email);
        if (this.authDao.selectSysUser(email, false,
                this.cacheService.getDecryptKey(DxRoutingDataSource.DX_DEFAULT_DATASOURCE)) != null) {
            return new ActionResult(false, "此邮箱已注册！");
        }
        String action = request.getParameter("action");
        System.out.println("-----r----" + action);
        if ("register".equals(action)) {

            if (!this.service.processregister(email, request)) {
                return new ActionResult(false, "邮件发送失败！");
            }
            this.authDao.saveUser(authDetail, new Date());

        } else if ("activate".equals(action)) {
            try {
                this.service.processActivate(email, validateCode);
            } catch (ServiceException e) {
                request.setAttribute("message", e.getMessage());
                return new ActionResult(false, "邮件发送失败！");
            }
        }
        return new ActionResult(true, email);
    }

    @Transactional
    @ResponseBody
    @RequestMapping({ "/saveCompany.do" })
    public ActionResult saveCompany(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String companyId = request.getParameter("companyId");
        session.setAttribute("companyId_save", companyId);
        String companyName = request.getParameter("companyName");
        String companyName_abbreviation = request.getParameter("companyName_abbreviation");

        if (this.authDao.selecctSysMaster(companyId).size() > 0) {
            return new ActionResult(false, "账号已存在，请重新输入！");
        }
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("logo");
        String path = "";
        if (null != file && !file.isEmpty()) {
            String filename = file.getOriginalFilename();
            String filepath = this.authDao.getFilepath();
            path = (new Date()).getTime() + filename;
            File files = new File(filepath + "\\" + path);
            try {
                file.transferTo(files);
            } catch (IllegalStateException e) {

                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            }
        }
        SysMasterDetails corp = new SysMasterDetails();
        corp.setName(companyId);
        corp.setUrl("jdbc:mysql://rm-uf62k1944wz99gu98o.mysql.rds.aliyuncs.com:3306/" + companyId
                + "?zeroDateTimeBehavior=convertToNull");
        corp.setCompanyName(companyName);
        corp.setCompanyName_abbreviation(companyName_abbreviation);
        corp.setLogo(path);
        this.authDao.saveCompany(corp);
        this.authDao.saveUserCompany(session.getAttribute("telephone_reg").toString(), companyId);
        this.addCompanyService.createCompany(companyId);
        AuthDetails authDetail = (AuthDetails) session.getAttribute("adminRegInfo");
        this.authDao.saveCompanyUser_admin(authDetail, new Date(), companyId);
        this.dxRoutingDataSource.setDomainKey(DxRoutingDataSource.DX_DEFAULT_DATASOURCE);
        session.setAttribute("regCompanyId", companyId);
        return new ActionResult(true);
    }

    @ResponseBody
    @RequestMapping({ "/joinCompany.do" })
    public ActionResult joinCompany(String email, String id, String sex, String telePhone, String entryDate,
            HttpSession session, HttpServletRequest request) {
        String domainName = this.authDao.getDomainName();
        String companyId = session.getAttribute("regCompanyId").toString();
        String regUrl = domainName + request.getContextPath() + "/auth/register.view?isleader=1&companyId=" + companyId;
        Date date = null;
        try {
            if (entryDate != null && !"".equals(entryDate)) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                date = sdf.parse(entryDate);
            }
            if (session.getAttribute("telephone_reg") != null && !"".equals(session.getAttribute("telephone_reg"))) {
                if (session.getAttribute("regCompanyId") != null && !"".equals(session.getAttribute("regCompanyId"))) {
                    this.dxRoutingDataSource.setDomainKey(session.getAttribute("regCompanyId").toString());

                    this.authDao.saveCompanyUserInfo(session.getAttribute("telephone_reg").toString(), email, id, sex,
                            telePhone, date, new Date());
                    this.dxRoutingDataSource.setDomainKey(DxRoutingDataSource.DX_DEFAULT_DATASOURCE);

                    CompanyWXData companyWXData = this.authDao
                            .getCompanyWXData(session.getAttribute("regCompanyId").toString());
                    if (companyWXData != null && companyWXData.getCorpId() != null
                            && !"".equals(companyWXData.getAgentId()) && companyWXData.getSecret() != null
                            && !"".equals(companyWXData.getSecret()) && companyWXData.getAgentId() != null
                            && !"".equals(companyWXData.getAgentId())) {

                        this.authDao.updateSysMaster(session.getAttribute("telephone_reg").toString(),
                                session.getAttribute("regCompanyId").toString(), companyWXData.getCorpId());

                        this.weiXinAPI.createWeiXinUser(companyWXData, session.getAttribute("telephone_reg").toString(),
                                session.getAttribute("userName_reg").toString(), sex, telePhone, email);

                        this.weiXinAPI.sendMessages("huangting", session.getAttribute("userName_reg") + "已注册成功！",
                                companyWXData.getCorpId(), companyWXData.getSecret(), companyWXData.getAgentId());
                    }
                    return new ActionResult(true, regUrl);
                }
                return new ActionResult(true, regUrl);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new ActionResult(false, "加入失败");
    }

    @ResponseBody
    @RequestMapping({ "/register_mobile.view" })
    public ModelAndView register_mobile(String isleader, String companyId, HttpSession session,
            HttpServletRequest request) {
        session.setAttribute("isleader_reg", isleader);
        session.setAttribute("regCompanyId", companyId);
        return new ModelAndView("reg_mobile");
    }

    @ResponseBody
    @RequestMapping({ "/mobile.view" })
    public ModelAndView mobileLogin(HttpServletRequest request, HttpServletResponse response, HttpSession session,
            String code, String state, String domain) {
        String authorization = request.getHeader("Authorization");
        System.out.println(authorization + "------aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        String sapId = "";
        if (authorization != null) {
            String authorizationUser = authorization.split(" ")[1];
            try {
                String user = new String(decoder.decode(authorizationUser), "utf-8");
                System.out.println(user + "------user");
                String username = user.split(":")[0];
                sapId = user.split(":")[1];
                System.out.println(username + "------username");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ModelAndView mv = new ModelAndView("mobileLogin");
        String companyLanguage = "";
        String openid = "";
        if (Common.isBlank(domain) && Common.isBlank(state))
            throw new ApplicationException(
                    this.dataService.getMessageText("Please provide account set", new Object[0]));
        domain = Common.isBlank(domain) ? state : domain;
        AuthDetails user = null;
        if (!Common.isBlank(state)) {
            this.dxRoutingDataSource.setDomainKey(domain);
            openid = this.weChatService.getOpenid(code);
        }
        if (!Common.isBlank(openid)) {
            this.dxRoutingDataSource.setDomainKey(domain);

            List<AuthDetails> users = this.authDao.selectUserBySapid(sapId, this.cacheService.getDecryptKey(domain));
            if (users.size() > 1)
                throw new ApplicationException("用户绑定不唯一！");
            if (users.size() == 1) {
                user = (AuthDetails) users.get(0);
                bindOpenid(user, openid);
            } else {
                throw new ApplicationException("用户不存在！");
            }
        }
        if (domain != null) {
            companyLanguage = this.authDao.selectCompanyDefaultLanguage();
            // String backgroud = this.authDao.selectLoginCss("backgroud");
            String name = this.authDao.selectLoginCss("companyName");
            String companyName = this.authDao.selectLoginInternational(companyLanguage, name);
            if (companyName == null) {
                mv.addObject("companyName", "");
            } else {
                mv.addObject("companyName", companyName);
            }
            mv.addObject("freeRegistration", "");
        }
        mv.addObject(inputPassword, this.authDao.selectLoginInternational(companyLanguage, inputPassword));
        mv.addObject(inputPhoneNum, this.authDao.selectLoginInternational(companyLanguage, inputPhoneNum));
        mv.addObject("inputSecurityCode", this.authDao.selectLoginInternational(companyLanguage, input_security_code));
        mv.addObject("forgetThePassword", this.authDao.selectLoginInternational(companyLanguage, forget_the_password));
        mv.addObject("rememberPassword", this.authDao.selectLoginInternational(companyLanguage, Remember_password));
        mv.addObject("login", this.authDao.selectLoginInternational(companyLanguage, Login));
        mv.addObject("passwordLogin", this.authDao.selectLoginInternational(companyLanguage, password_Login));
        mv.addObject(PleaseSelectTheNameOfTheCompany,
                this.authDao.selectLoginInternational(companyLanguage, PleaseSelectTheNameOfTheCompany));
        mv.addObject(login_back, this.authDao.selectLoginInternational(companyLanguage, login_back));
        mv.addObject(Can_not_see, this.authDao.selectLoginInternational(companyLanguage, Can_not_see));

        String username = "";
        String password = "";
        boolean ifCheck = (user != null);
        List<String> companys = new ArrayList<String>();
        if (ifCheck) {
            username = user.getId() + "@" + domain + "@1@1";
            password = user.get_password();
            companys = this.authDao.userCompanys(username);
        }
        if (!Common.isBlank(domain))
            companys.add(domain);
        mv.addObject("username", username);
        mv.addObject("password", password);
        mv.addObject("ifCheck", Boolean.valueOf(ifCheck));
        mv.addObject("openid", openid);
        mv.addObject("companys", companys);
        return mv;
    }

    private void bindOpenid(AuthDetails user, String openid) {
        if (user != null && !Common.isBlank(openid)) {
            String userPhone = this.weChatService.getUserPhone(openid);
            System.out.println("userPhone:" + userPhone);
            System.out.println("sys__userPhone:" + user.getMobile());
            if (userPhone != null && userPhone.equals(user.getMobile())) {
                this.authDao.updateOpenid(user.getId(), openid, this.weChatService.getUser(openid));
            }
        }
    }

    @ResponseBody
    @RequestMapping({ "/approveLogin.view" })
    public ModelAndView approveLogin(HttpServletRequest request, HttpServletResponse response, HttpSession session,
            String openid, String table, String dataid, String domain) {
        ModelAndView mv = new ModelAndView("mobileLogin");
        String companyLanguage = "";
        this.dxRoutingDataSource.setDomainKey(domain);
        List<AuthDetails> users = this.authDao.selectUserByOpenid(openid, this.cacheService.getDecryptKey(domain));
        AuthDetails user = (users.size() > 0 && users != null) ? (user = (AuthDetails) users.get(0)) : null;
        Map<String, Object> approveParam = new HashMap<String, Object>();
        approveParam.put("table", table);
        approveParam.put("dataid", dataid);
        session.setAttribute("approveParam", approveParam);

        if (domain != null) {
            companyLanguage = this.authDao.selectCompanyDefaultLanguage();
            // String backgroud = this.authDao.selectLoginCss("backgroud");
            String name = this.authDao.selectLoginCss("companyName");
            String companyName = this.authDao.selectLoginInternational(companyLanguage, name);
            if (companyName == null) {
                mv.addObject("companyName", "");
            } else {
                mv.addObject("companyName", companyName);
            }
            mv.addObject("freeRegistration", "");
        }
        mv.addObject(inputPassword, this.authDao.selectLoginInternational(companyLanguage, inputPassword));
        mv.addObject(inputPhoneNum, this.authDao.selectLoginInternational(companyLanguage, inputPhoneNum));
        mv.addObject("inputSecurityCode", this.authDao.selectLoginInternational(companyLanguage, input_security_code));
        mv.addObject("forgetThePassword", this.authDao.selectLoginInternational(companyLanguage, forget_the_password));
        mv.addObject("rememberPassword", this.authDao.selectLoginInternational(companyLanguage, Remember_password));
        mv.addObject("login", this.authDao.selectLoginInternational(companyLanguage, Login));
        mv.addObject("passwordLogin", this.authDao.selectLoginInternational(companyLanguage, password_Login));
        mv.addObject(PleaseSelectTheNameOfTheCompany,
                this.authDao.selectLoginInternational(companyLanguage, PleaseSelectTheNameOfTheCompany));
        mv.addObject(login_back, this.authDao.selectLoginInternational(companyLanguage, login_back));
        mv.addObject(Can_not_see, this.authDao.selectLoginInternational(companyLanguage, Can_not_see));

        String username = "";
        String password = "";
        boolean ifCheck = (user != null);
        if (ifCheck) {
            username = user.getId() + "@" + domain + "@1@1";
            password = this.dataService.getDecode(user.get_password());
        }
        mv.addObject("username", username);
        mv.addObject("password", password);
        mv.addObject("ifCheck", Boolean.valueOf(ifCheck));
        mv.addObject("openid", openid);
        mv.addObject("domain", domain);
        // this.dxRoutingDataSource;
        this.dxRoutingDataSource.setDomainKey(DxRoutingDataSource.DX_DEFAULT_DATASOURCE);
        return mv;
    }

    @ResponseBody
    @RequestMapping({ "/checkMobileLogin.do" })
    public ActionResult checkMobileLogin(HttpSession session, String susername, String password, String openid,
            String domain) {
        Map<String, Object> data = new HashMap<String, Object>();
        this.dxRoutingDataSource.setDomainKey(domain);
        System.out.println("openid111111:" + openid);
        AuthDetails user = this.authDao.checkLoginByPassword(susername, password,
                this.cacheService.getDecryptKey(domain));
        if (user != null) {
            System.out.println("openid22222:" + openid);
            if (!Common.isBlank(openid)) {
                String userPhone = this.weChatService.getUserPhone(openid);
                System.out.println("userPhone:" + userPhone);
                System.out.println("sys__userPhone:" + user.getMobile());
                if (userPhone != null && userPhone.equals(user.getMobile())) {
                    this.authDao.updateOpenid(susername, openid, this.weChatService.getUser(openid));
                }
            }
            data.put("ret", Boolean.valueOf(true));
            data.put("username", user.getId() + "@" + domain + "@1@1");
            data.put("password", password);
        } else {
            data.put("ret", Boolean.valueOf(false));
            data.put("msg", "用户名或密码错误请重试！");
        }
        return new ActionResult(true, data);
    }
}