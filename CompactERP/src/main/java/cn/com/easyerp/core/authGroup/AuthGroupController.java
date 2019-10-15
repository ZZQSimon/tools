package cn.com.easyerp.core.authGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.DeployTool.dao.TableDeployDao;
import cn.com.easyerp.auth.AuthService;
import cn.com.easyerp.core.approve.ApproveRequestModel;
import cn.com.easyerp.core.cache.CacheService;
import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.I18nDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.dao.ApproveDao;
import cn.com.easyerp.core.dao.AuthGroupDao;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.core.widget.menu.MenuModel;
import cn.com.easyerp.framework.common.ActionResult;

@Controller
@RequestMapping({ "/authGroup" })
public class AuthGroupController extends FormViewControllerBase {
    @Autowired
    private CacheService cacheService;
    @Autowired
    private AuthGroupDao authGroupDao;

    @RequestMapping({ "/authGroup.view" })
    public ModelAndView authGroup(@RequestBody AuthGroupRequestModel request) {
        AuthGroupModel form = new AuthGroupModel(request.getParent());

        List<MenuGroup> menuGroup = this.authGroupDao.selectMenuName();
        form.setMenuGroup(menuGroup);

        List<Table> listtable = this.authGroupDao.selectTable();
        for (Table selectTable : listtable) {
            I18nDescribe tableI18n = this.cacheService.getTableI18n(selectTable.getId());
            String i18nString = this.dataService.i18nString(tableI18n);
            if (tableI18n != null) {
                if (i18nString == null) {
                    selectTable.setName("");
                    continue;
                }
                selectTable.setName(i18nString);
            }
        }
        form.setTable(listtable);
        return buildModelAndView(form);
    }

    @Autowired
    private ApproveDao approveDao;
    @Autowired
    private TableDeployDao tableDeployDao;
    @Autowired
    private DataService dataService;

    @ResponseBody
    @RequestMapping({ "/selectMember.do" })
    public ActionResult selectMember(@RequestBody AuthGroupRequestModel request) {
        AuthGroupModel form = new AuthGroupModel(request.getParent());
        for (AuthGroup authGroup : request.getAuthGroup()) {
            if (authGroup.getType() == 1) {

                List<MenuModel> menu = this.authGroupDao.selectAllMenu();

                List<MenuModel> leafMenu = this.authGroupDao.selectAllLeafMenu();
                Map<String, MenuModel> leafMenuMap = new HashMap<String, MenuModel>();
                for (MenuModel leafMenuModel : leafMenu) {
                    leafMenuMap.put(leafMenuModel.getId(), leafMenuModel);
                }
                form.setMenu(menu);

                List<MenuGroup> menuId = this.authGroupDao.selectMenu(authGroup.getMenu());
                List<MenuTree> list = new ArrayList<MenuTree>();
                MenuTree mobileMenu = new MenuTree();
                mobileMenu.setParent("#");
                mobileMenu.setId("mobileMenu");
                mobileMenu.setText("�ֻ��˵�");
                list.add(mobileMenu);
                MenuTree PCMenu = new MenuTree();
                PCMenu.setParent("#");
                PCMenu.setId("PCMenu");
                PCMenu.setText("PC�˵�");
                list.add(PCMenu);
                changeMenu(menuId, menu, list, leafMenuMap);
                form.setMenuTree(list);

                List<AuthGroup> authGroupMember = this.authGroupDao.selectMember(authGroup);
                form.setAuthGroupMember(authGroupMember);
                continue;
            }
            List<AuthGroup> authGroupMember = this.authGroupDao.selectMember(authGroup);
            form.setAuthGroupMember(authGroupMember);

            List<TableAction> checkboxAll = this.authGroupDao.selectAllCheckBox(authGroup.getTable());
            for (TableAction tableAction : checkboxAll) {
                I18nDescribe tableI18n = this.cacheService.getMsgI18n(tableAction.getAction_name_international());
                String i18nString = this.dataService.i18nString(tableI18n);
                if (tableI18n != null) {
                    if (i18nString == null) {
                        tableAction.setOperate_name("");
                        continue;
                    }
                    tableAction.setOperate_name(i18nString);
                }
            }

            if (this.authGroupDao.selectImportUpdate(authGroup) != null
                    && this.authGroupDao.selectImportUpdate(authGroup).intValue() != 0) {
                TableAction tableAction_import = new TableAction();
                tableAction_import.setAction_id("import-update");
                tableAction_import.setAction_name_international("import-update");
                String name = this.dataService.i18nString(this.cacheService.getMsgI18n("import-update"));
                tableAction_import.setOperate_name(name);
                tableAction_import.setSystem_type("import-update");
                checkboxAll.add(tableAction_import);
            }
            if (this.authGroupDao.selectImportInsert(authGroup) != null
                    && this.authGroupDao.selectImportInsert(authGroup).intValue() != 0) {
                TableAction tableAction_import = new TableAction();
                tableAction_import.setAction_id("import-insert");
                tableAction_import.setAction_name_international("import-insert");
                String name = this.dataService.i18nString(this.cacheService.getMsgI18n("import-insert"));
                tableAction_import.setOperate_name(name);
                tableAction_import.setSystem_type("import-insert");
                checkboxAll.add(tableAction_import);
            }
            TableAction tableAction_export_selected = new TableAction();
            tableAction_export_selected.setAction_id("export-selected");
            tableAction_export_selected.setAction_name_international("export-selected");
            String name = this.dataService.i18nString(this.cacheService.getMsgI18n("export-selected"));
            tableAction_export_selected.setOperate_name(name);
            tableAction_export_selected.setSystem_type("export-selected");
            checkboxAll.add(tableAction_export_selected);

            TableAction tableAction_export_all = new TableAction();
            tableAction_export_all.setAction_id("exportall");
            tableAction_export_all.setAction_name_international("exportall");
            String names = this.dataService.i18nString(this.cacheService.getMsgI18n("exportall"));
            tableAction_export_all.setOperate_name(names);
            tableAction_export_all.setSystem_type("exportall");
            checkboxAll.add(tableAction_export_all);

            form.setCheckboxAll(checkboxAll);

            Map<String, List<AuthDataGroupDetail>> authDataGroupMap = new HashMap<String, List<AuthDataGroupDetail>>();
            if (authGroup.getType() == 2) {
                for (int i = 0; i < authGroupMember.size(); i++) {
                    if (!"".equals(((AuthGroup) authGroupMember.get(i)).getData_group_id())
                            && ((AuthGroup) authGroupMember.get(i)).getData_group_id() != null) {
                        List<AuthDataGroupDetail> authDataGroup = this.authGroupDao
                                .selectAuthDataGroup(((AuthGroup) authGroupMember.get(i)).getData_group_id());
                        authDataGroupMap.put(((AuthGroup) authGroupMember.get(i)).getData_group_id(), authDataGroup);
                    }
                }
            }
            form.setAuthDataGroupMap(authDataGroupMap);
        }

        return new ActionResult(true, form);
    }

    private void changeMenu(List<MenuGroup> menuId, List<MenuModel> menu, List<MenuTree> list,
            Map<String, MenuModel> leafMenuMap) {
        for (MenuModel menuModel : menu) {
            MenuTree menuTree = new MenuTree();
            for (MenuGroup menuGroup2 : menuId) {
                if (menuGroup2.getMenu_id().equals(menuModel.getId()) && !"".equals(leafMenuMap.get(menuModel.getId()))
                        && leafMenuMap.get(menuModel.getId()) != null) {
                    leafMenuMap.get(menuModel.getId());
                    menuTree.setState(new State(true));

                    break;
                }
            }
            menuTree.setId(menuModel.getId());
            I18nDescribe menuI18n = this.cacheService.getMsgI18n(menuModel.getInternational_id().toLowerCase());
            if (menuI18n != null) {
                switch (AuthService.getCurrentUser().getLanguage_id()) {
                case "cn":
                    menuTree.setText(menuI18n.getCn());
                    break;
                case "en":
                    menuTree.setText(menuI18n.getEn());
                    break;
                case "jp":
                    menuTree.setText(menuI18n.getJp());
                    break;
                case "other1":
                    menuTree.setText(menuI18n.getOther1());
                    break;
                case "other2":
                    menuTree.setText(menuI18n.getOther2());
                    break;
                }
            }
            if (menuModel.getParent_id() == null || menuModel.getParent_id().equals("")) {
                if (menuModel.getIs_mobile_menu() == 1) {
                    menuTree.setParent("mobileMenu");
                } else {
                    menuTree.setParent("PCMenu");
                }
            } else {
                menuTree.setParent(menuModel.getParent_id());
            }
            list.add(menuTree);
        }
    }

    @ResponseBody
    @RequestMapping({ "/selectColumn.do" })
    public ActionResult selectColumn(@RequestBody AuthGroupRequestModel request) {
        AuthGroupModel form = new AuthGroupModel(request.getParent());

        List<MenuTree> columnTree = new ArrayList<MenuTree>();
        List<Table> listtable = this.authGroupDao.selectTableByColumn();
        for (Table table : listtable) {
            TableDescribe tableDesc = this.cacheService.getTableDesc(table.getId());
            MenuTree menuTree = new MenuTree();
            menuTree.setParent("#");
            menuTree.setId(table.getId());
            I18nDescribe tableI18n = this.cacheService.getTableI18n(table.getId());
            if (tableI18n != null) {
                switch (AuthService.getCurrentUser().getLanguage_id()) {
                case "cn":
                    menuTree.setText(tableI18n.getCn());
                    break;
                case "en":
                    menuTree.setText(tableI18n.getEn());
                    break;
                case "jp":
                    menuTree.setText(tableI18n.getJp());
                    break;
                case "other1":
                    menuTree.setText(tableI18n.getOther1());
                    break;
                case "other2":
                    menuTree.setText(tableI18n.getOther2());
                    break;
                }

            }
            columnTree.add(menuTree);
            List<ColumnDescribe> columns = tableDesc.getColumns();

            for (ColumnDescribe columnDescribe : columns) {
                if (columnDescribe.getIs_auth() == 1) {
                    MenuTree menuTree2 = new MenuTree();
                    menuTree2.setParent(table.getId());
                    menuTree2.setId(table.getId() + "%" + columnDescribe.getColumn_name());
                    I18nDescribe columnI18n = this.cacheService
                            .getColumnI18n(table.getId() + "." + columnDescribe.getColumn_name());
                    if (columnI18n != null) {
                        switch (AuthService.getCurrentUser().getLanguage_id()) {
                        case "cn":
                            menuTree2.setText(columnI18n.getCn());
                            break;
                        case "en":
                            menuTree2.setText(columnI18n.getEn());
                            break;
                        case "jp":
                            menuTree2.setText(columnI18n.getJp());
                            break;
                        case "other1":
                            menuTree2.setText(columnI18n.getOther1());
                            break;
                        case "other2":
                            menuTree2.setText(columnI18n.getOther2());
                            break;
                        }

                    }
                    columnTree.add(menuTree2);
                }
            }
        }

        form.setColumnTree(columnTree);
        return new ActionResult(true, form);
    }

    @ResponseBody
    @RequestMapping({ "/addMenuGroup.do" })
    public ActionResult addMenuGroup(@RequestBody AuthGroupRequestModel request) {
        String result = "";
        List<String> menuId = new ArrayList<String>();
        menuId.add("0");
        for (AuthGroup authGroup : request.getAuthGroup()) {
            if (this.authGroupDao.addMenuGroup(authGroup.getMenu(), menuId) > 0) {
                result = "����ɹ�";
                continue;
            }
            result = "����ʧ��";
        }

        return new ActionResult(true, result);
    }

    @ResponseBody
    @RequestMapping({ "/deleteMenuGroup.do" })
    public ActionResult deleteMenuGroup(@RequestBody AuthGroupRequestModel request) {
        this.authGroupDao.deleteMenuGroup(request.getMenuGroupId());
        AuthGroup authGroup = new AuthGroup();
        authGroup.setType(1);
        authGroup.setMenu(request.getMenuGroupId());
        authGroup.setTable("");
        authGroup.setColumn("");
        this.authGroupDao.deleteAuthGroup(authGroup);
        return new ActionResult(true);
    }

    @ResponseBody
    @RequestMapping({ "/updateMenuGroup.do" })
    public ActionResult updateMenuGroup(@RequestBody AuthGroupRequestModel request) {
        this.authGroupDao.updateMenuGroup(request.getMenuGroupId(), request.getUpMenuGroupId());
        AuthGroup authGroup = new AuthGroup();
        authGroup.setType(1);
        authGroup.setMenu(request.getMenuGroupId());
        authGroup.setTable("");
        authGroup.setColumn("");
        this.authGroupDao.updateAuthGroup(authGroup, request.getUpMenuGroupId());
        return new ActionResult(true);
    }

    @ResponseBody
    @RequestMapping({ "/addAuthGroup.do" })
    public ActionResult addAuthGroup(@RequestBody AuthGroupRequestModel request) {
        List<AuthGroup> newAuthGroup = new ArrayList<AuthGroup>();
        String menuGroupId = "";
        for (AuthGroup authGroup : request.getAuthGroup()) {

            if (authGroup.getTemplate() != "" && authGroup.getTemplate() != null) {
                this.approveDao.deleteTemplate(authGroup);
                if (authGroup.getType() == 2
                        && ((authGroup.getDepartment() != null && !"".equals(authGroup.getDepartment())
                                && authGroup.getRole() != null && !"".equals(authGroup.getRole()))
                                || (authGroup.getUser() != null && !"".equals(authGroup.getUser())))) {
                    newAuthGroup.add(authGroup);
                }
            } else {

                if (authGroup.getType() == 3) {
                    String[] cloumn = authGroup.getColumn().split("%");
                    authGroup.setColumn(cloumn[1]);
                }
                this.authGroupDao.deleteAuthGroup(authGroup);
                if (authGroup.getType() == 1) {
                    if (!menuGroupId.equals(authGroup.getMenu()) && authGroup.getMenu() != null) {
                        menuGroupId = authGroup.getMenu();
                        this.authGroupDao.deleteMenuGroup(authGroup.getMenu());
                        if (request.getMenu_Id().size() > 0) {
                            this.authGroupDao.addMenuGroup(menuGroupId, request.getMenu_Id());
                        } else {
                            List<String> menuId = new ArrayList<String>();
                            menuId.add("0");
                            this.authGroupDao.addMenuGroup(menuGroupId, menuId);
                        }
                    }
                }
                if (authGroup.getType() == 2) {
                    this.authGroupDao.deleteAuthDataGroup(authGroup);
                }
            }
            if (authGroup.getType() != 2 && ((authGroup.getDepartment() != null && !"".equals(authGroup.getDepartment())
                    && authGroup.getRole() != null && !"".equals(authGroup.getRole()))
                    || (authGroup.getUser() != null && !"".equals(authGroup.getUser())))) {
                newAuthGroup.add(authGroup);
            }
        }

        if (newAuthGroup.size() > 0) {
            this.authGroupDao.addAuthGroup(newAuthGroup);
        }
        if (request.getAuthGroupMap() != null) {
            for (Map.Entry<String, List<AuthGroup>> authGroupList : request.getAuthGroupMap().entrySet()) {
                if (((List) authGroupList.getValue()).size() > 0) {
                    for (AuthGroup authDataGroup : authGroupList.getValue()) {
                        authDataGroup.setData_group_id((String) authGroupList.getKey());
                    }
                    this.authGroupDao.addAuthGroup((List) authGroupList.getValue());
                }
                if (!"".equals(authGroupList.getKey())) {
                    boolean flag = true;
                    for (AuthDataGroupDetail authDataGroup : request.getAuthDataGroupMap()
                            .get(authGroupList.getKey())) {
                        if (flag) {
                            if ("".equals(authDataGroup.getInternational_id())
                                    || authDataGroup.getInternational_id() == null) {
                                String[] uuids = UUID.randomUUID().toString().split("-");
                                String uuid = uuids[0].toLowerCase() + uuids[1].toLowerCase();
                                authDataGroup.setInternational_id(uuid);
                                authDataGroup.getI18n().setInternational_id(uuid);
                                this.tableDeployDao.addI18N(authDataGroup.getI18n());
                                this.authGroupDao.addAuthDataGroup(authDataGroup);
                                flag = false;
                            } else {
                                this.authGroupDao.addAuthDataGroup(authDataGroup);
                                flag = false;
                            }
                        }
                        this.authGroupDao.addAuthDataGroupDetail(authDataGroup);
                    }
                }
            }
        }
        return new ActionResult(true, "����ɹ�");
    }

    @ResponseBody
    @RequestMapping({ "/saveTemplate.do" })
    public ActionResult saveTemplate(@RequestBody ApproveRequestModel request) {
        String result = "����ɹ�";
        if (!"".equals(Integer.valueOf(((AuthGroup) request.getAuthGroup().get(0)).getEditOrdetele()))
                && ((AuthGroup) request.getAuthGroup().get(0)).getEditOrdetele() != 0) {
            if (((AuthGroup) request.getAuthGroup().get(0)).getEditOrdetele() == 1) {
                this.approveDao.updateTemplate((AuthGroup) request.getAuthGroup().get(0));
            } else if (((AuthGroup) request.getAuthGroup().get(0)).getEditOrdetele() == 2) {
                this.approveDao.deleteTemplate((AuthGroup) request.getAuthGroup().get(0));
            }
        } else {
            this.approveDao.deleteTemplate((AuthGroup) request.getAuthGroup().get(0));
            for (AuthGroup authGroup : request.getAuthGroup()) {
                if (this.approveDao.addAuthGroup(authGroup) > 0) {
                    result = "����ɹ�";
                    continue;
                }
                result = "����ʧ��";
            }
        }

        return new ActionResult(true, result);
    }

    @ResponseBody
    @RequestMapping({ "/lookAllTemplate.do" })
    public ActionResult lookAllTemplate(@RequestBody AuthGroupRequestModel request) {
        AuthGroupModel form = new AuthGroupModel(request.getParent());
        for (AuthGroup authGroup : request.getAuthGroup()) {
            List<AuthGroup> authGroupMember = this.authGroupDao.selectTemplate(authGroup.getType());

            List<TableAction> checkboxAll = this.authGroupDao.selectAllCheckBox(authGroup.getTable());
            for (TableAction tableAction : checkboxAll) {
                I18nDescribe tableI18n = this.cacheService.getMsgI18n(tableAction.getAction_name_international());
                String i18nString = this.dataService.i18nString(tableI18n);
                if (tableI18n != null) {
                    if (i18nString == null) {
                        tableAction.setOperate_name("");
                        continue;
                    }
                    tableAction.setOperate_name(i18nString);
                }
            }

            if (this.authGroupDao.selectImportUpdate(authGroup) != null
                    && this.authGroupDao.selectImportUpdate(authGroup).intValue() != 0) {
                TableAction tableAction_import = new TableAction();
                tableAction_import.setAction_id("import-update");
                tableAction_import.setAction_name_international("import-update");
                String name = this.dataService.i18nString(this.cacheService.getMsgI18n("import-update"));
                tableAction_import.setOperate_name(name);
                tableAction_import.setSystem_type("import-update");
                checkboxAll.add(tableAction_import);
            }
            if (this.authGroupDao.selectImportInsert(authGroup) != null
                    && this.authGroupDao.selectImportInsert(authGroup).intValue() != 0) {
                TableAction tableAction_import = new TableAction();
                tableAction_import.setAction_id("import-insert");
                tableAction_import.setAction_name_international("import-insert");
                String name = this.dataService.i18nString(this.cacheService.getMsgI18n("import-insert"));
                tableAction_import.setOperate_name(name);
                tableAction_import.setSystem_type("import-insert");
                checkboxAll.add(tableAction_import);
            }
            TableAction tableAction_export_selected = new TableAction();
            tableAction_export_selected.setAction_id("export-selected");
            tableAction_export_selected.setAction_name_international("export-selected");
            String name = this.dataService.i18nString(this.cacheService.getMsgI18n("export-selected"));
            tableAction_export_selected.setOperate_name(name);
            tableAction_export_selected.setSystem_type("export-selected");
            checkboxAll.add(tableAction_export_selected);

            TableAction tableAction_export_all = new TableAction();
            tableAction_export_all.setAction_id("exportall");
            tableAction_export_all.setAction_name_international("exportall");
            String names = this.dataService.i18nString(this.cacheService.getMsgI18n("exportall"));
            tableAction_export_all.setOperate_name(names);
            tableAction_export_all.setSystem_type("exportall");
            checkboxAll.add(tableAction_export_all);
            form.setCheckboxAll(checkboxAll);
            form.setAuthGroupMember(authGroupMember);
        }
        return new ActionResult(true, form);
    }
}
