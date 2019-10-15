/*function loadErrorMsg() {
    var errorMsg = $("#loadErrorMsgDialog").text();
    BootstrapDialog.show({
        title: "错误消息",
        message: errorMsg
    });
} */               

function getRootPath(){
	 return makeLoginUrl('');
}
var subjectID, backgroundID;
try{
 subjectID = localStorage.getItem('subject');
 backgroundID=localStorage.getItem('background');
}catch(e){

}
$(function(){
    $('.subjectID').val("blue");
    $('.backgroundID').val(backgroundID);
    $('.login1').submit();
});

function obtainID(id) {
	document.getElementById("subjectID").value=id;
}
/*$('.modal.select-title a').on('click', function(){
	var id = $(this).attr('id');
	alert(id);
})*/
