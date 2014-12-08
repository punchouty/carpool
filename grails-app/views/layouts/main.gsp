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


	<meta charset="UTF-8">
    <meta name="description" content="A car pool finder application">
    <meta name="keywords" content="carpool, car pool, carpool finder, carpool share">
    <meta name="author" content="Rajan Punchouty">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">



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
	<g:set var="currentUser"
		value="${UserBase.get(SecurityUtils.subject.principal)}" />
	
		<!-- =========================
	     PRE LOADER       
	    ============================== -->
	    
	
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
		

	    <div class="row marketing">
			<div class="col-md-4">
			    <h2>Save Money</h2>
			    <p>It is pocket friendly. Nothing better than some one sharing your commute cost. </p>
			</div>
			<div class="col-md-4">
			    <h2>Environment Friendly</h2>
			    <p>Having fewer cars on the road means reduced air pollution and improved air quality. Benefits for generations to come. It also mean reduced traffic congestion that will save time for all of us.</p>
			</div>
			<div class="col-md-4">
			    <h2>Make New Friends</h2>
			    <p>Car pool provide you new avenue of friendships and company for your commute. Drive Together.</p>
			</div>
	    </div>
	      
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
	</g:if>
</body>
</html>