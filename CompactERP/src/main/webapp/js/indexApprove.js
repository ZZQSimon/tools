/**
 * Created by Administrator on 2017/12/7.
 */
registerInit('indexApprove', function (form, ignore$li) {
    if (isEmpty(form))
        return;
    var $form = $('#' + form.id);
    //查看按钮事件
    $form.off('click');
    $form.find('.index_approve-view').on('click', function () {
        dx.processing.open();
        var isWaitMeApprove = $(this).hasClass('wait_me_approve');
        var tableId = $(this).parent().find('.approve-data').attr('table_id');
        var dataId = $(this).parent().find('.approve-data').attr('data_id');
        var table = getTableDesc(tableId);
        var param = {};
        param[table.id_column] = dataId;
        newTab('/detail/edit.view', {table: tableId, param: param, readonly: true, isIndexView: 1}, function (newForm) {
            var rowIndex;
            var rows;
            if (isWaitMeApprove){
                rows = form.waitMeApprove;
            }else {
                rows = form.myApprove;
            }
            for (var i=0; i<rows.length; i++){
                rows[i].rowid = rows[i].approve_id;
                var ref = {};
                var table = getTableDesc(rows[i].table_id);
                ref[table.id_column] = rows[i].data_id;
                rows[i].ref = ref;
                rows[i].indexGrid = true;
                rows[i].indexApprove = true;
                if (rows[i].table_id == tableId && rows[i].data_id == dataId){
                    rowIndex = rows[i].rowid;
                }
            }
            var beforeAfterRows = {};
            beforeAfterRows.rows = rows;
            beforeAfterRows.rowid = rowIndex;
            beforeAfterRows.indexGrid = true;
            beforeAfterRows.indexApprove = true;
            newForm.beforeAfterRows = beforeAfterRows;

            dx.processing.close();
        }, tableId);
    });
});
