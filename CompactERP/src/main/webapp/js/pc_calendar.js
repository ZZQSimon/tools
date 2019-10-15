/**
 * Created by Administrator on 2017/8/14.
 */

registerInit('pc_calendar', function (form) {
    function reload(){
        initAppointmentEvent(year, true);
        reloadEventDetail();
    }
    form.reload = reload;
    function reloadEventDetail(){
        var appointmentDetail = form.get().find('.appointment-detail').find('.dx-form');
        if (isEmpty(appointmentDetail) || 0 == appointmentDetail.length){
            return;
        }
        var appointmentDetailForm = getFormModel(appointmentDetail.attr('id'));
        eventDetail(appointmentDetailForm.appointment);
    }
    function appointmentButton(flag){
        if (flag){
            form.get().find('.appointment-add').hide();
        }else{
            form.get().find('.appointment-add').show();
        }
    }
    function removeAppointmentDetail(){
        form.get().find('.appointment-detail').empty();
    }
    //预约
    function initAppointmentSource(){
        function initTree() {
            $tree.jstree({
                'core': {
                    "multiple": true,
                    'data': null,
                    'dblclick_toggle': true          //tree的双击展开
                },
                "plugins": ["search"]
            });
        }
        function callback(result) {
            if (!isEmpty(result))
                if (isEmpty(result)) {
                    $tree.jstree(true).settings.core.data = null;
                    $tree.jstree(true).refresh();
                } else {
                    $tree.jstree(true).settings.core.data = result;
                    $tree.jstree(true).refresh();
                }
        }
        var $tree = $form.find('.appointment-source-tree');
        initTree();

        postJson('/calendar/selectAppointmentSource.do', {sourceTable: form.param.sourceTable,
            begin_time: form.param.sourceTable, end_time: form.param.end_time, group: form.param.group,
            color: form.param.color, filter: form.param.filter}, function (result){
            callback(result);
        });
        $tree.on("changed.jstree", function (e, data) {
            if (isEmpty(data) || isEmpty(data.node)){
                return;
            }
            var key = data.node.data[table.idColumns[0]];
            form.data_id = key;
            initAppointmentEvent(year);
            removeAppointmentDetail();
        });
        //左侧search
        var to;
        var leftSearch = form.get().find('.appointment-source-filter input');
        leftSearch.keyup(function () {
            if (to) {
                clearTimeout(to);
            }
            to = setTimeout(function () {
                $tree.jstree(true).search(leftSearch.val());
            }, 250);
        });
    }
    //新增预约
    form.get().find('.appointment-add').on('click', function () {
        if (isEmpty(form.data_id)){
            alert(msg('no source selected'));
            return;
        }
        var that = this;
        var appointment = {};
        appointment.data_id = form.data_id;
        appointment.table = form.table;
        postPage('/appointment/dayAppointmentCreate.view', {appointment: appointment}, function (result) {
            form.get().find('.appointment-calendar').hide();
            $(that).hide();
            form.get().find('.appointment-detail-cu').append(result);
            var newForm = getFormModel(form.get().find('.appointment-detail-cu').find('.dx-form').attr('id'));
            //buildFormCache(newForm, newForm.widgets);
            newForm.get = function () {
                return form.get().find('.appointment-detail-cu').find('.dx-form');
            };
            newForm.parentForm = form;
            newForm.param = form.param;
            dx.init.appointment(newForm);
            form.getSubmitData = newForm.getData;
        });
    });

    //删除预约
    form.get().find('.appointment-delete').on('click', function () {

    });
    var $form = form.get();
    // 1 表示上班
    var holidays = [];
    var workdays = [];
    var events = [];
    form.weeks = [];
    var changeCalendarEvents = {};
    inputui();

    $form.find(".pc-calendar-appoint .deploy-left-menu .g1-tree-icon-left").niceScroll({cursorborder: "", cursorcolor: "#aeb2b7"}); // First scrollable DIV


    function inputui() {
        $form.find(".pc-setDayCal input[type=checkbox]").iCheck({
            checkboxClass: 'icheckbox_flat-blue',
            radioClass: 'iradio_flat-blue'
        });
    }
    function removeByValue(arr, val) {
        for (var i = 0; i < arr.length; i++) {
            if (arr[i] == val) {
                arr.splice(i, 1);
                break;
            }
        }
    }
    function addByValue(arr, val) {
        if(!contain(arr, val)){
            arr.push(val);
        }
    }

    var year = new Date().getFullYear();
    var table = getTableDesc(form.table);
    // 第一次进入加载数据
    postJson('/calendar/selectCalendar.do', {type: form.type, eventDate: year}, function (result) {

        var sync = result.isSync;
        if (sync == 1) { //已同步
            $form.find("input[type='checkbox'][value='sync']").iCheck("check");
        }
        var weekdays = result.weekdays; // [0,1,1,1,1,1,0];
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
            // 设置星期选中
            for (var i = 0; i < form.weeks.length; i++) {
                $form.find("input[type='checkbox'][value=" + form.weeks[i] + "]").iCheck("check");
            }
        }
        if (form.type == '1') { //工作日历设置
            // 加载特殊日期
            holidays = result.holidays;
            workdays = result.workdays;
        } else if(form.type == '2'){
            if (isEmpty(form.eventYeas)) {
                form.eventYeas = {};
            }
            form.eventYeas[year] = result.pcCalendarModels;
            createEventShare(result.pcCalendarModels);
        }else if (form.type == '3'){

        }
        init(result);
        initEvents();
    });
    if (form.type == '3'){
        initAppointmentSource();
    }

    function eventDetail(appointment, isEdit) {
        $form.find(".pc-calendar-appoint .appoint-right-wrap").removeClass("dx-field-10").addClass("dx-field-7");
        var readonly = true;
        if (isEdit)
            readonly = false;
        postPage('/appointment/dayAppointment.view', {appointment: appointment, readonly: readonly}, function (result) {
            form.get().find('.appointment-detail').empty();
            form.get().find('.appointment-detail').append(result);
            var newForm = getFormModel(form.get().find('.appointment-detail').find('.dx-form').attr('id'));
            //buildFormCache(newForm, newForm.widgets);
            newForm.get = function () {
                return form.get().find('.appointment-detail').find('.dx-form');
            };
            newForm.parentForm = form;
            newForm.param = form.param;
            dx.init.appointment(newForm);
        });
    }
    $form.find(".pc-calendar-appoint").on("click", ".fc-agendaDay-button", function () {}).on("click", ".fc-month-button", function () {
        form.get().find('.appointment-detail').html("");
        // form.get().find('.appointment-detail').hide();
        $form.find(".pc-calendar-appoint .appoint-right-wrap").removeClass("dx-field-7").addClass("dx-field-10");

    });
    var option = {};
    function init(result) {
        // calendar option
        option = {
            locale: 'zh-cn',
            height: 'parent',
            header: {
                left: 'none',
                center: 'prev, title, next',
                right: "today"
            },
            editable: true,
            eventLimit: 3, // 一天有多个事件。是否全部显示。还是显示more链接。  或者整数。（表示一天中显示几个）
            //isRTL: true,     //从周一到周日。还是从周日到周一排列。
            // events
            navLinks: true, // can click day/week names to navigate views
            fixedWeekCount: false,
            timezone: "local",
            timeFormat: 'H:mm',
            events: events
        };
        if (form.type == '1') {
            option.dayClick = function (date, allDay, jsEvent, view) {
                var day = $.fullCalendar.formatDate(date, "YYYY-MM-DD");
                var d = new Date(day);
                var workWeek = form.weeks;
                var isExists = false;

                // 原本是休息日
                if (!contain(workWeek, d.getDay())) {
                    // isExists = false;
                    for (var i = 0; i < workdays.length; i++) {
                        // 如果存在则移除
                        if (workdays[i] == day) {
                            workdays.splice(i, 1);
                            isExists = true;
                            $(this).addClass("rest");
                            break;
                        }
                    }
                    // 如果不存在则添加
                    if (!isExists) {
                        workdays.push(day);
                        $(this).removeClass("rest");
                    }
                } else {
                    // 原本是工作日
                    // isExists = false;
                    for (var i = 0; i < holidays.length; i++) {
                        // 如果存在则移除
                        if (holidays[i] == day) {
                            holidays.splice(i, 1);
                            isExists = true;
                            $(this).removeClass("rest");
                            break;
                        }
                    }
                    // 如果不存在则添加
                    if (!isExists) {
                        holidays.push(day);
                        $(this).addClass("rest");
                    }
                }
            }
        } else if(form.type == '2'){
            option.header.left= 'month,agendaDay';
            //option.eventOrder = function (sourse, targer) {  //只有起始日。在同一天的。这个排序才有效。
            //    return sourse.data.seq - targer.data.seq;
            //},
            //option.eventRender = function(event, element) {
            //    $(element).find('.fc-content').prepend('<span class="pc-event-point" style="display:inline-block;width:5px;height:5px;background:' + event.data.calendarEvent.color + ';"><span>');
            //},
            option.eventClick = function (calEvent, jsEvent, view) {
                var calendar_id = calEvent.data.calendarEvent.calendar_id;
                var p_calendar_event_id = calEvent.data.calendarEvent.p_calendar_event_id;
                postPage('/calendar/eventDetail.view', {
                    calendar_id: calendar_id,
                    p_calendar_event_id: p_calendar_event_id
                }, function (result) {
                    closeForm(form.id, function () {
                        var $form = $('#' + form.id);
                        var $div = $form.parent();
                        $form.remove();
                        $div.append(result);

                        // init new form
                        var newForm = getFormModel($div.find('.dx-form').attr('id'));
                        buildFormCache(newForm, newForm.widgets);
                        dx.init.eventDetail(newForm);
                    });
                });
            };
            option.eventDrop = function (event, delta, revertFunc, jsEvent, ui, view) {
                if (isEmpty(delta) || isEmpty(delta._days) || delta._days == 0)
                    return;
                if (isEmpty(event.data) || isEmpty(dx.user) || isEmpty(event.data.calendarEvent) || event.data.calendarEvent.owner != dx.user.id) {
                    revertFunc();
                    dxToastAlert(msg('not your event'));
                    return;
                }
                var calendarEvent;
                if (!isEmpty(event.data)) {
                    calendarEvent = event.data.calendarEvent;
                    calendarEvent.begin_date = calendarEvent.begin_date + delta._days * 24 * 60 * 60 * 1000;
                    calendarEvent.end_date = calendarEvent.end_date + delta._days * 24 * 60 * 60 * 1000;
                    changeCalendarEvents[event.data.id] = calendarEvent;
                }
            };
            option.eventResize = function (event, delta, revertFunc, jsEvent, ui, view) {
                if (isEmpty(delta) || isEmpty(delta._days) || delta._days == 0)
                    return;
                if (isEmpty(event.data) || isEmpty(dx.user) || isEmpty(event.data.calendarEvent) || event.data.calendarEvent.owner != dx.user.id) {
                    revertFunc();
                    dxToastAlert(msg('not your event'));
                    return;
                }
                var calendarEvent;
                if (!isEmpty(event.data)) {
                    calendarEvent = event.data.calendarEvent;
                    calendarEvent.end_date = calendarEvent.end_date + delta._days * 24 * 60 * 60 * 1000;
                    changeCalendarEvents[event.data.id] = calendarEvent;
                }
            };
            option.viewRender = function (view, element) {
                var viewStart = view.intervalStart._d;
                var year = viewStart.getFullYear();
                if (isEmpty(form.eventYeas) || isEmpty(form.eventYeas[year])) {
                    postJson('/calendar/selectTimeCalendar.do', {eventDate: year}, function (result) {
                        var yearEvents = createEventShare(result.pcCalendarModels);
                        $form.find('.pcSetWorkDay').fullCalendar('addEventSource', yearEvents);
                        $form.find('.pcSetWorkDay').fullCalendar('refetchEvents');
                        if (isEmpty(form.eventYeas)) {
                            form.eventYeas = {};
                        }
                        form.eventYeas[year] = result.pcCalendarModels;
                    });
                }
            };
        }else if(form.type == '3'){

            option.header.left= 'month,agendaWeek,agendaDay';
            option.navLinks=true;
            option.navLinkDayClick = function(data){
                //eventDetail();
                $form.find('.pcSetWorkDay').fullCalendar('changeView', 'agendaDay',data._i);
            };
            option.viewRender = function (view, element) {
                var viewStart = view.intervalStart._d;
                year = viewStart.getFullYear();
                initAppointmentEvent(year);
                //预约按钮的隐藏与显示
                //var currentView = $form.find('.pcSetWorkDay').fullCalendar('getView');

            };
            option.eventClick = function (calEvent, jsEvent, view) {
                var currentView = $form.find('.pcSetWorkDay').fullCalendar('getView');
                if (currentView.type == 'agendaDay'){
                    //加载预约详情
                    eventDetail(calEvent.data);
                }else if(currentView.type == 'month'){
                    $form.find('.pcSetWorkDay').fullCalendar('changeView', 'agendaDay', calEvent.start._i);
                }
            };
            option.dayClick = function (date, jsEvent, view){
                $form.find('.pcSetWorkDay').fullCalendar('changeView', 'agendaDay', date._d);
            };
            option.eventDrop = function (event, delta, revertFunc, jsEvent, ui, view) {
                if (isEmpty(event) || isEmpty(event.data))
                    return;
                var appointment = event.data;
                switch (view.type){
                    case 'month':
                        appointment.begin_time = appointment.begin_time + delta._days * 24 * 60 * 60 * 1000;
                        appointment.end_time = appointment.end_time + delta._days * 24 * 60 * 60 * 1000;
                        break;
                    case 'agendaWeek':
                        appointment.begin_time = appointment.begin_time + delta._days * 24 * 60 * 60 * 1000 + delta._milliseconds;
                        appointment.end_time = appointment.end_time + delta._days * 24 * 60 * 60 * 1000 + delta._milliseconds;
                        break;
                    case 'agendaDay':
                        appointment.begin_time = appointment.end_time + delta._milliseconds;
                        appointment.end_time = appointment.end_time + delta._milliseconds;
                        break;
                }
                postJson('/appointment/appointmentEdit.do', {appointment: appointment}, function (result) {
                    if (!result.status){
                        revertFunc();
                    }
                    dxToastAlert(msg(result.msg));
                });
            };
            option.eventResize = function (event, delta, revertFunc, jsEvent, ui, view) {
                var aa = 1;
            };
        }
        // 初始化
        $form.find('.pcSetWorkDay').fullCalendar(option);
        calendarRule();
        calendarSet(result);

        $form.find(".pcSetWorkDay ").on("change", "select", function () {
            var fcsYear = $form.find(".fcs_date_year").val();
            var fcsMonth = $form.find(".fcs_date_month").val();
            $form.find(".pcSetWorkDay").fullCalendar('gotoDate', fcsYear + " " + fcsMonth);

            calendarRule();
            calendarSet(result);
        });

        $form.find('.fc-button').on('click', function () {
            calendarRule();
            calendarSet(result);
        });
    }

    function initEvents() {
        // set workday
        $form.find('.pc-setDayCal .select-box input').on('ifChecked', function () {
            var week = parseInt($(this).val());
            addByValue(form.weeks,week);
            changeWeekState(week,"select");
            // calendarSet(result);
            // 去掉这一列本来就是工作的日期 workdays
            var day;
            var temp = []; // 要移除的
            for (var i = 0; i < workdays.length; i++) {
                day = workdays[i];
                if (new Date(day).getDay() == week) {
                    temp.push(day);
                }
            }

            for (var i = 0; i < temp.length; i++) {
                for (var j = 0; j < workdays.length; j++) {
                    if (workdays[j] == temp[i]) {
                        workdays.splice(j, 1);
                        break;
                    }
                }
            }
        }).on('ifUnchecked', function () {
            var week = parseInt($(this).val());
            removeByValue(form.weeks, week);
            changeWeekState(week,"unselect");
            // 去掉这一列本来就是休假的日期 holidays
            var day;
            var temp = []; // 要移除的
            for (var i = 0; i < holidays.length; i++) {
                day = holidays[i];
                if (new Date(day).getDay() == week) {
                    temp.push(day);
                }
            }
            for (var i = 0; i < temp.length; i++) {
                for (var j = 0; j < holidays.length; j++) {
                    if (holidays[j] == temp[i]) {
                        holidays.splice(j, 1);
                        break;
                    }
                }
            }

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
        var $tbody=$form.find('.fc-bg').find('tbody');
        // var ruleData = form.ruleData;

        if (contain(weekdays, 1)) {
            $tbody.find('.fc-mon').each(function () {
                $(this).removeClass("rest");
            });
        }else {
            $tbody.find('.fc-mon').each(function () {
                $(this).addClass("rest");
            });
        }

        if (contain(weekdays, 2)) {
            $tbody.find('.fc-tue').each(function () {
                $(this).removeClass("rest");
            });
        }else {
            $tbody.find('.fc-tue').each(function () {
                $(this).addClass("rest");
            });
        }

        if (contain(weekdays, 3)) {
            $tbody.find('.fc-wed').each(function () {
                $(this).removeClass("rest");
            });
        }else {
            $tbody.find('.fc-wed').each(function () {
                $(this).addClass("rest");
            });
        }

        if (contain(weekdays, 4)) {
            $tbody.find('.fc-thu').each(function () {
                $(this).removeClass("rest");
            });
        }else {
            $tbody.find('.fc-thu').each(function () {
                $(this).addClass("rest");
            });
        }

        if (contain(weekdays, 5)) {
            $tbody.find('.fc-fri').each(function () {
                $(this).removeClass("rest");
            });
        }else {
            $tbody.find('.fc-fri').each(function () {
                $(this).addClass("rest");
            });
        }

        if (contain(weekdays, 6)) {
            $tbody.find('.fc-sat').each(function () {
                $(this).removeClass("rest");
            });
        }else {
            $tbody.find('.fc-sat').each(function () {
                $(this).addClass("rest");
            });
        }

        if (contain(weekdays, 0)) {
            $tbody.find('.fc-sun').each(function () {
                $(this).removeClass("rest");
            });
        }else {
            $tbody.find('.fc-sun').each(function () {
                $(this).addClass("rest");
            });
        }
    }

    function changeWeekState(week,state) {
        var $tbody=$form.find('.fc-bg').find('tbody');

        switch(week) {
            case 1:
                if(state == "select"){
                    $tbody.find('.fc-mon').each(function () {
                        $(this).removeClass("rest");
                    });
                }else{
                    $tbody.find('.fc-mon').each(function () {
                        $(this).addClass("rest");
                    });
                }
                break;
            case 2:
                if(state == "select"){
                    $tbody.find('.fc-tue').each(function () {
                        $(this).removeClass("rest");
                    });
                }else{
                    $tbody.find('.fc-tue').each(function () {
                        $(this).addClass("rest");
                    });
                }
                break;
            case 3:
                if(state == "select"){
                    $tbody.find('.fc-wed').each(function () {
                        $(this).removeClass("rest");
                    });
                }else{
                    $tbody.find('.fc-wed').each(function () {
                        $(this).addClass("rest");
                    });
                }
                break;
            case 4:
                if(state == "select"){
                    $tbody.find('.fc-thu').each(function () {
                        $(this).removeClass("rest");
                    });
                }else{
                    $tbody.find('.fc-thu').each(function () {
                        $(this).addClass("rest");
                    });
                }
                break;
            case 5:
                if(state == "select"){
                    $tbody.find('.fc-fri').each(function () {
                        $(this).removeClass("rest");
                    });
                }else{
                    $tbody.find('.fc-fri').each(function () {
                        $(this).addClass("rest");
                    });
                }
                break;
            case 6:
                if(state == "select"){
                    $tbody.find('.fc-sat').each(function () {
                        $(this).removeClass("rest");
                    });
                }else{
                    $tbody.find('.fc-sat').each(function () {
                        $(this).addClass("rest");
                    });
                }
                break;
            case 0:
                if(state == "select"){
                    $tbody.find('.fc-sun').each(function () {
                        $(this).removeClass("rest");
                    });
                }else{
                    $tbody.find('.fc-sun').each(function () {
                        $(this).addClass("rest");
                    });
                }
                break;
            default:
                break;

        }
    }
    //事件
    function createEventShare(pcCalendarModels) {
        var yearEvents = [];
        if (!isEmpty(pcCalendarModels)) {
            for (var i = 0; i < pcCalendarModels.length; i++) {
                var event = {};
                if (pcCalendarModels[i].calendarEvent.color == "#ffffff") {

                }
                event.id = pcCalendarModels[i].id;
                if (pcCalendarModels[i].calendarEvent.color == "#FFFFFF") {
                    event.className = "pcWhiteBg";
                }
                event.title = pcCalendarModels[i].calendarEvent.event_name;
                event.start = pcCalendarModels[i].calendarEvent.begin_date;
                event.end = pcCalendarModels[i].calendarEvent.end_date;
                // event.allDay = true;
                event.slotEventOverlap = false;
                event.data = pcCalendarModels[i];
                event.backgroundColor = pcCalendarModels[i].calendarEvent.color;
                //if (isEmpty(pcCalendarModels[i]) || isEmpty(dx.user)  || pcCalendarModels[i].calendarEvent.owner != dx.user.id){
                //    event.editable = false;
                //}
                events.push(event);
                yearEvents.push(event);
            }
        }
        return yearEvents;
    }
    //预约日历事件
    function createAppointmentEvent(appointments){
        var yearEvents = [];
        if (!isEmpty(appointments)) {
            for (var i = 0; i < appointments.length; i++) {
                var event = {};
                event.id = appointments[i].id;
                event.title = appointments[i].memo;
                event.start = appointments[i].begin_time;
                event.end = appointments[i].end_time;
                // event.allDay = true;
                event.slotEventOverlap = false;
                event.data = appointments[i];
                event.backgroundColor = appointments[i].color;
                events.push(event);
                yearEvents.push(event);
            }
        }
        return yearEvents;
    }
    function initAppointmentEvent(year, reload){
        if (isEmpty(form.appointmentEventYeas)){
            form.appointmentEventYeas = {};
        }
        if (isEmpty(form.appointmentEventYeas[form.table])){
            form.appointmentEventYeas[form.table] = {};
        }
        if (isEmpty(form.data_id)){
            return;
        }
        if (isEmpty(form.appointmentEventYeas[form.table][form.data_id])){
            form.appointmentEventYeas[form.table][form.data_id] = {};
        }
        if (!reload)
            if (!isEmpty(form.appointmentEventYeas[form.table][form.data_id][year])) {
                return;
            }
        var appointmentParam = {};
        appointmentParam.table = form.table;
        appointmentParam.data_id = form.data_id;
        appointmentParam.year = year;
        postJson('/calendar/selectAppointmentEvent.do', {appointmentParam: appointmentParam}, function (result) {
            var yearEvents = createAppointmentEvent(result);
            //移除其它ID的预约信息。
            removeOtherEvents(reload);
            $form.find('.pcSetWorkDay').fullCalendar('addEventSource', yearEvents);
            $form.find('.pcSetWorkDay').fullCalendar('refetchEvents');
            if (isEmpty(form.appointmentEventYeas[form.table][form.data_id]))
                form.appointmentEventYeas[form.table][form.data_id] = {};
            form.appointmentEventYeas[form.table][form.data_id][year] = result;
        });
    }
    function removeOtherEvents(reload){
        if (isEmpty(form.data_id))
            return;
        var tableEvents = form.appointmentEventYeas[form.table];
        for (var key in tableEvents){
            if (!reload)
                if (key == form.data_id){
                    continue;
                }
            var dataEvents = tableEvents[key];
            for (var yearKey in dataEvents){
                if (!isEmpty(dataEvents[yearKey]))
                    for (var i=0; i<dataEvents[yearKey].length; i++){
                        $form.find('.pcSetWorkDay').fullCalendar('removeEvents', dataEvents[yearKey][i].id);
                    }
            }
            tableEvents[key] = null;
        }
    }
    form.get().find('.addCalendar').on('click', function () {
        postPage('/calendar/eventCreate.view', {}, function (result) {
            closeForm(form.id, function () {
                var $form = $('#' + form.id);
                var $div = $form.parent();
                $form.remove();
                $div.append(result);

                // init new form
                var newForm = getFormModel($div.find('.dx-form').attr('id'));
                buildFormCache(newForm, newForm.widgets);
                dx.init.eventDetail(newForm);
            });
        });
    });
    // 同步国际节假日syncInternationalHoliday
    form.get().find('.syncInternationalHoliday').on('click', function () {
        if (confirm(msg('Will Clear Set Of Calendar Data If Alter'))) {
            postJson('/calendar/syncInternationalHoliday.do', {}, function (result) {
                var weekdays = result.weekdays;
                $form.find("input[type='checkbox']").iCheck("unCheck");
                if (weekdays != null && weekdays.length > 0) {
                    // 加载星期几
                    var value;
                    form.weeks = [];
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
                    // 设置星期选中
                    for (var i = 0; i < form.weeks.length; i++) {
                        form.get().find("input[type='checkbox'][value=" + form.weeks[i] + "]").iCheck("check");
                    }
                }
                // 增加特殊日期
                var day = '';
                for (var i = 0; i < result.workdays.length; i++) {
                    day = result.workdays[i];
                    if (!contain(workdays, day)) {
                        workdays.push(day);
                    }
                }
                for (var i = 0; i < result.holidays.length; i++) {
                    day = result.holidays[i];
                    if (!contain(holidays, day)) {
                        holidays.push(day);
                    }
                }

                calendarRule();
                calendarSet(result);
                dxToastAlert(msg('HaveSynchronous'));
            });
        }
    });

    // 获取选中的星期
    form.get().find(".saveCalendar").on("click", function () {
        if (form.type == '1') {
            var weekdays = [0, 0, 0, 0, 0, 0, 0];
            var week = form.weeks;
            var w;
            for (var i = 0; i < week.length; i++) {
                w = week[i];
                if (w == 0)
                    w = 7;
                weekdays[w - 1] = 1;
            }
            var param = {
                weekdays: weekdays,
                holidays: holidays,
                workdays: workdays
            };
           /* var param = {
             weekdays: weekdays,
             holidays: [],
             workdays: []
             };*/
            // 保存数据,先删除所有的特殊日期，再添加
            postJson('/calendar/saveCalendar.do', param, function (result) {
                if (result == "success") {
                    dxToastAlert(msg('Saved successfully!'));
                }
            });
        } else if (form.type == '2'){
            postJson('/calendar/saveCalendarEvents.do', {calendarEvents: changeCalendarEvents}, function (result) {
                dxToastAlert(msg('success'));
            });
        }else if (form.type == '3'){
            var data;
            if (!isEmpty(form.getSubmitData)){
                data = form.getSubmitData();
                if (data.type == 'create'){
                    postJson('/appointment/appointmentCreate.do', {appointment: data}, function (result) {
                        dxToastAlert(msg(result));

                        appointmentButton(false);
                        form.get().find('.appointment-calendar').show();
                        form.get().find('.appointment-detail-cu').empty();
                        reload();
                    });
                }else{
                    postJson('/appointment/appointmentEdit.do', {appointment: data}, function (result) {
                        dxToastAlert(msg(result));

                        form.get().find('.appointment-calendar').show();
                        form.get().find('.appointment-detail-cu').empty();
                        reload();
                    });
                }
            }
        }
    });
});
