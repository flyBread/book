<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html>
<html>
<head>
<title>BookUtil</title>
<script type="text/javascript" src="jquery-1.8.2.min.js"></script>

<script type="text/javascript">
	function init() {
	}
</script>
</head>
<body style="background: #f8f8f8;" onload="init()">
	<form name="form2" id="form2" action="shujia.aspx?action=yongjiudel"
		method="post">
		<div
			style="width: 760px; margin: auto; height: 20px; line-height: 20px; margin-top: 30px; border-bottom: 1px solid #aaa; text-align: left;">

			<div style="width: 188px; float: left;">书籍名称</div>
			<div style="width: 482px; float: left;">最新章节</div>
			<div style="width: 88px; float: left;">更新时间</div>
		</div>

		   <h1><%=request.getParameterMap()%></h1> 

		<%
			for(int i = 0 ; i < 10;i++){%>
		<div
			style="width: 760px; margin: auto; height: 30px; overflow: hidden; margin-top: 5px; text-align: left; line-height: 30px; border-bottom: 1px #aaaaaa dashed;">
			<div style="width: 188px; float: left;">
				<input type="checkbox" name="bookid" value="3659536" size="3">&nbsp;<a
					href="/mulu_3659536.html">重生完美时代</a>
			</div>
			<div style="width: 482px; float: left;">
				<a href="/mulu_3659536.html">第四百六十章 把天捅破了</a>
			</div>
			<div style="width: 88px; float: left;" class="xt1">9-6 13:41</div>
		</div>

		<%}%>
	</form>
	<br />
	<br />
	<br />
	<br />
	<br />
	<p
		style="margin-top: 5px; text-align: center; line-height: 30px; border-bottom: 1px #aaaaaa dashed;">
		需要改造的更加的符合自己的习惯，没有网络的情况下，也能够在本机生成</p>
</body>
</html>