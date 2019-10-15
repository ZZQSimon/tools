/**
 * Created by Administrator on 2017/12/14.
 */
registerInit('eventDetail', function (form) {
    form.get().find('.all-day-event[type="checkbox"]').iCheck({
        checkboxClass: 'icheckbox_flat-blue',
        radioClass: 'iradio_flat-blue'
    });
    form.get().find('.event-detail').on('click', function () {
        var item = {};
        item.table = form.calendarEvent.ref_table;
        item.readonly = true;
        item.param = form.calendarEvent.ref_table_data_map;
        showDialogForm({
            url: '/detail/edit.view',
            title: form.calendarEvent.event_name,
            data: item,
            class: 'event-dialog',
            shown: function (newForm, dialog) {
                newForm.get().find('.btn-toolbar').hide();
                newForm.submit = function () {
                    dialog.close();
                };
            },
            hidden: function (form) {

            }
        });
    });
    form.get().find('.event-edit').on('click', function () {
        evalReadonly()
    });
    form.get().find('.event-back').on('click', function () {
        if (form.action == 'edit' && !form.isSave)
            if (!confirm(msg('data not save')))
                return;
        postPage('/calendar/calendar.view', {type: 2}, function (result) {
            closeForm(form.id, function () {
                var $form = $('#' + form.id);
                var $div = $form.parent();
                $form.remove();
                $div.append(result);

                // init new form
                var newForm = getFormModel($div.find('.dx-form').attr('id'));
                buildFormCache(newForm, newForm.widgets);
                dx.init.pc_calendar(newForm);
            });
        });
    });
    form.get().find('.add-event-user').on('click', function () {
        showDialogForm({
            url: '/calendar/eventUser.view',
            title: msg('add event user'),
            data: {},
            class: 'event-user-dialog sm-modal',
            shown: function (newForm, dialog) {
                if (!isEmpty(form.selectUsers))
                    newForm.selectUsers = form.selectUsers;
                else {
                    newForm.selectUsers = [];
                    if (!isEmpty(form.calendarEvent) && !isEmpty(form.calendarEvent.calendarEventShare)) {
                        for (var key in form.calendarEvent.calendarEventShare) {
                            newForm.selectUsers.push(key);
                        }
                    }
                    form.selectUsers = newForm.selectUsers;
                }
                newForm.submit = function (users) {
                    dialog.close();
                    buildEventUser(form, users);
                    if (isEmpty(users)) {
                        form.selectUsers = null;
                    } else {
                        form.selectUsers = [];
                        for (var key in users) {
                            form.selectUsers.push(users[key].id);
                        }
                    }
                };
            },
            hidden: function (form) {

            }
        });
    });
    form.get().on('click', '.delete-user', function () {
        var user_id = $(this).parent().attr('user_id');
        deleteEventUser({user_id: user_id});
    });
    form.get().find('.event-color').on('click', function () {
        form.get().find('.event-color').removeClass("selected");
        $(this).addClass("selected");

        if (form.action == 'view') {
            return;
        }
        var eventBackgroundColor = $(this).attr('event_backgroud_color');
        form.eventBackgroudColor = eventBackgroundColor;
    });
    form.get().find('.event-file-download-icon button').on('click', function () {
        var filePath = $(this).attr('name');
        postJsonRaw("/storage/eventDownload.do", {data: {value: filePath}}, function (data) {
            $("body").append("<iframe src='" + makeUrl(data.data) + "' style='display: none;' ></iframe>");
        });
    });
    form.get().find('.all-day-event').on('ifChecked', function (event) {
        initDatetime(form, true);
        form.get().find('.end-date-div').hide();
    }).on('ifUnchecked', function (event) {
        initDatetime(form);
        form.get().find('.end-date-div').show();
    });
    function initDatetime(form, type) {
        form.get().find('input.datetime').datetimepicker('remove');
        if (type) {
            form.get().find('input.datetime').datetimepicker({
                format: 'yyyy-mm-dd',
                todayHighlight: true,
                autoclose: true,
                minView: 2
            });
        } else {
            form.get().find('input.datetime').datetimepicker({
                format: 'yyyy-mm-dd hh:ii:ss',
                todayHighlight: true,
                autoclose: true
            });
        }
    }

    function initField(form) {
        initDatetime(form);
        form.get().find('input.file-input').fileupload({
            dataType: 'json',
            replaceFileInput: false,
            formData: function () {
                return [{name: 'id', value: this.fileInput[0].id}]
            },
            add: function (e, data) {
                data.url = makeUrl('/storage/upload.do');
                progressBox('uploading', '<div>' + data.files[0].name + '</div><div class="event-progress progress"><div class="event-progress-bar progress-bar" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%;">0% </div></div>');
                data.submit();
            },
            done: function (e, data) {
                form.uploadFile = data.result ? data.result.data : null;
                hidePorgressBox();
                // form.get().find('.event-file-download').text(data.files[0].name);
                var file = data.files[0].name.split('.');
                var num = file[file.length - 1];
                if ((num == "jpg" ) || (num == "png") || (num == "jpg")) {
                    console.log("jpg");


                } else if ((num == "rar" )) {

                    console.log("rar")
                }
                form.get().find('.event-file-download').html("<span ></span>" + data.files[0].name);
            },
            progressall: function (e, data) {
                var progress = parseInt(data.loaded / data.total * 100, 10);
                $('.event-progress .event-progress-bar').attr({'aria-valuenow': progress}).html(progress + "%").css("width", progress + "%");
            }
        });
    }

    function checkField(calendarEvent) {
        var vali = [true, true, true, true];
        if (isEmpty(calendarEvent)) {
            vali[0] = false;
            // form.get().find(".event-title").addClass("has-error");
            // return false;
        }
        if (isEmpty(calendarEvent.event_name)) {
            vali[1] = false;
            form.get().find(".event-title").addClass("has-error");
            // return false;
        } else {
            form.get().find(".event-title").removeClass("has-error");
        }
        if (isEmpty(form.get().find(".begin-date").val())) {
            vali[2] = false;
            form.get().find(".begin-date").addClass("has-error");
            // return false;
        } else {
            form.get().find(".begin-date").removeClass("has-error");
        }

        var allDayEvent = form.get().find('.all-day-event').is(':checked');
        if (!allDayEvent && isEmpty(form.get().find(".end-date").val())) {
            vali[3] = false;
            form.get().find(".end-date").addClass("has-error");
        } else {
            form.get().find(".end-date").removeClass("has-error");
        }

        if (vali[0] && vali[1] && vali[2] && vali[3]) {
            return true;
        } else {
            return false;
        }
    }

    form.get().find('.event-save').on('click', function () {
        var calendarEvent = getEventData(form);
        if (!checkField(calendarEvent)) {
            alert(msg('vaildate failed'));
            return;
        }
        if (isEmpty(calendarEvent.color)) {
            alert(msg('color has no choose'));
            return;
        }
        if (form.type != 'create')
            postJson('/calendar/saveCalendarEvent.do', {calendarEvent: calendarEvent}, function (result) {
                dxToastAlert(msg("save success"));
                form.isSave = true;
                form.get().find('.event-back').click();
            });
        else
            postJson('/calendar/createCalendarEvent.do', {calendarEvent: calendarEvent}, function (result) {
                dxToastAlert(msg("save success"));
                form.isSave = true;
                form.get().find('.event-back').click();
            });
    });
    function getEventData(form) {
        var allDayEvent = form.get().find('.all-day-event').is(':checked');
        var eventData = {};
        if (!isEmpty(form.calendarEvent)) {
            eventData.calendar_id = form.calendarEvent.calendar_id;
            eventData.p_calendar_event_id = form.calendarEvent.p_calendar_event_id;
            eventData.file = form.calendarEvent.file;
        }
        eventData.uploadFile = form.uploadFile;
        eventData.event_name = form.get().find('.event-title').val();
        eventData.begin_date = form.get().find('.begin-date').data("datetimepicker").getDate();
        if (allDayEvent) {
            eventData.end_date = eventData.begin_date;
        } else {
            eventData.end_date = form.get().find('.end-date').data("datetimepicker").getDate();
        }
        eventData.color = form.eventBackgroudColor;
        eventData.content = form.get().find('.content').val();
        eventData.memo = form.get().find('.memo').val();
        eventData.users = {};
        eventData.users.deleteUsers = form.deleteEventUser;
        eventData.users.addUsers = form.addEventUser;
        return eventData;
    }

    function deleteEventUser(user) {
        if (isEmpty(user))
            return;
        form.get().find('.event-user-div').find('span[user_id="' + user.user_id + '"]').remove();
        if (isEmpty(form.deleteEventUser)) {
            form.deleteEventUser = {};
        }
        form.deleteEventUser[user.user_id] = true;
        if (!isEmpty(form.addEventUser) && !isEmpty(form.addEventUser[user.user_id])) {
            delete form.addEventUser[user.user_id];
        }
        if (!isEmpty(form.selectUsers)) {
            for (var i = 0; i < form.selectUsers.length; i++) {
                if (form.selectUsers[i] == user.user_id) {
                    form.selectUsers.splice(i, 1);
                }
            }
        }
    }

    function buildEventUser(form, users) {
        function addEventUser(user) {
            if (isEmpty(user))
                return;
            form.get().find('.add-event-user-div').before('<span class="add-inviter event-user-span" user_id="' + user.id + '">' +
                user.name + '<span class="delete-user">×</span> </span>');
            if (isEmpty(form.addEventUser)) {
                form.addEventUser = {};
            }
            form.addEventUser[user.id] = true;
        }

        if (isEmpty(users)) {
            if (!isEmpty(form.calendarEvent) && !isEmpty(form.calendarEvent.calendarEventShare)) {
                for (var key in form.calendarEvent.calendarEventShare) {
                    deleteEventUser(form.calendarEvent.calendarEventShare[key]);
                }
            }
            return;
        }
        var selectUsers = form.selectUsers;
        if (isEmpty(selectUsers)) {
            for (var key in users)
                addEventUser(users[key]);
        } else {
            for (var i = 0; i < selectUsers.length; i++) {
                if (isEmpty(users[selectUsers[i]])) {
                    deleteEventUser({user_id: selectUsers[i]});
                }
            }
            for (var key in users) {
                for (var i = 0; i < selectUsers.length; i++) {
                    if (selectUsers[i] == key)
                        break;
                    else if (i == selectUsers.length - 1 && selectUsers[i] != key)
                        addEventUser(users[key]);
                }
            }
        }
    }

    function evalReadonly() {
        initField(form);
        form.action = 'edit';
        form.get().find('.event-title').removeAttr('readonly');
        form.get().find('.all-day-event').iCheck('enable');
        form.get().find('.begin-date').removeAttr('readonly');
        form.get().find('.end-date').removeAttr('readonly');
        form.get().find('.content').removeAttr('readonly');
        form.get().find('.event-memo').removeAttr('readonly');
        form.get().find('.file-input-div').show();
        form.get().find('.add-event-user-div').show();
        form.get().find('.event-save').show();
        form.get().find('.event-edit').hide();
        form.get().find('.no-event-user-span').hide();
        form.get().find('.dx-upload-button').show();
        form.get().find('.dx-upload-div').removeClass("disable");
        form.get().find('.event-user-div').removeClass("disable");
        form.get().find('.event-color-wrap').removeClass("disable");
    }

    if (form.type == 'create') {
        evalReadonly()
    }
    if (!isEmpty(form.calendarEvent) && !isEmpty(form.calendarEvent.color)) {
        form.eventBackgroudColor = form.calendarEvent.color;
    }
});
