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
		
		<g:render template="/templates/shared/login" />
		<hr>
		
		<g:layoutBody />
      
		<nav class="container-narrow" role="navigation">
	        <g:render template="/templates/shared/footer" />
	   	</nav>
	</div>
	<r:layoutResources />
</body>
</html>
