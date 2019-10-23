<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <title>UserList</title>
      <script type="text/javascript" charset="UTF-8" src="<%=basePath%>js/jquery-1.7.1.js"></script>
	  <script type="text/javascript" charset="UTF-8" src="<%=basePath%>js/user.js"></script>
</head>
<body>
 
<div>
	<table class="user-list">
		<tr>
			<th>姓名</th>
			<th>年龄</th>
			<th>操作</th>
		</tr>
	</table>
</div>
<div>
	<image src="<%=basePath%>utils/getQrcode/测试图片" style="width:300px;height:300px"/>
	<image src="D:/img.png" style="width:300px;height:300px"/>
</div>
 
</body>
</html>