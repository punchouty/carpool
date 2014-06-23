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

<script src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>

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
	
		<g:render template="/templates/shared/navigation" />	
		
		<div class="container">
		<g:layoutBody />
		

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
		
	</div>
	<g:if env="development">
		<div class="well">
			${params }
		</div>
	</g:if>
	<script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script> 
	<r:layoutResources />
	<script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?v=3&key=AIzaSyD-2SVsFAN8CLCAU7gU7xdbF2Xdkox9JoI&sensor=false&libraries=places"></script>
	<script src="${resource(dir: 'js', file: 'map.js')}" ></script>
	<g:if env="development">
		<script>
			$(function() {
				/* 
				$('<div class="alert alert-error"><button type="button" class="close" data-dismiss="alert">&times;</button><h4>Hard coding</h4><p>Please comment or change java script code in main.gsp layout</p></div>').insertAfter("hr");
				var now = new Date();
				var reserveTime = 30;//in minutes
				var timeToJourneyFromNow = 90;//in minutes
				var timeLimitInDays = 7;//in days
				var validStartTime = new Date(now.getTime() + reserveTime * 60000);
				var dateOfJourney = new Date(now.getTime() + timeToJourneyFromNow * 60000);
				var initialTime = new Date(now.getTime() + (reserveTime + 15) * 60000);
				var validEndTime = new Date(now.getTime() + timeLimitInDays * 24 * 60 * 60000);							
				$('#dateOfJourneyString').val(dateOfJourney.toString('dd MMMM yyyy    HH:mm tt'));
				$('#validStartTimeString').val(validStartTime.toString('dd MMMM yyyy    HH:mm tt'));	

				var from = new google.maps.LatLng(28.6352494, 77.22443450000003);//delhi
				var to = new google.maps.LatLng(30.7333038, 76.77938949999998);//chandigarh
				calcRoute(from, to);
				/* */
			});
		</script>
	</g:if>
</body>
</html>