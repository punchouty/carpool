<%@ page import="com.racloop.staticdata.StaticData" %>
<%@ page import="grails.plugin.nimble.core.AdminsService"%>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="static">
		<g:set var="entityName" value="${message(code: 'staticData.label', default: 'StaticData')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<div class="row">
			<g:if test="${flash.message != null && flash.message.length() > 0}">
				<div id="flash-message" class="alert alert-info">
			         <a class="close" data-dismiss="alert" href="#">×</a>
			         <h4>Success!</h4>
			         <n:flashembed/>            
			     </div>
			</g:if>
			<g:hasErrors bean="${staticDataInstance}">
				<div id="flash-error" class="alert alert-error">
					<a class="close" data-dismiss="alert" href="#">×</a>
					<h4>Error!</h4>
					<g:renderErrors bean="${user}" as="list" />
				</div>
			</g:hasErrors>	
		</div>
		<div class="row">		    
			<g:form action="save"  class="form-horizontal">
			<g:hiddenField name="id" value="${staticDataInstance?.id}" />
			<g:hiddenField name="version" value="${staticDataInstance?.version}" />
			<fieldset>
			    <legend>Edit Page</legend>
			    <div class="control-group">
		         	 <label class="control-label">Page Name</label>
		             <div class="controls">
		                 <strong>${staticDataInstance?.staticDataKey}</strong>
					 </div> 
				</div>
				 <div class="control-group">
		              	<label class="control-label" for="pageData">Page Content</label>
		                  <div class="controls">
		                      <g:textArea name="pageData" value="${staticDataInstance?.pageData}" required="" style="margin: 0px; width: 650px; height: 300px; overflow-x: auto; overflow-y: auto;"/>
							  <p id="content-help-block" class="help-block"></p>
		                  </div>
				 </div>
				 <br />
                <div>
                	<g:actionSubmit action="update" value="Update" class="btn btn-large btn-info"/>
                	<g:link class="btn btn-large btn-info" action="list" class="btn btn-large">Cancel</g:link>
				</div>
			</fieldset>
			</g:form>
		</div>
	</body>
</html>
