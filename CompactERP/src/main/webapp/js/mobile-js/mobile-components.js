(function ($, window, document, undefined) {

    var Calendar = function (elem, options) {
        this.$calendar = elem;

        this.defaults = {
            ifSwitch: true,
            hoverDate: false,
            backToday: false
        };

        this.opts = $.extend({}, this.defaults, options);
    };

    Calendar.prototype = {
        showCalendar: function () { // 输入数据并显示
            var self = this;
            var year = dateObj.getDate().getFullYear();
            var month = dateObj.getDate().getMonth() + 1;
            var dateStr = returnDateStr(dateObj.getDate());
            var firstDay = new Date(year, month - 1, 1); // 当前月的第一天
            var firstDayStr = returnDateStr(firstDay);

            this.$calendarTitle_text.text(year + '  ' + dateStr.substr(4, 2));

            this.$calendarDate_item_div.each(function (i) {
                // allDay: 得到当前列表显示的所有天数
                var allDay = new Date(year, month - 1, i + 1 - firstDay.getDay());
                var allDay_str = returnDateStr(allDay);

                $(this).text(allDay.getDate()).parent().attr('data', allDay_str);

                if (returnDateStr(new Date()) === allDay_str) {
                    $(this).parent().attr('class', 'item item-curDay');
                } else if (returnDateStr(firstDay).substr(0, 6) === allDay_str.substr(0, 6)) {
                    $(this).parent().attr('class', 'item item-curMonth');
                } else {
                    $(this).parent().attr('class', 'item');
                }

                if (firstDayStr == allDay_str) {
                    $(this).parent().addClass('firstDay');
                }

            });
            if (self.opts.initCalendarRule != undefined)
                self.opts.initCalendarRule(self.opts.form, month);
            if (self.opts.initEventTag != undefined)
                self.opts.initEventTag(self.opts.form, month);

            // 已选择的情况下，切换日期也不会改变
            if (self.selected_data) {
                var selected_elem = self.$calendar_date.find('[data=' + self.selected_data + ']');
                selected_elem.addClass('item-selected');
            }
        },

        renderDOM: function () { // 渲染DOM
            this.$calendar_title = $('<div class="calendar-title"></div>');
            this.$calendar_week = $('<ul class="calendar-week"></ul>');
            this.$calendar_date = $('<ul class="calendar-date"></ul>');
            this.$calendar_today = $('<div class="calendar-today"></div>');


            var _titleStr =
                    // '<div class="">' +
                    '<span class="arrow-prev fa fa-angle-left"></span>' +
                    '<span class="title"></span>' +
                    '<span class="arrow-next fa fa-angle-right"></span>'
                // '</div>'
            ;
            var _weekStr =
            	'<li class="item">日</li>' +
                '<li class="item">一</li>' +
                '<li class="item">二</li>' +
                '<li class="item">三</li>' +
                '<li class="item">四</li>' +
                '<li class="item">五</li>' +
                '<li class="item">六</li>' ;
                
            var _dateStr = '';
            var _dayStr = '<i class="triangle"></i>' +
                '<p class="date"></p>' +
                '<p class="week"></p>';

            for (var i = 0; i < 6; i++) {
                _dateStr += '<div class="cal-row"><li class="item" name="monday"><div class="item-text">26</div></li>' +
                    '<li class="item" name="tuesday"><div class="item-text">26</div></li>' +
                    '<li class="item" name="wednesday"><div class="item-text">26</div></li>' +
                    '<li class="item" name="thursday"><div class="item-text">26</div></li>' +
                    '<li class="item" name="friday"><div class="item-text">26</div></li>' +
                    '<li class="item" name="saturday"><div class="item-text">26</div></li>' +
                    '<li class="item" name="sunday"><div class="item-text">26</div></li></div>';
            }

            this.$calendar_title.html(_titleStr);
            this.$calendar_week.html(_weekStr);
            this.$calendar_date.html(_dateStr);
            this.$calendar_today.html(_dayStr);

            this.$calendar.append(this.$calendar_title, this.$calendar_week, this.$calendar_date, this.$calendar_today);
            this.$calendar.show();
        },

        inital: function () { // 初始化
            dateObj.setDate(new Date());

            var self = this;
            this.renderDOM();

            this.$calendarTitle_text = this.$calendar_title.find('.title');
            this.$backToday = $('#backToday');
            this.$arrow_prev = this.$calendar_title.find('.arrow-prev');
            this.$arrow_next = this.$calendar_title.find('.arrow-next');
            this.$calendarDate_item = this.$calendar_date.find('.item');
            this.$calendarDate_item_div = this.$calendar_date.find('.item div');
            this.$calendarToday_date = this.$calendar_today.find('.date');
            this.$calendarToday_week = this.$calendar_today.find('.week');

            this.selected_data = 0;

            this.showCalendar();

            if (this.opts.ifSwitch) {
                this.$arrow_prev.bind('click', function () {
                	$(".calendar-date").find(".cal-row").removeClass("isopen");
                    $(".calendar-date").find(".cal-collapse").collapse('hide').remove()
                    
                    var _date = dateObj.getDate();
                    dateObj.setDate(new Date(_date.getFullYear(), _date.getMonth() - 1, 1));
                    self.showCalendar();
                });

                this.$arrow_next.bind('click', function () {
                    $(".calendar-date").find(".cal-row").removeClass("isopen");
                    $(".calendar-date").find(".cal-collapse").collapse('hide').remove();
                    
                    var _date = dateObj.getDate();
                    dateObj.setDate(new Date(_date.getFullYear(), _date.getMonth() + 1, 1));
                    self.showCalendar();

                });
            }

            if (this.opts.backToday) {
                var cur_month = dateObj.getDate().getMonth() + 1;

                this.$backToday.bind('click', function () {
                    var item_month = $('.item-curMonth').eq(0).attr('data').substr(4, 2);
                    var if_lastDay = (item_month != cur_month) ? true : false;

                    if (!self.$calendarDate_item.hasClass('item-curDay') || if_lastDay) {
                        dateObj.setDate(new Date());

                        self.showCalendar();
                    }
                });
            }

            this.$calendarDate_item_div.hover(function () {
                //self.showHoverInfo($(this));
            }, function () {
                self.$calendar_today.css({left: 0, top: 0}).hide();
            });

            this.$calendarDate_item_div.click(function (event) {
                /*  event.stopPropagation();
                 var _dateStr = $(this).parent().attr('data');
                 var _date = changingStr(addMark(_dateStr));
                 var $curClick = null;

                 self.selected_data = $(this).attr('data');

                 dateObj.setDate(new Date(_date.getFullYear(), _date.getMonth(), 1));

                 if (!$(this).hasClass('item-curMonth')) {
                 self.showCalendar();
                 }

                 $curClick = self.$calendar_date.find('[data=' + _dateStr + ']');
                 if (!$curClick.hasClass('item-selected')) {
                 self.$calendarDate_item.removeClass('item-selected');
                 $curClick.addClass('item-selected');
                 }
                 //self.opts.opened();
                 if(self.opts.initEventList!=undefined)
                 self.opts.initEventList(self.opts.form,_date);*/

            });


            this.$calendarDate_item.click(function () {
                var _dateStr = $(this).attr('data');
                var _date = changingStr(addMark(_dateStr));
                var $curClick = null;

                self.selected_data = $(this).attr('data');

                dateObj.setDate(new Date(_date.getFullYear(), _date.getMonth(), 1));

                if (!$(this).hasClass('item-curMonth')) {
                    self.showCalendar();
                }

                $curClick = self.$calendar_date.find('[data=' + _dateStr + ']');
                if (!$curClick.hasClass('item-selected')) {
                    self.$calendarDate_item.removeClass('item-selected');
                    $curClick.addClass('item-selected');
                }
                
                var dateStr = $(this).attr("data");

                if ($(this).parents(".cal-row").hasClass("isopen")) {
                    var mm = $(this).parents(".cal-row").find(".cal-collapse").attr("id");

                    if (dateStr == mm.split("_")[1]) {

                        $("#collapse_" + dateStr).collapse('hide').remove();
                        $(this).parents(".cal-row").removeClass("isopen");
                    } else {
                        $(this).parents(".cal-row").find(".cal-collapse").attr("id", "collapse_" + dateStr);
                    }


                } else {
                    $(this).parents(".calendar-container").find(".cal-row").removeClass("isopen");
                    $(this).parents(".calendar-container").find(".cal-collapse").collapse('hide').remove();
                    // if($(this).hasClass("item-curMonth")){
                    $(this).parents(".cal-row").append('<div class="collapse cal-collapse calendar-event" id="collapse_' + dateStr + '">' +
                        '<div class="event-list">' +
                        '</div>' +
                        '<div class="event-footer">'+msg("there are no more events")+'</div>' +
                        '</div>');

                    $("#collapse_" + dateStr).collapse('show');
                    $(this).parents(".cal-row").addClass("isopen");
                }
                
                if (self.opts.initEventList != undefined)
                    self.opts.initEventList(self.opts.form, _date);

            });


        },
        constructor: Calendar
    };

    $.fn.workCalendar = function (options) {
        var calendar = new Calendar(this, options);
        return calendar.inital();
    };


    // ========== 使用到的方法 ==========

    var dateObj = (function () {
        var _date = new Date();

        return {
            getDate: function () {
                return _date;
            },

            setDate: function (date) {
                _date = date;
            }
        }
    })();

    function returnDateStr(date) { // 日期转字符串
        var year = date.getFullYear();
        var month = date.getMonth() + 1;
        var day = date.getDate();
        month = month <= 9 ? ('0' + month) : ('' + month);
        day = day <= 9 ? ('0' + day) : ('' + day);
        return year + month + day;
    };

    function changingStr(fDate) { // 字符串转日期
        var fullDate = fDate.split("-");
        return new Date(fullDate[0], fullDate[1] - 1, fullDate[2]);
    };

    function addMark(dateStr) { // 给传进来的日期字符串加-
        return dateStr.substr(0, 4) + '-' + dateStr.substr(4, 2) + '-' + dateStr.substring(6);
    };

    function isLeapYear(year) { // 判断闰年
        return (year % 4 == 0) && (year % 100 != 0 || year % 400 == 0);
    };

})(jQuery, window, document);