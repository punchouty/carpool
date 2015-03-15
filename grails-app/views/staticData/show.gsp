<%@ page import="com.racloop.staticdata.StaticData" %>
<%@ page import="grails.plugin.nimble.core.AdminsService"%>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="edithtml">
		<g:set var="entityName" value="${message(code: 'staticData.label', default: 'StaticData')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
		<r:require module="core" />
	</head>
	<body>
		<section class="white-bg" id="section10">
        <div class="container">
            <!-- SECTION HEADER -->
            <div class="section-header-racloop">
                <div class="small-text-medium uppercase colored-text">
                    Please be <strong>careful</strong> while editing
                </div>
                <h2 class="dark-text">Editing ${staticDataInstance?.staticDataKey}</h2>
                <div class="colored-line">
                </div>
            </div>
        </div>
    	</section>
    	<div class="container">
	    	<div class="col-md-10 col-md-offset-1">
	    		 <div class="row">
					<g:if test="${flash.message != null && flash.message.length() > 0}">	
			        	<g:if test="${flash.type == 'error'}">			
					     <div class="alert alert-danger alert-dismissible" role="alert">
			                  <button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
			                  <strong>Error!</strong> <n:flashembed/> 
			             </div>
			            </g:if>
			            <g:else>
			            <div class="alert alert-info alert-dismissible" role="alert">
			                  <button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
			                  <strong>Success!</strong> <n:flashembed/> 
			             </div>
			            </g:else>
					</g:if>
			        <g:hasErrors bean="${staticDataInstance}">			
						 <div class="alert alert-danger alert-dismissible" role="alert">
		                    <button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
		                    <strong>Error!</strong> <g:renderErrors bean="${user}" as="list" />
		                </div>	
					</g:hasErrors>
				</div>
				
		    	<div class="row">
					<g:form action="update"  class="registration-form">
						<g:hiddenField name="id" value="${staticDataInstance?.id}" />
						<g:hiddenField name="version" value="${staticDataInstance?.version}" />
						<g:hiddenField name="pageData" value="${staticDataInstance?.pageData?.encodeAsHTML()}" />
						<div id="editor">
							
						</div>
						<br/>
						<g:submitButton name="edit" id="edit" value="Save" class="btn btn-large btn-primary" />
						<a type="button" id="reset" class="btn btn-warning"> Reset</a>
                		<g:link class="btn btn-large btn-info" action="list" >All Pages</g:link>
					</g:form>
				</div>
                		<br/>
	    	</div>
    	</div>
	</body>
</html>
