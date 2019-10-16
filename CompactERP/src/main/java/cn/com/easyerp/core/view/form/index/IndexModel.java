package cn.com.easyerp.core.view.form.index;

import java.util.List;
import java.util.Map;

import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.auth.AuthService;
import cn.com.easyerp.core.approve.MyApproveAndWaitMeApprove;
import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.framework.enums.ActionType;

@Widget("index")
public class IndexModel extends FormModelBase {
    private List<Map<String, Object>> personDesk;
    private List<Map<String, Object>> commonDesk;
    private Map<String, DesktopListModel> listMap;
    private int heightY;
    private List<MyApproveAndWaitMeApprove> myApprove;
    private List<MyApproveAndWaitMeApprove> waitMeApprove;
    private int myApproveSize;
    private int waitMeApproveSize;
    private int approveFlushTime = 300000;

    private Map<String, List<AuthDetails.UserMenu>> mobileMenu;
    private String sysLogo;
    private String sysCompanyName;
    private String interior_logo;
    private Map<String, Object> approveParam;
    private Map<String, Object> emailDetail;

    public String getInterior_logo() {
        return this.interior_logo;
    }

    public void setInterior_logo(String interior_logo) {
        this.interior_logo = interior_logo;
    }

    public String getSysLogo() {
        return this.sysLogo;
    }

    public void setSysLogo(String sysLogo) {
        this.sysLogo = sysLogo;
    }

    public String getSysCompanyName() {
        return this.sysCompanyName;
    }

    public void setSysCompanyName(String sysCompanyName) {
        this.sysCompanyName = sysCompanyName;
    }

    public IndexModel(List<Map<String, Object>> commonDesk, List<Map<String, Object>> personDesk, int heightY) {
        super(ActionType.view, null);
        this.commonDesk = commonDesk;
        this.personDesk = personDesk;
        this.heightY = heightY;
    }

    public IndexModel() {
        super(ActionType.view, null);
    }

    public int getHeightY() {
        return this.heightY;
    }

    public List<Map<String, Object>> getCommonDesk() {
        return this.commonDesk;
    }

    public List<Map<String, Object>> getPersonDesk() {
        return this.personDesk;
    }

    public AuthDetails getUser() {
        return AuthService.getCurrentUser();
    }

    public String getTitle() {
        return "Home";
    }

    public Map<String, DesktopListModel> getListMap() {
        return this.listMap;
    }

    public void setListMap(Map<String, DesktopListModel> listMap) {
        this.listMap = listMap;
    }

    public List<MyApproveAndWaitMeApprove> getMyApprove() {
        return this.myApprove;
    }

    public void setMyApprove(List<MyApproveAndWaitMeApprove> myApprove) {
        this.myApprove = myApprove;
    }

    public List<MyApproveAndWaitMeApprove> getWaitMeApprove() {
        return this.waitMeApprove;
    }

    public void setWaitMeApprove(List<MyApproveAndWaitMeApprove> waitMeApprove) {
        this.waitMeApprove = waitMeApprove;
    }

    public int getMyApproveSize() {
        return this.myApproveSize;
    }

    public void setMyApproveSize(int myApproveSize) {
        this.myApproveSize = myApproveSize;
    }

    public int getWaitMeApproveSize() {
        return this.waitMeApproveSize;
    }

    public void setWaitMeApproveSize(int waitMeApproveSize) {
        this.waitMeApproveSize = waitMeApproveSize;
    }

    public Map<String, List<AuthDetails.UserMenu>> getMobileMenu() {
        return this.mobileMenu;
    }

    public void setMobileMenu(Map<String, List<AuthDetails.UserMenu>> mobileMenu) {
        this.mobileMenu = mobileMenu;
    }

    public Map<String, Object> getApproveParam() {
        return this.approveParam;
    }

    public void setApproveParam(Map<String, Object> approveParam) {
        this.approveParam = approveParam;
    }

    public Map<String, Object> getEmailDetail() {
        return this.emailDetail;
    }

    public void setEmailDetail(Map<String, Object> emailDetail) {
        this.emailDetail = emailDetail;
    }

    public int getApproveFlushTime() {
        return this.approveFlushTime;
    }

    public void setApproveFlushTime(int approveFlushTime) {
        this.approveFlushTime = approveFlushTime;
    }
}
