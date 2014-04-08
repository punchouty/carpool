
<%@ page import="com.racloop.staticdata.StaticData" %>
<%@ page import="grails.plugin.nimble.core.AdminsService"%>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="static">
		<g:set var="entityName" value="${message(code: 'staticData.label', default: 'StaticData')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<div class="row-fluid">
			<g:if test="${flash.message != null && flash.message.length() > 0}">
				<div id="flash-message" class="alert alert-info">
			         <a class="close" data-dismiss="alert" href="#">×</a>
			         <h4>Message!</h4>
			         <p>${flash.message}</p>            
			     </div>
			</g:if>			
		</div>
		
		<h2>Web Content</h2>
		<div class="row-fluid">
		<table id="results" class="table table-striped">
			<thead>
				<tr>
					<th>Page Name</th>
					<th>Page Content</th>
				</tr>
			</thead>
			<tbody>
			<g:each in="${staticDataInstanceList}" status="i" var="staticDataInstance">
				<tr>
					<td><g:link action="show" id="${staticDataInstance.id}">${fieldValue(bean: staticDataInstance, field: "staticDataKey")}</g:link></td>	
					<td>${fieldValue(bean: staticDataInstance, field: "pageData")}</td>
				</tr>
			</g:each>
			</tbody>
		</table>
		</div>
	</body>
</html>
