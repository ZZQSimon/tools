<%@ page language="java"
         pageEncoding="UTF-8" %>
<!--contentType="text/html; charset=UTF-8" -->
<!--<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">-->
<!DOCTYPE html>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <base href="<%=basePath%>">
    <meta charset="UTF-8">
    <title>adidas</title>
    <style>
        body {
            margin: 0;
            background: #f9f9f9;
        }

        h1 {
            font-size: 30px;
            color: #909090;
            margin: 0 0 50px;
            border-bottom: 1px solid grey;
        }

        input {
            outline: none;
            border: none;
            border-bottom: 1px solid black;
        }

        p {
            font-size: 14px;
            line-height: 36px;
        }

        table {
            font-size: 14px;
            border-collapse: collapse;
        }

        th {
            color: white;
            background: black;
        }

        td, th {
            padding: 5px 10px;
        }

        label {
            font-size: 14px;
        }

        .wrap {
            width: 100%;
            padding: 50px;
            box-sizing: border-box;
            background: white;
            /*border:1px solid #efefef;*/
        }

        .margin-bottom {
            margin-bottom: 30px;
        }

        .text-right {
            font-size: 16px;
            text-align: right;
        }

        .setvalue {
            display: inline-block;
            padding: 0 15px;
            font-size: 14px;
            border-bottom: 1px solid black;
        }
    </style>
    <script type="text/javascript" src="http://libs.baidu.com/jquery/1.9.0/jquery.js"></script>
    <script type='text/javascript'>
        /* window.onload = function () {


            $('.click').click(function () {
                createHtml();
            });

            function createHtml() {
                var test = document.getElementsByClassName('cancelEntry')[0].innerHTML;
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

        } */
    </script>
</head>
<body>
<button class="click">保存文件</button>
<%--<a style="text-decoration: none;color: #f9f9f9;" id="download" href="#">保存文件</a>--%>
<div class="wrap cancelEntry">
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <h1>adidas</h1>

    <!-- <input id="btnSaveAs" type="button" value="保存为HTML" onclick="getFile()"/> -->
    <div class="margin-bottom"><span class="setvalue">${vo.name}</span><label>先生/小姐：</label></div>
    <p>${vo.remark}</p>

    <h4 class="text-right">阿迪达斯人力资源部</h4>

</div>
    <script type="text/javascript" src="./js/download.js"></script>
</body>
</html>