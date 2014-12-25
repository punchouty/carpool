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

<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
  <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
  <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
<![endif]-->
<g:render template="/templates/shared/head" />
<g:layoutHead />
<r:require module="core" />
<r:layoutResources />
</head>
<body>

	
	<div class="preloader">
        <div class="status">
            racloop
        </div>
    </div>
		
	<g:set var="currentUser"
		value="${UserBase.get(SecurityUtils.subject.principal)}" />
	
		<header class="header" data-stellar-background-ratio="0.5" id="home">

        <!-- COLOR OVER IMAGE -->
        <div class="overlay-layer">
		<g:render template="/templates/shared/login" />
		
		<g:layoutBody />
           
      
		<nav class="container" role="navigation">
	        <g:render template="/templates/shared/footer" />
	   	</nav>
	   	
       <script>
        /* PRE LOADER */
        jQuery(window).load(function () {
            "use strict";
            jQuery(".status").fadeOut();
            jQuery(".preloader").delay(1000).fadeOut("slow");
        })
    </script>
	
	<script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script> 
	<script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?v=3&key=AIzaSyD-2SVsFAN8CLCAU7gU7xdbF2Xdkox9JoI&sensor=false&libraries=places"></script>
	<r:layoutResources />
</body>
</html>