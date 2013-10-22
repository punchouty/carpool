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
<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
      <script src="js/html5shiv.js"></script>
<![endif]-->
<g:layoutHead />
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script> 
<r:require module="core" />
<r:layoutResources />
</head>
<body>
	<g:set var="currentUser"
		value="${UserBase.get(SecurityUtils.subject.principal)}" />
	<div class="container-narrow">
		<div class="nav-collapse collapse">
			<g:form controller="auth" action="signin" name="login-form" method="post"
				class="navbar-form form-inline pull-right">
				<input type="text" name="username" id="username" class="input-small"
					placeholder="User Id">
				<input type="password" name="password" id="password"
					class="input-small" placeholder="Password">
				<label class="checkbox"> <input type="checkbox" name="rememberme">
					Remember me
				</label>
				<button type="submit" class="btn btn-primary">Sign in</button>
				<div class="btn-group">
					<a class="btn dropdown-toggle" data-toggle="dropdown" href="#">
						Help <span class="caret"></span>
					</a>
					<ul class="dropdown-menu">
						<li>
							<g:link controller="account" action="createuser">
								Sign Up
							</g:link>
						</li>
						<li>
							<g:link controller="account" action="forgottenpassword">
								Forgot Password
							</g:link>
						</li>
						<li <g:if test="${isTerms == 'true'}">class="active"</g:if>><g:link
								controller="staticPage" action="terms">Terms</g:link>
						</li>
					</ul>
				</div>
				<input type="hidden" name="targetUri" value="${targetUri}" />
			</g:form>
			<h3 class="muted">raC looP</h3>
		</div>
		<hr>

		<g:layoutBody />


		<hr>
		<footer>
			<p>&copy; racloop 2013</p>
		</footer>
	</div>
	<r:layoutResources />
</body>
</html>
