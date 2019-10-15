/**
 * User: huyannan
 * Date: 10/31/14
 * Time: 17:14 PM
 */

registerInit('calendarDetail', function (form) {
    var $container = form.get();
    calendarDetailGrid($container, form);
    initField(form);
    $('#' + form.id + '_div').fullCalendar({
        locale: 'zh-cn',
        firstDay: 1,
        height: 'parent',
        header: {
            left: 'none',
            center: 'prev,title,next',
            right: "today"
        },
        fixedWeekCount: false,
        handleWindowResize: true,
        dayClick: function () {
            var is_holiday = '0';
            if ($(this).hasClass("rest")) {
                $(this).removeClass("rest");
            } else {
                $(this).addClass("rest");
                is_holiday = '1';
            }
            var postData = {calendar_id: form.calendar_id, is_holiday: is_holiday, date: $(this).attr('data-date')};
            postJson('/calendar/saveSet.do', postData, function (setData) {
                form.setData = setData;
            });
//	        var moment = $('#' + form.id +'_div').fullCalendar('getDate');
//	        var view = $('#' + form.id +'_div').fullCalendar('getView');
        }
    });

    calendarRule(form);
    calendarSet(form);
    $('#' + form.id + '_div').find('.fc-button').on('click', function () {
        calendarRule(form);
        calendarSet(form);
    });
});

function calendarSet(form) {
    $('#' + form.id + '_div').find('.fc-bg').find('tbody').find('td').each(function () {
        var $td = $(this);
        var value = $(this).attr('data-date');
        $.each(form.setData, function (i, setData) {
//			calendar_id= setData.calendar_id;
            if (setData.date == value && setData.is_holiday == '1') {
                $td.addClass("rest");
            } else if (setData.date == value && setData.is_holiday == '0') {
                $td.removeClass("rest");
            }
        });
    });
}

function calendarRule(form) {
    var ruleData = form.ruleData;
    if (ruleData.monday == '1') {
        $('#' + form.id + '_div').find('.fc-bg').find('tbody').find('.fc-mon').each(function () {
            $(this).addClass("rest");
        });
    }
    if (ruleData.tuesday == '1') {
        $('#' + form.id + '_div').find('.fc-bg').find('tbody').find('.fc-tue').each(function () {
            $(this).addClass("rest");
        });
    }
    if (ruleData.wednesday == '1') {
        $('#' + form.id + '_div').find('.fc-bg').find('tbody').find('.fc-wed').each(function () {
            $(this).addClass("rest");
        });
    }
    if (ruleData.thursday == '1') {
        $('#' + form.id + '_div').find('.fc-bg').find('tbody').find('.fc-thu').each(function () {
            $(this).addClass("rest");
        });
    }
    if (ruleData.friday == '1') {
        $('#' + form.id + '_div').find('.fc-bg').find('tbody').find('.fc-fri').each(function () {
            $(this).addClass("rest");
        });
    }
    if (ruleData.saturday == '1') {
        $('#' + form.id + '_div').find('.fc-bg').find('tbody').find('.fc-sat').each(function () {
            $(this).addClass("rest");
        });
    }
    if (ruleData.sunday == '1') {
        $('#' + form.id + '_div').find('.fc-bg').find('tbody').find('.fc-sun').each(function () {
            $(this).addClass("rest");
        });
    }
}

/**
 * init data grid
 *
 * @param $container
 */
function calendarDetailGrid($container) {
    $container.find('table.dx-grid').each(function () {

        var $grid = $(this);
        var grid = w(this.id);
        var id = $grid.attr('id');

        function ajaxLoad(ignore, callback) {
            var postData = {id: id, condition: grid.condition};
            postJson('/widget/grid/list.do', postData, function (records) {
                // using by dataTable for rendering
                callback({data: buildGridData(grid, records, form)});
            });
        }

        var table = getTableDesc(grid.table);
        var lastKey = getTableIdColumn(table);
        var form = getFormModel(findFormId(grid.id));

        var options = {
            sort: false,
            ajax: ajaxLoad,
            order: [],
            paging: false,
            serverSide: true,
            scrollX: "100%"
        };
        options.columns = [
            {
                data: 'rowid',
                visible: form.action != 'view',
                render: function (data) {
                    return sprintf('<button type="button" class="btn btn-default btn-xs dx-grid-delete dx-grid-child-delete" value="%s"><span class="glyphicon glyphicon-minus"></span></button><button type="button" class="btn btn-default btn-xs dx-grid-edit dx-grid-child-edit" value="%s"><span class="glyphicon glyphicon-edit"></span></button>', data, data);
                }
            }
        ];
        options.dom = 'rt';
        $.each(table.columns, function (i, desc) {
            // virtual or password field will not display
            if (desc.virtual || desc.data_type == 6)
                return;
            var visible = true;
            if ((desc.column_name != lastKey) && (table.id_column.indexOf("[" + desc.column_name + "]") >= 0))
                visible = false;
            if (desc.hidden)
                visible = false;
            options.columns.push({data: desc.column_name, visible: visible});
        });
        $grid.DataTable(options);
        $grid.on('draw.dt', function () {
            $grid.DataTable().columns.adjust();
            // all record is drawn, we need cache them
            $.each(grid.records, function (i, record) {
                w(record);
            });
        });
    });
}
