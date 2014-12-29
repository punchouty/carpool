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
<body>
	<%--
	<div class="preloader">
        <div class="status">
            racloop
        </div>
    </div>
     --%>
	<header class="header" data-stellar-background-ratio="0.5" id="home">
       		 <!-- COLOR OVER IMAGE -->
       	<div class="overlay-layer">
	
			<g:render template="/templates/shared/navigation" />	
			
			<div class="container">
				<g:layoutBody />
			</div>
		</div>
	</header>
	<nav class="container" role="navigation">
        <g:render template="/templates/shared/footer" />
   	</nav>
   	<%--
      <script>
       /* PRE LOADER */
       jQuery(window).load(function () {
           "use strict";
           jQuery(".status").fadeOut();
           jQuery(".preloader").delay(1000).fadeOut("slow");
       })
   	</script>
   	 --%>
	<r:layoutResources />
</body>
</html>