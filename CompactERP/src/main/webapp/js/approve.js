registerInit('approve', function (form) {
    // 加载控件
    var blocks = [];
    var lines = [];
    var informEvents = [];
    var rejectEvents = [];
    var authGroups = []; // 每一个节点有一个审批人对象（authGroup）
    var conditions = [];   //线的条件
    var conditionMap = {}; //线的条件
    var authGroupList = []; //审批人
    var tableName = "";
    var initLineFlag = 0;
    var blockI18N = {};
    var $form = form.get();
    var initApproveButtonEvent = {};
    var approveButtonEvent = {};
    var approveBlockEvent = {};
    var initApproveBlockEvent = {};
    var deptOrUserTree = {};
    init();
    initActionEvent();
    initActionPrerequistie();
    // 获取新增审批人时权限成员数据
    selectDpetAndRoleAndUser('.select_department_column', 'c_dept_role_user', 'data_dept');
    selectDpetAndRoleAndUser('.select_role_column', 'c_dept_role_user', 'role');
    selectDpetAndRoleAndUser('.select_user_column', 'c_dept_role_user', 'data_user');
    // 获取编辑审批人时权限成员数据
    selectDpetAndRoleAndUser('.edit_department_column', 'c_dept_role_user', 'data_dept');
    selectDpetAndRoleAndUser('.edit_department_column_ref', 'c_dept_role_user', 'data_dept_ref');
    selectDpetAndRoleAndUser('.edit_role_column', 'c_dept_role_user', 'role');
    selectDpetAndRoleAndUser('.edit_user_column', 'c_dept_role_user', 'data_user');
    selectDpetAndRoleAndUser('.edit_user_column_ref', 'c_dept_role_user', 'data_user_ref');
    //input和滚动条的样式
    function uiinit() {
        $('.approve-person-form input[type=checkbox]').iCheck({
            checkboxClass: 'icheckbox_flat-blue',
            radioClass: 'iradio_flat-blue'
        });
        $(".approve-config-form,.approve-event-form,.approve-button-form,.approve-person-form").niceScroll({
            cursorborder: "",
            cursorcolor: "#aeb2b7"
        });
        $(".approve-config-form,.approve-event-form,.approve-button-form,.approve-person-form").getNiceScroll().resize();

        $('.approve-config-btn a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
            $(".approve-config-form,.approve-event-form,.approve-button-form").getNiceScroll().resize();
        });
    }
    var $divTree =  $form.find('.ref_department_column_tree');
    initLeftTree();
  //初始化左侧树
    function initLeftTree() {
    	$divTree.jstree({
            'core': {
                "multiple": false,
                'data': null,
                'dblclick_toggle': true,          //tree的双击展开
                "check_callback": true        //
            },
            "plugins": ["search", "contextmenu"]
        });
    }
    function callback(result) {
        if (!isEmpty(result))
            if (isEmpty(result)) {
            	$divTree.jstree(true).settings.core.data = null;
            	$divTree.jstree(true).refresh();
            } else {
            	$divTree.jstree(true).settings.core.data = result;
            	$divTree.jstree(true).refresh();
            }
    }
    //初始化国际化控件
    function international(divClass, internationalId, menuName, isShow, onSubmintCallback, onSelectCallback) {
        $(divClass).internationalControl({
            internationalId: internationalId,
            menuName: menuName,
            isShow: isShow,
            onSubmit: function (data) {
                onSubmintCallback(data);
            },
            onSelect: function (data) {
                onSelectCallback(data);
            }
        })
    }

    // 查询部门角色用户数据,初始化控件
    function selectDpetAndRoleAndUser(divClass, tableName, columnName, init, param) {
        $(divClass).dxDefaultWidget({
            tableName: tableName,
            columnName: columnName,
            param: param,
            dxOnBlur: function (data) {

            },
            dxOnChange: function (data) {

            },
            dxOnInit: init
        });
    }

    $form.find('.collapse').on('shown.bs.collapse', function () {
        $(".approve-config-form,.approve-person-form").getNiceScroll().resize();
        $('.dropdown').on('shown.bs.dropdown', function () {
            $(".approve-config-form,.approve-person-form").getNiceScroll().resize();
        });
    });

    //右侧模块菜单点击
    $(".approveModelMenu").on("click", "li a", function () {
        $(".approveModelMenu li a").removeClass("active");
        $(this).addClass("active");
    });
    $(".approveModelMenu li:first-of-type a").click();

    //高级设置
    $form.find('.approve-more-setting').on("click", function () {
        $form.find('.approve-person,.approve-config-btn li').removeClass("active");
        $form.find('.approve-config-div').addClass("active");
        $form.find('.approve-configLi,.approve-config-div .approve-config').addClass("active");
    });

    //返回到审批人设置
    $form.find('.approve-config-back').on("click", function () {
        $form.find('.approve-config-div').removeClass("active");
        $form.find('.approve-person').addClass("active");
        $(".approve-config-form,.approve-event-form,.approve-person-form").getNiceScroll().resize();
    });
    //解析审批事件，过滤< >
    function replaceEventParam(param){
    	if(!isEmpty(param)){
    		param = param.replace('<', '&lt;');
    		param = param.replace('>', '&gt;');
    		return param;
    	}
//    	if(param){
//    		for(var key in param){
//    			if(!isEmpty(param[key]) && typeof param[key] == 'string'){
//    				param[key] = param[key].replace('<', '&lt;');
//    				param[key] = param[key].replace('>', '&gt;');
//    			}
//    		}
//    	}
    }

    //配置开始节点事件
    $form.find('.approve-chart-view').on("click",".approveNode1",function () {
        $(this).addClass("active");
        $form.find('.approve-config-tab .approve-start-event').addClass("active");
        var blockId=$(this).attr("id");
        $(".hidden_node_id").val(blockId);
        if(!isEmpty(approveBlockEvent) || !isEmpty(approveBlockEvent[blockId])){
        	$form.find(".blockEvent_submitEvent").attr("approve_event_id",approveBlockEvent[blockId]['submitEvent'].approve_event_id);
        	setInformMannerAndMoreEvents(approveBlockEvent[blockId]['submitEvent'], $form.find(".blockEvent_submitEvent .informManner"),
            		$form.find(".blockEvent_submitEvent .moreEvents"));
        }
    });

    //初始化
    function init() {
        uiinit();
        //初始化审批事件的下拉列表数据
        var urlMap = dx.urlInterface;
        for (var key in urlMap) {
            if (isEmpty(urlMap[key].i18n)) {
                $(".informMoreEvent").append('<option value="' + key + '">' + urlMap[key].name + '</option>');
                $(".rejectMoreEvent").append('<option value="' + key + '">' + urlMap[key].name + '</option>');
            } else {
                $(".informMoreEvent").append('<option value="' + key + '">' + urlMap[key].i18n[dx.user.language_id] + '</option>');
                $(".rejectMoreEvent").append('<option value="' + key + '">' + urlMap[key].i18n[dx.user.language_id] + '</option>');
            }
        }
        //初始化审批流控件
        var instance = jsPlumb.getInstance({
            DragOptions: {cursor: 'pointer', zIndex: 2000},
            ConnectionOverlays: [
                ["Arrow", {//箭头的样式
                    location: 1,
                    visible: true,
                    width: 11,
                    length: 11,
                    id: "ARROW"
                }],
                ["Label", {//连线上的label
                    location: 0.4,
                    id: "label",
                    cssClass: "aLabel"
                }]
            ],
            Container: "canvas" //画布容器
        });
        // instance.registerConnectionType("basic", basicType);
        var i = 0;
        //点击需要审批流的表,显示审批流程图
        $form.find(".approve-detail").off('click');
        $form.find(".approve-detail").on("click", function () {
            $(".approve_home").hide();
            tableName = $(this).attr("table-name");// 审批流表名称
            setDateByTableName(tableName);// 从后端获取块、线的数据
        });
        //点击返回，清空数据，显示所以需要审批流的表
        $form.find(".comeback-approve-list").off('click');
        $form.find(".comeback-approve-list").on("click", function () {
            postPage('/approve/approve.view', {}, function (result) {
                var $div = $form.parent();
                $div.empty();
                $div.append(result);
                var newForm = getFormModel($div.find('.dx-form').attr('id'));
                buildFormCache(newForm, newForm.widgets);
                dx.init.approve(newForm);

            })
        });
        //保存所有数据
        $form.find(".saveBtn").off('click');
        $form.find(".saveBtn").on("click", function () {
            save();
        });
        //验证所有数据
        $form.find(".validateAll").off('click');
        $form.find(".validateAll").on("click", function () {
            validateAll();
        });
        //验证单个数据
        $form.find(".validateOne").off('click');
        $form.find(".validateOne").on("click", function () {
            validateOne();
        });

        //允许加签的点击事件
        $form.find(".add_sign").off('click');
        $form.find(".add_sign").on("click", function () {
            if ($(this).is(":checked")) {
                $(".add-person-num").css("visibility", "visible");
            } else {
                $(".add-person-num").css("visibility", "hidden");
            }
        });
        //复选框的样式
        $form.find('.approve-config-tab input.icheckbox_flat-blue,.icheckbox_flat-blue').iCheck({
            checkboxClass: 'icheckbox_flat-blue',
            radioClass: 'iradio_flat-blue'
        });
        // 初始化端点样式设置
        // 基本连接线样式
        var connectorPaintStyle = {
            lineWidth: 2,
            fill: "#666666",
            strokeStyle: "#666666",
            strokeWidth: 2,
            fillStyle: "white",
            joinstyle: "round",
            outlineColor: "rgba(255, 255, 255,0)",
            outlineWidth: 1
        };
        // 鼠标悬浮在连接线上的样式
        var connectorHoverStyle = {
            lineWidth: 2,
            strokeStyle: "rgba(90, 179, 238,0.4)",
            outlineWidth: 1,
            outlineColor: "rgba(255, 255, 255,0)"
        };
        var hollowCircle = {
            endpoint: ["Dot", {radius: 3}], // 端点的形状
            //anchors: ["BottomCenter", "TopCenter", "RightMiddle", "LeftMiddle"],
            connectorStyle: connectorPaintStyle,// 连接线的颜色，大小样式
            connectorHoverStyle: connectorHoverStyle,
            paintStyle: {
                strokeStyle: "#666666",
                strokeWidth: 2,
                fillStyle: "white",
                radius: 3,
                lineWidth: 2
            },
            hoverPaintStyle: {
                lineWidth: 2,
                fillStyle: "white",
                strokeStyle: "#60b9e0"
            },
            overlays: [["Arrow", {
                location: 1,
                visible: true,
                width: 6,
                length: 6
            }]],
            isSource: true, // 是否可以拖动（作为连线起点）
            connector: ["Flowchart", {
                stub: 0,
                gap: 0
//                    curviness: 0, proximityLimit:150, cornerRadius: 5, alwaysRespectStubs: true
            }], // 连接线的样式种类有[Bezier],[Flowchart],[StateMachine],[Straight ]
            isTarget: true, // 是否可以放置（连线终点）
            maxConnections: -1, // 设置连接点最多可以连接几条线
            connectorOverlays: [
                ["Arrow", {location: 1, visible: true, width: 6, length: 6}]
                // ["Label", {location: 0.4, id: "approveALabel", label: "", cssClass: "approveALabel"}]
            ],
            Container: "canvas"
            //EndpointHoverStyle: {color: 'yellow'} //鼠标经过的样式
        };

        //新增块时的块拖动
        $(".approveleft").find(".approveNode").draggable({
            helper: "clone",
            scope: "ss"
        });

        var lastLineId = {};
        // 将块之间用线连接起来，线连线方法
        instance.bind("connection", function (info) {
            var line = {};
            // 页面加载的时候连接的线
            if (initLineFlag == 0) {
                for (var i = 0; i < lines.length; i++) {
                    line = lines[i];
                    initLineFlag = 1;
                    if (line.page_source_id == info.connection.sourceId
                        && line.page_target_id == info.connection.targetId) {
                        info.connection.id = line.connection_id;
                        info.connection.source.innerText = line.source_text;
                        info.connection.target.innerText = line.target_text;
                        break;
                    }
                }
            } else { // 增加一条线
                info.connection.id = uuid(9, 16);
                line.connection_id = info.connection.id;
                line.page_source_id = info.connection.sourceId;
                line.page_source_place = info.sourceEndpoint.anchor.type;
                line.page_target_id = info.connection.targetId;
                line.page_target_place = info.targetEndpoint.anchor.type;
                line.source_text = info.connection.source.innerText;
                line.target_text = info.connection.target.innerText;
                line.memo = "";
                lines.push(line);
            }

            //鼠标点击到线上事件
            info.connection.bind('click', function (conn, b) {
                $(".hidden_line_id").val(conn.id);
                var line_id = $(".hidden_line_id").val();// 正在编辑的线

                if (lastLineId.conn) {
                    if (!isEmpty(lastLineId.lastLine.memo)) {
                        lastLineId.conn.getOverlay('label').setLabel('<div class="' + lastLineId.conn.id + '">' + lastLineId.lastLine.memo + '</div>');
                    } else {
                        lastLineId.conn.getOverlay('label').setLabel('<div class="' + lastLineId.conn.id + '"></div>');
                    }
                }

                var line = conn;
                conn.getOverlay('label').setLabel('<div style="z-index:2;padding:5px;" class="' + conn.id + '"><img class="approve-line-active" src="js/jsplumb/images/close.png" style="width:22px;"/></div>');

                var lastLine;
                for (var i = 0; i < lines.length; i++) {
                    if (lines[i].connection_id == conn.id) {
                        lastLine = lines[i];
                        break;
                    }
                }

                if (lastLineId.conn) {
                    lastLineId.conn.unbind("mouseleave");
                }
                info.connection.bind("mouseleave", function (hoverconn, b) {
                    $(".approveright svg").find("path").attr({"stroke": "#666666", "fill": "#666666"});
                    $(hoverconn.canvas).find("path").attr({"stroke": "#60b9e0", "fill": "#60b9e0"});
                });

                lastLineId = {lastLine: lastLine, conn: conn};

                // 删除线
                $("." + line_id).on("click", function (reconn, originalEvent) {
                    delete conditionMap[line_id];// 删除条件数据
                    // 删除线数据
                    for (var i = 0; i < lines.length; i++) {
                        if (lines[i].connection_id == line_id) {
                            // lines.splice(i - 1, 1);
                            lines.splice(i, 1);
                            break;
                        }
                    }
                    $("#approve_node_detail_condition").removeClass("active");
                    instance.detach(line);
                    lastLineId = {};
                });
            });
        });
        
        //从后台获取数据块线数据
        function setDateByTableName(tableName) {
            postJson('/approve/selectApprove.do', {tableId: tableName}, function (result) {
                approveButtonEvent = result.approve.approveButtonEvent;
                approveBlockEvent = result.approve.approveBlockEvent;
                for(var key in approveBlockEvent){
                	var blockEvent=approveBlockEvent[key];
                	for(var blockEventKey in blockEvent){
                		var param=blockEvent[blockEventKey]['event'];
                		if(param){
                			for(var paramKey in param){
                				param[paramKey]['event_param'] = replaceEventParam(param[paramKey]['event_param']);
                                for (var replaceKey in param[paramKey]){
                                    if (replaceKey != 'event_param'){
                                        param[paramKey][replaceKey] = replaceParam(param[paramKey][replaceKey]);
                                    }
                                }
                			}
                		}
                	}
                }
                initApproveButtonEvent = result.approve.initApproveButtonEvent;
                initApproveBlockEvent = result.approve.initApproveBlockEvent;
                blocks = result.approve.flowBlock;
                lines = result.approve.flowLine;
                authGroups = result.approve.authGroup;
                conditions = result.approve.flowConditionDetail;
                result = replaceParam(result);
            	deptOrUserTree = result.deptOrUserTree;
            	callback(deptOrUserTree);
                var con_id;
                var con;
                if (typeof (blocks) == "undefined") {
                    blocks = [];
                }
                if (typeof (lines) == "undefined") {
                    lines = [];
                }
                if (typeof (authGroups) == "undefined") {
                    authGroups = [];
                }
                if (typeof (conditions) == "undefined") {
                    conditions = [];
                }
                if (typeof (events) == "undefined") {
                    events = [];
                }
                for (var i = 0; i < conditions.length; i++) {
                    con = conditions[i];
                    con_id = con.connection_id;
                    if (typeof (conditionMap[con_id]) == "undefined") {
                        conditionMap[con_id] = new Array();
                        conditionMap[con_id].push(con);
                    } else {
                        conditionMap[con_id].push(con);
                    }
                }
                $(".approve_detail_wrap").show();
                $("#approve_node_detail_process").removeClass("active");
                //加载线和块
                addEndpoints();
                addLines();
                initLineFlag = 1;
                jsPlumb.fire("jsPlumbDemoLoaded", instance);
            });
        }

        //加载线，初始化数据
        function addLines() {
            var line;
            for (var i in lines) {
                line = lines[i];
                var source = line.page_source_id;
                var target = line.page_target_id;
                var sourcePlace = line.page_source_place;
                var targetPlace = line.page_target_place;
                initLineFlag = 0;
                var connection = instance.connect({
                    source: source,
                    target: target,
                    anchors: [sourcePlace, targetPlace]
                }, hollowCircle);

                if (connection) {
                    var overlay = connection.getOverlay('label');
                    if (overlay) {
                        if (!isEmpty(line.memo)) {
                            connection.getOverlay('label').setLabel(line.memo);
                        }
                    }
                }
            }
        }

        // 加载块和点，初始化数据
        function addEndpoints() {
            var block;
            for (var i in blocks) {
                block = blocks[i];
                var id = block.block_id;
                var type = block.type;
                var left = block.block_x;
                var top = block.block_y;
                var text = block.text;
                var approve_name_I18N = block.approve_name_I18N;
                var approveNode = "";
                switch (type) {
                    case "startNode":
                        approveNode = "approveNode1";
                        break;
                    case "processNode":
                        approveNode = "approveNode2";
                        break;
                    case "conditionNode":
                        approveNode = "approveNode3";
                        break;
                    case "endNode":
                        approveNode = "approveNode4";
                        break;
                    default:
                        break;
                }
                $("#approveright").append('<div class="approveNode ' + approveNode
                    + '" approve_name_I18NId="' + approve_name_I18N.international_id + '" node-type="' + type + '"  id="' + id + '" >' + approve_name_I18N[dx.user.language_id] + '</div>');
                //设置块的位置
                $("#" + id).css("left", left).css("top", top);
                instance.addEndpoint(id, {anchors: "TopCenter"}, hollowCircle);
                instance.addEndpoint(id, {anchors: "RightMiddle"}, hollowCircle);
                instance.addEndpoint(id, {anchors: "BottomCenter"}, hollowCircle);
                instance.addEndpoint(id, {anchors: "LeftMiddle"}, hollowCircle);
                instance.draggable(id);
                $("#" + id).draggable({containment: "parent"});
                doubleclick(id, 0);//点击块
            }
        }

        //初始化块时，拖动块，初始化快的位置
        $(".approveright").droppable({
            scope: "ss",
            drop: function (event, ui) {
                var left = parseInt(ui.offset.left - $(this).offset().left);
                var top = parseInt(ui.offset.top - $(this).offset().top);
                var name = ui.draggable[0].id;
                switch (name) {
                    case "approveNode1":
                        i++;
                        var id = uuid(8, 16);
                        if ($(".approveright").find(".approveNode1").length <= 0) {
                            $(this).append('<div class="approveNode approveNode1 approve-node" approve_name_I18NId="approve_begin_lump" node-type="startNode" id="'
                                + id + '" >' + $(ui.helper).html() + '</div>');
                            $("#" + id).css("left", left).css("top", top);
                            instance.addEndpoint(id, {anchors: "TopCenter"}, hollowCircle);
                            instance.addEndpoint(id, {anchors: "RightMiddle"}, hollowCircle);
                            instance.addEndpoint(id, {anchors: "BottomCenter"}, hollowCircle);
                            instance.addEndpoint(id, {anchors: "LeftMiddle"}, hollowCircle);
                            instance.draggable(id);
                            $("#" + id).draggable({containment: "parent"});
                            doubleclick(id, 1);
                        }
                        break;
                    case "approveNode2":
                        i++;
                        var id = uuid(8, 16);
                        $(this).append("<div class='approveNode approveNode2' approve_name_I18NId='approve_flow_lump' node-type='processNode' id='"
                            + id + "'>" + $(ui.helper).html() + "</div>");
                        $("#" + id).css("left", left).css("top", top);
                        instance.addEndpoint(id, {anchors: "TopCenter"}, hollowCircle);
                        instance.addEndpoint(id, {anchors: "RightMiddle"}, hollowCircle);
                        instance.addEndpoint(id, {anchors: "BottomCenter"}, hollowCircle);
                        instance.addEndpoint(id, {anchors: "LeftMiddle"}, hollowCircle);
                        // instance.addEndpoint(id, hollowCircle);
                        instance.draggable(id);
                        $("#" + id).draggable({containment: "parent"});
                        doubleclick(id, 1);
                        break;
                    case "approveNode3":
                        i++;
                        var id = uuid(8, 16);
                        $(this).append("<div class='approveNode approveNode3' approve_name_I18NId='approve_judeg_lump' node-type='conditionNode'  id='"
                            + id + "'>" + $(ui.helper).html() + "</div>");
                        $("#" + id).css("left", left).css("top", top);

                        instance.addEndpoint(id, {anchors: "TopCenter"}, hollowCircle);
                        instance.addEndpoint(id, {anchors: "RightMiddle"}, hollowCircle);
                        instance.addEndpoint(id, {anchors: "BottomCenter"}, hollowCircle);
                        instance.addEndpoint(id, {anchors: "LeftMiddle"}, hollowCircle);
                        // instance.addEndpoint(id, hollowCircle);
                        instance.draggable(id);
                        $("#" + id).draggable({containment: "parent"});
                        doubleclick(id, 1);
                        break;
                    case "approveNode4":
                        i++;
                        var id = uuid(8, 16);
                        if ($(".approveright").find(".approveNode4").length <= 0) {
                            $(this).append('<div class="approveNode approveNode4" approve_name_I18NId="approve_over_lump" node-type="endNode"  id="'
                                + id + '" >' + $(ui.helper).html() + '</div>');
                            $("#" + id).css("left", left).css("top", top);
                            instance.addEndpoint(id, {anchors: "TopCenter"}, hollowCircle);
                            instance.addEndpoint(id, {anchors: "RightMiddle"}, hollowCircle);
                            instance.addEndpoint(id, {anchors: "BottomCenter"}, hollowCircle);
                            instance.addEndpoint(id, {anchors: "LeftMiddle"}, hollowCircle);
                            instance.draggable(id);
                            $("#" + id).draggable({containment: "parent"});
                            doubleclick(id, 1);
                        }
                        break;
                }
            }
        });

        //删除块
        function deleteNode(id) {
            var blockId = id;
            if (confirm("确定要删除吗?")) {
                // var blockId = $(this).parent().attr("id");
                // 删除块数据
                for (var i = 0; i < blocks.length; i++) {
                	if(!isEmpty(blocks[i])){
                		if (blocks[i].block_id == blockId) {
                            blocks.splice(i, 1);
                            break;
                        }
                	}
                }
                // 删除审批人数据
                for (var i = 0; i < authGroups.length; i++) {
                	if(!isEmpty(authGroups[i])){
                		if (authGroups[i][0].block == blockId) {
                            authGroups.splice(i, 1);
                            break;
                        }
                	}
                }
                var line;
                // 删除链接该节点的线
                var temp = [];
                for (var i = 0; i < lines.length; i++) {
                    line = lines[i];
                    if (line.page_source_id == blockId || line.page_target_id == blockId) {
                        // 删除条件数据
                        delete conditionMap[line.connection_id];
                        // 删除线数据
                        temp.push(line);
                    }
                }
                // 正在编辑的线
                var line_id = $(".hidden_line_id").val();
                for (var t in temp) {
                    for (var i = 0; i < lines.length; i++) {
                        if (temp[t].connection_id == lines[i].connection_id) {
                            lines.splice(i, 1);
                            if (line_id == temp[t].connection_id) {
                                $("#approve_node_detail_condition").removeClass("active");
                            }
                            break;
                        }
                    }
                }

                instance.removeAllEndpoints(blockId);
                $(".approveright #" + id).remove();

                // 如果右边显示的是删除的节点，则隐藏右边
                if (blockId == $(".hidden_node_id").val()) {
                    $("#approve_node_detail_condition").removeClass("active");
                    $("#approve_node_detail_process").removeClass("active");
                }

            }
            if(!isEmpty(approveBlockEvent)){
            	delete approveBlockEvent[blockId];
            }
			if(!isEmpty(approveButtonEvent)){
				delete approveButtonEvent[blockId];
			}
			
			if(!isEmpty(initApproveButtonEvent)){
				delete initApproveButtonEvent[blockId];
			}
			if(!isEmpty(initApproveBlockEvent)){
				delete initApproveBlockEvent[blockId];
			}
        }

        $form.find(".approveright").off("keydown", ".approveNode");
        $form.find(".approveright").on("keydown", ".approveNode", function (ev) {
            if (ev.keyCode == 46) {
                deleteNode($(this).attr("id"));
            }
        });
        $form.find(".approveright").off("click", ".approveNodeClose");
        $form.find(".approveright").on("click", ".approveNodeClose", function () {
            deleteNode($(this).parent().attr("id"));
        });
        /* // 链接双击事件，在此事件中加入删除链接的逻辑，双击删除线
         instance.bind("dblclick", function (conn, originalEvent) {
         if (confirm("确定删除吗？")) {
         var line_id = $(".hidden_line_id").val();// 正在编辑的线
         delete conditionMap[conn.id];// 删除条件数据
         // 删除线数据
         for (var i = 0; i < lines.length; i++) {
         if (lines[i].connection_id == conn.id) {
         lines.splice(i - 1, 1);
         break;
         }
         }
         if (line_id == conn.id) {
         $("#approve_node_detail_condition").removeClass("active");
         }
         instance.detach(conn);
         }
         });*/

        //改变条件详情里面的数据
        $form.find(".condition_list").off("change", ".condition_select");
        $form.find(".condition_list").on("change", ".condition_select", function () {
            var value = $(this).val();
            //改变右边的值
            var uid = $(this).attr("uid");
            // 清空下拉框
            $('.condition_value_select_' + uid).empty();
            selectDpetAndRoleAndUser('.condition_value_select_' + uid, tableName, value);
        });
        //条件详情里面删除按钮的点击事件
        $form.find(".condition_list").off("click", ".deleteACondition");
        $form.find(".condition_list").on("click", ".deleteACondition", function () {
            $(this).parent().remove();
        });
        // 条件详情里面新增按钮的点击事件
        $form.find(".approve-condition-add").off("click");
        $form.find(".approve-condition-add").on("click", function () {
            var uid = uuid(6, 16);
            var condition = '<div><select name="" id="" uid="' + uid + '" class="condition_select">';
            var table = dx.table[tableName];
            var columns = dx.table[tableName].columns;
            var first_column = "";
            for (var i = 0; i < columns.length; i++) {
                var column = columns[i];
                if (first_column == "") {
                    first_column = column.column_name;
                }
                condition += '<option value="' + column.column_name + '">' + column.i18n.cn + '</option>';
            }

            condition += '</select><select class="symbol_select" name="" id="">'
                + '<option value="=">=</option>'
                + '<option value="!=">!=</option>'
                + '<option value="&gt;">&gt;</option>'
                + '<option value="&lt;">&lt;</option>'
                + '<option value="&gt;=">&gt;=</option>'
                + '<option value="&lt;=">&lt;=</option>'
                + '</select>';

            condition += '<div class="input-1 clearfix condition_value_select_'
                + uid + '"></div><a class="deleteACondition">删除</a></div>';

            $form.find(".condition_list").append(condition);

            selectDpetAndRoleAndUser('.condition_value_select_' + uid, tableName, first_column);
        });

        // 保存线上的条件，保存审批详情
        $form.find(".approve-condition-save").off("click");
        $form.find(".approve-condition-save").on("click", function () {
            // 正在编辑的线
            var line_id = $(".hidden_line_id").val();
            var title = $(".approve-condition-title").val();
            var condition_list = $(".condition_list>div");
            var line;
            for (var i = 0; i < lines.length; i++) {
                line = lines[i];
                if (line_id == line.connection_id) {
                    break;
                }
            }
            var condition; // 条件
            line.memo = title;
            var condition_detail_id;
            var column_name;
            var symbol;
            var value;
            var name;
            var select_1;
            if (typeof (conditionMap[line_id]) == "undefined") {
                conditionMap[line_id] = new Array();
            } else {
                conditionMap[line_id] = [];
            }
            for (var i = 0; i < condition_list.length; i++) {
                condition = condition_list[i];
                select_1 = $form.find(condition).find("select[class='condition_select']");
                condition_detail_id = select_1.attr("uid");
                column_name = select_1.val();
                symbol = $form.find(condition).find("select[class='symbol_select']").val();
                var param = $form.find('.condition_value_select_' + condition_detail_id).dxDefaultWidget(true).getDate();
                var column = getColumnDesc(tableName, column_name);
                var refTable = dx.table[column.ref_table_name];
                if (isEmpty(refTable)) {
                    value = param[column_name];
                    name = column_name;
                } else {
                    value = param[refTable.id_column];
                    name = param[refTable.name_column];
                }
                conditionMap[line_id].push({
                    connection_id: line_id,
                    condition_detail_id: condition_detail_id,
                    column_name: column_name,
                    symbol: symbol,
                    value: value,
                    name: name
                });
            }
            dxToastAlert("条件已保存");
        });

        //链接单击事件，点击线编辑线的条件，单击编辑线
        instance.bind("click", function (conn, originalEvent) {
            var hid = $form.find(".hidden_line_id");
            $form.find(".approveright .approveNode").removeClass("active");
            $form.find("#approve_node_detail_process,.approve-start-event").removeClass("active");
            $form.find("#approve_node_detail_condition").removeClass("active");
            $form.find(".approve-person").removeClass("active");
            $form.find(".approveright").attr("name", "conn.id");
            for (var i = 0; i < blocks.length; i++) {
                if (blocks[i].block_id == conn.sourceId && blocks[i].type == 'conditionNode') {
                    // 如果源节点是条件类型,显示右边
                    $form.find(".hidden_line_id").val(conn.id);
                    var line = {};
                    for (var j = 0; j < lines.length; j++) {
                        line = lines[j];
                        if (line.connection_id == conn.id) {
                            break;
                        }
                    }
                    var name = '';
                    if (!isEmpty(line.memo)){
                        name = line.memo.replace(/&lt;/g, '<');
                        name = name.replace(/&gt;/g, '>');
                    }
                    $form.find(".approve-condition-title").val(name);
                    $form.find(".approve-condition-title").focus();
                    // 获取这条线的所有条件
                    $form.find(".condition_list").empty();
                    var conditions_tmp = conditionMap[conn.id];
                    var con;
                    if (typeof (conditions_tmp) != "undefined") {
                        for (var k = 0; k < conditions_tmp.length; k++) {
                            con = conditions_tmp[k];
                            var uid = con.condition_detail_id;
                            var symbol = con.symbol;
                            var condition = '<div><select name="" id="" uid="' + uid + '" class="condition_select">';
                            var table = dx.table[tableName];
                            var columns = dx.table[tableName].columns;
                            var choose_column = con.column_name;
                            for (var m = 0; m < columns.length; m++) {
                                var column = columns[m];

                                if (choose_column == column.column_name) {
                                    condition += '<option selected value="' + column.column_name + '">' + column.i18n.cn + '</option>';
                                } else {
                                    condition += '<option value="' + column.column_name + '">' + column.i18n.cn + '</option>';
                                }
                            }
                            condition += '</select><select class="symbol_select" name="" id="">';
                            if (symbol == "=") {
                                condition += '<option value="=" selected>=</option>'
                                    + '<option value="!=">!=</option>'
                                    + '<option value="&gt;">&gt;</option>'
                                    + '<option value="&lt;">&lt;</option>'
                                    + '<option value="&gt;=">&gt;=</option>'
                                    + '<option value="&lt;=">&lt;=</option>'
                                    + '</select>';
                            } else if (symbol == "!=") {
                                condition += '<option value="=">=</option>'
                                    + '<option value="!=" selected>!=</option>'
                                    + '<option value="&gt;">&gt;</option>'
                                    + '<option value="&lt;">&lt;</option>'
                                    + '<option value="&gt;=">&gt;=</option>'
                                    + '<option value="&lt;=">&lt;=</option>'
                                    + '</select>';
                            } else if (symbol == ">") {
                                condition += '<option value="=">=</option>'
                                    + '<option value="!=">!=</option>'
                                    + '<option value="&gt;" selected>&gt;</option>'
                                    + '<option value="&lt;">&lt;</option>'
                                    + '<option value="&gt;=">&gt;=</option>'
                                    + '<option value="&lt;=">&lt;=</option>'
                                    + '</select>';
                            } else if (symbol == "<") {
                                condition += '<option value="=">=</option>'
                                    + '<option value="!=">!=</option>'
                                    + '<option value="&gt;">&gt;</option>'
                                    + '<option value="&lt;" selected>&lt;</option>'
                                    + '<option value="&gt;=">&gt;=</option>'
                                    + '<option value="&lt;=">&lt;=</option>'
                                    + '</select>';
                            } else if (symbol == ">=") {
                                condition += '<option value="=">=</option>'
                                    + '<option value="!=">!=</option>'
                                    + '<option value="&gt;">&gt;</option>'
                                    + '<option value="&lt;">&lt;</option>'
                                    + '<option value="&gt;=" selected>&gt;=</option>'
                                    + '<option value="&lt;=">&lt;=</option>'
                                    + '</select>';
                            } else if (symbol == "<=") {
                                condition += '<option value="=">=</option>'
                                    + '<option value="!=">!=</option>'
                                    + '<option value="&gt;">&gt;</option>'
                                    + '<option value="&lt;">&lt;</option>'
                                    + '<option value="&gt;=">&gt;=</option>'
                                    + '<option value="&lt;=" selected>&lt;=</option>'
                                    + '</select>';
                            }

                            condition += '<div class="input-1 clearfix condition_value_select_'
                                + uid + '"></div><a class="deleteACondition">删除</a></div>';

                            $(".condition_list").append(condition);

                            selectDpetAndRoleAndUser('.condition_value_select_' + uid, tableName, choose_column,
                                function (node, param) {
                                    var column = getColumnDesc(tableName, choose_column);
                                    var refTable = dx.table[column.ref_table_name];
                                    var data = {};
                                    var name = '';
                                    if (!isEmpty(param.param.value)){
                                        name = param.param.value.replace(/&lt;/g, '<');
                                        name = name.replace(/&gt;/g, '>');
                                    }
                                    if (isEmpty(refTable)) {
                                        data[choose_column] = name;
                                        //data[refTable.name_column] = param.param.name;
                                    } else {
                                        data[refTable.id_column] = name;
                                        data[refTable.name_column] = param.param.name;
                                    }
                                    $(node).dxDefaultWidget(true).setDate(data);
                                }, con);
                        }
                    }
                    $("#approve_node_detail_condition").addClass("active");
                    break;
                }
            }
        });

        //删除多余的div的功能:
        $("#approveright").off("mouseenter", ".approveNode");
        $("#approveright").on("mouseenter", ".approveNode", function () {
            $(this).append('<img class="approveNodeClose" src="js/jsplumb/images/close.png"  style="position:absolute;width: 22px;" />');
            if ($(this).text() == "开始" || $(this).text() == "结束") {
                $(".approveNodeClose").css({"right": -5, "top": -3});
            } else {
                $(".approveNodeClose").css({"right": -5, "top": -3});
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////////
        $("#approveright").off("mouseleave", ".approveNode");
        $("#approveright").on("mouseleave", ".approveNode", function () {
            $(".approveNodeClose").remove();
        });

        // 块的点击事件。
        function doubleclick(id, flag) {
            if (flag == 1) {
                var block_source = $("#" + id);
                var text = block_source.text();
                var approve_name = block_source.attr("approve_name_I18NId");
                var block_x = parseInt(block_source.css("left"), 10);
                var block_y = parseInt(block_source.css("top"), 10);
                var node_type = block_source.attr("node-type");
                // 在数据里新增这个block
                blocks.push({
                    table_id: tableName,
                    block_id: id,
                    type: node_type,
                    text: text,
                    block_x: block_x,
                    block_y: block_y,
                    approve_name: approve_name,
                    approve_name_I18N: dx.i18n.message[approve_name],
                    task_msg: "",
                    turn_down_up: 0,
                    turn_down_source: 0,
                    time_limit: 0,
                    time_out_rule: "",
                    is_addApprove: 0,
                    is_until_block: 0,
                    is_approve_block: 1,
                    is_work_block: 0,
                    need_agree_count_people: 1,
                    need_opinion_count_people: 1
                });
                if(node_type == "startNode"){
                	defaultApproveEvent(id,"submitEvent",true);
                }
                if (node_type == "processNode") {
                	defaultApproveEvent(id, "agreeEvent",true);
                	defaultApproveEvent(id, "rejectEvent",true);
                	defaultApproveEvent(id, "terminationEvent",true);
                	defaultButtonEvent(id,"agreeEvent",1,"agreeevent_button",uuid(10,16),true);
                	defaultButtonEvent(id,"rejectEvent",3,"rejectevent_button",uuid(10,16),true);
                    // 显示数据
                    //移除国际化控件
                    var approve_name_I18N = $form.find(".approve_name_I18N");
                    $form.find(".approve_name_I18N").html("");
                    //初始化国际化控件
                    international(approve_name_I18N, approve_name, dx.i18n.message[approve_name][dx.user.language_id], "",
                        function (submitData) {
                            var hidden_node_id = $(".hidden_node_id").val();
                            $("#" + hidden_node_id).text(submitData[dx.user.language_id]);
                        }, function (selectData) {
                            var hidden_node_id = $(".hidden_node_id").val();
                            $("#" + hidden_node_id).text(selectData[dx.user.language_id]);
                        });
                    $(".task_msg").val("");
                    $('input[name="config-radio"][value="turn_down_up"]').iCheck("check");

                    $(".time_limit").val(0);
                    $(".time_out_rule option:first").prop("selected", "selected");// 设置第一个选中
                    $('.need_agree_count_people').val(1);
                    $('.need_opinion_count_people').val(1);
                }
            }

            //点击块，显示块数据
            $("#" + id).click(function () {
                $(this).attr('tabindex', 0);
                $(this).focus();

                $form.find(".approveright .approveNode").removeClass("active");
                $("#approve_node_detail_condition").removeClass("active");
                $("#approve_node_detail_process").removeClass("active");
                $form.find(".approve-config-div").removeClass("active");
                $form.find(".approve-person,.approve-start-event").removeClass("active");
                $form.find(".approve-configLi").removeClass("active");
                $form.find(".approve-personLi").removeClass("active");
                $form.find(".approve-eventLi").removeClass("active");
                $form.find(".approve-event").removeClass("active");

                $(this).addClass("active");
                var node_type = $(this).attr("node-type");
                if (node_type == "processNode") {
                    $form.find(".approve-person").addClass("active");
                    $form.find(".approve-config").addClass("active");
                    $form.find(".approve-configLi").addClass("active");
                    // 显示流程节点
                    $form.find(".hidden_node_id").val(id);
                    // 显示数据
                    var block;
                    var workBlockFlag = false;
                    var flag = 0;
                    for (var i = 0; i < blocks.length; i++) {
                        block = blocks[i];
                        // 表示blocks数组里有这个节点
                        if (block.block_id == id) {
                        	//初始化审批事件数据
                        	var informMannerClass = "";
                            var moreEvents = "";
                            if(!isEmpty(initApproveBlockEvent)){
                            	for(var key in initApproveBlockEvent[id]){
                                	informMannerClass = $form.find(".blockEvent_"+key+" .informManner");
                                    moreEvents = $form.find(".blockEvent_"+key+" .moreEvents");
                                    $form.find(".blockEvent_"+key).attr("approve_event_id",initApproveBlockEvent[id][key].approve_event_id);
                                	setInformMannerAndMoreEvents(initApproveBlockEvent[id][key], informMannerClass, moreEvents);
                                }
                            }
                            //判断是否是工作节点，//初始化审批按钮数据
                            $form.find('.event-subhead').remove();
                            if (block.is_work_block == 1) {
                            	 $form.find('.work_block').iCheck("check");
                            	 $form.find('.workBlockBtn').remove();
                            	 if(!isEmpty(initApproveButtonEvent)){
                            		 for(var key in initApproveButtonEvent[id]){
                                		 if(initApproveButtonEvent[id][key].event_type=="finishEvent"){
                                			 $form.find('.eventListAgree').attr("flow_event_id",initApproveButtonEvent[id][key].flow_event_id);
                                			 if(initApproveButtonEvent[id][key].is_index_button==1){
                                				 $form.find('.eventListAgree .input-bt-Switcher-wrap input').prop("checked", "true");
                                			 }else{
                                				 $form.find('.eventListAgree .input-bt-Switcher-wrap input').prop("checked", "");
                                			 }
                                			 if(initApproveButtonEvent[id][key].international_id !="finishevent_button" && !isEmpty(initApproveButtonEvent[id][key].i18n)){
                                				 $form.find('.eventListAgree .eventListText').append('<span class="event-subhead">'
                                    					 + initApproveButtonEvent[id][key].i18n[dx.user.language_id] + '</span>');
                                				 $form.find(".eventListAgree .eventBtnEdit").attr("internationalId",initApproveButtonEvent[id][key].i18n.international_id);
                                				 $form.find(".eventListAgree .eventBtnEdit").attr("interValue",initApproveButtonEvent[id][key].i18n[dx.user.language_id]);
                                    		 }
                                		 }else if(initApproveButtonEvent[id][key].event_type!="agreeEvent" 
                                			 && initApproveButtonEvent[id][key].event_type!="disagreeEvent"
                                			 && initApproveButtonEvent[id][key].event_type!="rejectEvent"
                                			 && initApproveButtonEvent[id][key].event_type!="retainEvent"
                                			 && initApproveButtonEvent[id][key].event_type!="terminationEvent"){
                                			 	var internationalId=initApproveButtonEvent[id][key].international_id;
                                			 	var btnName=dx.i18n.message[internationalId][dx.user.language_id];
                                			 	var flow_event_id=initApproveButtonEvent[id][key].flow_event_id;
                                			 	$form.find('.approve-button-work-wrap').append('<div class="eventlist workBlockBtn" flow_event_id="'+flow_event_id+'">'
                                	    				 + '<div class="dx-field-5 eventListText">'
                                	    				 + '<span class="event-subhead">'+btnName+'</span>'
                                	    				 + '</div>'
                                	    				 + '<div class="dx-field-6">'
                                	    				 + '<button class="button-color3 eventBtnRename" type="button">确认</button>'
                                	    				 + '<button class="button-color4 eventBtnCancel" type="button">取消</button>'
                                	    				 + '<div class="dropdown">'
                                	    				 + '<button type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">'
                                	    				 + '<span class="fa fa-ellipsis-h"></span>'
                                	    				 + '</button>'
                                	    				 + '<ul class="dropdown-menu">'
                                	    				 + '<li>'
                                	    				 + '<button class="button-blue-font eventBtnEdit" internationalId="'+internationalId+'" interValue="'
                                	    				 +btnName+'" type="button">编辑</button>'
                                	    				 + '</li>'
                                	    				 + '<li>'
                                	    				 + '<button class="button-red-font eventBtnDelete" type="button">删除</button>'
                                	    				 + '</li>'
                                	    				 + '</ul>'
                                	    				 + '</div>'
                                	    				 + '<div class="input-bt-Switcher-wrap">'
                                	    				 + '<div class="input-bt-Switcher">'
                                	    				 + '<input type="checkbox" class="is_index_button work_is_index_button"/><label></label>'
                                	    				 + '</div></div></div> </div>');
//    	                            			$form.find('.approve-button-work-wrap').append('<div class="eventlist workBlockBtn" flow_event_id="'+flow_event_id+'">'
//                                 		            + '<div class="input-1 eventListText">'
//                                 		            + '<span class="event-subhead">'+btnName+'</span>'
//                                 		            + '</div>'
//                                 		            + '<button class="button-red-font eventBtnDelete" type="button">删除</button>'
//                                 		            + '<button class="button-blue-font eventBtnEdit" internationalId="'+internationalId+'" interValue="'
//                                 		            +btnName+'" type="button">编辑</button>'
//                                 		            + '<button class="button-color3 eventBtnRename" type="button">确认</button>'
//                                 		            + '</div>');
                                			 if(initApproveButtonEvent[id][key].is_index_button==1){
                                 				 $form.find('.eventlist .work_is_index_button').prop("checked", "true");
                                 			 }else{
                                 				 $form.find('.eventlist .work_is_index_button').prop("checked", "");
                                 			 }
                              				$form.find('.eventlist .input-bt-Switcher-wrap input').removeClass("work_is_index_button");
                                		 }
                                	 }
                            	 }else{
                            		 $form.find('.eventListAgree .input-bt-Switcher-wrap input').prop("checked", "true");
                            	 }
                            } else {
                            	$form.find('.work_block').iCheck("uncheck");
                            	if(!isEmpty(initApproveButtonEvent)){
                                	 $form.find('.buttonEventIsUsing').iCheck("uncheck");
                                	 $form.find('.allButtonIsUsing').iCheck("uncheck");
                                	 $form.find(".is_index_button").prop("checked", "");
                                	 $form.find('.buttonEventIsUsing').parents(".eventlist").attr("flow_event_id","");
                                	 for(var key in initApproveButtonEvent[id]){
                                		 var btnDiv=$form.find("."+initApproveButtonEvent[id][key].event_type);
                                		 $(btnDiv).attr("flow_event_id",initApproveButtonEvent[id][key].flow_event_id);
                                		 if(initApproveButtonEvent[id][key].is_index_button==1){
                                			 $(btnDiv).find(".input-bt-Switcher-wrap input").prop("checked", "true");
                                		 }
                                		 if(initApproveButtonEvent[id][key].is_using==1){
                                			 $(btnDiv).find(".buttonEventIsUsing").iCheck("check");
                                		 }
                                		 if(initApproveButtonEvent[id][key].international_id !="agreeevent_button"
                                			 && initApproveButtonEvent[id][key].international_id !="disagreeevent_button"
                            				 && initApproveButtonEvent[id][key].international_id !="rejectevent_button"
                    						 && initApproveButtonEvent[id][key].international_id !="retainevent_button"
                							 && initApproveButtonEvent[id][key].international_id !="terminationevent_button"){
	                                			 if(!isEmpty(initApproveButtonEvent[id][key].i18n)){
	                                    			 $(btnDiv).find(".eventListText").append('<span class="event-subhead">'
	                                    					 + initApproveButtonEvent[id][key].i18n[dx.user.language_id] + '</span>');
	                                    			 $(btnDiv).find(".eventBtnEdit").attr("internationalId",initApproveButtonEvent[id][key].i18n.international_id);
	                                    			 $(btnDiv).find(".eventBtnEdit").attr("interValue",initApproveButtonEvent[id][key].i18n[dx.user.language_id]);
	                                    		 }
                                		 }
                                	 }
                            	}else{
                            		$form.find('.buttonEventIsUsing').iCheck("check");
                            		$form.find('.allButtonIsUsing').iCheck("check");
                            		$form.find('.buttonEventIsUsing').parents(".eventlist").attr("flow_event_id","");
                            		$form.find(".is_index_button").prop("checked", "true");
                            	}
                            }
                            blockI18N = block;
                            flag = 1;
                            //移除国际化控件
                            var approve_name_I18N = $form.find(".approve_name_I18N");
                            $form.find(".approve_name_I18N").html("");
                            //初始化国际化控件
                            international(approve_name_I18N, block.approve_name_I18N.international_id, block.approve_name_I18N[dx.user.language_id], "",
                                function (submitData) {
                                    var hidden_node_id = $(".hidden_node_id").val();
                                    $("#" + hidden_node_id).text(submitData[dx.user.language_id]);
                                    if (isEmpty(submitData.international_id)) {
                                        block.approve_name_I18N = {};
                                        block.approve_name_I18N.international_id = "";
                                        block.approve_name = "";
                                        block.text = submitData.interValue;
                                        block.approve_name_I18N[dx.user.language_id] = submitData.interValue;
                                    } else {
                                        block.approve_name = submitData.international_id;
                                        block.approve_name_I18N = submitData;
                                        block.text = submitData[dx.user.language_id];
                                    }
                                }, function (selectData) {
                                    var hidden_node_id = $(".hidden_node_id").val();
                                    $("#" + hidden_node_id).text(selectData[dx.user.language_id]);
                                    if (isEmpty(selectData.international_id)) {
                                        block.approve_name_I18N = {};
                                        block.approve_name_I18N.international_id = "";
                                        block.approve_name = "";
                                        block.text = selectData.interValue;
                                        block.approve_name_I18N[dx.user.language_id] = selectData.interValue;
                                    } else {
                                        block.approve_name = selectData.international_id;
                                        block.approve_name_I18N = selectData;
                                        block.text = selectData[dx.user.language_id];
                                    }
                                });
                            $(".task_msg").val(block.task_msg);

                            var turn_down_up = block.turn_down_up;
                            var turn_down_source = block.turn_down_source;
                            if (turn_down_up == 1) {
                                $('input:radio[name="config-radio"][value="turn_down_up"]').iCheck("check");
                            } else if (turn_down_source == 1) {
                                $('input:radio[name="config-radio"][value="turn_down_source"]').iCheck("check");
                            } else {
                                $('input[name="config-radio"][value="turn_down_up"]').iCheck("check");
                            }

                            $(".time_limit").val(block.time_limit);
                            if (block.time_out_rule == "") {
                                $(".time_out_rule option:first").prop("selected", "selected");// 设置第一个选中
                            } else {
                                $('.time_out_rule').val(block.time_out_rule);
                            }

                            $('.need_agree_count_people').val(block.need_agree_count_people);
                            $('.need_opinion_count_people').val(block.need_opinion_count_people);
                            //允许加签
                            if (block.is_addApprove == 1) {
                                $form.find(".add_sign").prop("checked", "true");
                                $form.find(".add-person-num").css("visibility", "visible");
                                $form.find(".add-person-num input").val(block.addApprove_count);
                            } else {
                                $form.find(".add_sign").prop("checked", "");
                                $form.find(".add-person-num").css("visibility", "hidden");
                                $form.find(".add-person-num input").val("");
                            }
                            //是否是直到节点
                            if (block.is_until_block == 1) {
                                $form.find(".is_until_block").prop("checked", "true");
                            } else {
                                $form.find(".is_until_block").prop("checked", "");
                            }
                            //是否是审批节点
                            if (block.is_approve_block == 1) {
                                $form.find(".is_approve_block").prop("checked", "true");
                            } else {
                                $form.find(".is_approve_block").prop("checked", "");
                            }
                            break;
                        }
                    }

                    //审批人数据
                    var authGroupMember = {};
                    var flag = 0;
                    for (var i = 0; i < authGroups.length; i++) {
                        authGroupMember = authGroups[i];
                        // 表示存在
                        if (authGroupMember[0] != null && authGroupMember[0].block == id) {
                            flag = 1;
                            break;
                        }
                    }

                    // 如果有审批人数据
                    if (flag == 1) {
                        AuthGroupMember(
                            '.deptAndRole_column',
                            '.userDiv_column',
                            authGroupMember,
                            '.select_department_column',
                            '.select_role_column',
                            '.select_user_column');
                    } else {
                        AuthGroupMember('.deptAndRole_column', '.userDiv_column',
                            null, '.select_department_column', '.select_role_column',
                            '.select_user_column');
                    }
                    uiinit();
                }
            });
        }
    }

    //设置通知方式，更多事件页面显示的值
    function setInformMannerAndMoreEvents(blockEvent, informMannerClass, moreEventsClass) {
        //事件名称
//        if (blockEvent.event_type == "workEvent") {
//            $form.find(".moreEvent_Name").html(blockEvent.i18n[dx.user.language_id]);
//        }
        //通知方式
        if (blockEvent.email == 1 && blockEvent.sms == 1) {
            $(informMannerClass).html(msg('email_inform') + "," + msg('sms_inform'));
        } else {
            if (blockEvent.email == 1) {
                $(informMannerClass).html(msg('email_inform'));
            } else if (blockEvent.sms == 1) {
                $(informMannerClass).html(msg('sms_inform'));
            } else {
                $(informMannerClass).html(msg('more_event_null'));
            }
        }
        //更多事件
        if (!isEmpty(blockEvent.event)) {
            $(moreEventsClass).empty();
            var moreEvent = blockEvent.event;
            for (var key in moreEvent) {
                var urlInterface = dx.urlInterface[moreEvent[key].event_name];
                var eventName = null;
                if (!isEmpty(urlInterface)) {
                    if (!isEmpty(urlInterface.i18n)) {
                        eventName = urlInterface.i18n[dx.user.language_id];
                    }else{
                    	eventName = urlInterface.name;
                    }
                }
                $(moreEventsClass).append('<p>' + eventName + '</p>');
            }
        } else {
            $(moreEventsClass).empty();
            $(moreEventsClass).append('<p>' + msg("more_event_null") + '</p>');
        }
    }

    //按钮编辑的点击事件
    $form.find(".approve-button-form").on("click", ".eventBtnEdit", function () {
        var $thisWrap = $(this).parents(".eventlist");
        $thisWrap.removeClass("renameStatus").addClass("editStatus");
        //$thisWrap.find(".eventListText").append('<input type="text" class="btnEditIpt input-1"/>');
        $thisWrap.find(".eventListText").append('<div class="btnEditIpt buttonEditI18N"></div>');
        //移除国际化控件
        var buttonEditI18N = $form.find(".buttonEditI18N");
        $form.find(".buttonEditI18N").html("");
        var internationalId=$(this).attr("internationalId");
        var interValue=$(this).attr("interValue");
        if(!isEmpty(internationalId) || !isEmpty(interValue)){
        	 //初始化国际化控件
        	if(isEmpty(internationalId)){
        		internationalId="";
        	}
            international(buttonEditI18N, internationalId, interValue,"",
                function (submitData) {
            	
            }, function (selectData) {

            });
//            if ($thisWrap.find('.event-subhead').html()) {
//                $thisWrap.find('.btnEditIpt').val($thisWrap.find('.event-subhead').html());
//            }
        }else{
        	 //初始化国际化控件
            international(buttonEditI18N, "", "", "",
                function (submitData) {
            	
            }, function (selectData) {
            	
            });
        }

    });
    //按钮删除的点击事件
    $form.find(".approve-button-form").on("click", ".eventBtnDelete", function () {
    	var $thisWrap = $(this).parents(".eventlist");
    	var flow_event_id=$thisWrap.attr("flow_event_id");
    	var blockId = $(".hidden_node_id").val();
    	delete approveButtonEvent[blockId][flow_event_id];
    	$(this).parents(".eventlist").remove();

    });
    //重命名确认按钮
    $form.find(".approve-button-form").on("click", ".eventBtnRename", function () {
    	var $thisWrap = $(this).parents(".eventlist");
    	$thisWrap.removeClass("addAppBtnIng");
    	var flow_event_id=$thisWrap.attr("flow_event_id");
    	var blockId = $(".hidden_node_id").val();

    	if(isEmpty(flow_event_id)){
    		flow_event_id=uuid(10,16);
    		$thisWrap.attr("flow_event_id",flow_event_id);
    		approveButtonEvent[blockId][flow_event_id]={};
    		approveButtonEvent[blockId][flow_event_id].event_type="workEvent";
    		approveButtonEvent[blockId][flow_event_id].table_id=tableName;
    		approveButtonEvent[blockId][flow_event_id].flow_event_id=flow_event_id;
    		approveButtonEvent[blockId][flow_event_id].international_id="";
    		approveButtonEvent[blockId][flow_event_id].i18n={};
    		approveButtonEvent[blockId][flow_event_id].block_id=blockId;
    		approveButtonEvent[blockId][flow_event_id].condition=[];
    		approveButtonEvent[blockId][flow_event_id].email=0;
    		approveButtonEvent[blockId][flow_event_id].event={};
    		approveButtonEvent[blockId][flow_event_id].is_index_button=0;
    		approveButtonEvent[blockId][flow_event_id].sms=0;
    		approveButtonEvent[blockId][flow_event_id].table_id=tableName;
    	}
    	$thisWrap.find(".event-subhead").remove();
    	var getInternational = $form.find(".buttonEditI18N").internationalControl(true).getData();
    	if(isEmpty(getInternational.international_id)){
    		if(isEmpty(getInternational.interValue)){
    			if ($thisWrap.parent().hasClass('approve-button-work-wrap') && !$thisWrap.hasClass("eventListAgree")) {
                    //新增的按钮名不能为空
                    alert('按钮名称不能为空！');
                } else {
                    //原有的按钮，重命名可以为空
                    $thisWrap.removeClass("renameStatus");
                    $thisWrap.removeClass("editStatus");
                    $thisWrap.find(".btnEditIpt").remove();
                    $thisWrap.find(".eventBtnEdit").attr("internationalId","");
                    $thisWrap.find(".eventBtnEdit").attr("interValue","");
            		approveButtonEvent[blockId][flow_event_id].international_id="";
            		approveButtonEvent[blockId][flow_event_id].i18n={};
                }
    		}else{
    			$thisWrap.addClass("renameStatus");
                $thisWrap.find(".eventListText").append('<span class="event-subhead">' + getInternational.interValue + '</span>');
                $thisWrap.removeClass("editStatus");
                $thisWrap.find(".btnEditIpt").remove();
                $thisWrap.find(".eventBtnEdit").attr("internationalId",getInternational.international_id);
                $thisWrap.find(".eventBtnEdit").attr("interValue",getInternational.interValue);
        		var i18n={};
        		i18n.international_id=getInternational.international_id;
        		i18n[dx.user.language_id]=getInternational.interValue;
        		approveButtonEvent[blockId][flow_event_id].international_id=getInternational.international_id;
        		approveButtonEvent[blockId][flow_event_id].i18n=i18n;
    		}
    	}else{
    		$thisWrap.addClass("renameStatus");
            $thisWrap.find(".eventListText").append('<span class="event-subhead">' + getInternational[dx.user.language_id] + '</span>');
            $thisWrap.removeClass("editStatus");
            $thisWrap.find(".btnEditIpt").remove();
            $thisWrap.find(".eventBtnEdit").attr("internationalId",getInternational.international_id);
            $thisWrap.find(".eventBtnEdit").attr("interValue",getInternational[dx.user.language_id]);
    		approveButtonEvent[blockId][flow_event_id].international_id=getInternational.international_id;
    		approveButtonEvent[blockId][flow_event_id].i18n=getInternational;
    	}
    });

    //添加新按钮
    $form.find('.approve-button-work').on("click", ".addNewButton", function () {
    	if($form.find('.approve-button-work-wrap .addAppBtnIng').length<=0){
//    		 $form.find('.approve-button-work-wrap').append('<div class="eventlist editStatus addButtonDiv" flow_event_id="">'
//    		            + '<div class="input-1 eventListText">'
//    		            + '<div class="btnEditIpt input-1 buttonEditI18N"></div>'
//    		            + '</div>'
//    		            + '<button class="button-red-font eventBtnDelete" type="button">删除</button>'
//    		            + '<button class="button-blue-font eventBtnEdit" internationalId="" interValue="" type="button">编辑</button>'
//    		            + '<button class="button-color3 eventBtnRename" type="button">确认</button>'
//    		            + '<button class="button-color3 eventBtnCancel" type="button">取消</button>'
//    		            + '</div>');

            $form.find('.approve-button-work-wrap .eventlist').removeClass("addAppBtnIng");
    		 $form.find('.approve-button-work-wrap').append('<div class="eventlist workBlockBtn addAppBtnIng" flow_event_id="">'
    				 + '<div class="dx-field-5 eventListText">'
    			//+ '<span class="event-subhead">终止</span>'
    				 + '</div>'
    				 + '<div class="dx-field-6">'
    				 + '<button class="button-color3 eventBtnRename" type="button">确认</button>'
    				 + '<button class="button-color4 eventBtnCancel" type="button">取消</button>'
    				 + '<div class="dropdown">'
    				 + '<button type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">'
    				 + '<span class="fa fa-ellipsis-h"></span>'
    				 + '</button>'
    				 + '<ul class="dropdown-menu">'
    				 + '<li>'
    				 + '<button class="button-blue-font eventBtnEdit" internationalId="" interValue="" type="button">编辑</button>'
    				 + '</li>'
    				 + '<li>'
    				 + '<button class="button-red-font eventBtnDelete" type="button">删除</button>'
    				 + '</li>'
    				 + '</ul>'
    				 + '</div>'
    				 + '<div class="input-bt-Switcher-wrap">'
    				 + '<div class="input-bt-Switcher">'
    				 + '<input type="checkbox" class="is_index_button"/><label></label>'
    				 + '</div></div></div> </div>');
    		 $(".addAppBtnIng .eventBtnEdit").click();
    		 $(".approve-button-form").getNiceScroll().resize();
    		 var buttonEditI18N = $form.find(".buttonEditI18N");
    	     $form.find(".buttonEditI18N").html("");
    		 //初始化国际化控件
             international(buttonEditI18N, "", "", "",
                 function (submitData) {
             	
             }, function (selectData) {
             	
             });
    	}
    });
    //添加新按钮 取消
    $form.find(".approve-button-form").on("click", ".eventBtnCancel",function() {
        var itemWrap=$(this).parents(".eventlist");
        if(itemWrap.find(".event-subhead").length||itemWrap.parent().hasClass("approve-button-nowork")||itemWrap.hasClass("eventListAgree")){
            itemWrap.removeClass("editStatus");
            itemWrap.find(".btnEditIpt").remove();
        }else{
            itemWrap.remove();
        }
    });
    // 改变节点的text,改变块上的文字
    $form.find(".approve_name_I18N").on("change", ".retrievalInter", function () {
        var hidden_node_id = $(".hidden_node_id").val();
        $("#" + hidden_node_id).text($(this).val());
        var getInternational = $(".approve_name_I18N").internationalControl(true).getData();
        blockI18N.approve_name = getInternational.international_id;
        if (isEmpty(getInternational.international_id)) {
            blockI18N.approve_name_I18N = {};
            blockI18N.approve_name_I18N.international_id = "";
            blockI18N.approve_name = "";
            blockI18N.text = getInternational.interValue;
            blockI18N.approve_name_I18N[dx.user.language_id] = getInternational.interValue;
        } else {
            blockI18N.approve_name = getInternational.international_id;
            blockI18N.approve_name_I18N = getInternational;
            blockI18N.text = getInternational[dx.user.language_id];
        }
    });
    //审批按钮复选框的点击事件
    $form.find('.buttonEventIsUsing').on('ifChecked', function () {
    	var flow_event_id=$(this).parents(".eventlist").attr("flow_event_id");
    	var blockId = $(".hidden_node_id").val();
    	if(isEmpty(flow_event_id)){
    		flow_event_id=uuid(10,16);
    		$(this).parents(".eventlist").attr("flow_event_id",flow_event_id);
    		if($(this).parents().hasClass("agreeEvent")){
    			defaultButtonEvent(blockId,"agreeEvent",1,"agreeevent_button",flow_event_id);
    		}else if($(this).parents().hasClass("disagreeEvent")){
    			defaultButtonEvent(blockId,"disagreeEvent",2,"disagreeevent_button",flow_event_id);
    		}else if($(this).parents().hasClass("rejectEvent")){
    			defaultButtonEvent(blockId,"rejectEvent",3,"rejectevent_button",flow_event_id);
    		}else if($(this).parents().hasClass("retainEvent")){
    			defaultButtonEvent(blockId,"retainEvent",4,"retainevent_button",flow_event_id);
    		}else if($(this).parents().hasClass("terminationEvent")){
    			defaultButtonEvent(blockId,"terminationEvent",5,"terminationevent_button",flow_event_id);
    		}
    	}
    	if ($(this).parents(".eventlist").find(".is_index_button").is(":checked")) {
    		approveButtonEvent[blockId][flow_event_id].is_index_button=1;
        } else {
        	approveButtonEvent[blockId][flow_event_id].is_index_button=0;
        }
    }).on('ifUnchecked', function () {
    	
    });
    //审批中心显示的点击事件
//    $form.find(".is_index_button").off('click');
    $form.find(".approve-button-form").on("click", ".is_index_button",function () {
    	var flow_event_id=$(this).parents(".eventlist").attr("flow_event_id");
    	var blockId = $(".hidden_node_id").val();
    	if(!isEmpty(flow_event_id)){
    		if ($(this).is(":checked")) {
    			approveButtonEvent[blockId][flow_event_id].is_index_button=1;
            } else {
            	approveButtonEvent[blockId][flow_event_id].is_index_button=0;
            }
    	}
    });
    //审批中心是否显示
    function approveCentreIsShow(){
    	$form.find('.approveCentreIsShow').append('<div class="form-group">'
                +'<label class="label-1">同意</label>'
                +'<div class="input-bt-Switcher-wrap">'
                +'<div class="input-bt-Switcher">'
                +'<input type="checkbox" class="is_index_button"/><label></label>'
                +'</div>'
                +'</div></div>');
    }
    //工作节点的点击事件
    $form.find('.work_block').on('ifChecked', function () {
    	$form.find('.workBlockBtn').remove();
        $form.find(".selected-work-wrap").hide();
        $form.find(".approve-button-nowork").hide();
        $form.find(".approve-button-work").show();
        var blockId = $(".hidden_node_id").val();
        for (var i = 0; i < blocks.length; i++) {
            if (blocks[i].block_id == blockId) {
                blocks[i].is_work_block = 1;
            }
        }
        var flag=true;
        for(var key in approveButtonEvent[blockId]){
        	if(approveButtonEvent[blockId][key].event_type=="finishEvent"){
        		flag=false;
        		break;
        	}else if(approveButtonEvent[blockId][key].event_type=="workEvent"){
        		delete approveButtonEvent[blockId][key];
        	}
        }
        if(flag){
    		defaultButtonEvent(blockId,"finishEvent",1,"finishevent_button",uuid(10,16));
    		$form.find('.eventListAgree .input-bt-Switcher-wrap input').prop("checked", "true");
    	}
    }).on('ifUnchecked', function () {
        $form.find(".selected-work-wrap").show();
        $form.find(".approve-button-work").hide();
        $form.find(".approve-button-nowork").show();
        var blockId = $(".hidden_node_id").val();
        for (var i = 0; i < blocks.length; i++) {
            if (blocks[i].block_id == blockId) {
                blocks[i].is_work_block = 0;
            }
        }
        var flag=true;
        for(var key in approveButtonEvent[blockId]){
        	if(approveButtonEvent[blockId][key].event_type=="agreeEvent" 
        			|| approveButtonEvent[blockId][key].event_type=="disagreeEvent"
        			|| approveButtonEvent[blockId][key].event_type=="rejectEvent"
        			|| approveButtonEvent[blockId][key].event_type=="retainEvent"
        			|| approveButtonEvent[blockId][key].event_type=="terminationEvent"){
        		flag=false;
        		break;
        	}
        }
        if(flag){
    		defaultButtonEvent(blockId,"agreeEvent",1,"agreeevent_button",uuid(10,16));
        	defaultButtonEvent(blockId,"rejectEvent",3,"rejectevent_button",uuid(10,16));
    	}
        $form.find(".agreeEvent .is_index_button").prop("checked", "true");
        $form.find(".agreeEvent .buttonEventIsUsing").iCheck("check");
        $form.find(".rejectEvent .is_index_button").prop("checked", "true");
        $form.find(".rejectEvent .buttonEventIsUsing").iCheck("check");
    });
    //表格新增行
    $form.find('.approve-event-add-row').on('click', function () {
        var grid = $(this).parent().parent().find('.deploy-grid');
        addGridRow(grid);
    });
    //表格删除行
    $form.find('.approve-event-delete-row').on('click', function () {
        var grid = $(this).parent().parent().find('.deploy-grid');
        var selectRows = grid.datagrid('getSelections');
        for (var i = 0; i < selectRows.length; i++) {
            deleteGridRows(grid, grid.datagrid('getRowIndex', selectRows[i]));
        }
    });

    //新增行
    function addGridRow($node, row) {
        if (isEmpty(row)) {
            endAllRowEdit($node);
            var approve_event_id = $form.find("#approve-event-modal").attr("approve_event_id");
            $($node).datagrid('appendRow', {"table_action_id": approve_event_id});
            var rows = $node.datagrid('getRows');
            var lastIndex = rows.length - 1;
            $($node).datagrid('unselectAll');
            $($node).datagrid('selectRow', lastIndex);
            $($node).datagrid('beginEdit', lastIndex);
        }
    }
    //删除行
    function deleteGridRows($node, index) {
        $($node).datagrid('deleteRow', index);
    }
    //结束所有行编辑
    function endAllRowEdit($node) {
        var rows = $node.datagrid('getRows');
        if (isEmpty(rows))
            return;
        for (var i = 0; i < rows.length; i++) {
            $($node).datagrid('endEdit', $($node).datagrid('getRowIndex', rows[i]));
        }
    }

    //添加审批事件
    $form.find(".approve-event-form").on("click", ".approve-add-event-btn", function () {
        $('#approve-event-modal').addClass("add-event-modal");
        $('#approve-event-modal').find(".approve-add-event-title").show();
        $('#approve-event-modal').attr("moreEventId", uuid(10, 16));
        $form.find('.approve-event-modal .action_prerequistie').datagrid('loadData', []);
        $form.find('.approve-event-modal .action_event').datagrid('loadData', []);
        $('.event_email').iCheck("uncheck");
        $('.event_sms').iCheck("uncheck");
        //加载国际化控件
        international($form.find(".approve-event-name"), "", "", "",
            function (submitData) {
                submitData;
            }, function (selectData) {
                selectData;
            });
    });
    //编辑审批事件
    $form.find(".approve-event-form").on("click", ".approve-update-event-btn", function () {
//        var eventType = $(this).parent().attr("Event-Type");
//        $('#approve-event-modal').attr("event_Type", eventType);
        $('#approve-event-modal').addClass("update-event-modal");
        var block_event_id= $(this).parents(".form-group").attr("moreEventId");
        $('#approve-event-modal').attr("moreEventId", block_event_id);
        var approve_event_id= $(this).parents(".form-group").attr("approve_event_id");
        $('#approve-event-modal').attr("approve_event_id", approve_event_id);
        if (!isEmpty(block_event_id)) {
            var blockId = $(".hidden_node_id").val();
            var blockEvent = approveBlockEvent[blockId][block_event_id];
            if ($(this).parents(".form-group").hasClass("work_events")) {
                $('#approve-event-modal').find(".approve-add-event-title").show();
                //加载国际化控件
                international($form.find(".approve-event-name"), blockEvent.international_id, blockEvent.i18n[dx.user.language_id], "",
                    function (submitData) {
                        submitData;
                    }, function (selectData) {
                        selectData;
                    });
            }
            if (blockEvent.email == 1) {
                $('.event_email').iCheck("check");
            } else {
                $('.event_email').iCheck("uncheck");
            }
            if (blockEvent.sms) {
                $('.event_sms').iCheck("check");
            } else {
                $('.event_sms').iCheck("uncheck");
            }
            if (isEmpty(blockEvent.event)) {
                $form.find('.approve-event-modal .action_event').datagrid('loadData', []);
            } else {
                var event = [];
                for (var key in blockEvent.event) {
                    var cloneObj = cloneObject(blockEvent.event[key]);
                    cloneObj.event_param = formatSql(cloneObj.event_param);
                    event.push(cloneObj);
                }
                dataOrder(event);
                $form.find('.approve-event-modal .action_event').datagrid('loadData', event);
            }
        }
    });
    //删除审批事件
    $form.find(".approve-event-form").on("click", ".approve-delete-event-btn", function () {
        var flow_event_id = $(this).parents(".work_events").attr("moreEventId");
        var blockId = $(".hidden_node_id").val();
        delete approveButtonEvent[blockId][flow_event_id]
        $(this).parents(".work_events").remove();
    });
    //打开审批事件弹出框
    $('#approve-event-modal').on('shown.bs.modal', function (e) {
        $form.find('.approve-event-modal .action_prerequistie').datagrid('resize');
        $form.find('.approve-event-modal .action_event').datagrid('resize');
    });
    //关闭审批事件弹出框
    $('#approve-event-modal').on('hidden.bs.modal', function (e) {
        $(this).find(".approve-add-event-title").hide();
        $('#approve-event-modal').removeClass("add-event-modal").removeClass("update-event-modal");
        //移除国际化控件
        $form.find(".approve-event-name").html("");
    });

    //保存添加或编辑的审批事件
    $form.find("#approve-event-modal").on("click", ".event-edit-add-Save", function () {
        var eventDataGrid = $form.find('.approve-event-modal .action_event');
        endAllRowEdit(eventDataGrid);
        var eventRow = eventDataGrid.datagrid('getRows');
        var eventRows = [];
        for (var i = 0; i < eventRow.length; i++) {
        	if(!isEmpty(eventRow[i].event_type) && !isEmpty(eventRow[i].event_name)){
        		eventRows.push(eventRow[i]);
        	}
        }
        var blockId = $(".hidden_node_id").val();
        if ($(this).parents(".modal").hasClass("update-event-modal")) {
            //编辑保存
            var block_event_id = $form.find("#approve-event-modal").attr("moreEventId");
            var approve_event_id = $form.find("#approve-event-modal").attr("approve_event_id");
            if(isEmpty(approveBlockEvent[blockId][block_event_id])){
        		approveBlockEvent[blockId][block_event_id] = {};
        	}
            approveBlockEvent[blockId][block_event_id].block_id = blockId;
            if ($form.find(".approve-event-modal .icheckbox_flat-blue .event_email").is(":checked")) {
            	approveBlockEvent[blockId][block_event_id].email = 1;
            } else {
            	approveBlockEvent[blockId][block_event_id].email = 0;
            }
            if ($form.find(".approve-event-modal .icheckbox_flat-blue .event_sms").is(":checked")) {
            	approveBlockEvent[blockId][block_event_id].sms = 1;
            } else {
            	approveBlockEvent[blockId][block_event_id].sms = 0;
            }
            var eventMap = {};
            for (var i = 0; i < eventRows.length; i++) {
                eventRows[i].event_id = uuid(10, 16);
                eventRows[i].seq=i+1;
                eventRows[i].event_param=replaceEventParam(eventRows[i].event_param);
                eventMap[eventRows[i].event_id] = eventRows[i];
            }
            approveBlockEvent[blockId][block_event_id].event = eventMap;
            approveBlockEvent[blockId][block_event_id].event_type = block_event_id;
            approveBlockEvent[blockId][block_event_id].approve_event_id = approve_event_id;
            approveBlockEvent[blockId][block_event_id].table_id = tableName;
            var informMannerClass = $form.find(".blockEvent_"+block_event_id+" .informManner");
            var moreEvents = $form.find(".blockEvent_"+block_event_id+" .moreEvents");
            setInformMannerAndMoreEvents(approveBlockEvent[blockId][block_event_id], informMannerClass, moreEvents);
        } else if ($(this).parents(".modal").hasClass("add-event-modal")) {
            //新增保存
            var flow_event_id = $form.find("#approve-event-modal").attr("moreEventId");
            approveButtonEvent[blockId][flow_event_id] = {};
            approveButtonEvent[blockId][flow_event_id].block_id = blockId;
            approveButtonEvent[blockId][flow_event_id].condition = prerequistieRows;
            if ($form.find(".approve-event-modal .icheckbox_flat-blue .event_email").is(":checked")) {
                approveButtonEvent[blockId][flow_event_id].email = 1;
            } else {
                approveButtonEvent[blockId][flow_event_id].email = 0;
            }
            if ($form.find(".approve-event-modal .icheckbox_flat-blue .event_sms").is(":checked")) {
                approveButtonEvent[blockId][flow_event_id].sms = 1;
            } else {
                approveButtonEvent[blockId][flow_event_id].sms = 0;
            }
            var eventMap = {};
            for (var i = 0; i < eventRows.length; i++) {
                eventRows[i].event_id = uuid(10, 16);
                eventRows[i].event_param=replaceEventParam(eventRows[i].event_param);
                eventMap[eventRows[i].event_id] = eventRows[i];
            }
            approveButtonEvent[blockId][flow_event_id].event = eventMap;
            approveButtonEvent[blockId][flow_event_id].event_type = "workEvent";
            approveButtonEvent[blockId][flow_event_id].flow_event_id = flow_event_id;
            var getInternational = $form.find(".approve-event-name").internationalControl(true).getData();
            var i18n = {}
            var internationalId = "";
            if (isEmpty(getInternational.international_id)) {
                i18n.international_id = "";
                i18n[dx.user.language_id] = getInternational.interValue;
            } else {
                i18n = getInternational;
                internationalId = getInternational.international_id;
            }
            approveButtonEvent[blockId][flow_event_id].i18n = i18n;
            approveButtonEvent[blockId][flow_event_id].international_id = internationalId;
            approveButtonEvent[blockId][flow_event_id].table_id = tableName;
            $form.find(".flowEvent_workEvent_all").append('<div class="form-group clearfix work_events" moreEventId="' + flow_event_id + '">' +
                '<div class="dx-field-10 clearfix event-detail-wrap">' +
                '<h4 class="moreEvent_Name">' + i18n[dx.user.language_id] + '</h4>' +
                '<div>' +
                '<span class="event-detail-tip">通知方式</span>' +
                '<p class="informManner"></p>' +
                '</div>' +
                '<div>' +
                '<span class="event-detail-tip">更多事件</span>' +
                '<div class="pull-left moreEvents">' +
                '</div>' +
                '</div>' +
                '</div>' +
                '<div class="dx-field-2 approve-event-btns" Event-Type="flowEvent_workEvent">' +
                '<button class="button-blue-font approve-update-event-btn" data-toggle="modal" data-target="#approve-event-modal" type="button">编辑</button>' +
                '<button class="button-red-font approve-delete-event-btn" type="button">删除</button>' +
                '</div>' +
                '</div>');
            setInformMannerAndMoreEvents(approveButtonEvent[blockId][flow_event_id], $form.find(".work_events[moreEventId='" + flow_event_id + "'] .informManner"),
                $form.find(".work_events[moreEventId='" + flow_event_id + "'] .moreEvents"));
        }
    });
    //默认的审批事件
    function defaultApproveEvent(blockId,event_type,isNewBlock) {
    	if (isEmpty(approveBlockEvent)) {
    		approveBlockEvent = {};
        }
        if (isEmpty(approveBlockEvent[blockId])) {
        	approveBlockEvent[blockId] = {};
        }
        var approve_event_id=uuid(10,16);
        approveBlockEvent[blockId][event_type] = {};
        approveBlockEvent[blockId][event_type].block_id = blockId;
        approveBlockEvent[blockId][event_type].email = 0;
        approveBlockEvent[blockId][event_type].sms = 0;
        approveBlockEvent[blockId][event_type].event = {};
        approveBlockEvent[blockId][event_type].event_type = event_type;
        approveBlockEvent[blockId][event_type].approve_event_id = approve_event_id;
        approveBlockEvent[blockId][event_type].table_id = tableName;
        if(isNewBlock){
        	if (isEmpty(initApproveBlockEvent)) {
        		initApproveBlockEvent = {};
            }
            if (isEmpty(initApproveBlockEvent[blockId])) {
            	initApproveBlockEvent[blockId] = {};
            }
            initApproveBlockEvent[blockId][event_type] = {};
            initApproveBlockEvent[blockId][event_type].block_id = blockId;
            initApproveBlockEvent[blockId][event_type].email = 0;
            initApproveBlockEvent[blockId][event_type].sms = 0;
            initApproveBlockEvent[blockId][event_type].event = {};
            initApproveBlockEvent[blockId][event_type].event_type = event_type;
            initApproveBlockEvent[blockId][event_type].approve_event_id = approve_event_id;
            initApproveBlockEvent[blockId][event_type].table_id = tableName;
        }
    }
    //默认的审批按钮
    function defaultButtonEvent(blockId,event_type,seq,international_id,flow_event_id,isNewBlock) {
    	if (isEmpty(approveButtonEvent)) {
    		approveButtonEvent = {};
        }
        if (isEmpty(approveButtonEvent[blockId])) {
        	approveButtonEvent[blockId] = {};
        }
        approveButtonEvent[blockId][flow_event_id] = {};
        approveButtonEvent[blockId][flow_event_id].block_id = blockId;
        approveButtonEvent[blockId][flow_event_id].email = 0;
        approveButtonEvent[blockId][flow_event_id].sms = 0;
        approveButtonEvent[blockId][flow_event_id].seq = seq;
        approveButtonEvent[blockId][flow_event_id].event = {};
        approveButtonEvent[blockId][flow_event_id].condition = [];
        approveButtonEvent[blockId][flow_event_id].event_type = event_type;
        approveButtonEvent[blockId][flow_event_id].flow_event_id = flow_event_id;
        approveButtonEvent[blockId][flow_event_id].international_id = international_id;
        approveButtonEvent[blockId][flow_event_id].i18n = dx.i18n.message[international_id];
        approveButtonEvent[blockId][flow_event_id].is_using = 1;
        approveButtonEvent[blockId][flow_event_id].is_index_button= 1;
        approveButtonEvent[blockId][flow_event_id].table_id = tableName;
        if(isNewBlock){
        	if (isEmpty(initApproveButtonEvent)) {
        		initApproveButtonEvent = {};
            }
            if (isEmpty(initApproveButtonEvent[blockId])) {
            	initApproveButtonEvent[blockId] = {};
            }
            initApproveButtonEvent[blockId][flow_event_id] = {};
            initApproveButtonEvent[blockId][flow_event_id].block_id = blockId;
            initApproveButtonEvent[blockId][flow_event_id].email = 0;
            initApproveButtonEvent[blockId][flow_event_id].sms = 0;
            initApproveButtonEvent[blockId][flow_event_id].seq = seq;
            initApproveButtonEvent[blockId][flow_event_id].event = {};
            initApproveButtonEvent[blockId][flow_event_id].condition = [];
            initApproveButtonEvent[blockId][flow_event_id].event_type = event_type;
            initApproveButtonEvent[blockId][flow_event_id].flow_event_id = flow_event_id;
            initApproveButtonEvent[blockId][flow_event_id].international_id = international_id;
            initApproveButtonEvent[blockId][flow_event_id].i18n = dx.i18n.message[international_id];
            initApproveButtonEvent[blockId][flow_event_id].is_using = 1;
            initApproveButtonEvent[blockId][flow_event_id].is_index_button= 1;
            initApproveButtonEvent[blockId][flow_event_id].table_id = tableName;
        }
    }

    // 保存审批人,审批配置，审批事件，审批按钮
    $(".confirm-approve-config-user").on("click", function () {
        var block_id = $(".hidden_node_id").val();
        var block_source = $("#" + block_id);
        var config_radio = $('input:radio[name="config-radio"]:checked').val();
        var turn_down_up = 0;
        var turn_down_source = 0;
        if (config_radio == "turn_down_up") {
            turn_down_up = 1;
        } else {
            turn_down_source = 1;
        }
        // 把blocks里对应的对象更新
        var block;
        var seq = 0;
        for (var i = 0; i < blocks.length; i++) {
            block = blocks[i];
            // 找到blocks数组里有这个节点
            if (block.block_id == block_id && block.type!="startNode") {
                block.text = block_source.text();
                block.block_x = parseInt(block_source.css("left"), 10);
                block.block_y = parseInt(block_source.css("top"), 10);
                var getInternational = $(".approve_name_I18N").internationalControl(true).getData();
                block.approve_name = getInternational.international_id;
                if (isEmpty(getInternational.international_id)) {
                    block.approve_name_I18N = {};
                    block.approve_name_I18N.international_id = "";
                    block.approve_name = "";
                    block.text = getInternational.interValue;
                    block.approve_name_I18N[dx.user.language_id] = getInternational.interValue;
                } else {
                    block.approve_name = getInternational.international_id;
                    block.approve_name_I18N = getInternational;
                    block.text = getInternational[dx.user.language_id];
                }
                block.task_msg = $(".task_msg").val();
                block.turn_down_up = turn_down_up;
                block.turn_down_source = turn_down_source;
                block.time_limit = $(".time_limit").val();
                block.time_out_rule = $('.time_out_rule').val();
                block.need_agree_count_people = $('.need_agree_count_people').val();
                block.need_opinion_count_people = $('.need_opinion_count_people').val();
                if ($form.find(".add_sign").prop("checked")) {
                    block.is_addApprove = 1;
                    block.addApprove_count = $form.find(".add-person-num input").val();
                } else {
                    block.is_addApprove = 0;
                    block.addApprove_count = 0;
                }
                block.is_until_block = $form.find(".is_until_block").is(":checked") ? 1 : 0;
                block.is_approve_block = $form.find(".is_approve_block").is(":checked") ? 1 : 0;
                break;
            }
        }

        var list_deptAndRole = $('.deptAndRole_column div');
        var list_user = $('.userDiv_column div');
        // 如果审批人没有配置，则跳到审批人配置
        if (list_deptAndRole.length == 0 && list_user.length == 0) {
            $(".ap").click();
        }
        // 保存审批人
        addAuthGroup('deptAndRole_column>div', 'userDiv_column>div', tableName, block_id, '', 4);
        
        //保存审批事件
        if(!isEmpty(approveBlockEvent)){
        	var blockEvent=approveBlockEvent[block_id];
            var blockEventMap={}
            for(var key in blockEvent){
        		blockEventMap[key]=blockEvent[key];
            }
            initApproveBlockEvent[block_id]=blockEventMap;
        }
        //保存审批按钮
        if(!isEmpty(approveButtonEvent)){
        	var buttonEvent=approveButtonEvent[block_id];
            var buttonEventMap={}
            for (var i = 0; i < blocks.length; i++) {
            	if(blocks[i].block_id==block_id){
            		if(blocks[i].is_work_block==1){
                		for(var key in buttonEvent){
                			if(buttonEvent[key].event_type == "finishEvent" || buttonEvent[key].event_type == "workEvent"){
                				buttonEventMap[key]=buttonEvent[key];
                			}
                		}
                	}else{
                		for(var key in buttonEvent){
                        	if($form.find("."+buttonEvent[key].event_type+" .buttonEventIsUsing").is(":checked")){
                        		buttonEvent[key].is_using==1
                        		buttonEventMap[key]=buttonEvent[key];
                        	}else{
                        		buttonEvent[key].is_using==0;
                        	}
                        }
                	}
            	}
            }
            initApproveButtonEvent[block_id]=buttonEventMap;
        }
        dxToastAlert("节点已保存");
    });

    // 保存模板
    $form.find('.saveTemplate_column').on("click", function () {
        var template_name = $(this).parent().find('input').val();
        addAuthGroup('deptAndRole_column>div','userDiv_column>div', '', '', template_name, 4);
    });

    // 从模板中选择
    $form.find('.lookAllTemplate_column').on("click", function () {
        $(this).parent().find('ul').empty();
        var authGroup = new Array();
        var param = {};
        param.table = tableName;
        param.type = 4;
        authGroup.push(param);
        lookAllTemplate(authGroup, ".lookAllTemplate_column",
            "template_column", ".deptAndRole_column",
            ".userDiv_column", '.select_department_column',
            '.select_role_column', '.select_user_column');
    });

    //模板管理点击事件
    $form.find('#approve-tag-modal').on('show.bs.modal', function (e) {
        $form.find('.modal-body').empty();
        for (var i = 0; i < allTemplate.length; i++) {
            allTemplate[i];
            $form.find('.modal-body').append('<div class="dx-field-12 tab-list">' +
                '<div class="dx-field-7"><input class="input-1" type="text" disabled value="' + allTemplate[i].template + '"/></div>' +
                '<div class="dx-field-5"><button class="tag-edit" type="button">编辑</button><button class="tag-delete" ' +
                'type="button">删除</button><button class="tag-save button-color3" type="button">确认</button></div></div>');
        }
    });
    var templateEditOrDelete = {};
    $form.find(".model-tag-modal").on("click", ".tag-delete", function () {
        templateEditOrDelete = {};
        for (var i = 0; i < allTemplate.length; i++) {
            if (allTemplate[i].template == $(this).parents(".tab-list").find("input").val()) {
                templateEditOrDelete = allTemplate[i];
                break;
            }
        }
        templateEditOrDelete.editOrdetele = 2;
        var template = [];
        template.push(templateEditOrDelete);
        postJson('/approve/saveTemplate.do', {authGroup: template}, function (result) {
            dxToastAlert(msg('success'));
        });
        $(this).parents(".tab-list").remove();
    });

    $form.find(".model-tag-modal").on("click", ".tag-edit", function () {
        templateEditOrDelete = {};
        for (var i = 0; i < allTemplate.length; i++) {
            if (allTemplate[i].template == $(this).parents(".tab-list").find("input").val()) {
                templateEditOrDelete = allTemplate[i];
                break;
            }
        }
        $(this).parents(".tab-list").find("button").hide();
        $(this).parents(".tab-list").find(".tag-save").show();
        $(this).parents(".tab-list").find("input").removeAttr("disabled").focus();
    });
    //保存模板
    $form.find(".model-tag-modal").on("click", ".tag-save", function () {
        if (isEmpty($(this).parents(".tab-list").find("input").val())) {
            alert("模板名称不能为空！");
        } else {
            templateEditOrDelete.editOrdetele = 1;
            templateEditOrDelete.editTemplateName = $(this).parents(".tab-list").find("input").val();
            var template = [];
            template.push(templateEditOrDelete);
            postJson('/approve/saveTemplate.do', {authGroup: template}, function (result) {
                dxToastAlert(msg('success'));
            });
            $(this).parents(".tab-list").find("button").show();
            $(this).parents(".tab-list").find(".tag-save").hide();
            $(this).parents(".tab-list").find("input").attr("disabled", true);
        }
    });

    // 添加一条部门审批人保存
    $('.addAuthGroupMember_column_dept').on("click", function () {
        var param_dept_column = $form.find('.select_department_column').dxDefaultWidget(true).getDate();
        var param_role_column = $form.find('.select_role_column').dxDefaultWidget(true).getDate();
        var param_user_column = $form.find('.select_user_column').dxDefaultWidget(true).getDate();
        var butttonType = $(this).attr("add_type");
        saveOnclick('.deptAndRole_column', '', param_dept_column, param_role_column, param_user_column, '.select_department_column',
            '.select_role_column', '.select_user_column',butttonType);
        /*按钮隐藏显示-选择隐藏*/
        $form.find(".showBotton").show();
        $form.find(".selectShow").hide();
        $form.find(".addAuthGroupMemberDept").show();
        $form.find("#approve-section-add").css({border:"0"});
    });
    // 添加一条用户审批人保存
    $('.addAuthGroupMember_column_user').on("click", function () {
        var param_dept_column = $form.find('.select_department_column').dxDefaultWidget(true).getDate();
        var param_role_column = $form.find('.select_role_column').dxDefaultWidget(true).getDate();
        var param_user_column = $form.find('.select_user_column').dxDefaultWidget(true).getDate();
        var butttonType = $(this).attr("add_type");
        saveOnclick('', '.userDiv_column', param_dept_column, param_role_column, param_user_column, '.select_department_column',
            '.select_role_column', '.select_user_column',butttonType);
        /*显示添加 按钮*/
        $(".addAuthGroupMemberUser").show();
    });
    // TODO 编辑一条部门审批人保存
    $form.find('.editAuthGroupMember_column_dept').on("click", function () {
        var param_dept_column = $form.find('.edit_department_column').dxDefaultWidget(true).getDate();
        var param_dept_column_ref = $form.find('.edit_department_column_ref').dxDefaultWidget(true).getDate();
        var param_role_column = $form.find('.edit_role_column').dxDefaultWidget(true).getDate();
        var editType = $(this).attr("editType");
        var ref_column_dept = getRefColumn($form.find(".ref_column_name_deptOrUser").attr("ref_column_dept"));
		var ref_column_name = $form.find(".ref_column_name_deptOrUser").val();
		var showSpan = "";
        if (isEmpty(param_dept_column.id) || isEmpty(param_role_column.id)) {
            alert("内容不能为空");
        } else {
        	if(editType == "dynamic"){
            	if(isEmpty(ref_column_dept)){
        			alert("内容不能为空");
        			return;
        		}else{
        			showSpan=ref_column_name+"的";
        			param_dept_column = param_dept_column_ref;
        		}
            }
            var div_id = $form.find(".dept_edit_id").val(); // 正在编辑的div
            var displayDiv = $form.find(".deptAndRole_column").find("div");

            var flag = 0;
            for (var i = 0; i < displayDiv.length; i++) {
                var deptValue = $(displayDiv[i]).find('input[name=department]').val();
                var ref_column_dept_name = $(displayDiv[i]).find('input[name=department]').attr("ref_column_dept");
                var roleValue = $(displayDiv[i]).find('input[name=role]').val();
                var idValue = $(displayDiv[i]).attr("class");
                if(editType == "dynamic"){
                	if (div_id != idValue && param_dept_column.id == deptValue && param_role_column.id == roleValue 
                			&& ref_column_dept_name == ref_column_dept) {
                        flag = 1;
                        break;
                    }
                }else{
                	if (div_id != idValue && param_dept_column.id == deptValue && param_role_column.id == roleValue) {
                        flag = 1;
                        break;
                    }
                }
            }

            if (flag == 1) {
                alert("内容重复");
            } else {
            	$form.find("." + div_id).find('input[name="department"]').val(param_dept_column.id);
            	$form.find("." + div_id).find('input[name="department"]').attr("valueName", param_dept_column.name);
            	$form.find("." + div_id).find('input[name="department"]').attr("ref_column_dept", ref_column_dept);

            	$form.find("." + div_id).find('input[name="role"]').val(param_role_column.id);
            	$form.find("." + div_id).find('input[name="role"]').attr("valueName", param_role_column.name);

            	$form.find("." + div_id + " .textSpan").text(showSpan+param_dept_column.name + "的"
                    + param_role_column.name);
            	$form.find("#approve-section-edit").collapse('hide');
            }
        }
        $form.find(".ref_column_name_deptOrUser").val("");
        $form.find(".ref_column_name_deptOrUser").val("").attr("ref_column_dept","");
    });

    // 编辑一条用户审批人保存
    $form.find('.editAuthGroupMember_column_user').on("click", function () {
    	var ref_column_dept = getRefColumn($form.find(".ref_column_name_deptOrUser").attr("ref_column_dept"));
		var ref_column_name = $form.find(".ref_column_name_deptOrUser").val();
		var showSpan="";
		var editType = $(this).attr("editType");
        var div_id = $form.find(".user_edit_id").val();
        var param_user_column = $form.find('.edit_user_column').dxDefaultWidget(true).getDate();
        var param_user_column_ref = $form.find('.edit_user_column_ref').dxDefaultWidget(true).getDate();

        if (isEmpty(param_user_column.id)) {
            alert("内容不能为空");
        } else {
        	if(editType == "dynamic"){
            	if(isEmpty(ref_column_dept)){
        			alert("内容不能为空");
        			return;
        		}else{
        			showSpan=ref_column_name+"的";
        			param_user_column= param_user_column_ref;
        		}
            }
            var displayDiv = $form.find(".userDiv_column").find("div");
            var flag = 0;
            for (var i = 0; i < displayDiv.length; i++) {
                var userValue = $(displayDiv[i]).find('input[name=user]').val();
                var idValue = $(displayDiv[i]).attr("class");
                var ref_column_dept_name = $(displayDiv[i]).find('input[name=department]').attr("ref_column_user");
                if(editType == "dynamic"){
                	if (div_id != idValue && param_user_column.id == userValue && ref_column_dept_name == ref_column_dept) {
                        flag = 1;
                        break;
                    }
                }else{
                	if (div_id != idValue && param_user_column.id == userValue) {
                        flag = 1;
                        break;
                    }
                }
            }
            if (flag == 1) {
                alert("内容重复");
            } else {
            	$form.find("." + div_id).find('input').val(param_user_column.id);
            	$form.find("." + div_id).find('input').attr("valueName", param_user_column.name);
            	$form.find("." + div_id).find('input').attr("ref_column_user", ref_column_dept);

            	$form.find("." + div_id + " .textSpan").text(showSpan+param_user_column.name);
            	$form.find("#approve-user-edit").collapse('hide');
            }
        }
        $form.find(".ref_column_name_deptOrUser").val("");
        $form.find(".ref_column_name_deptOrUser").val("").attr("ref_column_dept","");
    });

    /*添加部门+岗位+取消按钮*/
    $(".cancelAddButton").click(function(){
        $(".addAuthGroupMemberDept").trigger('click');
    });
    /*添加用户+取消按钮*/
    $(".cancelAddButtonFoUser").click(function(){
        $(".addAuthGroupMemberUser").trigger('click');
    });
//    $(document).click(function(){
//        $form.find('.ref_department_column_tree').hide();
//    });
    // 点击按钮部门添加审批权限
    $('.addAuthGroupMemberDept').on("click", function () {
    	$form.find("#approve-section-edit").collapse('hide');
    	$form.find("#approve-section-add").collapse('show');
    	$form.find('.approve-person .collapse').collapse('hide');
        $form.find(".showBotton").show();
        $form.find(".selectShow").hide();
        /*显示添加 按钮*/
        $(".addAuthGroupMemberUser").show();
    });
    // 点击添加按钮用户
    $('.addAuthGroupMemberUser').on("click", function () {
    	 /*显示添加 按钮*/
    	$form.find(".addAuthGroupMemberDept").show();
    	$form.find("#approve-user-edit").collapse('hide');
    	$form.find("#approve-user-add").collapse('show');
    	$form.find('.approve-person .collapse').collapse('hide');
        /*border*/
        $form.find("#approve-user-add").css({border:"0"});
        $form.find(".showBottonFoUser").show();
        $form.find(".selectShowFoUser").hide();
    });
    // 点击取消按钮部门add
    $('.cancelAuthGroupMemberDeptAdd').on("click", function () {
        $("#approve-section-add").collapse('hide');
        /*按钮隐藏显示-选择隐藏*/
        $form.find(".showBotton").show();
        $form.find(".selectShow").hide();
        $form.find(".addAuthGroupMemberDept").show();
        $form.find("#approve-section-add").css({border:"0"});
    });
    // 点击取消按钮部门edit
    $('.cancelAuthGroupMemberDeptEdit').on("click", function () {
        $("#approve-section-edit").collapse('hide');
    });

    // 点击取消按钮用户add
    $('.cancelAuthGroupMemberUserAdd').on("click", function () {
        $("#approve-user-add").collapse('hide');
        /*取消*/
        $(".addAuthGroupMemberDept").show();
        $(".showBottonFoUser").show();
        $(".selectShowFoUser").hide();
        $("#approve-section-edit").css({border: "0"});
    });
    // 点击取消按钮用户edit
    $('.cancelAuthGroupMemberUserEdit').on("click", function () {
        $("#approve-user-edit").collapse('hide');
        $("#approve-user-add").css({border: "0"});
        $(".addAuthGroupMemberUser").show();
    });
    //删除权限组
    $(".deptAndRole,.userDiv").on("click", ".deleteAuthGroupMember", function () {
        $(this).parent().remove();
    });
    /*失去焦点的时候消失*/
    $form.find('.ref_column_name_deptOrUser').blur(function(){
        console.log(this)
        // $form.find('.ref_department_column_tree').hide();
    });
  //树节点点击事件。点击后给table赋值。
    $divTree.bind("changed.jstree", function (e, data) {
    	var node = data.node;
    	if(!isEmpty(node)){
    		if(node.children<=0){
    			var parents = node.parents;
    			var ref_column_name = "";
    			var ref_column_dept = "";
    			for(var i=parents.length-1;i>-1;i--){
    				if(parents[i] !="#"){
    					var getNode = $divTree.jstree('get_node', parents[i]);
    					ref_column_name+=getNode.text+".";
        				ref_column_dept+=getNode.id+".";
    				}
    			}
    			ref_column_name+=node.text;
				ref_column_dept+=node.id;
    			$form.find(".ref_column_name_deptOrUser").val(ref_column_name);
    			$form.find(".ref_column_name_deptOrUser").attr("ref_column_dept",ref_column_dept);
                /*赋值之后消失*/
                $form.find(".ref_department_column_tree").css({"display":"none"});
    		}else{
    			$form.find(".ref_column_name_deptOrUser").val("");
    			$form.find(".ref_column_name_deptOrUser").attr("ref_column_dept","");
    		}
    	}

    });
    //点击固定部门
    $form.find(".fixed_dept").on("click", function () {
    	$form.find(".addAuthGroupMember_column_dept").attr("add_type","fixed");
    	$form.find(".select_ref_deptOrUser_column").hide();
    	$form.find('.select_department_column').html("");
    	selectDpetAndRoleAndUser('.select_department_column', 'c_dept_role_user', 'data_dept');
    	/*隐藏按钮，添加权限，显示选择内容*/
    	$form.find(".showBotton").hide();
        $form.find(".selectShow").show();
        $form.find(".addAuthGroupMemberDept").hide();
        $form.find("#approve-section-add").css({border:"1px solid #60b9e0"});
        $form.find(".ref_column_name_deptOrUser").val("");
        $form.find(".ref_column_name_deptOrUser").val("").attr("ref_column_dept","");
    });
    /*点击动态添加部门下的输入框*/
    $form.find('.ref_column_name_deptOrUser').on("click",function(){
        event.stopPropagation();
        $(this).parent().append($(".ref_department_column_tree"));
        $form.find('.ref_department_column_tree').show();
    });
    //点击动态部门
    $form.find(".dynamic_dept").on("click", function () {
    	$form.find(".addAuthGroupMember_column_dept").attr("add_type","dynamic");
    	$form.find(".select_ref_deptOrUser_column").show();
    	$form.find('.select_department_column').html("");
    	selectDpetAndRoleAndUser('.select_department_column', 'c_dept_role_user', 'data_dept_ref');
        /*隐藏按钮，添加权限，显示选择内容*/
        $form.find(".showBotton").hide();
        $form.find(".selectShow").show();
        $form.find(".addAuthGroupMemberDept").hide();
        $form.find("#approve-section-add").css({border:"1px solid #60b9e0"});
        $form.find(".ref_column_name_deptOrUser").val("");
        $form.find(".ref_column_name_deptOrUser").val("").attr("ref_column_dept","");
    });
    //点击固定用户
    $form.find(".fixed_user").on("click", function () {
    	$form.find(".addAuthGroupMember_column_user").attr("add_type","fixed");
    	$form.find(".select_ref_deptOrUser_column").hide();
    	$form.find('.select_user_column').html("");
    	selectDpetAndRoleAndUser('.select_user_column', 'c_dept_role_user', 'data_user');
        /*隐藏按钮，添加权限，显示选择内容*/
        $form.find(".showBottonFoUser").hide();
        $form.find(".selectShowFoUser").show();
        $form.find("#approve-user-add").css({border:"1px solid #60b9e0"});
        $form.find(".ref_column_name_deptOrUser").val("");
        $form.find(".ref_column_name_deptOrUser").val("").attr("ref_column_dept","");
    });
    //点击动态用户
    $form.find(".dynamic_user").on("click", function () {
    	$form.find(".addAuthGroupMember_column_user").attr("add_type","dynamic");
    	$form.find(".select_ref_deptOrUser_column").show();
    	// $form.find('.select_user_column').html("");
    	// selectDpetAndRoleAndUser('.select_user_column', 'c_dept_role_user', 'data_user_ref');
        /*隐藏按钮，添加权限，显示选择内容*/
        $form.find(".showBottonFoUser").hide();
        $form.find(".selectShowFoUser").show();
        $form.find("#approve-user-add").css({border:"1px solid #60b9e0"});
        $form.find(".ref_column_name_deptOrUser").val("");
        $form.find(".ref_column_name_deptOrUser").val("").attr("ref_column_dept","");
    });
    
    // 保存添加的审批人数据
    function saveOnclick(deptAndRoleDiv, userDiv, param_dept, param_role, param_user, select_department, select_role,
                         select_user,buttonType) {
        var displayDiv = $(deptAndRoleDiv).find('div');
        var displayDiv_user = $(userDiv).find('div');
        var uid;
        var ref_column_dept = getRefColumn($form.find(".ref_column_name_deptOrUser").attr("ref_column_dept"));
		var ref_column_name = $form.find(".ref_column_name_deptOrUser").val();
        if (!isEmpty(deptAndRoleDiv)) {
            if (isEmpty(param_dept.id) || isEmpty(param_role.id)) {
                alert("内容不能为空");
            } else {
    			var showSpan = param_dept.name + "的" + param_role.name;
            	if(buttonType == "dynamic"){
            		if(isEmpty(ref_column_dept)){
            			alert("内容不能为空");
            			return;
            		}else{
            			showSpan = ref_column_name+"的"+ param_dept.name + "的" + param_role.name;
            		}
                }
                var flag = 0;
                for (var i = 0; i < displayDiv.length; i++) {
                    var deptValue = $(displayDiv[i]).find('input[name=department]').val();
                    var roleValue = $(displayDiv[i]).find('input[name=role]').val();
                    var ref_column_dept_name = $(displayDiv[i]).find('input[name=department]').attr("ref_column_dept");
                    if(buttonType == "dynamic"){
                    	if (param_dept.id == deptValue && param_role.id == roleValue && ref_column_dept == ref_column_dept_name) {
                            flag = 1; // 如果已经存在
                            break;
                        }
                    }else{
                    	if (param_dept.id == deptValue && param_role.id == roleValue) {
                            flag = 1; // 如果已经存在
                            break;
                        }
                    }
                }
                if (flag == 1) {
                    alert("内容重复")
                } else {
                    uid = uuid(7, 16);
                    $(deptAndRoleDiv).append("<div class='dept_"
                        + uid + "'><label><input type='checkbox' name='department' value='"
                        + param_dept.id + "' valueName='" + param_dept.name + "' ref_column_dept='"+ref_column_dept
                        +"'/><span class='textSpan'>"+showSpan+"</span></label><a class='deleteAuthGroupMember'>"
                        + "</a><a class='editAuthGroupMember'></a><input type='hidden' name='role'  value='"
                        + param_role.id + "' valueName='" + param_role.name + "'/></div>");
                    editAuthGroupBind(deptAndRoleDiv,"dept_"+uid,"","",buttonType);
                }
            }
        }
        if (!isEmpty(userDiv)) {
            if (isEmpty(param_user.id)) {
                alert("内容不能为空");
            } else {
            	var showSpan = param_user.name;
            	if(buttonType == "dynamic"){
            		if(isEmpty(ref_column_dept)){
            			alert("内容不能为空");
            			return;
            		}else{
            			showSpan = ref_column_name+"的"+ param_user.name;
            		}
                }
                var flag = 0;
                for (var i = 0; i < displayDiv_user.length; i++) {
                    var userValue = $(displayDiv_user[i]).find('input[name=user]').val();
                    var ref_column_user_name = $(displayDiv[i]).find('input[name=user]').attr("ref_column_user");
                    if(buttonType == "dynamic"){
                    	if (param_user.id == userValue && ref_column_user_name == ref_column_dept) {
                            flag = 1;
                            break;
                        }
                    }else{
                    	if (param_user.id == userValue) {
                            flag = 1;
                            break;
                        }
                    }
                }
                if (flag == 1) {
                    alert("内容重复");
                } else {
                    uid = uuid(7, 16);
                    $(userDiv).append("<div class='user_" + uid + "'><label><input type='checkbox' name='user' value='"
                        + param_user.id + "' valueName='" +param_user.name+ "' ref_column_user='"+ref_column_dept
                        +"'/><span class='textSpan'>" + showSpan
                        + "</span></label><a class='deleteAuthGroupMember'></a><a class='editAuthGroupMember'></a></div>");
                    editAuthGroupBind("","",userDiv,"user_"+uid,buttonType);
                }
            }
        }
        uiinit();
    }

    var allTemplate = [];
    // 从模板中选择
    function lookAllTemplate(authGroup, lookAllTemplateUl, liClass, deptAndRoleDiv, userDiv,
                             select_department, select_role, select_user, type) {
        postJson('/approve/lookAllTemplate.do', {
            authGroup: authGroup
        }, function (result) {
            result = replaceParam(result);
            var tmp = result.authGroup;
            var templateName = "";
            allTemplate = [];
            if (!isEmpty(tmp)) {
                for (var i = 0; i < tmp.length; i++) {
                    if (!isEmpty(tmp[i].template)) {
                        if (templateName != tmp[i].template) {
                            allTemplate.push(tmp[i]);
                            templateName = tmp[i].template;
                            $(lookAllTemplateUl).parent().find('ul').append("<li class='" + liClass + "' name='"
                                + tmp[i].template + "'>" + tmp[i].template + "</li>");
                        }
                    }
                }
                $(lookAllTemplateUl).parent().find('ul').append('<li class="model-tag-manage">'
                    + '<button type="button" class="modelManage" data-toggle="modal" data-target="#approve-tag-modal">模板管理 </button></li>');

                $('.' + liClass).on("click", function () {
                    var authGroup = new Array();
                    var template_name = $(this).attr('name');
                    for (var i = 0; i < tmp.length; i++) {
                        if (tmp[i].template == template_name) {
                            authGroup.push(tmp[i]);
                        }
                    }
                    AuthGroupMember(deptAndRoleDiv, userDiv,
                        authGroup, select_department, select_role, select_user)
                });
            }
        });
    }

    // TODO 根据块ID改变审批人数据
    function AuthGroupMember(deptAndRoleDiv, userDiv, authGroupMember, select_department, select_role, select_user) {
        $(deptAndRoleDiv).empty();
        $(userDiv).empty();
        if (!isEmpty(authGroupMember)) {
            var uid;
            for (var j = 0; j < authGroupMember.length; j++) {
                if (isEmpty(authGroupMember[j].department)
                    && isEmpty(authGroupMember[j].department_relation)) {
                    uid = uuid(7, 16);
                    var editType ="";
                    var showSpan = "";
                    if(!isEmpty(authGroupMember[j].ref_column_user)){
                    	editType = "dynamic";
                    	showSpan = setRefColumn(authGroupMember[j].ref_column_user)+"的";
                    }else{
                    	authGroupMember[j].ref_column_user = "";
                    }
                    if (!isEmpty(authGroupMember[j].user_relation)) {
                    	showSpan+= authGroupMember[j].user_relation_name ;
                        var appendHtml = '<div class="user_' + uid + '"><label><input type="checkbox" name="user" value="'
                            + authGroupMember[j].user_relation + '" valueName="' + authGroupMember[j].user_relation_name
                            + '" ref_column_user="'+authGroupMember[j].ref_column_user+'"><span class="textSpan">' 
                            + showSpan + '</span></label>'
                            + '<a class="deleteAuthGroupMember"></a><a class="editAuthGroupMember" editType="'+editType+'"></a></div>';
                        $(userDiv).append(appendHtml);
                    } else {
                    	showSpan+= authGroupMember[j].userName;
                        var appendHtml = '<div class="user_' + uid + '"><label><input type="checkbox" name="user" value="'
                            + authGroupMember[j].user + '" valueName="' + authGroupMember[j].userName
                            + '" ref_column_user="'+authGroupMember[j].ref_column_user+'"><span class="textSpan">' + showSpan + '</span></label>'
                            + '<a class="deleteAuthGroupMember"></a><a class="editAuthGroupMember" editType="'+editType+'"></a></div>';
                        $(userDiv).append(appendHtml);
                    }
                    editAuthGroupBind("","",userDiv,"user_" + uid,editType);
                } else if (isEmpty(authGroupMember[j].user_relation)
                    && isEmpty(authGroupMember[j].user)) {
                    uid = uuid(7, 16);
                    var editType ="";
                    var showSpan ="";
                    if(!isEmpty(authGroupMember[j].ref_column_dept)){
                    	editType = "dynamic";
                    	showSpan = setRefColumn(authGroupMember[j].ref_column_dept)+'的';
                    }else{
                    	authGroupMember[j].ref_column_dept="";
                    }
                    if (!isEmpty(authGroupMember[j].department_relation)) {
                    	showSpan+= authGroupMember[j].department_relation_name +'的'+ authGroupMember[j].roleName;
                        var appendHtml = '<div class="dept_' + uid + '"><label><input type="checkbox" name="department" value="'
                            + authGroupMember[j].department_relation + '" valueName="' + authGroupMember[j].department_relation_name
                            +'" ref_column_dept="'+authGroupMember[j].ref_column_dept+'"/><span class="textSpan">'+showSpan
                            + '</span></label><a class="deleteAuthGroupMember"></a>'
                            + '<a class="editAuthGroupMember"></a><input type="hidden" name="role"  value="'
                            + authGroupMember[j].role + '" valueName="' + authGroupMember[j].roleName + '"></div>';
                        $(deptAndRoleDiv).append(appendHtml);

                    } else {
                    	showSpan+= authGroupMember[j].deptName + '的'+ authGroupMember[j].roleName;
                        var appendHtml = '<div class="dept_' + uid + '"><label><input type="checkbox" name="department" value="'
                            + authGroupMember[j].department + '" valueName="'+ authGroupMember[j].deptName+
                            '"  ref_column_dept="'+authGroupMember[j].ref_column_dept
                            +'"/><span class="textSpan">' +showSpan+ '</span></label><a class="deleteAuthGroupMember"></a>'
                            + '<a class="editAuthGroupMember"></a><input type="hidden" name="role"  value="'
                            + authGroupMember[j].role + '" valueName="' + authGroupMember[j].roleName + '"/></div>';
                        $(deptAndRoleDiv).append(appendHtml);
                    }
                    editAuthGroupBind(deptAndRoleDiv,"dept_"+uid ,"","",editType);
                }
            }
            uiinit();
        }
    }
    //部门角色或用户编辑按钮的处理事件
    function editAuthGroupBind(deptAndRoleDiv,deptDivUUid,userDiv,userDivUUid,editType){
    	if(!isEmpty(deptAndRoleDiv)){
    		$(deptAndRoleDiv+" ." +deptDivUUid+ ' .editAuthGroupMember').attr("editType",editType);
    		$(deptAndRoleDiv+" ." +deptDivUUid+ ' .editAuthGroupMember').on("click", function () {
    			$form.find("#approve-section-add").collapse('hide');
                $form.find("#approve-section-edit").collapse('show');
                $form.find('.editAuthGroupMember_column_dept').attr("editType",$(this).attr("editType"))
    			if($(this).attr("editType") == "dynamic"){
    				var ref_column_dept = $(this).parent().find('input[name="department"]').attr("ref_column_dept");
    				$form.find(".select_ref_deptOrUser_column").show();
    				$form.find(".ref_column_name_deptOrUser").val(setRefColumn(ref_column_dept));
        			$form.find(".ref_column_name_deptOrUser").attr("ref_column_dept",ref_column_dept);
    		    	$form.find('.edit_department_column').hide();
    		    	$form.find('.edit_department_column_ref').show();
    		    	//selectDpetAndRoleAndUser('.edit_department_column', 'c_dept_role_user', 'data_dept_ref');
    			}else{
    				$form.find(".select_ref_deptOrUser_column").hide();
    				$form.find('.edit_department_column').show();
    				$form.find('.edit_department_column_ref').hide();
    		    	//selectDpetAndRoleAndUser('.edit_department_column', 'c_dept_role_user', 'data_dept');
    			}
                var div_id = $(this).parent().attr("class");
                var id = $(this).parent().find('input[name="department"]').val();
                var name = $(this).parent().find('input[name="department"]').attr("valueName");
                var data = {id: id, name: name};
                var ss = $form.find(".edit_department_column");
                $form.find(".edit_department_column").dxDefaultWidget(true).setDate(data);
                $form.find(".edit_department_column_ref").dxDefaultWidget(true).setDate(data);
                var id2 = $(this).parent().find('input[name="role"]').val();
                var name2 = $(this).parent().find('input[name="role"]').attr("valueName");
                var data2 = {id: id2, name: name2};
                $form.find(".edit_role_column").dxDefaultWidget(true).setDate(data2);
                $form.find(".dept_edit_id").val(div_id);
            });
    	}else{
    		$(userDiv+' .' +userDivUUid+ ' .editAuthGroupMember').attr("editType",editType);
    		$(userDiv+' .' +userDivUUid+ ' .editAuthGroupMember').on("click", function () {
    			if($(this).attr("editType") == "dynamic"){
    				var ref_column_user = $(this).parent().find('input[name="user"]').attr("ref_column_user");
    				$form.find(".select_ref_deptOrUser_column").show();
    				$form.find(".ref_column_name_deptOrUser").val(setRefColumn(ref_column_user));
        			$form.find(".ref_column_name_deptOrUser").attr("ref_column_dept",ref_column_user);
//    		    	$form.find('.edit_user_column').html("");
//    		    	selectDpetAndRoleAndUser('.edit_user_column', 'c_dept_role_user', 'data_user_ref');
        			$form.find('.edit_user_column').hide();
    		    	$form.find('.edit_user_column_ref').show();
    			}else{
    				$form.find(".select_ref_deptOrUser_column").hide();
//    				$form.find('.edit_user_column').html("");
//    		    	selectDpetAndRoleAndUser('.edit_user_column', 'c_dept_role_user', 'data_user');
    				$form.find('.edit_user_column_ref').hide();
    		    	$form.find('.edit_user_column').show();
    			}
                var div_id = $(this).parent().attr("class");
                var id = $(this).parent().find('input').val();
                var name = $(this).parent().find('input').attr("valueName");
                var data = {id: id, name: name};
                $(".edit_user_column").dxDefaultWidget(true).setDate(data);
                $(".edit_user_column_ref").dxDefaultWidget(true).setDate(data);
                $(".user_edit_id").val(div_id);
                $("#approve-user-add").collapse('hide');
                $("#approve-user-edit").collapse('show');
                $form.find('.editAuthGroupMember_column_user').attr("editType",$(this).attr("editType"))
            });
    	}
    	
    }
  //解析关联字段的数据
    function setRefColumn(refCollumn){
    	var refCollumns = refCollumn.split(".");
    	var ref_column_name="";
    	var columnMap= dx.table[tableName].columnMap;
    	for(var i=0;i<refCollumns.length;i++){
    		if(i>0){
    			columnMap = dx.table[columnMap[refCollumns[i-1]].ref_table_name].columnMap;
    		}
    		if(i==refCollumns.length-1){
    			ref_column_name+= msg(columnMap[refCollumns[i]].international_id);
    		}else{
    			ref_column_name+= msg(columnMap[refCollumns[i]].international_id)+".";
    		}
    	}
    	return ref_column_name;
    }
  //获取关联字段的数据
    function getRefColumn(refCollumn){
    	var refCollumns = refCollumn.split(".");
    	var ref_column="";
    	for(var i=0;i<refCollumns.length;i++){
    		var column = refCollumns[i].split("%");
    		if(i==refCollumns.length-1){
    			ref_column+= column[0];
    		}else{
    			ref_column+= column[0]+".";
    		}
    	}
    	return ref_column;
    }
    // 保存审批人
    function addAuthGroup(deptAndRoleDiv, userDiv, tableId_data, blockId, template_name, type) {
        var authGroup = new Array();
        var list_deptAndRole = $('.' + deptAndRoleDiv);
        var list_user = $('.' + userDiv);
        for (var j = 0; j < list_deptAndRole.length; j++) {
            var operateValue = "";
            var param = {};
            var dept = $(list_deptAndRole[j]).find('input[name=department]').val();
            var dept_name = $(list_deptAndRole[j]).find('input[name=department]').attr("valueName");
            var ref_column_dept = $(list_deptAndRole[j]).find('input[name=department]').attr("ref_column_dept");
            var role = $(list_deptAndRole[j]).find('input[name=role]').val();
            var role_name = $(list_deptAndRole[j]).find('input[name=role]').attr("valueName");
            if (dept == '1' || dept == '2' || dept == '0') {
                param.department_relation = dept;
                param.department_relation_name = dept_name;
            }
            param.ref_column_dept = getRefColumn(ref_column_dept);
            param.department = dept;
            param.deptName = dept_name;
            param.role = role;
            param.roleName = role_name;
            param.table = tableId_data;
            param.block = blockId;
            param.type = type;
            param.template = template_name;
            authGroup.push(param);
        }
        for (var j = 0; j < list_user.length; j++) {
            var param = {};
            var user = $(list_user[j]).find('input[name=user]').val();
            var user_name = $(list_user[j]).find('input[name=user]').attr("valueName");
            var ref_column_user = $(list_user[j]).find('input[name=user]').attr("ref_column_user");
            if (user == '1' || user == '2' || user == '0') {
                param.user_relation = user;
                param.user_relation_name = user_name;
            }
            param.ref_column_user = getRefColumn(ref_column_user);
            param.user = user;
            param.userName = user_name;
            param.table = tableId_data;
            param.block = blockId;
            param.type = type;
            param.template = template_name;
            authGroup.push(param);
        }
        if (blockId != "") {
            for (var i = 0; i < authGroups.length; i++) {
                if (authGroups[i][0] != null
                    && authGroups[i][0].block == blockId) {// 如果数组里有
                    // 则更新
                    // 先删除再添加
                    authGroups.splice(i, 1);
                    break;
                }
            }
            // 添加到数组
            authGroups.push(authGroup);
        }
        if (!isEmpty(template_name)) {
            postJson('/approve/saveTemplate.do', {authGroup: authGroup}, function (result) {
                dxToastAlert(msg('success'));
            });

        }
    }

    //保存所有数据到数据库
    function save() {
        conditions = [];
        for (var key in conditionMap) {
            var cons = conditionMap[key];
            for (var i in cons) {
                conditions.push(cons[i]);
            }
        }
        authGroupList = [];
        if (authGroups != null) {
            var ag = [];
            for (var i = 0; i < authGroups.length; i++) {
                ag = authGroups[i];
                if (ag != null) {
                    for (var j = 0; j < ag.length; j++) {
                        authGroupList.push(ag[j]);
                    }
                }
            }
        }
        //  var saveFlag=false;
        var overBlock = "";
        var events = {};
        for (var b = 0; b < blocks.length; b++) {
            blocks[b].block_x = parseInt($(".approve-chart-view #" + blocks[b].block_id).css("left"));
            blocks[b].block_y = parseInt($(".approve-chart-view #" + blocks[b].block_id).css("top"));
            if (blocks[b].approve_name == "approve_over_lump") {
                overBlock = blocks[b].block_id;
            }
        }
        //判断表是否有且只有一个主键
        var idColumn = dx.table[tableName].idColumns;
        if(idColumn.length=1){
        	dx.processing.open();
            postJson('/approve/saveApprove.do', {
                tableId: tableName,
                flowBlock: blocks,
                flowLine: lines,
                authGroup: authGroupList,
                flowConditionDetail: conditions,
                saveApproveButtonEvent: initApproveButtonEvent,
                saveApproveBlockEvent :initApproveBlockEvent
            }, function (result) {
                dxReload(function () {
                    dxToastAlert(msg('Saved successfully!'));
                    dx.processing.close();
                });
            });
        }else{
        	msg('primary key is not the only')
        }
    }

    //   }

    // 验证全部
    function validateAll() {
        conditions = [];
        for (var key in conditionMap) {
            var cons = conditionMap[key];
            for (var i in cons) {
                conditions.push(cons[i]);
            }
        }
        authGroupList = [];
        if (authGroups != null) {
            var ag = [];
            for (var i = 0; i < authGroups.length; i++) {
                ag = authGroups[i];
                if (ag != null) {
                    for (var j = 0; j < ag.length; j++) {
                        authGroupList.push(ag[j]);
                    }
                }
            }
        }
        postJson('/approve/validateAllApprove.do', {
            tableId: tableName,
            flowBlock: blocks,
            flowLine: lines,
            authGroup: authGroupList,
            flowConditionDetail: conditions
        }, function (result) {

        });
    }

    // 验证一个流程节点
    function validateOne() {
        var block_id = $(".hidden_node_id");
        var block;
        for (var i = 0; i < blocks.length; i++) {
            block = blocks[i];
            if (block.block_id == block_id) {
                break;
            }
        }
        var authGroup;
        for (var i = 0; i < authGroups.length; i++) {
            authGroup = authGroups[i];
            if (authGroup != null && authGroup[0].block == block_id) {
                break;
            }
        }
        postJson('/approve/validateOneApprove.do', {
            tableId: tableName,
            flowBlock: block,
            authGroup: authGroup
        }, function (result) {

        });
    }

    function uuid(len, radix) {
        var chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split('');
        var uuid = [], i;
        radix = radix || chars.length;
        if (len) {
            for (i = 0; i < len; i++)
                uuid[i] = chars[0 | Math.random() * radix];
        } else {
            var r;
            uuid[8] = uuid[13] = uuid[18] = uuid[23] = '-';
            uuid[14] = '4';
            for (i = 0; i < 36; i++) {
                if (!uuid[i]) {
                    r = 0 | Math.random() * 16;
                    uuid[i] = chars[(i == 19) ? (r & 0x3) | 0x8 : r];
                }
            }
        }
        return uuid.join('');
    }

    //初始化处理前提表格
    function initActionPrerequistie() {
        //表格文本编辑器
        $.extend($.fn.datagrid.defaults.editors, {
            interViolateMsgI18N: {
                init: function (container, options) {
                    var input = $('<div class="violate_msg_I18N"></div>').appendTo(container);
                    return input;
                },
                getValue: function (target) {
                    var getInternational = $form.find(".violate_msg_I18N").internationalControl(true).getData();
                    var index = dataGrid.datagrid('getRowIndex', getInterRow);
                    var row = dataGrid.datagrid('getRows')[index];
                    if (isEmpty(getInternational.international_id)) {
                        row.violate_msg_I18N = {};
                        row.violate_msg_I18N[dx.user.language_id] = getInternational.interValue;
                        row.violate_msg_international_id = ""
                    } else {
                        row.violate_msg_I18N = getInternational;
                        row.violate_msg_international_id = getInternational.international_id;
                    }
                    return row.violate_msg_I18N;
                },
                setValue: function (target, value) {
                    getInterRow = dataGrid.datagrid('getSelected');
                    if (isEmpty(getInterRow)) {
                        international($form.find(".violate_msg_I18N"), "", "", "",
                            function (submitData) {
                                submitData;
                            }, function (selectData) {
                                selectData;
                            });
                    } else {
                        if (isEmpty(getInterRow.violate_msg_international_id)) {
                            international($form.find(".violate_msg_I18N"), "", "", "",
                                function (submitData) {
                                    submitData;
                                }, function (selectData) {
                                    selectData;
                                });
                        } else {
                            international($form.find(".violate_msg_I18N"), getInterRow.violate_msg_international_id,
                                getInterRow.violate_msg_I18N[dx.user.language_id], "",
                                function (submitData) {
                                    submitData;
                                }, function (selectData) {
                                    selectData;
                                });
                        }
                    }
                }
            }
        });
        var dataGrid = $form.find('.approve-event-modal .action_prerequistie');
        var eventTypeOptionsData = gridDiceSelect('event_type');
        var urlSelectOptionsData = getUrlSelect();
        dataGrid.datagrid({
            height: "100%",
            singleSelect: false,
            rownumbers: true,
            striped: true,
            fitColumns: true,
            onClickRow: function (rowIndex, rowData) {
                $(this).datagrid('unselectAll');
                $(this).datagrid('selectRow', rowIndex);
            },
            onLoadSuccess: function () {
               // dataGrid.datagrid('enableDnd');
            },
            onDrop: function (targetRow, sourceRow, point) {
                //拖拽某行到指定位置后触发
                var rows = dataGrid.datagrid('getRows');
            },
            onClickCell: function (rowIndex, field, value) {
                var ed = dataGrid.datagrid('getEditor', {index: rowIndex, field: field});
                if (ed) {
                    ($(ed.target).data('checkbox') ? $(ed.target).textbox('checkbox') : $(ed.target)).focus();
                }
            },
            onDblClickRow: function onClickCells(index, field) {
                endAllRowEdit(dataGrid);
                dataGrid.datagrid('selectRow', index)
                    .datagrid('beginEdit', index);
                var ed = dataGrid.datagrid('getEditor', {index: index, field: field});
                if (ed) {
                    ($(ed.target).data('checkbox') ? $(ed.target).textbox('checkbox') : $(ed.target)).focus();
                }
            },
            columns: [[
                {field: 'checkbox', title: '复选框', width: 100, checkbox: true},
                {field: 'table_action_id', title: 'ID', width: 100, hidden: true},
                {field: 'seq', title: '顺序', hidden: true, width: 100, editor: {type: 'text'}},
                {field: 'check_condition', title: '验证条件', width: 100, editor: {type: 'text'}},
                {
                    field: 'level', title: '验证级别', width: 100,
                    formatter: function (value, row, index) {
                        var options = this.editor.options.data;
                        for (var i = 0; i < options.length; i++) {
                            if (options[i].key == value) {
                                return options[i].productname
                            }
                        }
                    },
                    editor: {
                        type: 'combobox',
                        options: {
                            valueField: 'key',
                            textField: 'productname',
                            data: eventTypeOptionsData
                        }
                    }
                },
                {field: 'violate_msg_international_id', title: '违反信息ID', width: 100, hidden: true},
                {
                    field: 'violate_msg_I18N', title: '违反信息', width: 100, editor: {type: 'interViolateMsgI18N'},
                    formatter: function (value, row, index) {
                        if (!isEmpty(row.violate_msg_international_id)) {
                            row.violate_msg_I18N = dx.i18n.message[row.violate_msg_international_id]
                            return row.violate_msg_I18N[dx.user.language_id];
                        } else {
                            if (!isEmpty(value)) {
                                return value[dx.user.language_id];
                            }
                        }
                    }
                },
                {field: 'violate_msg_param', title: '信息参数', width: 100, editor: {type: 'text'}},
                {
                    field: 'is_using', title: '启用', width: 100,
                    formatter: function (value, row, index) {
                        if (value == "1") {
                            return '<div class="input-bt-Switcher-wrap"><div class="input-bt-Switcher">' +
                                '<input type="checkbox"  rowIndex = "' + index + '" checked/><label></label></div></div>';
                        } else {
                            return '<div class="input-bt-Switcher-wrap"><div class="input-bt-Switcher">' +
                                '<input type="checkbox"  rowIndex = "' + index + '" /><label></label></div></div>';
                        }
                    }
                }
            ]],
            onAfterEdit: function (rowIndex, rowData, changes) {
                var rows = dataGrid.datagrid('getRows');
                dataGrid.datagrid('loadData', rows);
            }
        });
    }

    //初始化触发事件表格
    function initActionEvent() {
    	//表格文本编辑器
        $.extend($.fn.datagrid.defaults.editors, {    
        	interParam: {    
                init: function(container, options){ 
                	var str ='<div class="input-4 clearfix" style="position: relative;width:100%">'
                			+'<input class="urlValue" type="text" name="urlValue" readonly disabled/>'
                			+'<button class="btn parameterInput" type="button" ></button></div>'
                    var input = $(str).appendTo(container);    
                    return input;    
                },     
                getValue: function(target){
                	return form.get().find(".urlValue").val();
                },    
                setValue: function(target, value){
                	form.get().find(".urlValue").val(value);
                	var getInterRow=dataGrid.datagrid('getSelected');
                	var index=dataGrid.datagrid('getRowIndex',getInterRow);
                	var id = form.get().find('tr[datagrid-row-index='+index+']').find("td[field=event_url_id]").find('input.textbox-text').val();
                	//加载参数输入控件
                    form.get().find(".parameterInput").on("click", function () {
                        var josnstr = form.get().find(".urlValue").val();
                        dataGrid.datagrid('endEdit',index);
                        dataGrid.datagrid('selectRow', index).datagrid('beginEdit', index);
                        var row=dataGrid.datagrid('getRows')[index];
                        var urlId = row.event_name;
                        var modalX = "40";
                        var modalY = "40";
                        form.get().find(".parameterInput").parameterInputControl(true).setData("onSubmit", function (data) {
                            form.get().find(".urlValue").val(data);
                        }, josnstr, urlId, modalX, modalY, "aa");

                    });
                } 
            }    
        });  
        var dataGrid = $form.find('.approve-event-modal .action_event');
        var eventTypeOptionsData = gridDiceSelect('event_type');
        var urlSelectOptionsData = getUrlSelect();
        dataGrid.datagrid({
            height: "100%",
            singleSelect: false,
            rownumbers: true,
            striped: true,
            fitColumns: true,
            onClickRow: function (rowIndex, rowData) {
                $(this).datagrid('unselectAll');
                $(this).datagrid('selectRow', rowIndex);
            },
            onLoadSuccess: function () {
                dataGrid.datagrid('enableDnd');
            },
            onDrop: function (targetRow, sourceRow, point) {
                //拖拽某行到指定位置后触发
                var rows = dataGrid.datagrid('getRows');
            },
            onClickCell: function (rowIndex, field, value) {
                var ed = dataGrid.datagrid('getEditor', {index: rowIndex, field: field});
                if (ed) {
                    ($(ed.target).data('checkbox') ? $(ed.target).textbox('checkbox') : $(ed.target)).focus();
                }
            },
            onDblClickRow: function onClickCells(index, field) {
                endAllRowEdit(dataGrid);
                dataGrid.datagrid('selectRow', index)
                    .datagrid('beginEdit', index);
                var ed = dataGrid.datagrid('getEditor', {index: index, field: field});
                if (ed) {
                    ($(ed.target).data('checkbox') ? $(ed.target).textbox('checkbox') : $(ed.target)).focus();
                }
            },
            columns: [[
                {field: 'checkbox', title: '复选框', width: 100, checkbox: true},
                {field: 'event_id', title: '事件ID', width: 100, hidden: true},
                {field: 'table_action_id', title: '父表ID', width: 100, hidden: true},
                {field: 'seq', title: msg('seq'), hidden: true, width: 100, editor: {type: 'text'}},
                {
                    field: 'event_type', title: msg('event_type'), width: 100,
                    formatter: function (value, row, index) {
                        var options = this.editor.options.data;
                        for (var i = 0; i < options.length; i++) {
                            if (options[i].key == value) {
                                return options[i].productname
                            }
                        }
                    },
                    editor: {
                        type: 'combobox',
                        options: {
                            valueField: 'key',
                            textField: 'productname',
                            data: eventTypeOptionsData
                        }
                    }
                },
                {
                    field: 'event_name', title: msg('event_name'), width: 100,
                    formatter: function (value, row, index) {
                        var options = this.editor.options.data;
                        for (var i = 0; i < options.length; i++) {
                            if (options[i].key == value) {
                                return options[i].productname
                            }
                        }
                    },
                    editor: {
                        type: 'combobox',
                        options: {
                            valueField: 'key',
                            textField: 'productname',
                            data: urlSelectOptionsData
                        }
                    }
                },
                {field: 'event_param', title: msg('event_parame'), width: 100, editor: {type: 'interParam'},
	            	formatter: function(value,row,index){
		        		 if(!isEmpty(value)){
		        			 return row.event_param;
		        		 }else{
		        			 return "";
		        		 }
		        	 }
	            },
                {
                    field: 'is_using', title: msg('start'), width: 100,
                    formatter: function (value, row, index) {
                        if (value == "1") {
                            return '<div class="input-bt-Switcher-wrap"><div class="input-bt-Switcher">' +
                                '<input type="checkbox"  rowIndex = "' + index + '" checked/><label></label></div></div>';
                        } else {
                            return '<div class="input-bt-Switcher-wrap"><div class="input-bt-Switcher">' +
                                '<input type="checkbox"  rowIndex = "' + index + '" /><label></label></div></div>';
                        }
                    }
                }
            ]],
            onAfterEdit: function (rowIndex, rowData, changes) {
                var rows = dataGrid.datagrid('getRows');
                dataGrid.datagrid('loadData', rows);
            }
        });
    }

    //处理前提 启用的点击事件
    $form.find('.prerequistie_is_using').on("click", ".input-bt-Switcher", function () {
        var dataGrid = $form.find('.approve-event-modal .action_prerequistie');
        var row = dataGrid.datagrid('getRows');
        var index = $(this).find("input").attr("rowIndex");
        if ($(this).find("input").is(":checked")) {
            row[index].is_using = 1;
        } else {
            row[index].is_using = 0;
        }
    });
    //触发事件 启用的点击事件
    $form.find('.actionEvent_is_using').on("click", ".input-bt-Switcher", function () {
        var dataGrid = $form.find('.approve-event-modal .action_event');
        var row = dataGrid.datagrid('getRows');
        var index = $(this).find("input").attr("rowIndex");
        if ($(this).find("input").is(":checked")) {
            row[index].is_using = 1;
        } else {
            row[index].is_using = 0;
        }
    });
    //处理前提和触发事件的table切换
    $form.find('.deploy-tab-ul a[href="#approve_action_prerequistie"]').on('shown.bs.tab', function (e) {
        $form.find('.approve-event-modal .action_prerequistie').datagrid('resize');
        endAllRowEdit($form.find('.approve-event-modal .action_event'));
    });
    $form.find('.deploy-tab-ul a[href="#approve_action_event"]').on('shown.bs.tab', function (e) {
        $form.find('.approve-event-modal .action_event').datagrid('resize');
        endAllRowEdit($form.find('.approve-event-modal .action_prerequistie'));
    });

    //事件类型下拉框
    function gridDiceSelect(dictId) {
        var dict = dx.dict[dictId];
        var OptionsData = [];
        OptionsData.push({key: '', productname: ''});
        for (var key in dict) {
            OptionsData.push({
                key: key,
                productname: dict[key][dx.user.language_id]
            });
        }
        return OptionsData;
    }

    //事件名称下拉框
    function getUrlSelect(type) {
        var url = dx.urlInterface;
        var OptionsData = [];
        OptionsData.push({key: '', productname: ''});
        for (var key in url) {
            OptionsData.push({
                key: key,
                productname: url[key].i18n ? url[key].i18n[dx.user.language_id] : url[key].name
            });
        }
        return OptionsData;
    }
  //表格向上移动按钮
    $form.find('.datagrid_row_up_approve_event').on("click",function(){
    	var dataGrid = $form.find('.approve-event-modal .action_event');
    	var row = dataGrid.datagrid('getChecked');
    	dataGridMove(dataGrid,row,"up");
    })
    //表格向下移动按钮
    $form.find('.datagrid_row_down_approve_event').on("click",function(){
    	var dataGrid = $form.find('.approve-event-modal .action_event');
    	var row = dataGrid.datagrid('getChecked');
    	dataGridMove(dataGrid,row,"down");
    })
    //排序
    function dataOrder(data) {
        if (isEmpty(data))
            return;
        else if (typeof data == 'object') {
            var tmp;
            for (var i = 0; i < data.length; i++) {
                for (var j = i + 1; j < data.length; j++) {
                    if (data[i].seq > data[j].seq) {
                        tmp = data[i];
                        data[i] = data[j];
                        data[j] = tmp;
                    }
                }
            }
        }
        return data;
    }
});