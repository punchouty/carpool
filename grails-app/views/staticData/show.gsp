
<%@ page import="com.racloop.staticdata.StaticData" %>
<%@ page import="grails.plugin.nimble.core.AdminsService"%>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'staticData.label', default: 'StaticData')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-staticData" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<n:isLoggedIn>
				<n:hasRole name="${AdminsService.ADMIN_ROLE}">
					<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
				</n:hasRole>
				</n:isLoggedIn>
			</ul>
		</div>
		<div id="show-staticData" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list staticData">
			
				<g:if test="${staticDataInstance?.data}">
				<li class="fieldcontain">
					<span id="data-label" class="property-label"><g:message code="staticData.data.label" default="Data" /></span>
					
						<span class="property-value" aria-labelledby="data-label"><g:fieldValue bean="${staticDataInstance}" field="data"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${staticDataInstance?.key}">
				<li class="fieldcontain">
					<span id="key-label" class="property-label"><g:message code="staticData.key.label" default="Key" /></span>
					
						<span class="property-value" aria-labelledby="key-label"><g:fieldValue bean="${staticDataInstance}" field="key"/></span>
					
				</li>
				</g:if>
			
			</ol>
			<n:isLoggedIn>
				<n:hasRole name="${AdminsService.ADMIN_ROLE}">
					<g:form>
						<fieldset class="buttons">
							<g:hiddenField name="id" value="${staticDataInstance?.id}" />
							<g:link class="edit" action="edit" id="${staticDataInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
							<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
						</fieldset>
					</g:form>
				</n:hasRole>
			</n:isLoggedIn>
		</div>
	</body>
</html>
