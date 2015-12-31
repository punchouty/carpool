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
	    <!-- =========================
	     SECTION: HOME / HEADER  
	    ============================== -->
    	<header class="header" data-stellar-background-ratio="0.5" id="home">
       		 <!-- COLOR OVER IMAGE -->
        	<div class="overlay-layer">
		
				<g:render template="/templates/shared/navigation" />	
				
				<div class="container">
					<g:layoutBody />
				</div>
			</div>
		</header>
		

	    
	    <g:render template="/templates/shared/homeServices" />  
	    
		<nav class="container-narrow" role="navigation">
	        <g:render template="/templates/shared/footer" />
	   	</nav>
		
	
	<g:if env="development">
		<div class="well">
			${params }
		</div>
	</g:if>
	
	<script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?v=3&key=AIzaSyD-2SVsFAN8CLCAU7gU7xdbF2Xdkox9JoI&libraries=places"></script>
	<r:layoutResources />
 	
	<%--<g:if env="development"  test = "${session?.currentJourney && session?.currentJourney?.dateOfJourneyString}">
		<script>
		
			$(function() {
				var now = new Date();
				var reserveTime = 30;//in minutes
				var timeToJourneyFromNow = 90;//in minutes
				var timeLimitInDays = 7;//in days
				var validStartTime = new Date(now.getTime() + reserveTime * 60000);
				$('#dateOfJourneyString').val("${session?.currentJourney?.dateOfJourneyString}");
				$('#validStartTimeString').val(validStartTime.toString('dd MMMM yyyy    hh:mm tt'));	

				var from = new google.maps.LatLng(${session?.currentJourney?.fromLatitude}, ${session?.currentJourney?.fromLongitude});//delhi
				var to = new google.maps.LatLng(${session?.currentJourney?.toLatitude}, ${session?.currentJourney?.toLongitude});//chandigarh
				calcRoute(from, to);
				
			});
		
		</script>
	</g:if> --%>
</body>
</html>