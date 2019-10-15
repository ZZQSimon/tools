registerInit('checkWork', function (form) {

    // 1 表示上班
    var holidays = [];
    var workdays = [];
    var events = [];
    var $form = form.get();
    form.weeks = [];

    // 第一次进入加载数据
    postJson('/calendar/selectCalendar.do', {}, function (result) {
        var weekdays = result.weekdays; // [0,1,1,1,1,1,0];
        // 加载特殊日期
        holidays = result.holidays;
        workdays = result.workdays;
        if (weekdays != null && weekdays.length > 0) {
            // 加载星期几
            var value;
            for (var i = 0; i < weekdays.length; i++) {
                // 1表示工作
                if (weekdays[i] == 1) {
                    value = i + 1;
                    if (value == 7) {
                        form.weeks.push(0);
                    } else {
                        form.weeks.push(value);
                    }
                }
            }
        }

        init();
        calendarRule();
        calendarSet(result);
        // 加载当月考勤
        selectCheckWork();

        $form.find(".checkWorkCal").on("change", "select", function () {
            var fcsYear = $form.find(".fcs_date_year").val();
            var fcsMonth = $form.find(".fcs_date_month").val();
            $form.find(".checkWorkCal").fullCalendar('gotoDate', fcsYear + " " + fcsMonth);

            calendarRule();
            calendarSet(result);
        });

        $form.find(".checkWorkCal").on('click', '.fc-prev-button,.fc-next-button', function () {
            calendarRule();
            calendarSet(result);
            selectCheckWork();
        });
    });

    function init() {
        // calendar option
        var option = {
            locale: 'zh-cn',
            height: 'parent',
            width: "100%",
            header: {
                left: 'none',
                center: 'prev, title, next',
                right: "today"
            },
            // defaultDate: '2017-05-12',
            // navLinks : true, // can click day/week names to navigate
            // views
            editable: true,
            eventLimit: true, // allow "more" link when too many
            // events
            fixedWeekCount: false,
            timezone: "local",
            // businessHours : {
            // days of week. an array of zero-based day of week
            // integers
            // (0=Sunday)
            // dow : weeks
            // Monday - Thursday
            // },
            events: events
        };
        // 初始化
        $form.find('.checkWorkCal').fullCalendar(option);

    }

    //添加考勤记录
    function selectCheckWork() {
        var view = $form.find('.checkWorkCal').fullCalendar('getView');
        var beginDate = $.fullCalendar.formatDate(view.start, "YYYY-MM-DD");
        var endDate = $.fullCalendar.formatDate(view.end, "YYYY-MM-DD");

        postJson('/checkWork/selectCheckWork.do', {
            beginDate: beginDate,
            endDate: endDate
        }, function (result) {
            // workDate,start_work_time,end_work_time,is_late,is_leave;// 1表示是

            var source = [];

            var work;
            var color;
            for (var i = 0; i < result.length; i++) {
                work = result[i];

                if (work.startClass != "") {
                    if (work.startClass == "a-loss") {
                        color = "#cb3a21";
                    } else if (work.startClass == "a-early") {
                        color = "#eca85e";
                    } else if (work.startClass == "a-common") {
                        color = "#5ec1ec";
                    }

                    source.push({
                        title: "上班：" + work.startTime,
                        start: work.workDate,
                        // color: color,
                        className: work.startClass
                    });
                }
                if (work.endClass != "") {
                    if (work.endClass == "b-loss") {
                        color = "#cb3a21";
                    } else if (work.endClass == "b-early") {
                        color = "#eca85e";
                    } else if (work.endClass == "b-common") {
                        color = "#5ec1ec";
                    }
                    source.push({
                        title: "下班：" + work.endTime,
                        start: work.workDate,
                        // color: color,
                        className: work.endClass
                    });
                }
            }
            $form.find('.checkWorkCal').fullCalendar('removeEventSources');
            // 增加事件
            $form.find('.checkWorkCal').fullCalendar('addEventSource', source);
            $form.find('.checkWorkCal').fullCalendar('refetchEvents');
        });
    }

    function calendarSet(result) {
        var holidays = result.holidays;
        var workdays = result.workdays;
        $form.find('.fc-bg').find('tbody').find('td').each(function () {
            var $td = $(this);
            var value = $(this).attr('data-date');
            if (holidays || workdays) {
                $.each(holidays, function (i, holiday) {
                    if (holiday == value) {
                        $td.addClass("rest");
                    }
                });
                $.each(workdays, function (i, workday) {
                    if (workday == value) {
                        $td.removeClass("rest");
                    }
                });
            }
        });
    }

    //是否更换某一周状态
    function calendarRule() {
        var weekdays = form.weeks;
        var $tbody = $form.find('.fc-bg').find('tbody');
        // var ruleData = form.ruleData;

        if (contain(weekdays, 1)) {
            $tbody.find('.fc-mon').each(function () {
                $(this).removeClass("rest");
            });
        } else {
            $tbody.find('.fc-mon').each(function () {
                $(this).addClass("rest");
            });
        }

        if (contain(weekdays, 2)) {
            $tbody.find('.fc-tue').each(function () {
                $(this).removeClass("rest");
            });
        } else {
            $tbody.find('.fc-tue').each(function () {
                $(this).addClass("rest");
            });
        }

        if (contain(weekdays, 3)) {
            $tbody.find('.fc-wed').each(function () {
                $(this).removeClass("rest");
            });
        } else {
            $tbody.find('.fc-wed').each(function () {
                $(this).addClass("rest");
            });
        }

        if (contain(weekdays, 4)) {
            $tbody.find('.fc-thu').each(function () {
                $(this).removeClass("rest");
            });
        } else {
            $tbody.find('.fc-thu').each(function () {
                $(this).addClass("rest");
            });
        }

        if (contain(weekdays, 5)) {
            $tbody.find('.fc-fri').each(function () {
                $(this).removeClass("rest");
            });
        } else {
            $tbody.find('.fc-fri').each(function () {
                $(this).addClass("rest");
            });
        }

        if (contain(weekdays, 6)) {
            $tbody.find('.fc-sat').each(function () {
                $(this).removeClass("rest");
            });
        } else {
            $tbody.find('.fc-sat').each(function () {
                $(this).addClass("rest");
            });
        }

        if (contain(weekdays, 0)) {
            $tbody.find('.fc-sun').each(function () {
                $(this).removeClass("rest");
            });
        } else {
            $tbody.find('.fc-sun').each(function () {
                $(this).addClass("rest");
            });
        }
    }

});