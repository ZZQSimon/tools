registerInit('mobileList', function (form) { 
	var $form = form.get();
	var $list=$form.find(".list");
	var tableName=form.table;
	var columns=dx.table[form.table].columns;
	var grid=form.grid;
	
	var recordLength=10;
	var groupIds={};
	var paging={start: 0, length: recordLength};
	var filter={tableName: tableName, filters: {}, action: "exec"};
	var postData = {id: form.grid.id, filter: filter, paging: paging,reorder:{cre_date:0}};
	
	if(form.groups.length==0){
		$list.append('<div class="list-month g-cell-wrap default-empty"><div class="list-wrap-title"  style="display:none"></div></div>');
	}else{
		for(var key in form.groups){
			for(var col in form.groups[key]){
				var uid=uuid(10,16);
				groupIds[uid]=form.groups[key][col];
				$list.append('<div class="list-month g-cell-wrap '+uid+'" style="display:none"><div class="list-wrap-title">'+form.groups[key][col]+'</div></div>');
			} 
		}
	}

	var template=buildTemplate();
	initList(postData);
	grid.buildRecordData = function (recordId) {
        var record;
        for (var i=0; i<grid.records.length; i++){
            if (grid.records[i].id == recordId){
                record = grid.records[i];
                break;
            }
        }
        var data = {};
        $.each(record.columns, function (key, field) {
            data[key] = field.value;
        });
        return {parent: record.id, param: data, table: grid.table};
    };
  
    
    var loading = false; 
    var noMore=false;
    var cur_Count=0;
    $(document.body).infinite(0).on("infinite", function() {
    	if(loading) return;
    	loading = true;
    	if($(".list-footer").length==1){
    		recordLength+=10;
    		postData.paging={start: 0, length: recordLength};
    		if(!noMore){
    			initList(postData);
    		}
    		loading = false;
    	}
    }); 
	
	function buildTemplate(){
		var template="";
		var status=1;
		var leftMould='<div class="record-list-left">';
		var rightMould='<div class="record-list-right"><span class="record-create-time">${cre_date}</span>';
		for(var key in columns){
			if(columns[key].mobile_column=="1"){
				if(columns[key].column_name!="cre_date"){
					if(columns[key].column_name=="approve_status") rightMould+='<p class="record-state ${state_class}">${approve_status}</p>';
					else leftMould+='<p>'+columns[key].i18n[dx.user.language_id] +':${'+columns[key].column_name+'}</p>'
				}
			}
		}
		return template=leftMould+'</div>'+rightMould+'</div>';
	}
	
	function initList(postData){
		postJson('/widget/grid/page.do', postData, function (ret) {
			if(ret.records.length==cur_Count){
				noMore=true;
				return ;
			};
			cur_Count=ret.records.length;
			$list.find(".record").remove();
			var list=buildGridData(grid, ret.records, form);
	        for(var key in list){
	        	var record='<div class="record  list-day g-cell-item" id="'+list[key].DT_RowId+'">'+template+'</div>';
	        	
	        	fields=ret.records[key].fields;
	        	for(var column in list[key]){
	        		for(var i in columns){
	        			// if(columns[i].mobile_column==1 && columns[i].column_name==column) {
	        				record=record.replace("${"+column+"}",list[key][column]==null?"":list[key][column]);
	        				if(column=="approve_status"){
	        					var state_class=list[key].rawData.columns.approve_status.value;
	        					switch(state_class)
	        					{
		        				    case "1":
		        				    	state_class="record-state-unsubmit";
		        				        break;
		        				    case "2":
		        				    	state_class="record-state-wait";
		        				        break;
		        				    case "3":
		        				    	state_class="record-state-finish";
		        				        break;
		        				    case "4":
		        				    	state_class="record-state-reject";
		        				        break;
		        				    default:
		        				    	state_class="";
	        					}
	        					record=record.replace("${state_class}",state_class);
	        				}
	        			// }
	        		}
	        	}
	        	if(form.group_column=="cre_date"){
	        		var value=list[key]["cre_date"].substring(0,7).replace("-","年")+"月";
	        		for(var k in groupIds){
	        			if(groupIds[k]==value) {
	        				value=k;
	        				break;
	        			}
	        		}
	        		$list.find("."+value).css("display","block");
	        		$list.find("."+value).append(record);
	        	}else{
	        		$list.find(".default-empty").append(record);
	        	}
	        }
	        $list.find(".record").click(function(){
	        	var item = grid.buildRecordData(this.id);
	        	item.readonly = true;
                item.hasNext = false;
	            newMobilePage("/detail/edit.view", item);
	        });
	    });
	}
	

});
