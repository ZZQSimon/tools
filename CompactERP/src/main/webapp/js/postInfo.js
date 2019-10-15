registerInit('postInfo', function (form) {

	saveViewReport();
	function saveViewReport(){
		 postJson('/postInfo/selectPostInfo.do', {}, function (data) {
			 var list=data;
				for(var i=0;i<list.length;i++){
					var name=list[i].name;
					var postId=list[i].id;
					$(".jobSelectList").append("<span class='item newItem' postId='"+postId+"'>"+name+"<span class='jobclose'>×</span></span>");

					for(var j=0;j<$(".jobList input").length;j++){
						if($(".jobList input").eq(j).attr("postId")==postId){
							$(".jobList input").eq(j).iCheck("check");
	                        $(".jobSelectList .item[postId="+postId+"]").removeClass("newItem");
						}
					};
				}
          });
	}
    $(".jobSelectList").on("click",".jobclose",function () {
    	var closeId=$(this).parent().attr("postId");
		if($(this).parent().hasClass("newItem")){
			postJson('/postInfo/deletePost.do', {postId:closeId}, function (data) {
				$(".jobSelectList .item[postId="+closeId+"]").remove();
			 });
		}else{
            for(var n=0;n<$(".jobList input").length;n++){
                if($(".jobList input").eq(n).attr("postId") === closeId){
                    $(".jobList input").eq(n).iCheck("uncheck");
                    $(".jobSelectList .item[postId="+closeId+"]").remove();
                }
            }
		}

    });
	$(".tabConfigNew").on("click",function () {
		var postName=$(".postName").val();
		postJson('/postInfo/addPostInfo.do', {postName:postName}, function (data) {
			if(!isEmpty(data)){
				var postId=data;
                $(".jobSelectList").append("<span class='item newItem' postId='"+postId+"'>"+postName+"<span class='jobclose'>×</span></span>");
            }
		});
	});
	$('input[type=checkbox]').iCheck({
	    checkboxClass: 'icheckbox_flat-blue',
	    radioClass: 'iradio_flat-blue'
	});
	$('.corpJobBd .jobList input[type=checkbox]').on('ifChecked',function(){
		   //选中触发
			var postName=$(this).attr("name");
			var postId=$(this).attr("postId");
			postJson('/postInfo/addPostInfo.do', {postId:postId,postName:postName}, function (data) {
				if(!isEmpty(data)){
					$(".jobSelectList").append("<span class='item' postId='"+postId+"'>"+postName+"<span class='jobclose'>×</span></span>");
				}
			 });
		}).on('ifUnchecked',function(){
		  //不选中触发
			var postId=$(this).attr("postId");
			postJson('/postInfo/deletePost.do',{postId:postId}, function (data) {
				$(".jobSelectList .item[postId="+postId+"]").remove();
			});
	});
	$(".jobFooter button[name=corpJobPosi],.jobFooter button[name=corpJobFun]").on("click",function () {
		var toTag=$(this).attr("name");
		$("."+toTag+" .jobList input").iCheck("check");
    })

})