<%@page import="org.apache.shiro.SecurityUtils"%>
<%@page import="grails.plugin.nimble.core.UserBase"%>
<%@ page import="grails.plugin.nimble.core.AdminsService"%>
<!doctype html>
<html lang="en">
<head>
	<g:render template="/templates/shared/head" />
	<g:layoutHead />
	<r:layoutResources />
</head>
<!-- ####### Disable Facebook login for time being##########
<facebook:initJS appId="${racloop.getAppId()}" />
 -->	
<body>
	<header class="header-racloop" data-stellar-background-ratio="0.5">
        <!-- COLOR OVER IMAGE -->
        <div class="overlay-layer-racloop">
			<g:render template="/templates/shared/navigation" />
		</div>
	</header>
	<div class="container">
		
	<g:layoutBody />
		
	</div>
      
	<nav class="container-narrow" role="navigation">
        <g:render template="/templates/shared/footer" />
   	</nav>
	
	<g:if env="development">
	<div class="well">
		${params }
	</div>
	</g:if>	
	<r:layoutResources />
</body>
</html>