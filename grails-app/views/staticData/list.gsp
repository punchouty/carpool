
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
		<a href="#list-staticData" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<n:isLoggedIn>
				<n:hasRole name="${AdminsService.ADMIN_ROLE}">
					<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
				</n:hasRole>
				</n:isLoggedIn>
			</ul>
		</div>
		<div id="list-staticData" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
						<g:sortableColumn property="key" title="${message(code: 'staticData.key.label', default: 'Key')}" />
						<g:sortableColumn property="data" title="${message(code: 'staticData.data.label', default: 'Data')}" />
						
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${staticDataInstanceList}" status="i" var="staticDataInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${staticDataInstance.id}">${fieldValue(bean: staticDataInstance, field: "key")}</g:link></td>
						<td>${fieldValue(bean: staticDataInstance, field: "data")}</td>
					
						
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${staticDataInstanceTotal}" />
			</div>
		</div>
	</body>
</html>
