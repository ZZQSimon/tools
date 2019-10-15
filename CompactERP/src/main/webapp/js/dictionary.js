registerInit('dictionary', function (form) {
    function getRootPath() {
        return makeLoginUrl('');
    }
    form.get().find(".treeScroll").niceScroll({cursorborder: "", cursorcolor: "#aeb2b7"});

    var dataGrid = form.get().find('#dictionTable');
    var DicContext=[];
    var addDicValue=[];
    var getInterRow={};
    initDic();
    retrieveDic();
    deleteDic();
    DicClick();
    dicListAdd();
    //表格文本编辑器
    $.extend($.fn.datagrid.defaults.editors, {    
    	international: {    
            init: function(container, options){    
                var input = $('<div class="interDic"></div>').appendTo(container);    
                return input;    
            },    
            getValue: function(target){    
            	getInternational=$(".interDic").internationalControl(true).getData();
            	var index=dataGrid.datagrid('getRowIndex',getInterRow);
            	var row=dataGrid.datagrid('getRows')[index];
        		if(getInternational.international_id != getInterRow.key_international){
        			row.key_international = getInternational.international_id;
        			if(isEmpty(getInternational.international_id)){
        				row[dx.user.language_id]=getInternational.interValue;
        			}else{
        				row[dx.user.language_id]=getInternational[dx.user.language_id];
        			}
            	}else{
            		
            		if(isEmpty(getInternational.international_id)){
            			row[dx.user.language_id]=getInternational.interValue;
        			}else{
        				row[dx.user.language_id]=getInternational[dx.user.language_id];
        			}
            	}
        		return row[dx.user.language_id];
            },    
            setValue: function(target, value){
            	getInterRow=dataGrid.datagrid('getSelected');
            	if(isEmpty(getInterRow.key_international)){
            		international(".interDic","",value,"isShow");
            	}else{
            		international(".interDic",getInterRow.key_international,value,"isShow");
            	}
            } 
        }    
    });  
    
  //初始化国际化控件
    function international(divClass,internationalId,dicName,isShow){
       $(divClass).internationalControl({
    	   internationalId : internationalId,
       	   menuName : dicName,
          onSubmit : function(data){
        	 
          },
          onSelect : function(data){
        	  
          }
       })
     }
    //初始化数据
    function initDic(){
		postJson('/dictionary/initDic.do', {}, function (data) {
			 var list = data.dictionaryList;
	         if (list) {
	             for (var i = 0; i < list.length; i++) {
	                 var dic_id = list[i].id_international.toLowerCase();
	                 var dictName =i18n(dx.i18n.message[dic_id]);
	                 form.get().find(".dicList .dicItemList").append("<div class='item'><span class='dictionContent' " +
	                 		"dicId='" + dic_id + "' dicInterId='"+list[i].dic_id+"'>" + dictName + "</span></div>");
	             }
	         }
	         var gridRows = data.dictionaryById;
	         if(gridRows){
	             var c_dictionary = dx.table.c_dictionary;
	             var c_international = dx.table.c_international;
	             var column = c_dictionary.columns;
	             var inter_column = c_international.columns;
	             var row;
	             var hiddenCol=[];
	             var gridCol = [{field: 'firstckbox',hidden: false, checkbox: 'true'}];
	             gridCol.push({field: 'dic_key',title: '字典Key', width: 80,hidden: false,editor: {type:'text'}});
	             gridCol.push({field: 'key_international',title: '国际化Id', width: 80,hidden: true,editor: {type:'text'}});
	             gridCol.push({field: dx.user.language_id,title: '字典名称', width: 80,hidden: false,editor: {type:'international'},
	            	 formatter: function(value,row,index){
	            		 if(!isEmpty(value)){
	            			 return row[dx.user.language_id];
	            		 }
	            	 }
	             });
	             gridCol.push({field: 'hidden',title: '隐藏',hidden: false, width: 80,
	            	 formatter: function(value,row,index){
	            		 var inputName=row.dic_id+"-"+index;
	            		 if(value=="1"){
	            			 return '<div class="input-bt-Switcher-wrap"><div class="input-bt-Switcher"><input type="checkbox" name="'
	            			 +index+'" value="'+value+'" checked/><label></label></div></div>';
	            		 }else{
	            			 return '<div class="input-bt-Switcher-wrap"><div class="input-bt-Switcher"><input type="checkbox" name="'
	            			 +index+'" value="'+value+'" /><label></label></div></div>';
	            		 }
	     			}
	             });
	             easydatagrid(gridCol);
	             dataGrid.datagrid('loadData', gridRows);
	         }
	         dataGrid.attr({"dicId":list[0].dic_id ,"dicName":i18n(dx.dictNameI8N[list[0].dic_id.toLowerCase()])});
	         form.get().find(".dicList .dictionContent[dicId="+list[0].dic_id.toLowerCase()+"]").click();
	      });
    }
    //隐藏按钮的点击事件
    $(".dicContent").on("click",".input-bt-Switcher",function () {
    	var index=$(this).find("input").attr("name");
    	var row=dataGrid.datagrid('getRows');
    	if(row[index].hidden=="1"){
    		var dic_Id=dataGrid.attr("dicId");
        	var dic_Name=dataGrid.attr("dicName");
        	var dictionaryAdd = {};
        	form.get().find(".dicItemList span[dicId="+dic_Id+"]").addClass("change");
            dictionaryAdd.dicId=dic_Id;
            dictionaryAdd.dicName=dic_Name;
            dictionaryAdd.dicList=dataGrid.datagrid('getRows');
            dictionaryAdd.dicList[index].hidden="0";
            if(!isEmpty(DicContext)){
            	for (var i = 0; i < DicContext.length; i++) {
                    if(DicContext[i].dicId==dic_Id){
                    	DicContext[i]=dictionaryAdd;
                    	break;
                    }else if(i==DicContext.length-1){
                    	 DicContext.push(dictionaryAdd);
                    }
                }
            }else{
            	 DicContext.push(dictionaryAdd);
            }
    	}else{
    		var dic_Id=dataGrid.attr("dicId");
        	var dic_Name=dataGrid.attr("dicName");
        	var dictionaryAdd = {};
        	form.get().find(".dicItemList span[dicId="+dic_Id+"]").addClass("change");
            dictionaryAdd.dicId=dic_Id;
            dictionaryAdd.dicName=dic_Name;
            dictionaryAdd.dicList=dataGrid.datagrid('getRows');
            dictionaryAdd.dicList[index].hidden="1";
            if(!isEmpty(DicContext)){
            	for (var i = 0; i < DicContext.length; i++) {
                    if(DicContext[i].dicId==dic_Id){
                    	DicContext[i]=dictionaryAdd;
                    	break;
                    }else if(i==DicContext.length-1){
                    	 DicContext.push(dictionaryAdd);
                    }
                }
            }else{
            	 DicContext.push(dictionaryAdd);
            }
    	}
    });
    //检索
    function retrieveDic(){
    	 var to;
    	 var leftSearch = form.get().find('.retrieve_input');
    	 leftSearch.keyup(function () {
    		 if (to) {
    			 clearTimeout(to);
    		 }
	        to = setTimeout(function () {
	    	 var retrieveValue = leftSearch.val();
	    	 postJson('/dictionary/retrieveDic.do', {retrieveValue: retrieveValue}, function (data) {
	    		 form.get().find(".dicList .item").remove();
	             var list = data;
	        	  for (var i = 0; i < list.length; i++) {
	                  var dic_id = list[i].id_international;
	                  var dictName = i18n(dx.i18n.message[dic_id]);
	                  form.get().find(".dicList .dicItemList").append("<div class='item'><span class='dictionContent' dicId='" 
	                		  + dic_id + "'  dicInterId='"+list[i].dic_id+"'>" + dictName + "</span></div>");
	              }
	             if(!isEmpty(addDicValue)){
	             	for (var i = 0; i < addDicValue.length; i++) {
	                     var dic_id = addDicValue[i].dic_id;
	                     var dictName = addDicValue[i].dictName;
	                     if(dictName.indexOf(retrieveValue)!=-1){
	                    	 form.get().find(".dicList .dicItemList").append("<div class='item'><span class='dictionContent' dicId='"
	                    			 + dic_id + "' dicInterId='"+dic_id+"'>" + dictName + "</span></div>");
	                     }else{
	                     	if(isEmpty(retrieveValue)){
	                     		form.get().find(".dicList .dicItemList").append("<div class='item'><span class='dictionContent' dicId='"
	                     				+ dic_id + "' dicInterId='"+dic_id+"'>" + dictName + "</span></div>");
	                         }
	                     }
	                 }
	             }
	          });
	        }, 250);
	    });
    }
	//删除字典
    function deleteDic(){
    	form.get().find(".deleteDic").on("click",function () {
	    	var DeList=[];
	    	var dicId= form.get().find(".dicItemList .active");
	    	for(var n=0;n<dicId.length;n++){
	    		var dictionaryDe={};
	    		dictionaryDe.dicId=dicId.eq(n).attr("dicId");
	    		DeList.push(dictionaryDe);
	    	}
	    	if(confirm("是否删除字典？")){
	    		postJson('/dictionary/deleteDic.do', {dictionaryAdd: DeList}, function (result) {
		          	// alert(result);
		          	dxToastAlert(result);
		          	form.get().find(".dicItemList .active").remove();
		         });
	    	}
	    });
    }
    //点击字典
    function DicClick(){
    	form.get().find(".dicList").on("click", ".dictionContent", function () {
    		if(endAllRowEdit()){
    			form.get().find(".dicList .dictionContent").removeClass("active");
	            $(this).addClass("active");
                form.get().find(".deploy-left-tree-delete").show();

		    	var ifChange=dataGrid.datagrid('getChanges');
		    	var dicId = $(this).attr("dicId");
		    	var dicInterId = $(this).attr("dicInterId");
		        var dicName = $(this).html();
		        if(ifChange.length>0){
		        	var dic_Id=dataGrid.attr("dicId");
		        	var dic_Name=dataGrid.attr("dicName");
		        	var dictionaryAdd = {};

		        	form.get().find(".dicItemList span[dicId="+dic_Id+"]").addClass("change");

		            dictionaryAdd.dicId=dic_Id;
		            dictionaryAdd.dicName=dic_Name;
		            dictionaryAdd.dicList=dataGrid.datagrid('getRows');
		            if(!isEmpty(DicContext)){
		            	for (var i = 0; i < DicContext.length; i++) {
		                    if(DicContext[i].dicId==dic_Id){
		                    	DicContext[i]=dictionaryAdd;
		                    	break;
		                    }else if(i==DicContext.length-1){
		                    	 DicContext.push(dictionaryAdd);
		                    }
		                }
		            }else{
		            	 DicContext.push(dictionaryAdd);
		            }
		    	}
		        var flag=false;
		        if(!isEmpty(DicContext)){
		        	for (var i = 0; i < DicContext.length; i++) {
		                if(DicContext[i].dicId==dicId){
		                	//显示修改数据
		                	dataGrid.attr({"dicId":DicContext[i].dicId,"dicName":DicContext[i].dicName});
		                    dataGrid.datagrid('loadData', DicContext[i].dicList);
		                    break;
		                }else if(i==DicContext.length-1){
		                	flag=true;
		                }
		            }
		        }else{
		        	 postJson('/dictionary/dictionaryById.do', {dicId:dicInterId}, function (data) {
		              	dataGrid.attr({"dicId":dicId,"dicName":dicName});
		                dataGrid.datagrid('loadData', data);
		        	 });
		        }
		        if(flag){
		        	 postJson('/dictionary/dictionaryById.do', {dicId:dicInterId}, function (data) {
		               	dataGrid.attr({"dicId":dicId,"dicName":dicName});
		                 dataGrid.datagrid('loadData', data);
		         	 });
		        }
    		}
    	});
    }
    
    //新增字典
    function dicListAdd(){
    	form.get().find(".dicListAdd").on("click", function () {
    		endAllRowEdit();
        	var dic_id=uuid(12,16);
        	var dictName=form.get().find("input[name=adddicValue]").val();
        	form.get().find(".dicList .dicItemList").prepend("<div class='item'><span class='dictionContent' dicId='" 
        			+ dic_id + "' dicInterId='"+dic_id+"'>" + dictName + "</span></div>");
        	addDicValue.push({dic_id : dic_id, dictName : dictName});
        	dataGrid.attr({"dicId":dic_id,"dicName":dictName});
        	var data=[];
            dataGrid.datagrid('loadData', data);
            form.get().find(".dicList .dictionContent[dicId="+dic_id+"]").click();
            //$(".easyui-linkbutton.append").click();
        });
    }
    //初始化表格
    function easydatagrid(columns) {
        dataGrid.datagrid({
            height: "100%",
            singleSelect: false,
            rownumbers: true,
            // toolbar: '#dic_tb_btn',
            striped: true,
            fitColumns: true,
            onClickRow : function (rowIndex, rowData) {
                $(this).datagrid('unselectAll');
                $(this).datagrid('selectRow', rowIndex);
            },
            onLoadSuccess:function(){
                //dataGrid.datagrid('enableDnd');
            },
            //可选-绑定dnd的触发事件
            onDrop:function(targetRow, sourceRow, point) {
                //拖拽某行到指定位置后触发
                var rows = dataGrid.datagrid('getRows');
                var dic_Id=dataGrid.attr("dicId");
                var dic_Name=dataGrid.attr("dicName");
                var dictionaryAdd = {};

                form.get().find(".dicItemList span[dicId="+dic_Id+"]").addClass("change");

                dictionaryAdd.dicId=dic_Id;
                dictionaryAdd.dicName=dic_Name;
                dictionaryAdd.dicList=rows;
                if(!isEmpty(DicContext)){
                    for (var i = 0; i < DicContext.length; i++) {
                        if(DicContext[i].dicId==dic_Id){
                            DicContext[i]=dictionaryAdd;
                            break;
                        }else if(i==DicContext.length-1){
                            DicContext.push(dictionaryAdd);
                        }
                    }
                }else{
                    DicContext.push(dictionaryAdd);
                }
            },
            onClickCell : function(rowIndex, field, value){
            	var ed = dataGrid.datagrid('getEditor', {index: rowIndex, field: field});
                if (ed) {
                    ($(ed.target).data('checkbox') ? $(ed.target).textbox('checkbox') : $(ed.target)).focus();
                }
            },
           /* onClickRow : function(rowIndex, field, value){
            	endAllRowEdit();
            },*/
            onDblClickRow: onClickCells,
            // onEndEdit: onEndEdit,
            // selectOnCheck: true,
            columns: [columns],
            onAfterEdit : function(rowIndex, rowData, changes){
            	var rows = dataGrid.datagrid('getRows');
            	var ifChange=dataGrid.datagrid('getChanges');
		        if(ifChange.length>0){
		        	var dic_Id=dataGrid.attr("dicId");
		        	var dic_Name=dataGrid.attr("dicName");
		        	var dictionaryAdd = {};

		        	form.get().find(".dicItemList span[dicId="+dic_Id+"]").addClass("change");

		            dictionaryAdd.dicId=dic_Id;
		            dictionaryAdd.dicName=dic_Name;
		            dictionaryAdd.dicList=rows;
		            if(!isEmpty(DicContext)){
		            	for (var i = 0; i < DicContext.length; i++) {
		                    if(DicContext[i].dicId==dic_Id){
		                    	DicContext[i]=dictionaryAdd;
		                    	break;
		                    }else if(i==DicContext.length-1){
		                    	 DicContext.push(dictionaryAdd);
		                    }
		                }
		            }else{
		            	 DicContext.push(dictionaryAdd);
		            }
		    	}
            	dataGrid.datagrid('loadData', rows);
            }
        });
        
    }

    form.get().find(".datagrid-foot .append").on("click", function () {
        append();
    });
    form.get().find(".datagrid-foot .removeit").on("click", function () {
        removeit();
    });
    form.get().find(".btn-toolbar .accept").on("click", function () {
        accept();
    });



    var editIndex = undefined;
    //保存
    function accept() {
    	if(endAllRowEdit()){
    		var ifChange=dataGrid.datagrid('getChanges');
        	var dicId=dataGrid.attr("dicId");
        	var dicName=dataGrid.attr("dicName");
        	if(ifChange.length>0){
            	var dic_Id=dataGrid.attr("dicId");
            	var dic_Name=dataGrid.attr("dicName");
            	var dictionaryAdd = {};
                dictionaryAdd.dicId=dic_Id;
                dictionaryAdd.dicName=dic_Name;
                dictionaryAdd.dicList=dataGrid.datagrid('getRows');
                if(!isEmpty(DicContext)){
                	for (var i = 0; i < DicContext.length; i++) {
                        if(DicContext[i].dicId==dicId){
                        	DicContext[i]=dictionaryAdd;
                        	break;
                        }else if(i==DicContext.length-1){
                        	 DicContext.push(dictionaryAdd);
                        }
                    }
                }else{
                	 DicContext.push(dictionaryAdd);
                }
        	}
        	dx.processing.open();
            postJson('/dictionary/saveDictionary.do', {dictionaryAdd: DicContext}, function (result) {
            	$(".dictionContent").removeClass("change");
            	DicContext=[];
            	postJson('/data/cache/reload.do', function(){
            		dxReload(function(){
                		dxToastAlert(msg('Saved successfully!'));
                		dx.processing.close();
                	});
                })
            });
    	}
    }
  //结束所有行编辑
    function endAllRowEdit(){
        var rows = dataGrid.datagrid('getRows');
        if (isEmpty(rows))
            return true;
        for (var i=0; i<rows.length; i++){
        	dataGrid.datagrid('endEdit', $(dataGrid).datagrid('getRowIndex', rows[i]));
        }
        //判断key是否为空
        for(var i = 0; i < rows.length; i++){
        	if(isEmpty(rows[i].dic_key)){
        		alert("字典Key不能为空！");
        		return false;
        	}
        	for(var j =i+1; j < rows.length; j++){
        		if(rows[i].dic_key==rows[j].dic_key){
        			alert("字典Key不能重复！");
            		return false;
        		}
        	}
        }
    	return true;
    }
    function endEditing() {
        if (editIndex == undefined) {
            return true
        }
        if (dataGrid.datagrid('validateRow', editIndex)) {
            dataGrid.datagrid('endEdit', editIndex);
            editIndex = undefined;
            return true;
        } else {
            return false;
        }
    }

    function onClickCells(index, field) {
//    	$(this).datagrid('beginEdit', index);
//		var ed = $(this).datagrid('getEditor', {index:index,field:field});
//		$(ed.target).focus();

        if (editIndex != index) {
            if (endEditing()) {
                dataGrid.datagrid('selectRow', index)
                    .datagrid('beginEdit', index);
                var ed = dataGrid.datagrid('getEditor', {index: index, field: field});
                if (ed) {
                    ($(ed.target).data('checkbox') ? $(ed.target).textbox('checkbox') : $(ed.target)).focus();
                }
                editIndex = index;
            } else {
                setTimeout(function () {
                    dataGrid.datagrid('selectRow', editIndex);
                }, 0);
            }
        }
    }
    

    $(".datagrid .datagrid-editable").on("click",".datagrid-editable-input",function(){
    	$(this).parents(".datagrid-editable").parent("td").click()
    })

    function onEndEdit(index, row) {
        var ed = $(this).datagrid('getEditor', {
            index: index,
            field: 'module'
        });
        row.module = $(ed.target).combobox('getText');
    }

    function append() {
        if (endEditing()) {
        	dataGrid.datagrid('clearChecked');
        	var dic_id=dataGrid.attr("dicId");
        	var dicName=dataGrid.attr("dicName");
            dataGrid.datagrid('appendRow', {"dic_id": dic_id});
            editIndex = dataGrid.datagrid('getRows').length - 1;
            dataGrid.datagrid('selectRow', editIndex)
                .datagrid('beginEdit', editIndex);
        }
    }

    function removeit() {
        console.log(editIndex);
//        if (editIndex == undefined){return}
        var ss = [];
        var rows = dataGrid.datagrid('getSelections');
        console.log(rows);
        for (var i = 0; i < rows.length; i++) {
            var row = rows[i];
            var rowIndex = dataGrid.datagrid('getRowIndex', row);
            dataGrid.datagrid('deleteRow', rowIndex);
//				ss.push('<span>'+row.itemid+":"+row.productid+":"+row.attr1+'</span>');
        }
        var re = {
            data: rows,
            leng: rows.length
        };
        return re;
//        editIndex = undefined;
    }

    function reject() {
        dataGrid.datagrid('rejectChanges');
        editIndex = undefined;
    }

    function getChanges() {
        var rows = dataGrid.datagrid('getChanges');
        var delRows = dataGrid.datagrid('getChanges', 'deleted');
        var insRows = dataGrid.datagrid('getChanges', 'inserted');
        var updateRows = dataGrid.datagrid('getChanges', 'updated');

        var totleRows = {
            insert: insRows,
            deleted: delRows,
            updated: updateRows
        };
        return totleRows;
    }
    
    function uuid(len, radix) {
        var chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'
            .split('');
        var uuid = [], i;
        radix = radix || chars.length;

        if (len) {
            for (i = 0; i < len; i++)
                uuid[i] = chars[0 | Math.random() * radix];
        } else {
            var r;
            uuid[8] = uuid[13] = uuid[18] = uuid[23] = '-';
            uuid[14] = '4';
            for (i = 0; i < 36; i++) {
                if (!uuid[i]) {
                    r = 0 | Math.random() * 16;
                    uuid[i] = chars[(i == 19) ? (r & 0x3) | 0x8 : r];
                }
            }
        }
        return uuid.join('');
    }
  //表格向上移动按钮
    form.get().find('.datagrid_row_up_dictionary').on("click",function(){
    	var row = dataGrid.datagrid('getChecked');
    	dataGridMove(dataGrid,row,"up");
    	dataGridMoveChange();
    })
    //表格向下移动按钮
    form.get().find('.datagrid_row_down_dictionary').on("click",function(){
    	var row = dataGrid.datagrid('getChecked');
    	dataGridMove(dataGrid,row,"down");
    	dataGridMoveChange();
    })
    function dataGridMoveChange(){
    	var rows = dataGrid.datagrid('getRows');
        var dic_Id=dataGrid.attr("dicId");
        var dic_Name=dataGrid.attr("dicName");
        var dictionaryAdd = {};

        form.get().find(".dicItemList span[dicId="+dic_Id+"]").addClass("change");

        dictionaryAdd.dicId=dic_Id;
        dictionaryAdd.dicName=dic_Name;
        dictionaryAdd.dicList=rows;
        if(!isEmpty(DicContext)){
            for (var i = 0; i < DicContext.length; i++) {
                if(DicContext[i].dicId==dic_Id){
                    DicContext[i]=dictionaryAdd;
                    break;
                }else if(i==DicContext.length-1){
                    DicContext.push(dictionaryAdd);
                }
            }
        }else{
            DicContext.push(dictionaryAdd);
        }
    }
});