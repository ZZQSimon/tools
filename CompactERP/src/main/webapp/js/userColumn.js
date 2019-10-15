registerInit('userColumn', function (form) {
    function getUserColumn(){
        var dataArray = new Array();
        var $userColumns = form.get().find('.dx-user-column-down ul li');
        for(var i=0; i<$userColumns.length; i++){
            if (!$($userColumns[i]).is(":hidden")){
                var data = {};
                data.table_name = form.table;
                data.column_name = $($userColumns[i]).attr('name');
                data.seq = i;
                dataArray.push(data)
            }
        }
        return dataArray;
    }
    form.get().find('.dx-is-user-column').sortable().bind('sortupdate', function() {

    });
    form.get().find('.dx-user-column-top ul li').on('click', function(){
    	var $li=form.get().find('.dx-user-column-top ul li');
        var columnName = $(this).attr('name');
        $(this).hide();
        var $userColumn = form.get().find('.dx-user-column-down ul li');
        $.each($userColumn, function(){
            if (columnName == $(this).attr('name')){
                $(this).show();
            }
        });
        var flag=true;
        for(var i=0;i<$li.length;i++){
            if(!$li.eq(i).is(":hidden")){
                flag=false;
                break;
            }
        }
        if(flag){
            $(this).parents(".dx-form").find(" .dx-user-column-empty").show();
        }
    });
    form.get().find('.dx-user-column-down ul li span').on('click', function(){
        if(!$(this).parents(".dx-form").find(" .dx-user-column-empty").is(":hidden")){
            $(this).parents(".dx-form").find(" .dx-user-column-empty").hide();
        }

        var columnName = $(this).parent().attr('name');
        $(this).parent().hide();
        var $userColumn = form.get().find('.dx-user-column-top ul li');
        $.each($userColumn, function(){
            if (columnName == $(this).attr('name')){
                $(this).show();
            }
        });
    });
    form.get().find('.dx-user-column-submit a').on('click', function(){
        var userColumns = getUserColumn();
        postJson('/widget/grid/saveUserColumn.do',
            {userColumns : userColumns, tableName : form.table}, function (data) {
            if (data == 'success'){
                form.close();
            }
        });
    });
});