/**
 * Created by Administrator on 2017/9/4.
 */
registerInit('menuDeploy', function (form) {
    var getInternational;
    var nodeForAdd;
    var $tableTree = form.get().find('.deploy-left-menu-tree');
    initLeftTree();
    searchMenu();
    var editMenuMap = [];//编辑菜单集合。
    var addMenuMap = []; //新增菜单集合。
    var deleteMenuMap = [];  //删除菜单集合
    var internationalControl = [];  //国际化控件对象
    var initMenuList = [];//初始化菜单的数据
    var treeRef = $tableTree.jstree(true), sel = treeRef.get_selected();

    form.get().find(".deploy-left-menu-tree").niceScroll({cursorborder: "", cursorcolor: "#aeb2b7"});

    //初始化左侧树
    function ajaxLoadGroup(form, callBack) {
        postJson('/menuDeploy/initMenuDeploy.do', {}, function (result) {
            initMenuList = result;
            callBack(result);
        });
    }

    //初始化左侧树
    function initLeftTree() {
        $tableTree.jstree({
            'core': {
                "multiple": false,
                'data': null,
                'dblclick_toggle': true,          //tree的双击展开
                "check_callback": true        //
            },
            "plugins": ["search", "dnd"]
        });
    }

    //左侧输赋值函数
    function callback(result) {
        if (!isEmpty(result)) {
            if (isEmpty(result)) {
                $tableTree.jstree(true).settings.core.data = null;
                $tableTree.jstree(true).refresh();
            } else {
                $tableTree.jstree(true).settings.core.data = result;
                $tableTree.jstree(true).refresh();
            }
        }
    }

    //左侧search检索
    function searchMenu() {
        var to;
        var leftSearch = form.get().find('.retrievalMenu');
        leftSearch.keyup(function () {
            if (to) {
                clearTimeout(to);
            }
            to = setTimeout(function () {
                $tableTree.jstree(true).search(leftSearch.val());
            }, 250);
        });
        ajaxLoadGroup(form, callback);
    }

    //初始化国际化控件
    var interMenu = form.get().find(".interMenu");
    international(interMenu, "", "", "isShow");
    function international(divClass, internationalId, menuName, isShow) {
        $(divClass).internationalControl({
            internationalId: internationalId,
            menuName: menuName,
            isShow: isShow,
            onSubmit: function (data) {
                if (!isEmpty(internationalControl)) {
                    for (var i = 0; i < internationalControl.length; i++) {
                        if (data.international_id == internationalControl[i].international_id) {
                            internationalControl[i] = data;
                        }
                    }
                } else {
                    internationalControl.push(data);
                }
            },
            onSelect: function (data) {
                if (!isEmpty(internationalControl)) {
                    for (var i = 0; i < internationalControl.length; i++) {
                        if (data.international_id == internationalControl[i].international_id) {
                            internationalControl[i] = data;
                        }
                    }
                } else {
                    internationalControl.push(data);
                }
            }
        })
    }

    //菜单图片点击事件,判断菜单图片是否改变，有修改则保存到editMenuMap
    form.get().find(".menuIconDrop ul li a").on("click", function () {
        var icon = form.get().find(".deploy-right-menu-data span[name=icon]").attr("class");
        var menuimg = $(this).attr("class");
        var flag = true;
        if (!isEmpty(addMenuMap)) {
            for (var i = 0; i < addMenuMap.length; i++) {
                if (addMenuMap[i].id == nodeForAdd.id) {
                    addMenuMap[i].icon = menuimg;
                    flag = false;
                }
            }
        }
        if (flag) {
            nodeForAdd.icon = menuimg;
            nodeForAdd.updateType = "1";
        }
        form.get().find(".deploy-right-menu-data span[name=icon]").attr("class", menuimg);
    });
    //菜单url改变事件,判断菜单url是否改变，有修改则保存到editMenuMap
    form.get().find(".urlList").change(function () {
        var url = nodeForAdd.url;
        var param = nodeForAdd.param;
        var changeUrl = form.get().find(".urlList option:selected").val();
        var urlInterface = dx.urlInterface[changeUrl];
        if (isEmpty(changeUrl) || isEmpty(urlInterface.urlParam)) {
            form.get().find(".urlValueStyle").hide();
        } else {
            form.get().find(".urlValueStyle").show();
        }
        var changeParam = "";
        var page = dx.pages;
        for (var key in page) {
            if (page[key].url_id == changeUrl && page[key].id == nodeForAdd.page_id) {
                changeParam = page[key].param;
            }
        }
        var flag = true;
        if (!isEmpty(addMenuMap)) {
            for (var i = 0; i < addMenuMap.length; i++) {
                if (addMenuMap[i].id == nodeForAdd.id) {
                    form.get().find(".urlValue").val("");
                    addMenuMap[i].page_id = "";
                    addMenuMap[i].url = changeUrl;
                    addMenuMap[i].param = "";
                    flag = false;
                }
            }
        }
        if (flag) {
            form.get().find(".urlValue").val(changeParam);
            nodeForAdd.page_id = nodeForAdd.page_id;
            nodeForAdd.url = changeUrl;
            nodeForAdd.param = changeParam;
            nodeForAdd.updateType = "1";
        }
    });
    //树节点点击事件。点击后赋值。
    $tableTree.bind("changed.jstree", function (e, data) {
        if (!isEmpty(data) && !isEmpty(data.node)) {
            form.get().find(".empty-form").hide();
            form.get().find(".deploy-right-menu-main").show();
            form.get().find(".deploy-left-tree-delete").show();

            if (data.node.id == "mobileMenu" || data.node.id == "PCMenu") {
                form.get().find(".deploy-right-menu-data input,.deploy-right-menu-data select,.deploy-right-menu-data button").attr("disabled", "true");
                form.get().find(".deploy-right-menu-data .interMenu").html("");
                form.get().find(".deploy-right-menu-data span[name=icon]").attr("class", "");
                form.get().find(".urlValue").val('');
                form.get().find(".urlList option").remove();
                international(".interMenu", "", "", "isShow");
                treeRef = $tableTree.jstree(true);
                sel = treeRef.get_selected();
                nodeForAdd = [];
                return;
            }
            var editflag = true;
            var menu;
            var page = {};
            if (!isEmpty(nodeForAdd)) {
                //获取菜单点击前的菜单国际化控件返回的数据  判断国际化返回的数据，判断菜单国际化是否修改
                InterOnClick();
                //将修改状态为1的保存到editMenuMap
                if (nodeForAdd.updateType == "1") {
                    if (!isEmpty(editMenuMap)) {
                        for (var i = 0; i < editMenuMap.length; i++) {
                            //判断点击的菜单是否修改
                            if (editMenuMap[i].id == nodeForAdd.id) {
                                editMenuMap[i] = nodeForAdd;
                                break;
                            }
                            if (editMenuMap.length - 1 == i) {
                                editMenuMap.push(nodeForAdd);
                                break;
                            }
                        }
                    } else {
                        editMenuMap.push(nodeForAdd);
                    }
                }
            }
            treeRef = $tableTree.jstree(true), sel = treeRef.get_selected();
            //判断点击的菜单是不是新增的，将新增的数据给nodeForAdd,设置menu值
            if (!isEmpty(addMenuMap)) {
                for (var i = 0; i < addMenuMap.length; i++) {
                    if (addMenuMap[i].id == data.node.id) {
                        nodeForAdd = addMenuMap[i];
                        menu = addMenuMap[i];
                        page.id = addMenuMap[i].page_id;
                        page.url_id = addMenuMap[i].url;
                        page.param = addMenuMap[i].param;
                        editflag = false;
                    }
                }
            }
            //判断菜单点击前的菜单是否有修改，若修改则显示修改的数据
            if (!isEmpty(editMenuMap)) {
                for (var i = 0; i < editMenuMap.length; i++) {
                    //判断点击的菜单是否修改
                    if (editMenuMap[i].id == data.node.id) {
                        nodeForAdd = editMenuMap[i];
                        menu = editMenuMap[i];
                        page.id = editMenuMap[i].page_id;
                        page.url_id = editMenuMap[i].url;
                        page.param = editMenuMap[i].param;
                        editflag = false;
                    }
                }
            }
            //点击的菜单不是新增的，将点击的菜单数据保存到nodeForAdd
            if (editflag) {
                nodeForAdd = data.node.data;
                menu = data.node.data;
                if (!isEmpty(menu)) {
                    page = dx.pages[menu.page_id];
                }
            }
            var urlMap = dx.urlInterface;
            form.get().find(".deploy-right-menu-data input,.deploy-right-menu-data select,.deploy-right-menu-data button").removeAttr("disabled");
            form.get().find(".deploy-right-menu-data .interMenu").html("");
            form.get().find(".deploy-right-menu-data span[name=icon]").attr("class", "");
            form.get().find(".urlValue").val('');
            form.get().find(".urlList option").remove();
            //加载下拉所有url
            form.get().find(".urlList").append('<option value=""></option>');
            for (var key in urlMap) {
                if (isEmpty(urlMap[key].i18n)) {
                    form.get().find(".urlList").append('<option value="' + key + '">' + urlMap[key].name + '</option>');
                } else {
                    form.get().find(".urlList").append('<option value="' + key + '">' + urlMap[key].i18n[dx.user.language_id] + '</option>');
                }
            }
            //加载国际化控件和菜单图标
            if (menu == null) {
                international(".interMenu", "", "");
                form.get().find(".deploy-right-menu-data span[name=icon]").attr("class", "");
            } else {
                international(".interMenu", menu.international_id, menu[dx.user.language_id]);
                form.get().find(".deploy-right-menu-data span[name=icon]").attr("class", menu.icon);
            }
            //判断选择url
            if (!isEmpty(page)) {
                for (var key in urlMap) {
                    if (key == page.url_id && page.id == nodeForAdd.page_id) {
                        if (!isEmpty(nodeForAdd.updateType)) {
                            form.get().find(".urlValue").val(nodeForAdd.param);
                        } else {
                            form.get().find(".urlValue").val(page.param);
                            nodeForAdd.param = page.param;
                        }
                        form.get().find(".urlList option[value=" + key + "]").attr("selected", "true");
                        nodeForAdd.url = key;
                        var urlInterface = dx.urlInterface[key];
                        if (isEmpty(key) || isEmpty(urlInterface.urlParam)) {
                            form.get().find(".urlValueStyle").hide();
                        } else {
                            form.get().find(".urlValueStyle").show();
                        }
                        break;
                    } else {
                        form.get().find(".urlList option:first-of-type").attr("selected", "true");
                        form.get().find(".urlValueStyle").hide();
                    }
                }
            } else {
                form.get().find(".urlList option:first-of-type").attr("selected", "true");
                form.get().find(".urlValueStyle").hide();
            }
        }
    });
    //获取菜单点击前的菜单国际化控件返回的数据  判断国际化返回的数据，判断菜单国际化是否修改
    function InterOnClick() {
        if (!isEmpty(nodeForAdd)) {
            //获取菜单点击前的菜单国际化控件返回的数据
            getInternational = form.get().find(".interMenu").internationalControl(true).getData();
            //判断国际化返回的数据，判断菜单国际化是否修改
            if (!isEmpty(getInternational.international_id)) {
                for (var i = 0; i < sel.length; i++) {
                    var id = sel[i];
                    if (id == nodeForAdd.id) {
                        treeRef.set_text(id, getInternational[dx.user.language_id]);
                    }
                }
                if (!isEmpty(internationalControl)) {
                    for (var i = 0; i < internationalControl.length; i++) {
                        if (getInternational.international_id == internationalControl[i].international_id) {
                            getInternational = internationalControl[i];
                        }
                    }
                }
                if (nodeForAdd.updateType == "2") {
                    if (!isEmpty(addMenuMap)) {
                        for (var i = 0; i < addMenuMap.length; i++) {
                            if (addMenuMap[i].id == nodeForAdd.id) {
                                addMenuMap[i].international_id = getInternational.international_id;
                                addMenuMap[i].id_international = getInternational.international_id;
                                addMenuMap[i].cn = getInternational.cn;
                                addMenuMap[i].en = getInternational.en;
                                addMenuMap[i].international_id = getInternational.international_id;
                                addMenuMap[i].jp = getInternational.jp;
                                addMenuMap[i].other1 = getInternational.other1;
                                addMenuMap[i].other2 = getInternational.other2;
                            }
                        }
                    }
                } else {
                    nodeForAdd.international_id = getInternational.international_id;
                    nodeForAdd.id_international = getInternational.international_id;
                    nodeForAdd.cn = getInternational.cn;
                    nodeForAdd.en = getInternational.en;
                    nodeForAdd.international_id = getInternational.international_id;
                    nodeForAdd.jp = getInternational.jp;
                    nodeForAdd.other1 = getInternational.other1;
                    nodeForAdd.other2 = getInternational.other2;
                    nodeForAdd.updateType = "1";
                    if (nodeForAdd.updateType == "1") {
                        if (!isEmpty(editMenuMap)) {
                            for (var i = 0; i < editMenuMap.length; i++) {
                                //判断点击的菜单是否修改
                                if (editMenuMap[i].id == nodeForAdd.id) {
                                    editMenuMap[i] = nodeForAdd;
                                    break;
                                }
                                if (editMenuMap.length - 1 == i) {
                                    editMenuMap.push(nodeForAdd);
                                    break;
                                }
                            }
                        } else {
                            editMenuMap.push(nodeForAdd);
                        }
                    }
                }
            } else {
                for (var i = 0; i < sel.length; i++) {
                    var id = sel[i];
                    if (id == nodeForAdd.id) {
                        treeRef.set_text(id, getInternational.interValue);
                    }
                }

                if (nodeForAdd.updateType == "2") {
                    if (!isEmpty(addMenuMap)) {
                        for (var i = 0; i < addMenuMap.length; i++) {
                            if (addMenuMap[i].id == nodeForAdd.id) {
                                addMenuMap[i].international_id = "";
                                addMenuMap[i].id_international = "";
                                addMenuMap[i].cn = "";
                                addMenuMap[i].en = "";
                                addMenuMap[i].jp = "";
                                addMenuMap[i].other1 = "";
                                addMenuMap[i].other2 = "";
                                addMenuMap[i].updateType = "2";
                                addMenuMap[i][dx.user.language_id] = getInternational.interValue;
                                interflag = false;
                            }
                        }
                    }
                } else {
                    nodeForAdd.international_id = "";
                    nodeForAdd.id_international = "";
                    nodeForAdd.cn = "";
                    nodeForAdd.en = "";
                    nodeForAdd.jp = "";
                    nodeForAdd.other1 = "";
                    nodeForAdd.other2 = "";
                    nodeForAdd.updateType = "1";
                    nodeForAdd[dx.user.language_id] = getInternational.interValue;
                    if (nodeForAdd.updateType == "1") {
                        if (!isEmpty(editMenuMap)) {
                            for (var i = 0; i < editMenuMap.length; i++) {
                                //判断点击的菜单是否修改
                                if (editMenuMap[i].id == nodeForAdd.id) {
                                    editMenuMap[i] = nodeForAdd;
                                    break;
                                }
                                if (editMenuMap.length - 1 == i) {
                                    editMenuMap.push(nodeForAdd);
                                    break;
                                }
                            }
                        } else {
                            editMenuMap.push(nodeForAdd);
                        }
                    }
                }
            }
        }
        getInternational = {};
    }

    //加载参数输入控件
    form.get().find(".parameterInput").on("click", function () {
        var josnstr = form.get().find(".urlValue").val();
        var urlId = form.get().find(".urlList option:selected").val();
        var modalX = "44";
        var modalY = "44";
        form.get().find(".parameterInput").parameterInputControl(true).setData("onSubmit", function (data) {
            form.get().find(".urlValue").val(data);
            nodeForAdd.param = data;
            if (nodeForAdd.updateType != "2") {
                nodeForAdd.updateType = "1";
            }
        }, josnstr, urlId, modalX, modalY, "aa");

    });

    //左侧树新增节点
    form.get().find('.addMenuDeploy').on('click', function () {
        var par;
        var addMenuDeploy = {};
        var menuNode;
        if (isEmpty(nodeForAdd)) {
            alert("无法新增一级菜单！");
            return;
        } else {
            var node = $tableTree.jstree("select_node", nodeForAdd.id);
            if (node) {
                par = nodeForAdd.id;
                menuNode = $tableTree.jstree('get_node', par);
                addMenuDeploy.parent_id = nodeForAdd.id;
                addMenuDeploy.is_mobile_menu = nodeForAdd.is_mobile_menu;
            } else {
                par = nodeForAdd;
                addMenuDeploy.parent_id = nodeForAdd;
                addMenuDeploy.is_mobile_menu = form.get().find('input[name=addMenuDeployOneValue]').val();
            }
        }
        var node = form.get().find('input[name=addMenuDeployOneValue]').val();
        if (isEmpty(node)) {
            alert("请输入菜单名称！");
            return;
        }
        var pos = "last";
        var callback = function (node) {
            //新增成功更改新增表集合数据。
            addMenuDeploy.id = node.id;
            addMenuDeploy[dx.user.language_id] = node.text;
            addMenuDeploy.url = "";
            addMenuDeploy.param = "";
            addMenuDeploy.seq = menuNode.children.length;
            addMenuDeploy.id_international = "";
            addMenuDeploy.updateType = "2";
            addMenuMap.push(addMenuDeploy);
            //添加成功后选中该节点
            $tableTree.jstree("select_node", node.id);
        };
        var is_loaded = true;
        $tableTree.jstree("create_node", par, node, pos, callback, is_loaded);
    });

    //左侧输删除节点
    form.get().find('.deMenuDeploy').on('click', function () {
        var deleteMenuNodeId = [];

        function getDeleteTables(node) {
            var childs = ref.get_children_dom(node);
            for (var i = 0; i < childs.length; i++) {
                var nodes = $tableTree.jstree('get_node', childs[i]);
                deleteMenuNodeId.push(nodes.id);
                if (!isEmpty(nodes.children)) {
                    for (var j = 0; j < nodes.children.length; j++) {
                        deleteMenuNodeId.push(nodes.children[j]);
                        getDeleteTables(nodes.children[j]);
                    }
                }
            }
        }

        if (confirm(msg('delete real?'))) {
            var ref = $tableTree.jstree(true);
            getDeleteTables(nodeForAdd);
            deleteMenuNodeId.push(nodeForAdd.id);
            if (!isEmpty(deleteMenuNodeId)) {
                for (var j = 0; j < deleteMenuNodeId.length; j++) {
                    var flag = true;
                    //删除的不为新增的才记录到删除里
                    if (!isEmpty(addMenuMap)) {
                        for (var i = 0; i < addMenuMap.length; i++) {
                            if (addMenuMap[i].id == deleteMenuNodeId[j]) {
                                addMenuMap.splice(i, 1);
                                flag = false;
                            }
                        }
                    }
                    if (flag) {
                        var tableNode = $tableTree.jstree('get_node', deleteMenuNodeId[j]);
                        var deptMenu = {};
                        deptMenu.id = tableNode.data.id;
                        deptMenu.page_id = tableNode.data.page_id;
                        deleteMenuMap.push(deptMenu);
                    }
                }
            }
            ref.delete_node(nodeForAdd);
            nodeForAdd = [];
        }
        form.get().find(".deploy-right-menu-data input,.deploy-right-menu-data select,.deploy-right-menu-data button").attr("disabled", "true");
        form.get().find(".deploy-right-menu-data .interMenu").html("");
        form.get().find(".deploy-right-menu-data span[name=icon]").attr("class", "");
        form.get().find(".urlValue").val('');
        form.get().find(".urlList option").remove();
        international(".interMenu", "", "", "isShow");
    });

    //保存菜单
    form.get().find(".MenuDeploySave").on("click", function () {
        //获取菜单点击前的菜单国际化控件返回的数据  判断国际化返回的数据，判断菜单国际化是否修改
        InterOnClick();
        deleteMenuMap;
        addMenuMap;
        editMenuMap;
        var menuDeploySave = {};
        menuDeploySave.insert = addMenuMap;
        menuDeploySave.update = editMenuMap;
        menuDeploySave.deletes = deleteMenuMap;
        dx.processing.open();
        postJson('/menuDeploy/menuDeploySave.do', {menuDeploySave: menuDeploySave}, function (data) {
        	dxToastAlert(msg('Saved successfully!'));
            deleteMenuMap = [];
            addMenuMap = [];
            editMenuMap = [];
            internationalControl = [];
            postJson('/data/cache/reload.do', function () {
                initCache(function () {
                	dxToastAlert(msg('reload successful'));
                    ajaxLoadGroup(form, callback);
                    dx.processing.close();
                }, true)
            })
        });
    });
    //树的拖动
    $tableTree.on('move_node.jstree', function (e, data) {
        if (data.old_parent == data.parent) {
            jstreeMove(data.parent);
        } else {
            jstreeMove(data.old_parent);
            jstreeMove(data.parent);
        }
    }).jstree({});
    function jstreeMove(parentId) {
        var menuNode = $tableTree.jstree('get_node', parentId).children;
        for (var j = 0; j < menuNode.length; j++) {
            var flag = true;
            if (!isEmpty(addMenuMap)) {
                for (var i = 0; i < addMenuMap.length; i++) {
                    if (addMenuMap[i].id == menuNode[j]) {
                        addMenuMap[i].seq = j + 1;
                        addMenuMap[i].parent_id = parentId;
                        flag = false;
                        break;
                    }
                }
            }
            if (!isEmpty(editMenuMap)) {
                for (var i = 0; i < editMenuMap.length; i++) {
                    //判断点击的菜单是否修改
                    if (editMenuMap[i].id == menuNode[j]) {
                        editMenuMap[i].seq = j + 1;
                        editMenuMap[i].parent_id = parentId;
                        flag = false;
                        break;
                    }
                }
            }
            if (flag) {
                var node = $tableTree.jstree('get_node', menuNode[j]).data;
                node.seq = j + 1;
                node.parent_id = parentId;
                editMenuMap.push(node);
            }
        }
    }

    //复制
    form.get().find(".copyMenuTree").on("click", function () {
        //InterOnClick();
        var ref = $tableTree.jstree(true),
            sel = ref.get_selected();
        var menuNode = $tableTree.jstree('get_node', sel[0]);
        /* var flag=true;
         copyOrcutMap={};
         if(!isEmpty(addMenuMap)){
         for(var i=0;i<addMenuMap.length;i++){
         if(addMenuMap[i].id==sel[0]){
         copyOrcutMap["1"]=addMenuMap[i];
         flag=false;
         }
         }
         }
         if(!isEmpty(editMenuMap)){
         for(var i=0;i<editMenuMap.length;i++){
         //判断点击的菜单是否修改
         if(editMenuMap[i].id==sel[0]){
         copyOrcutMap["1"]=editMenuMap[i];
         flag=false;
         }
         }
         }
         if(flag){
         var menuNode = $tableTree.jstree('get_node',sel[0]);
         copyOrcutMap["1"] = menuNode.data;
         }*/
        if (!sel.length) {
            return false;
        }
        ref.copy(sel);
        ref.paste(menuNode.parent);
        var addMenuDeploy = JSON.parse(JSON.stringify(menuNode.data));
        addMenuDeploy.id = $tableTree.jstree('get_node', menuNode.parent).children[0];
        addMenuDeploy.seq = $tableTree.jstree('get_node', menuNode.parent).children.length + 1;
        addMenuDeploy.updateType = "2";
        addMenuMap.push(addMenuDeploy);
    });
    //剪切
    /*  form.get().find(".cutMenuTree").on("click",function () {
     InterOnClick();
     var ref = $tableTree.jstree(true),
     sel = ref.get_selected();
     var flag=true;
     copyOrcutMap={};
     if(!isEmpty(addMenuMap)){
     for(var i=0;i<addMenuMap.length;i++){
     if(addMenuMap[i].id==sel[0]){
     copyOrcutMap["2"]=addMenuMap[i];
     flag=false;
     }
     }
     }
     if(!isEmpty(editMenuMap)){
     for(var i=0;i<editMenuMap.length;i++){
     //判断点击的菜单是否修改
     if(editMenuMap[i].id==sel[0]){
     copyOrcutMap["2"]=editMenuMap[i];
     flag=false;
     }
     }
     }
     if(flag){
     var menuNode = $tableTree.jstree('get_node',sel[0]);
     copyOrcutMap["2"]=menuNode.data;
     }
     if(!sel.length) { return false; }
     ref.cut(sel);
     });*/
    //粘贴
    /*form.get().find(".pasteMenuTree").on("click",function () {
     var ref = $tableTree.jstree(true),
     sel = ref.get_selected();
     for(var key in copyOrcutMap){
     if(key=="1"){
     copyOrcutMap[key].parent_id=sel[0];
     copyOrcutMap[key].updateType="2";
     //新增addMenuMap
     addMenuMap.push(copyOrcutMap[key]);
     }
     if(key=="2"){
     var flag=true;
     if(!isEmpty(addMenuMap)){
     for(var i=0;i<addMenuMap.length;i++){
     if(addMenuMap[i].id==copyOrcutMap[key].id){
     addMenuMap[i].parent_id=sel[0];
     flag=false;
     }
     }
     }
     if(!isEmpty(editMenuMap)){
     for(var i=0;i<editMenuMap.length;i++){
     //判断点击的菜单是否修改
     if(editMenuMap[i].id==copyOrcutMap[key].id){
     editMenuMap[i].parent_id=sel[0];
     flag=false;
     }
     }
     }
     if(flag){
     copyOrcutMap[key].parent_id=sel[0];
     copyOrcutMap[key].updateType="1";
     //新增editMenuMap
     editMenuMap.push(copyOrcutMap[key]);
     }
     }
     }
     if(!sel.length) { return false; }
     ref.paste(sel);
     });*/

});