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
<facebook:initJS appId="${racloop.getAppId()}" />	
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
	
	<script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?v=3&key=AIzaSyD-2SVsFAN8CLCAU7gU7xdbF2Xdkox9JoI&sensor=false&libraries=places"></script>
	<r:layoutResources />
 	
	<g:if env="development"  test = "${session?.currentJourney && session?.currentJourney?.dateOfJourneyString}">
		<script>
		/*
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
		*/
		</script>
	</g:if>
</body>
</html>