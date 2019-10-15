/**
 * User: dong.wenwu
 * Date: 4/28/15
 * Time: 13:38 PM
 */
registerInit('tree2', function (form) {
	var $form = form.get();
	
	initComponent(form);
	
	var formId=form.id;
	var inputData = {};
	var inputArray=new Array();
	$form.find("#"+formId+"_up").on("click", function () {
		var arrIndex=0;
		$.each(form.fieldIds, function (i, id) {
			var field = w(id);
			inputArray[arrIndex] = field.val();
			arrIndex++;
		});
		var item_no=inputArray[0];
		var process_no=inputArray[1];
		var lot_no=inputArray[2];
		var params={item_no:item_no,process_no:process_no,lot_no:lot_no, id: formId, conditions: ""};
		treeT("/tree2/searchUpTreeNode.do",formId,params);
	});
	$form.find("#"+formId+"_down").on("click", function () {
		var arrIndex=0;
		$.each(form.fieldIds, function (i, id) {
			var field = w(id);
			inputArray[arrIndex] = field.val();
			arrIndex++;
		});
		var item_no=inputArray[0];
		var process_no=inputArray[1];
		var lot_no=inputArray[2];
		var params={item_no:item_no,process_no:process_no,lot_no:lot_no, id: formId, conditions: ""};
		treeT("/tree2/searchDownTreeNode.do",formId,params);
	});
	
});
function treeT(url,formId,params){
	postJson(url, params,
			function (retData) {
				if (retData["TreeNode"].length > 0) {
					$('#' + formId + '_div').jstree({
						'core': {
							'data': retData["TreeNode"]
						}
					});
					/*.bind('click.jstree', function(event) {               
						var eventNodeName = event.target.nodeName;  
						alert(eventNodeName);
						//alert($(event.target).parent().attr('id'));
						alert(event.target.parentNode);
						/*if (eventNodeName == 'INS') {                   
							return;               
						} else if (eventNodeName == 'A') {                   
							var $subject = $(event.target).parent();                   
							if ($subject.find('ul').length > 0) {            
							} else { 
								//选择的id值
								alert($(event.target).parents('li').attr('id'));                   
							}               
						}           
					});*/
				}
				$('#' + formId + '_div').jstree(true).settings.core.data = retData["TreeNode"];
				$('#' + formId + '_div').jstree(true).refresh();
			});
}