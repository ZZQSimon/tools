registerInit('checkWorkCount', function (form) {

    var plan = [];
    var data = [];
    var holiday = [];
    var now = new Date(); // 当前日期 
    var nowYear = now.getFullYear();
    var nowMonth = now.getMonth(); // 当前月
    if (nowMonth < 10)
        nowMonth = "0" + (nowMonth + 1);

    var year = $("#select-year").val(nowYear);
    var month = $("#select-month").val(nowMonth);

    var year = $("#select-year").val();
    var month = $("#select-month").val();
    var name = $(".grid_search").val();

    search(year, month, name);

    $(".grid_search").bind('keydown', function (event) {
        if (event.keyCode == "13") {
            $("span").click();
        }
    });

    $("span").on("click", function () {
        var year = $("#select-year").val();
        var month = $("#select-month").val();
        var name = $(".grid_search").val();
        search(year, month, name);
    });

    $("#select-year").on("change", function () {
        var year = $("#select-year").val();
        var month = $("#select-month").val();
        var name = $(".grid_search").val();
        search(year, month, name);
    });
    $("#select-month").on("change", function () {
        var year = $("#select-year").val();
        var month = $("#select-month").val();
        var name = $(".grid_search").val();
        search(year, month, name);
    });

    function search(year, month, name) {

        postJson('/checkWorkCount/selectAttendanceCount.do', {
            year: year,
            month: month
        }, function (result) {
            plan = result.plan;
            holiday = [];
            for (var i = 0; i < plan.length; i++) {
                if (plan[i] == 0) {
                    holiday.push(i + 2);
                }
            }
            init(year, month, name);
        });
    }

    function init(year, month, name) {

        console.log("holiday" + holiday);
        console.log("data" + data);
        console.log("plan" + plan);
        var days = plan.length + 1;
        var columns = [{
            "data": "name",
            "title": "姓名",
            "className": "my_class"
        }, {
            "data": "type",
            "title": "类型"
        }];
        for (var i = 1; i < days; i++) {
            columns.push({
                "data": i,
                "title": i
            });
        }

        if ($.fn.dataTable.isDataTable("#checkWCTable")) {
            try {
                console.log("#checkWCTable" + " DataTable.destroy+empty");
                $("#checkWCTable").DataTable().destroy();
                $("#checkWCTable").empty();
            } catch (e) {
                console.log("#checkWCTable" + " DataTable.empty");
                $("#checkWCTable").empty();
            }
        }

        $("#checkWCTable").DataTable({
            scrollY : true,
            scrollX: true,
            ordering: false,
            bPaginate: true, // 翻页功能
            iDisplayLength: 10, // 默认显示的记录数
            bLengthChange: true, // 改变每页显示数据数量
            bAutoWidth: false,
            sAjaxSource: '/checkWorkCount/selectAttendanceCount.do', // 请求资源路径
            serverSide: true, // 开启服务器处理模式
            /*
             * 使用ajax，在服务端处理数据 sSource:即是"sAjaxSource" aoData:要传递到服务端的参数
             * fnCallback:处理返回数据的回调函数
             */
            fnServerData: function (sSource, aoData, fnCallback) {
                postJson('/checkWorkCount/selectAttendanceCount.do', {
                    year: year,
                    month: month,
                    name: name,
                    aodata: JSON.stringify(aoData)
                }, function (result) {
                    console.log(result);
                    data = [];
                    var r = {};
                    var index = 0;
                    for (var i = 0; i < result.list.length; i++) {
                        var d1 = {};
                        var d2 = {};
                        r = result.list[i];
                        d1.name = r.user;
                        d1.type = "签到";

                        d2.name = r.user;
                        d2.type = "签退";
                        var rr = {};
                        for (var j = 0; j < r.list.length; j++) {
                            rr = r.list[j];
                            d1[j + 1] = rr.startState;
                            d2[j + 1] = rr.endState;
                        }

                        data[index] = d1;
                        data[index + 1] = d2;
                        index = index + 2;
                    }
                    var resp = {};
                    resp.sEcho = result.sEcho;
                    resp.iTotalRecords = result.iTotalRecords;
                    resp.iTotalDisplayRecords = result.iTotalDisplayRecords;
                    resp.aaData = data;
                    fnCallback(resp);

                });

            },
            language: {
                "zeroRecords": "没有找到记录",
                "info": "第 _PAGE_ 页 ( 总共 _PAGES_ 页 )",
                "infoEmpty": "无记录",
                "infoFiltered": "(从 _MAX_ 条记录过滤)",
                "oPaginate": {
                    "sFirst": "首页",
                    "sPrevious": "上页",
                    "sNext": "下页",
                    "sLast": "末页"
                },
                "sEmptyTable": "表中数据为空",
                "sLoadingRecords": "载入中..."
            },
            pagingType: "full_numbers",
            dom: 'rtlip',
            data: data,
            columns: columns,
            columnDefs: [{
                "sClass": "weeks",
                "aTargets": holiday
            }, {
                "aTargets": ["All"],
                "mRender": function (data, type, full) {
                    if (data != "未打卡")
                        return data.addClass("aa");
                    else
                        return "<font color='font-red-mint'>data</font>";
                }
            }]
            /*createdRow: function ( row, data, index ) {
             console.log(row);
             console.log(data);
             console.log(index);
             for(var i in data){
             if (data[i]=='未打卡') {
             var n=i+2;
             console.log(i);
             $(row).find("td").eq(n).css("color","red");
             // $('td', row).eq(0).css('color','red');
             // $(row+' td').eq(i).css('color', 'blue');
             }else if(data[i]=='迟到'){

             console.log($(row));
             }
             }

             }*/
        });
    }


    $("#checkWCTable").parents('.dx-auto-expand').on('dx.auto-expand', function () {
        console.log($(".checkWorkCount").height());
        console.log($(".checkWorkCount").height() - ($("#checkWCTable").listing ? 83 : 30));
        $(this).find('div.dataTables_scrollBody').height($(".checkWorkCount").height() - ($("#checkWCTable").listing ? 83 : 30));
    });

});