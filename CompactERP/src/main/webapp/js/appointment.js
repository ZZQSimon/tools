/**
 * Created by Administrator on 2017/12/14.
 */
registerInit('appointment', function (form) {
    form.parentForm.get().find('.appointment-hide').show();
    function initDatetime(){
        form.get().find('.g1-datetime').datetimepicker({format: 'yyyy-mm-dd hh:ii:ss', autoclose: true});
    }
    form.getData = function getAppointmentData(){
        var appointment = {};
        appointment.id = form.appointment.id;
        appointment.user_id = form.appointment.user_id;
        appointment.table = form.appointment.table;
        appointment.data_id = form.appointment.data_id;
        appointment.memo = form.get().find('.appointment-memo').val();
        appointment.begin_time = form.get().find('.appointment-begin-time').data("datetimepicker").getDate();
        appointment.end_time = form.get().find('.appointment-end-time').data("datetimepicker").getDate();
        appointment.owner = undefined;
        appointment.status = undefined;
        if (isEmpty(form.param) || isEmpty(form.param.color))
            appointment.color = 'red';
        else
            appointment.color = form.appointmentData[form.param.color];
        appointment.upd_date = form.appointment.upd_date;
        appointment.type = form.action;
        return appointment;
    };
    form.parentForm.get().find('.appointment-hide').on('click', function () {
        form.get().remove();
        form.parentForm.get().find('.appointment-calendar').show();
        form.parentForm.get().find('.appointment-add').show();
        form.parentForm.get().find('.appointment-hide').hide();
    });
    //预约source名称列
    var table = getTableDesc(form.appointment.table);
    form.get().find('.appointment-source-name').val(getNameExpression(table, form.appointmentData));

    initDatetime();

    //编辑预约
    form.get().find('.appointment-edit').on('click', function () {
        form.get().find('.appointment-begin-time').removeAttr('readonly');
        form.get().find('.appointment-end-time').removeAttr('readonly');
        form.get().find('.appointment-memo').removeAttr('readonly');
        form.get().find('.appointment-edit').hide();
        form.get().find('.appointment-delete').hide();
        form.get().find('.save-appointment').show();
    });

    form.get().find('.save-appointment').on('click', function(){
        var data = form.getData();
        if (form.action == 'create'){
            postJson('/appointment/appointmentCreate.do', {appointment: data}, function (result) {
                dxToastAlert(msg(result));
                form.parentForm.get().find('.appointment-calendar').show();
                form.get().find('.appointment-detail-cu').empty();
                form.parentForm.get().find('.appointment-add').show();
                form.parentForm.reload();
            });
        }else{
            postJson('/appointment/appointmentEdit.do', {appointment: data}, function (result) {
                dxToastAlert(msg(result.msg));
                form.parentForm.get().find('.appointment-calendar').show();
                form.get().find('.appointment-detail-cu').empty();
                form.parentForm.get().find('.appointment-add').show();
                form.parentForm.reload();
            });
        }
    });
});
