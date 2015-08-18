<%@page import="org.apache.shiro.SecurityUtils"%>
<%@page import="grails.plugin.nimble.core.UserBase"%>
<%@ page import="grails.plugin.nimble.core.AdminsService"%>
<!doctype html>
<html lang="en">
<head>
	<g:render template="/templates/shared/head" />
	<g:layoutHead />
	<r:layoutResources />
	<g:if env="production">
	<script>
	  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
	  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
	  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
	  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');
	
	  ga('create', 'UA-66121379-2', 'auto');
	  ga('send', 'pageview');
	
	</script>
	</g:if>
</head>
<facebook:initJS appId="${racloop.getAppId()}"  xfbml="true", version="2.4"/>
<body>
	<header class="header-racloop" data-stellar-background-ratio="0.5">
        <!-- COLOR OVER IMAGE -->
        <div class="overlay-layer-racloop">
			<g:render template="/templates/shared/navigation" />
		</div>
	</header>
		
	<g:layoutBody />
      
	<nav class="container" role="navigation">
        <g:render template="/templates/shared/footer" />
   	</nav>
	<g:if env="development">
	<div>
		${params }
	</div>
	</g:if>	
	<r:layoutResources />
</body>
</html>