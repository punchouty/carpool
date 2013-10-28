<%@ page import="com.racloop.JourneyRequestCommand" %>
<!DOCTYPE html>
<html>
<head>
<meta name="layout" content="main" />
<title>Welcome to raC looP</title>
</head>
<body>
	<g:if test="${flash.message}">
		<div class="alert">			
		  <button type="button" class="close" data-dismiss="alert">&times;</button>
		  ${flash.message}
		</div>
	</g:if>
	<g:hasErrors bean="${commandInstance}">
		<div class="alert">	
			<ul class="errors" role="alert">
				<g:eachError bean="${commandInstance}" var="error">
				<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
				</g:eachError>
			</ul>
		</div>
	</g:hasErrors>
	<g:set var="isHome" value="true" scope="request" />
	<div class="jumbotron" id="jumbotron">
		<h1>Car Pool Finder</h1>
		<p class="lead">Share your Car, Taxi and Auto rides. Make new friends and Contribute to Greener Environment.
		</p>
	</div>

	<div id="main-controls">
		<g:form name="search-form" controller="journey" action="save" class="form-inline">
			<div id="travelDateDiv" class="input-append">
				<g:textField name="dateOfJourneyString" value="${commandInstance?.dateOfJourneyString}" class="search-field" placeholder="Date and Time"  autocomplete="off" />
				<a href="#" class="add-on"> 
					<i data-time-icon="icon-time" data-date-icon="icon-calendar"> </i>
				</a>
			</div>			
			
			<div id="fromDiv" class="input-append dropdown">
				<g:textField name="fromPlace" value="${commandInstance?.fromPlace}" placeholder="Start - Landmark or Locality"  autocomplete="off"/>
				<a href="#" class="add-on dropdown-toggle"  data-toggle="dropdown"> 
					<i class="icon-map-marker"> </i>
				</a>
				<ul class="dropdown-menu">
					<li><a href="#"> Caunaught place, Delhi </a></li>
					<li><a href="#"> Sector 47-D,  Chandigarh </a></li>
					<li><a href="#"> IFFCO chowk, Gurgaon </a></li>
				</ul>
			</div>	
					
			<g:hiddenField name="fromLatitude" value="${commandInstance?.fromLatitude}" />
			<g:hiddenField name="fromLongitude" value="${commandInstance?.fromLongitude}" />
			<div id="toDiv" class="input-append dropdown"> 
				<g:textField name="toPlace" value="${commandInstance?.toPlace}" placeholder="End - Landmark or Locality"  autocomplete="off" />
				<a href="#" class="add-on dropdown-toggle"  data-toggle="dropdown"> 
					<i class="icon-map-marker"></i>
				</a>
				<ul class="dropdown-menu">
					<li><a href="#"> IFFCO chowk, Gurgaon </a></li>
					<li><a href="#"> Sector 47-D,  Chandigarh </a></li>
					<li><a href="#"> Caunaught place, Delhi </a></li>
				</ul>
			</div>
			
			<g:hiddenField name="toLatitude" value="${commandInstance?.toLatitude}" />
			<g:hiddenField name="toLongitude" value="${commandInstance?.toLongitude}" />
			
			<div class="btn-group">
				<button class="btn btn-primary">Car?</button>
				<button class="btn btn-primary dropdown-toggle"
					data-toggle="dropdown">
					<span class="caret"></span>
				</button>
				<ul class="dropdown-menu">
					<li><a id="driver" href="#">Yes, I am driving</a></li>
					<li><a id="rider" href="#">No, I need a ride</a></li>
				</ul>
			</div>
			<g:hiddenField name="validStartTimeString" value="${commandInstance?.validStartTimeString}" />
			<g:hiddenField name="tripDistance" value="${commandInstance?.tripDistance}" />
			<g:hiddenField name="tripUnit" value="${commandInstance?.tripUnit}" />
			<g:hiddenField name="isDriver" value="${commandInstance?.isDriver}" />
		</g:form>
	</div>
	<div id="map-canvas" class="map_canvas"></div>
	<small id="map-help"></small>
	<div id="myModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	  <div class="modal-header">
	    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
	    <h3 id="myModalLabel">Error Message</h3>
	  </div>
	  <div class="modal-body">
	    <p id="errorMessage"></p>
	  </div>
	  <div class="modal-footer">
	    <button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
	  </div>
	</div>
	<script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?v=3&key=AIzaSyD-2SVsFAN8CLCAU7gU7xdbF2Xdkox9JoI&sensor=false&libraries=places">		
	</script>
</body>
</html>
