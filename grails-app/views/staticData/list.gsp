
<%@ page import="com.racloop.staticdata.StaticData" %>
<%@ page import="grails.plugin.nimble.core.AdminsService"%>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="static">
		<g:set var="entityName" value="${message(code: 'staticData.label', default: 'StaticData')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
		<r:require module="core" />
	</head>
	<body>
		<section class="white-bg" id="section10">
        <div class="container">
            <!-- SECTION HEADER -->
            <div class="section-header-racloop">
                <div class="small-text-medium uppercase colored-text">
                    Master Data Configuration
                </div>
                <h2 class="dark-text">Please be <strong>careful</strong> while editing</h2>
                <div class="colored-line">
                </div>
            </div>
        </div>
    	</section>
    	<div class="container">
	    	<div class="col-md-8 col-md-offset-2">
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
	    	</div>
	    	<div class="row">
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
    	</div>
		
	</body>
</html>
