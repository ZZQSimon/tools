function getRootPath() {
    return makeLoginUrl('');
}

$('.btn-OK.login-btn').on('click', function () {
	alert("11");
    var username = $('#susername').val();
    var password = $('#password').val();
    var verifyCode = $('#loginvail').val();
    
    $('#username').val($('#susername').val() + '@' + data.data);
    $('.form-horizontal').submit();
   
});
