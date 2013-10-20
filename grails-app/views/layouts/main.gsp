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
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="Car pool site">
<meta name="author" content="racloop">
<title><g:layoutTitle default="raC looP - The Car Pool App" /></title>
<link rel="shortcut icon"
	href="${resource(dir: 'img', file: 'favicon.ico')}" type="image/x-icon">
<link rel="apple-touch-icon"
	href="${resource(dir: 'img', file: 'apple-touch-icon.png')}">
<link rel="apple-touch-icon" sizes="114x114"
	href="${resource(dir: 'img', file: 'apple-touch-icon-retina.png')}">
<link rel="stylesheet"
	href="${resource(dir: 'css', file: 'bootstrap.css')}" type="text/css">
<link rel="stylesheet"
	href="${resource(dir: 'css', file: 'bootstrap-datetimepicker.min.css')}"
	type="text/css">
<link rel="stylesheet"
	href="${resource(dir: 'css', file: 'bootstrap-responsive.css')}"
	type="text/css">
<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
      <script src="js/html5shiv.js"></script>
<![endif]-->
<g:layoutHead />
<%-- <r:require module="jquery" /> --%>
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
<r:layoutResources />
</head>
<body>
	<g:set var="currentUser"
		value="${UserBase.get(SecurityUtils.subject.principal)}" />
	<div class="container-narrow">
		<div class="masthead">
			<ul class="nav nav-pills pull-right">
				<n:isNotLoggedIn>
					<li <g:if test="${isHome == 'true'}">class="active"</g:if>><a
						href="${request.contextPath}">Home</a></li>
					<li <g:if test="${isTerms == 'true'}">class="active"</g:if>><g:link
							controller="staticPage" action="terms">Terms</g:link></li>
					<li <g:if test="${isEtiquettes == 'true'}">class="active"</g:if>><g:link
							controller="staticPage" action="etiquettes">Etiquettes</g:link></li>
					<li <g:if test="${isAbout == 'true'}">class="active"</g:if>><g:link
							controller="staticPage" action="about">About</g:link></li>
					<li><g:link controller="auth" action="login">Sign In</g:link></li>
				</n:isNotLoggedIn>
				<n:isLoggedIn>
					<li class="active dropdown"><a class="dropdown-toggle"
						data-toggle="dropdown" href="#"> Home <b class="caret"></b>
					</a>
						<ul class="dropdown-menu">
							<li <g:if test="${isTerms == 'true'}">class="active"</g:if>><g:link
									controller="staticPage" action="terms">Terms</g:link></li>
							<li <g:if test="${isEtiquettes == 'true'}">class="active"</g:if>><g:link
									controller="staticPage" action="etiquettes">Etiquettes</g:link></li>
							<li <g:if test="${isAbout == 'true'}">class="active"</g:if>><g:link
									controller="staticPage" action="about">About</g:link></li>
						</ul></li>
					<li><a href="#about">Active Requests</a></li>
					<li><a href="#contact">Notifications <span
							class="badge badge-important">4</span></a></li>
					<li><a href="#about">History</a></li>
					<li class="dropdown"><a class="dropdown-toggle"
						data-toggle="dropdown" href="#"> ${currentUser.profile.fullName}&nbsp;<i
							class="icon-user"></i> <b class="caret"></b>
					</a>
						<ul class="dropdown-menu">
							<n:hasRole name="${AdminsService.ADMIN_ROLE}">
								<li><g:link controller="admins" action="index">Admin Functions</g:link>
								</li>
							</n:hasRole>
							<li><a href="#about">Profile</a></li>
							<li><a href="#about">Change Password</a></li>
							<li><g:link controller="auth" action="logout">Sign Out</g:link></li>
						</ul></li>
				</n:isLoggedIn>
			</ul>
			<h3 class="muted">raC looP</h3>
		</div>
		<hr>		
		
		<g:layoutBody />

		<hr>
		<footer>
			<p>&copy; racloop 2013</p>
		</footer>
	</div>
	<g:if env="development">
	<div>
		${params }
	</div>
	</g:if>
	<g:javascript library="application" />
	<r:layoutResources />
</body>
</html>
