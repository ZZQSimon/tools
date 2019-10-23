<%@ page language="java" 
    pageEncoding="UTF-8"%>
    <!--contentType="text/html; charset=UTF-8"  -->
<!--<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">-->
<!DOCTYPE html>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<html lang="en"xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<base href="<%=basePath%>">
    <meta charset="UTF-8">
    <title>adidas</title>
    <style>
        body{
            margin: 0;
            background: #f9f9f9;
        }
        h1{
            font-size: 30px;
            color: #909090;
            margin: 0 0 50px;
            border-bottom: 1px solid grey;
        }
        input{
            outline: none;
            border: none;
            border-bottom: 1px solid black;
        }
        p{
            font-size: 14px;
            line-height: 36px;
        }
        table{
            font-size: 14px;
            border-collapse: collapse;
        }
        th{
            color: white;
            background: black;
        }
        td,th{
            padding: 5px 10px;
        }
        label{
            font-size: 14px;
        }

        .wrap{
            width: 100%;
            padding: 50px;
            box-sizing: border-box;
            background: white;
            /*border:1px solid #efefef;*/
        }
        .margin-bottom{
            margin-bottom: 30px;
        }
        .text-right{
            font-size: 16px;
            text-align: right;
        }
        .setvalue{
            display: inline-block;
            padding: 0 15px;
            font-size: 14px;
            border-bottom: 1px solid black;
        }
    </style>
       <script type="text/javascript" src="http://libs.baidu.com/jquery/1.9.0/jquery.js"></script>
	   <script type='text/javascript'>
	   window.onload = function () {


           $('.click').click(function () {
               createHtml();
           });

           function createHtml() {
               var test = document.getElementsByClassName('formal')[0].innerHTML;
               console.log(test);
               getHtml(test);
               //test = test.replace(/<\/?[^>]*>/g,'');
               // export_raw('cancelEntry.html', test);
           }

           function getHtml(text) {
               $.ajax({
                   url: '/friendlyPage/downloadHtml',
                   type: 'POST',
                   data: {'text': text},
                   success: function (data) {
                       console.log(data);
                       window.open(data);
                       // $('#download').attr("href", data);
                   }
               })
           }

       }
/* window.onload = function(){
	
function fake_click(obj) {
    var ev = document.createEvent("MouseEvents");
    ev.initMouseEvent(
        "click", true, false, window, 0, 0, 0, 0, 0
        , false, false, false, false, 0, null
        );
    obj.dispatchEvent(ev);
}
	function export_raw(name, data) {
		var urlObject = window.URL || window.webkitURL || window;
		   var export_blob = new Blob([data]);
		   var save_link = document.createElementNS("http://www.w3.org/1999/xhtml", "a")
		   save_link.href = urlObject.createObjectURL(export_blob);
		   save_link.download = name;
		   fake_click(save_link);
	}
	$('button').click(function() {
		//正则表达式过滤html标签
		var test = document.getElementsByClassName('formal')[0].innerHTML;
		//test = test.replace(/<\/?[^>]*>/g,'');
		export_raw('formal.html', test);
	});
} */
</script>
</head>
<body>
 <button >保存文件</button>
<div class="wrap formal">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <h1>adidas</h1>
    
    <!-- <input id="btnSaveAs" type="button" value="保存为HTML" onclick="getFile()"/> -->
    <div class="margin-bottom"><span class="setvalue">${vo.name}</span><label>先生/小姐：</label></div>
    <p>您将与阿迪达斯体育（中国）有限公司签订劳动合同，为了使您顺利签约，请仔细阅读以下条款：</p>
    <p>
        1、	阿迪达斯将委托中智上海经济合作公司为您办理入职签约，请您一个工作日内联系当地签约机构并约定可签约日期。
    </p>
    <form>
        <div class="margin-bottom">
            <label>城市：</label>
            <span class="setvalue">${vo.city}</span>
        </div>
        <div class="margin-bottom">
            <label>联系人：</label>
            <span class="setvalue">${vo.contracts}</span>
        </div>
        <div class="margin-bottom">
            <label>电话：</label>
            <span class="setvalue">${vo.phone}</span>
        </div>
        <div class="margin-bottom">
            <label>地址：</label>
            <span class="setvalue">${vo.address}</span>
        </div>
    </form>
    <p>2、	请提前准备好如下材料并于签约当日携带到机构。</p>
    <table border="1px solid black" >
        <tr>
            <th>正式员工</th>
            <th>缴纳材料</th>
        </tr>
        <tr>
            <td>1</td>
            <td>身份证复印件 1 份</td>
        </tr>
        <tr>
            <td>2</td>
            <td>原单位离职证明</td>
        </tr>
        <tr>
            <td>3</td>
            <td>健康证发票原件或健康证复印件</td>
        </tr>
        <tr>
            <td>4</td>
            <td>工资卡号及开户银行（建设银行借记卡）</td>
        </tr>
        <tr>
            <td>5</td>
            <td>一寸照片 1 张</td>
        </tr>
        <tr>
            <td>6</td>
            <td>最高学历证书复印件</td>
        </tr>
        <tr>
            <td>7</td>
            <td>办理用工、社保公积金材料（如劳动手册、退工单等）</td>
        </tr>
        <tr>
            <td>8</td>
            <td>如工作超过10年提供证明材料（如社保缴费记录或退工证明）</td>
        </tr>
    </table>
    <p>3、	请您保持手机畅通，如对入职流程有任何疑问，请与机构联系人沟通。</p>
    <h4 class="text-right">阿迪达斯人力资源部</h4>

</div>

</body>
</html>