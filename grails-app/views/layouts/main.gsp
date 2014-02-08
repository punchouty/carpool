<!DOCTYPE html>
<%@page import="org.apache.shiro.SecurityUtils"%>
<%@page import="grails.plugin.nimble.core.UserBase"%>
<%@ page import="grails.plugin.nimble.core.AdminsService"%>

<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!-->
<html lang="en" class="no-js">
<!--<![endif]-->
<head>
<g:render template="/templates/shared/head" />
<g:layoutHead />
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script> 
<r:require module="core" />
<r:layoutResources />
</head>
<body>
	<g:set var="currentUser"
		value="${UserBase.get(SecurityUtils.subject.principal)}" />
	<div class="container-narrow">
		<g:render template="/templates/shared/navigation" />	
		<hr>		
			
		<g:layoutBody />
		

	    <div class="row-fluid marketing">
			<div class="span4">
			    <h2>Save Money</h2>
			    <p>It is pocket friendly. Nothing better than some one sharing your commute cost. </p>
			</div>
			<div class="span4">
			    <h2>Environment Friendly</h2>
			    <p>Having fewer cars on the road means reduced air pollution and improved air quality. Benefits for generations to come. It also mean reduced traffic congestion that will save time for all of us.</p>
			</div>
			<div class="span4">
			    <h2>Make New Friends</h2>
			    <p>Car pool provide you new avenue of friendships and company for your commute. Drive Together.</p>
			</div>
	    </div>
	      
		<nav class="container-narrow" role="navigation">
	        <g:render template="/templates/shared/footer" />
	   	</nav>
		
	</div>
	<g:if env="development">
		<div class="well">
			${params }
		</div>
	</g:if>
	<r:layoutResources />
</body>
</html>
