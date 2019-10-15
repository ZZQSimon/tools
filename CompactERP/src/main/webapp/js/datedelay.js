/**
 * Created by Administrator on 2017/9/5.
 */

registerInit('datedelay', function (form) {
    function formatID(pre, length, number) {
        var str = "" + number;
        var id = "0000000000000000000000000000000000".substr(0, length - str.length - pre.length);
        return pre + id + number;
    }

    //开关
    function rcSwitch(className) {
        form.get().find('.date-delay-detail .swraper[input-name=' + className + ']').remove();
        var rcSwitch = form.get().find('.date-delay-detail .' + className + '[type=checkbox]').rcSwitcher({
            width: 35, 				// 56  in 'px'
            height: 20,
            inputs: false,
            onText: '&nbsp;',
            offText: '&nbsp;'
        });
    }

    //生成下拉框的数据
    function RightOptionsData(tagClass, OptionData, optionvalue) {
        $(tagClass).html("");
        for (var i = 0; i < OptionData.length; i++) {
            if (OptionData[i].selectId == optionvalue) {
                $(tagClass).append("<option selected selectId=" + OptionData[i].selectId + ">" + OptionData[i].selectName + "</option>");
            } else {
                $(tagClass).append("<option selectId=" + OptionData[i].selectId + ">" + OptionData[i].selectName + "</option>");
            }
        }
    }

    //格式化下拉框的数据
    function gridDiceSelect(dictId) {
        var dict = dx.dict[dictId];
        var OptionsData = [];
        for (var key in dict) {
            OptionsData.push({
                selectId: key,
                selectName: dict[key][dx.user.language_id]
            });
        }
        return OptionsData;
    }

    var passageMap;
    var passageRowMap;
    var rowFormula;

    //找各级最大ID
    // var maxId, maxId2, maxId3;

    function findMaxId1() {
        var maxId;
        for (var i in passageMap) {
            if (!maxId) {
                maxId = i;
            } else if (maxId < i) {
                maxId = i;
            }
        }
        return maxId;
    }

    function findMaxId2() {
        var maxId2;
        for (var key in rowFormula) {
            if (!maxId2) {
                maxId2 = key;
            } else if (maxId2 < key) {
                maxId2 = key;
            }
        }
        return maxId2;
    }

    function findMaxId3() {
        var maxId3;
        for (var key2 in rowFormula) {
            for (var key3 = 0; key3 < rowFormula[key2].length; key3++) {
                if (!maxId3) {
                    maxId3 = rowFormula[key2][key3].id;
                } else if (maxId3 < rowFormula[key2][key3].id) {
                    maxId3 = rowFormula[key2][key3].id;
                }
            }
        }
        return maxId3;
    }


    init();
    function init() {
        postJson('/datedelay/selectDateDelay.do', {}, function (data) {
            console.log(data);
            passageMap = data.passageMap;
            passageRowMap = data.passageRowMap;
            rowFormula = data.passageRowFormulaMap;
            var item = {};
            form.get().find(".itemlist").html("");

            for (var i in passageMap) {
                form.get().find(".passageMap .itemlist").append('<div class="item"><a href="#"  dateMapId="' + passageMap[i].id + '">' + passageMap[i].name + '</a></div>');
            }

        }, function () {
            //失败触发
        }, false);
    }

    //点击保存
    form.get().find(".datedelay_save").on("click", function () {
        console.log(passageMap);
        console.log(passageRowMap);
        console.log(rowFormula);

        var vail2, vail3;
        var vaild2 = [];
        var vaild3 = [];
        if (vaild2.length == 0) {
            console.log(vaild2.length);
        }
        for (var key2 in passageRowMap) {
            if (passageRowMap[key2].length == 0) {
                //新增未添加
                vail2 = 1;
                break;
            } else {
                var flag = true;//true表全删除
                if (passageMap[key2].frontDeleted !== "true") {
                    //父级没删除,第二级是否全删除
                    label2:for (var key22 = 0; key22 < passageRowMap[key2].length; key22++) {
                        if (passageRowMap[key2][key22].frontDeleted !== "true") {
                            flag = false;
                            break label2;
                        }
                    }
                    vail2 = flag ? 1 : 0;
                }
            }
        }
        for (var key3 in rowFormula) {
            if (rowFormula[key3].length == 0) {
                //新增未添加
                vail3 = 1;
                break;
            } else {
                vail3 = 0;
                //判断第三级是否全删除
                var flag3 = true;//true表全被删除
                label3:for (var key32 = 0; key32 < rowFormula[key3].length; key32++) {
                    if (rowFormula[key3][key32].frontDeleted !== "true") {
                        flag3 = false;
                        break label3;
                    }
                }
                if (flag3) {
                    //如果全删除，看它的第二级是否删除
                    for (var a in passageRowMap) {
                        for (var aa = 0; aa < passageRowMap[a].length; aa++) {
                            if (passageRowMap[a][aa].id == key3) {
                                if (passageRowMap[a][aa].frontDeleted !== "true") {
                                    vail3 = 1;
                                }
                            }
                        }
                    }
                }
                // vail3=flag3?1:0;
            }
        }

        //配置是否合法
        if (vail2 === 1 || vail3 === 1) {
            // $.messager.alert(msg("tishi"),msg("Configuration is not legal"));
            alert(msg("Configuration is not legal"));
        } else {
            var frontdata = {
                passageMap: {
                    inserted: [],
                    deleted: [],
                    updated: []
                },
                passageRowMap: {
                    inserted: [],
                    deleted: [],
                    updated: []
                },
                rowFormula: {
                    inserted: [],
                    deleted: [],
                    updated: []
                }
            };

            for (var i in passageMap) {
                if (passageMap[i].frontInsert == "true" && passageMap[i].frontDeleted != "true") {
                    //新增未被删除
                    frontdata.passageMap.inserted.push(passageMap[i]);
                }
                if (passageMap[i].frontDeleted == "true" && passageMap[i].frontInsert != "true") {
                    frontdata.passageMap.deleted.push(passageMap[i]);
                }
                if (passageMap[i].frontUpdated == "true") {
                    if (passageMap[i].frontInsert != "true" && passageMap[i].frontDeleted != "true") {
                        //修改的
                        frontdata.passageMap.updated.push(passageMap[i]);
                    }
                }
            }
            function getFrontdata(sectionData, sectionStr) {
                for (var j in sectionData) {
                    for (var x = 0; x < sectionData[j].length; x++) {
                        if (sectionData[j][x].frontInsert == "true" && sectionData[j][x].frontDeleted != "true") {
                            // 新增未被删除
                            frontdata[sectionStr].inserted.push(sectionData[j][x]);
                        }
                        if (sectionData[j][x].frontDeleted == "true" && sectionData[j][x].frontInsert != "true") {
                            //删除不是新增的
                            frontdata[sectionStr].deleted.push(sectionData[j][x]);
                        }
                        if (sectionData[j][x].frontUpdated == "true") {
                            if (sectionData[j][x].frontInsert != "true" && sectionData[j][x].frontDeleted != "true") {
                                frontdata[sectionStr].updated.push(sectionData[j][x]);
                            }
                        }
                    }
                }
            }

            getFrontdata(passageRowMap, "passageRowMap");
            getFrontdata(rowFormula, "rowFormula");
            console.log(frontdata);

            postJson('/datedelay/saveDelay.do', {frontdata: frontdata}, function (data) {
                console.log("222" + data);
                if (data) {
                    init();
                    alert(msg("Saved successfully!"));
                } else {
                    alert(msg("Save failed!"));
                }
            });
        }


    });

    //点击日历推移配置--->一级
    form.get().find(".passageMap").on("click", ".item a", function () {
        var dateMapId = $(this).attr("dateMapId");
        var passageMapItem = passageMap[dateMapId];

        if (passageMapItem) {
            //初始化编辑框  第一部分
            //基准表
            var table_id = [];
            var tables = dx.table;
            for (var key in tables) {
                if (tables[key].table_type != 2) {
                    table_id.push({
                        selectId: key,
                        selectName: tables[key].i18n[dx.user.language_id]
                    });
                }
            }

            var optionDelayType = gridDiceSelect("d_delayType");

            var tag = form.get().find("div[name=passageMap]");
            tag.find(".id").val(passageMapItem.id);
            tag.find(".name").val(passageMapItem.name);
            //推移类型
            RightOptionsData("div[name=passageMap] .type", optionDelayType, passageMapItem.type);
            // tag.find(".type").prepend('<option></option>');

            //基准表的下拉框
            RightOptionsData("div[name=passageMap] .main_table", table_id, passageMapItem.main_table);
            // tag.find(".main_table").prepend('<option></option>');

            //分组字段
            // console.log(passageMapItem.group_cols);
            var group_cols = groupColumn(passageMapItem.main_table, passageMapItem);
            RightOptionsData("div[name=passageMap] .group_cols", group_cols, passageMapItem.group_cols);
            tag.find(".disp_cols").val(passageMapItem.disp_cols);
            tag.find(".filter_tables").val(passageMapItem.filter_tables);
            tag.find(".filter_sql").val(passageMapItem.filter_sql);
            //是否追加
            if (passageMapItem.has_append == "0") {
                form.get().find("input.has_append").prop("checked", false);
            } else {
                form.get().find("input.has_append").prop("checked", true);
            }
            rcSwitch("has_append");

            var rowMap = passageRowMap[dateMapId];
            if (rowMap.length) {
                for (var i = 0; i < rowMap.length; i++) {
                    if (rowMap[i].frontDeleted != "true") {
                        form.get().find(".passageRowMap .itemlist").append('<div class="item"><a href="#" dateMapId="' + rowMap[i].id + '">' + rowMap[i].disp_name_key + '</a></div>');
                    }
                }
            }
        }
    });

    //获取---分组字段---数组
    function groupColumn(tableName) {
        var group_cols = [];
        if (dx.table[tableName]) {
            var columnList = dx.table[tableName].columns;
            for (var c = 0; c < columnList.length; c++) {
                // if (tables[key].table_type != 2){
                group_cols.push({
                    selectId: columnList[c].column_name,
                    selectName: columnList[c].i18n[dx.user.language_id]
                });
                // }
            }
        }
        return group_cols;
    }

    //基准表变化-->字段变化
    form.get().find("div[name=passageMap] .main_table").on("change", function () {
        var table_id = $(this).find("option:selected").attr("selectId");
        var group_cols = groupColumn(table_id);
        RightOptionsData("div[name=passageMap] .group_cols", group_cols, "");
    });

    //点击行信息--->二级
    form.get().find(".passageRowMap").on("click", ".item a", function () {
        var dateMapId = $(this).attr("dateMapId");
        var parentId = form.get().find(".passageMap a.active").attr("dateMapId");
        // passageRowMap
        //初始化行信息编辑框    第二部分
        var conList = passageRowMap[parentId];

        if (conList) {
            var conitem;
            for (var j = 0; j < conList.length; j++) {
                if (conList[j].id == dateMapId) {
                    conitem = conList[j];
                    break;
                }
            }

            var tag = form.get().find("div[name=passageRowMap]");

            tag.find(".disp_name_key").val(conitem.disp_name_key);
            tag.find(".upd_statement").val(conitem.upd_statement);
            tag.find(".edit_cond").val(conitem.edit_cond);
            tag.find(".edit_part_bg_color").colorpicker("val", conitem.edit_part_bg_color);
            tag.find(".edit_part_fg_color").colorpicker("val", conitem.edit_part_fg_color);
            tag.find(".unedit_part_bg_color").colorpicker("val", conitem.unedit_part_bg_color);
            tag.find(".unedit_part_fg_color").colorpicker("val", conitem.unedit_part_fg_color);
            tag.find(".decimal_digit").val(conitem.decimal_digit);
            tag.find(".total_row").val(conitem.total_row);
            tag.find(".total_col").val(conitem.total_col);
            tag.find(".detail_sql").val(conitem.detail_sql);

            //行末合计
            if (conitem.total_row == "0") {
                form.get().find("input.total_row").prop("checked", false);
                // honeySwitch.showOff(".total_row");
                // $("span[input-name=total_row]").find(".stoggler").removeClass("on").addClass("off");
            } else {
                form.get().find("input.total_row").prop("checked", true);

                // honeySwitch.showOn(".total_row");
                // $("span[input-name=total_row]").find(".stoggler").removeClass("off").addClass("on");
            }

            rcSwitch("total_row");
            //列末合计
            if (conitem.total_col == "0") {
                // honeySwitch.showOff(".total_col");
                form.get().find("input.total_col").prop("checked", false);
                // $("span[input-name=total_col]").find(".stoggler").removeClass("on").addClass("off");
            } else if (conitem.total_col == "1") {
                // honeySwitch.showOn(".total_col");

                form.get().find("input.total_col").prop("checked", true);
                // $("span[input-name=total_col]").find(".stoggler").removeClass("off").addClass("on");
            }
            rcSwitch("total_col");

            var rowMap = rowFormula[dateMapId];
            if (rowMap) {
                for (var i = 0; i < rowMap.length; i++) {
                    if (rowMap[i].frontDeleted != "true") {
                        form.get().find(".rowFormula .itemlist").append('<div class="item"><a href="#" dateMapId="' + rowMap[i].id + '">' + rowMap[i].id + '</a></div>');
                    }
                }
            }
        }
    });

    //点击公式配置--->三级
    form.get().find(".rowFormula").on("click", ".item a", function () {
        var dateMapId = $(this).attr("dateMapId");
        var parentId = form.get().find(".passageRowMap a.active").attr("dateMapId");
        //初始化行信息编辑框    第三部分
        var conList = rowFormula[parentId];
        if (conList) {
            var conitem;
            for (var i = 0; i < conList.length; i++) {
                if (conList[i].id == dateMapId) {
                    conitem = conList[i];
                    break;
                }
            }
            var tag = form.get().find("div[name=rowFormula]");
            tag.find(".level").val(conitem.level);
            var optionUseType = gridDiceSelect("d_useType");

            RightOptionsData("div[name=rowFormula] .type", optionUseType, conitem.type);
            tag.find(".type").prepend('<option></option>');

            tag.find(".cond").val(conitem.cond);
            tag.find(".formula").val(conitem.formula);
            tag.find(".sql").val(conitem.sql);
        }
    });

    //清空右边编辑栏-->归零
    function dateDetailClear() {
        form.get().find(".date-delay-detail input[type=text],.date-delay-detail input[type=number],.date-delay-detail textarea").val("");
        form.get().find(".swraper").find(".stoggler").removeClass("on").addClass("off");
        // $(".colorPick").colorpicker("val", "");
    }

    //新建是否可点出弹框
    form.get().find(".dropup .append").on("click", function () {
        if ($(this).attr("id") === "delay_add_rowMap") {
            if (form.get().find(".passageMap .itemlist a").hasClass("active")) {
                $(this).attr("data-toggle", "dropdown");
            } else {
                alert(msg("The previous menu is not selected"));
            }
        }
        /*else if ($(this).attr("id") === "delay_add_formula") {
         if ($(".passageRowMap .itemlist a").hasClass("active")) {
         $(this).attr("data-toggle", "dropdown");
         } else {
         alert("未选中上一级菜单");
         }
         }*/
    });

    //新建item--清空编辑框内容
    form.get().find(".dropup .rowadd").on("click", function () {
        var additem = $(this).parent().find("input").val();

        if ($.trim(additem) != "") {
            if ($(this).parents(".section").hasClass("passageMap")) {
                dateDetailClear();

                var mapListId = addItemFun(".passageMap", additem, "0");
                passageMap[mapListId] = {
                    frontInsert: "true",
                    disp_cols: "",
                    filter_sql: "",
                    filter_tables: "",
                    group_cols: "",
                    has_append: "0",
                    id: mapListId,
                    main_table: "0",
                    name: additem,
                    type: ""
                };
                passageRowMap[mapListId] = [];
                form.get().find(".passageMap").find(".itemlist").find("[dateMapId=" + mapListId + "]").click();
            } else if ($(this).parents(".section").hasClass("passageRowMap")) {
                dateDetailClear();

                var rowListId = addItemFun(".passageRowMap", additem, "R");
                var rowListParentId = form.get().find(".passageMap .itemlist a.active").attr("dateMapId");
                passageRowMap[rowListParentId].push({
                    frontInsert: "true",
                    edit_part_fg_color: "",
                    decimal_digit: "",
                    detail_sql: "",
                    disp_name_key: additem,
                    edit_cond: "",
                    edit_part_bg_color: "",
                    id: rowListId,
                    module: "",
                    passage_id: rowListParentId,
                    total_col: 0,
                    total_row: 0,
                    unedit_part_bg_color: "",
                    unedit_part_fg_color: "",
                    upd_statement: ""
                });
                rowFormula[rowListId] = [];
                form.get().find(".passageRowMap").find(".itemlist").find("[dateMapId=" + rowListId + "]").click();
            }
        } else if ($(this).parents(".section").hasClass("rowFormula")) {
            var formuListParentId = form.get().find(".passageRowMap .itemlist a.active").attr("dateMapId");
            if (formuListParentId) {
                var formuListId = addItemFun(".rowFormula", additem, "D");
                rowFormula[formuListParentId].push({
                    frontInsert: "true",
                    cond: "",
                    formula: "",
                    id: formuListId,
                    level: "",
                    module: "",
                    passage_row_id: formuListParentId,
                    sql: "",
                    type: 0
                });
                form.get().find(".rowFormula").find(".itemlist").find("[dateMapId=" + formuListId + "]").click();
            } else {
                // $.messager.alert(msg("tishi"),msg("The previous menu is not selected"));
                alert(msg("The previous menu is not selected"));
                // $.messager.alert('我的消息','未选中上一级菜单！','info');
            }
        } else {
            // $.messager.alert(msg("tishi"),msg("The input can not be empty"));
            alert(msg("The input can not be empty"));
        }

        function addItemFun(secClass, inpValue, headStr) {
            var num2, idNum;
            if (secClass == ".passageMap") {
                maxIdStr = findMaxId1();
            } else if (secClass == ".passageRowMap") {
                maxIdStr = findMaxId2();
            } else if (secClass == ".rowFormula") {
                maxIdStr = findMaxId3();
            }
            var secLeng = maxIdStr.length - 1;

            // if ($(secClass + " a").hasClass("newitem")) {
            //     idNum = parseInt($(secClass + " .item:last-child a").attr("dateMapId").substr(1, secLeng)) + 1;
            // } else {
            idNum = parseInt(maxIdStr.substr(1, secLeng)) + 1;
            // }

            num2 = formatID(headStr, maxIdStr.length, idNum);
            if (secClass == ".rowFormula") {
                form.get().find(secClass).find(".itemlist").append('<div class="item"><a href="#" class="newitem"  dateMapId="' + num2 + '">' + num2 + '</a></div>');
            } else {
                form.get().find(secClass).find(".itemlist").append('<div class="item"><a href="#" class="newitem"  dateMapId="' + num2 + '">' + inpValue + '</a></div>');
            }
            return num2;

        }

    });

    //新增修改input框
    form.get().find(".date-delay-detail input,.date-delay-detail textarea").on("blur", function () {
        var contIptName = $(this).attr("name");
        var contIptVal = $(this).val();
        var warpDetail = $(this).parents(".date-delay-section").attr("name");
        var activelist;
        var activeParent;

        if (warpDetail == "passageMap") {
            activelist = form.get().find(".passageMap .itemlist a.active").attr("dateMapId");
            //修改一级
            passageMap[activelist].frontUpdated = "true";
            passageMap[activelist][contIptName] = contIptVal;

            if (contIptName == "name") {
                form.get().find(".passageMap .itemlist a[dateMapId=" + activelist + "]").html(contIptVal);
            }

        } else if (warpDetail == "passageRowMap") {
            activelist = form.get().find(".passageRowMap .itemlist a.active").attr("dateMapId");
            activeParent = form.get().find(".passageMap .itemlist a.active").attr("dateMapId");
            //修改二级
            for (var i = 0; i < passageRowMap[activeParent].length; i++) {
                if (passageRowMap[activeParent][i].id == activelist) {
                    passageRowMap[activeParent][i].frontUpdated = "true";
                    passageRowMap[activeParent][i][contIptName] = contIptVal;
                }
            }

            if (contIptName == "disp_name_key") {
                form.get().find(".passageRowMap .itemlist a[dateMapId=" + activelist + "]").html(contIptVal);
            }

        } else if (warpDetail == "rowFormula") {
            activelist = form.get().find(".rowFormula .itemlist a.active").attr("dateMapId");
            activeParent = form.get().find(".passageRowMap .itemlist a.active").attr("dateMapId");
            //修改三级
            for (var j = 0; j < rowFormula[activeParent].length; j++) {
                if (rowFormula[activeParent][j].id == activelist) {
                    rowFormula[activeParent][j].frontUpdated = "true";
                    rowFormula[activeParent][j][contIptName] = contIptVal;
                }
            }
        }
    });

    //新增修改select框
    form.get().find(".date-delay-detail select").on("change", function () {
        var contIptName = $(this).attr("name");
        var contIptVal = $(this).find("option:selected").attr("selectid");
        console.log(contIptVal);
        var warpDetail = $(this).parents(".date-delay-section").attr("name");
        var activelist;
        var activeParent;

        if (warpDetail == "passageMap") {
            activelist = form.get().find(".passageMap .itemlist a.active").attr("dateMapId");
            //修改一级
            passageMap[activelist].frontUpdated = "true";
            passageMap[activelist][contIptName] = contIptVal;

        } else if (warpDetail == "passageRowMap") {
            activelist = form.get().find(".passageRowMap .itemlist a.active").attr("dateMapId");
            activeParent = form.get().find(".passageMap .itemlist a.active").attr("dateMapId");
            //修改二级
            for (var i = 0; i < passageRowMap[activeParent].length; i++) {
                if (passageRowMap[activeParent][i].id == activelist) {
                    passageRowMap[activeParent][i].frontUpdated = "true";
                    passageRowMap[activeParent][i][contIptName] = contIptVal;
                }
            }
        } else if (warpDetail == "rowFormula") {
            activelist = form.get().find(".rowFormula .itemlist a.active").attr("dateMapId");
            activeParent = form.get().find(".passageRowMap .itemlist a.active").attr("dateMapId");
            //修改三级
            for (var j = 0; j < rowFormula[activeParent].length; j++) {
                if (rowFormula[activeParent][j].id == activelist) {
                    rowFormula[activeParent][j].frontUpdated = "true";
                    rowFormula[activeParent][j][contIptName] = contIptVal;
                }
            }
        }
    });

    // rcSwitch(".total_col");
    form.get().find(".total_col,.total_row,.has_append").on({
        'turnon.rcSwitcher': function (e, dataObj) {
            var className = dataObj.$input.context.className;
            changeRcSwitchState(className, 1);
        },
        'turnoff.rcSwitcher': function (e, dataObj) {
            var className = dataObj.$input.context.className;
            changeRcSwitchState(className, 0);
        }
    });
    function changeRcSwitchState(contIptName, switchstate) {
        var state = switchstate;
        var activelist;
        var activeParent;
        // var contIptName=$(this).attr("input-name");
        var warpDetail = form.get().find("." + contIptName).parents(".date-delay-section").attr("name");


        if (warpDetail == "passageMap") {
            activelist = form.get().find(".passageMap .itemlist a.active").attr("dateMapId");
            //修改一级
            passageMap[activelist][contIptName] = state;
            console.log(passageMap);
        } else if (warpDetail == "passageRowMap") {
            activelist = form.get().find(".passageRowMap .itemlist a.active").attr("dateMapId");
            activeParent = form.get().find(".passageMap .itemlist a.active").attr("dateMapId");
            //修改二级
            for (var i = 0; i < passageRowMap[activeParent].length; i++) {
                if (passageRowMap[activeParent][i].id == activelist) {
                    passageRowMap[activeParent][i].frontUpdated = "true";
                    passageRowMap[activeParent][i][contIptName] = state;
                }
            }
            console.log(passageRowMap);
        }
    }

    //新增修改颜色框
    form.get().find(".date-delay-detail input.colorPick").on("change.color", function (event, color) {
        var activelist;
        var activeParent;
        var ff = color;
        var contColor = $(this).val();
        var contIptName = $(this).attr("name");

        activelist = form.get().find(".passageRowMap .itemlist a.active").attr("dateMapId");
        activeParent = form.get().find(".passageMap .itemlist a.active").attr("dateMapId");
        //修改二级
        if (passageRowMap[activeParent].length) {
            for (var i = 0; i < passageRowMap[activeParent].length; i++) {
                if (passageRowMap[activeParent][i].id == activelist) {
                    passageRowMap[activeParent][i].frontUpdated = "true";
                    passageRowMap[activeParent][i][contIptName] = contColor;
                }
            }
        }
    });

    //删除选中item
    form.get().find(".removeit").on("click", function () {
        var parentId;
        var parentArray;
        var removeId = $(this).parents(".section").find(".itemlist").find("a.active").attr("datemapid");
        if (removeId) {
            if ($(this).parents(".section").hasClass("passageMap")) {
                //删一级
                passageMap[removeId].frontDeleted = "true";
                $(this).parents(".section").find(".itemlist").find("a.active").remove();
                form.get().find(".passageRowMap .itemlist").html("");
                form.get().find(".rowFormula .itemlist").html("");

                form.get().find("[name=passageMap]").removeClass("active");

                if (passageRowMap[removeId].length) {
                    for (var key12 = 0; key12 < passageRowMap[removeId].length; key12++) {
                        passageRowMap[removeId][key12].frontDeleted = "true";
                        for (var key13 = 0; key13 < rowFormula[passageRowMap[removeId][key12].id].length; key13++) {
                            rowFormula[passageRowMap[removeId][key12].id][key13].frontDeleted = "true";
                        }
                    }
                }

                // dateDetailClear();
            } else if ($(this).parents(".section").hasClass("passageRowMap")) {
                //删二级
                parentId = form.get().find(".passageMap .item a.active").attr("datemapid");
                parentArray = passageRowMap[parentId];
                for (var i = 0; i < parentArray.length; i++) {
                    if (parentArray[i].id == removeId) {
                        passageRowMap[parentId][i].frontDeleted = "true";
                        break;
                    }
                }

                $(this).parents(".section").find(".itemlist").find("a.active").remove();
                form.get().find(".rowFormula .itemlist").html("");

                form.get().find("[name=passageRowMap]").removeClass("active");

                if (rowFormula[removeId].length) {
                    for (var key23 = 0; key23 < rowFormula[removeId].length; key23++) {
                        rowFormula[removeId][key23].frontDeleted = "true";
                    }
                }
                // dateDetailClear();
            } else if ($(this).parents(".section").hasClass("rowFormula")) {
                //删三级
                parentId = form.get().find(".passageRowMap .item a.active").attr("datemapid");
                parentArray = rowFormula[parentId];
                for (var j = 0; j < parentArray.length; j++) {
                    if (parentArray[j].id == removeId) {
                        rowFormula[parentId][j].frontDeleted = "true";
                        break;
                    }
                }
                $(this).parents(".section").find(".itemlist").find("a.active").remove();
                form.get().find("[name=rowFormula]").removeClass("active");
                // dateDetailClear();
            }
            // var removeId=activeId;
        }
    });

    //
    form.get().find(".section .itemlist").on("click", "a", function () {

        $(this).parents(".section").find("a").removeClass("active");
        $(this).addClass("active");

        var parentTag = $(this).parents(".section");

        if (parentTag.hasClass("passageMap")) {
            form.get().find(".passageRowMap .itemlist").html("");
            form.get().find(".rowFormula .itemlist").html("");

            form.get().find(".date-delay-detail .date-delay-section").removeClass("active");
            form.get().find(".date-delay-detail .date-delay-section[name=passageMap]").addClass("active");
        } else if (parentTag.hasClass("passageRowMap")) {
            form.get().find(".rowFormula .itemlist").html("");

            form.get().find(".date-delay-detail .date-delay-section").removeClass("active");
            form.get().find(".date-delay-detail .date-delay-section[name=passageRowMap]").addClass("active");

        } else if (parentTag.hasClass("rowFormula")) {
            form.get().find(".date-delay-detail .date-delay-section").removeClass("active");
            form.get().find(".date-delay-detail .date-delay-section[name=rowFormula]").addClass("active");
        }

    });

    //颜色选择插件
    form.get().find(".colorPick").colorpicker({
        strings: ",,,",
        history: false
    });

});