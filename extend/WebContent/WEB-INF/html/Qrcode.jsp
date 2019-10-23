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
      <script type="text/javascript" charset="UTF-8" src="<%=basePath%>js/jquery-zclip-master/jquery.zclip.js"></script>
      <style>
      	.code-picture{
      		width:200px;
      		height:200px;
      		padding:8px;
      		border:1px solid #cccccc;     
      	}
      	.code-copy-btn{ 
      		line-height:28px;
      		padding:0 15px;
      		font-size:12px;
      		margin-top:10px;	
      		color:white;
      		border:none;
      		border-radius:3px;
      		background:#60B9E0;
      	}
      	.copy-wrap{
      		border:1px dashed #E6E6E6;
      		border-radius:3px;
      		background:#FAFAFA;
      		padding:5px 10px;
      		margin-top:10px;
      		text-align:left;
      		font-size:12px;
      	}
      	.copy-wrap a{
      		color: #333333;
      		word-break:break-all;
      	}
      </style>
</head>
<body>
<div style="text-align:center">
	<div>
		<img class="code-picture" src="<%=basePath%>img/${path}"/>
	</div>
	<div style="margin-top:10px">
		<span style="font-size: 14px;color: #333333">手机扫码</span>
		<p style="margin: 0;font-size: 14px;color: #808080">跳转到外部页面</p>
	</div>
	<div class="copy-wrap" >
		<label>链接:</label>
		<a id="url" href="${url}" target="_blank">${url}</a>
		<div  style="text-align:center">
			<input id="copy" class="code-copy-btn" type="button" value="复制链接"/>
		</div>
	</div>
</div>
</body>
<script>

	$("#copy").zclip({  
        path: makeUrl("/js/jquery-zclip-master/ZeroClipboard.swf"),  
        copy: function(){//复制内容  
            return url=$("#url").text();  
        },  
        afterCopy: function(){//复制成功  
            alert("复制成功") ; 
        }  
    });

</script>
</html>