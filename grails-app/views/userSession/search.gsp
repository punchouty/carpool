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
		<%-- 
		<p class="lead">Share your Car, Taxi and Auto rides. Make new friends and Contribute to Greener Environment.
		</p>
		--%>
	</div>

	<div id="main-controls">
		<g:form name="search-form" controller="journey" action="findMatching" class="form-inline">
			<div id="travelDateDiv" class="controls input-append date form_datetime" data-date-format="dd-MM-yyyy-HH:ii p" data-link-field="dtp_input1">
				<input name="dateOfJourneyString" size="16" type="text" value="" placeholder="Date and Time"  autocomplete="off" readonly>
				<span class="add-on"><i class="icon-th"></i></span>
				<input type="hidden" id="dtp_input1" value="" />
			</div>			
			
			<div id="fromDiv" class="input-append dropdown">
				<g:textField name="fromPlace" value="${commandInstance?.fromPlace}" placeholder="From - Landmark or Locality"  autocomplete="off"/>
				<a href="#" class="add-on dropdown-toggle"  data-toggle="dropdown"> 
					<i class="icon-map-marker"> </i>
				</a>
				<g:if test="${history?.size() > 0}">
				<ul class="dropdown-menu">
					<g:each in="${history}" status="i" var="historyInstance">
						<li><a href="#"  id="from_${historyInstance.id}" class="history_dropdown">${historyInstance.place}</a></li>
					</g:each>
				</ul>
				</g:if>
			</div>	
					
			<g:hiddenField name="fromLatitude" value="${commandInstance?.fromLatitude}" />
			<g:hiddenField name="fromLongitude" value="${commandInstance?.fromLongitude}" />
			<div id="toDiv" class="input-append dropdown"> 
				<g:textField name="toPlace" value="${commandInstance?.toPlace}" placeholder="To - Landmark or Locality"  autocomplete="off" />
				<a href="#" class="add-on dropdown-toggle"  data-toggle="dropdown"> 
					<i class="icon-map-marker"></i>
				</a>
				<g:if test="${history?.size() > 0}">
				<ul class="dropdown-menu">
					<g:each in="${history}" status="i" var="historyInstance">
						<li><a href="#" id="to_${historyInstance.id}" class="history_dropdown">${historyInstance.place}</a></li>
					</g:each>
				</ul>
				</g:if>
			</div>
			
			<g:each in="${history}" status="i" var="historyInstance">
				<input type="hidden" id="history_place_${historyInstance.id}" value="${historyInstance.place}" />
				<input type="hidden" id="history_geoHash_${historyInstance.id}" value="${historyInstance.geoHash}" />
				<input type="hidden" id="history_latitude_${historyInstance.id}" value="${historyInstance.latitude}" />
				<input type="hidden" id="history_longitude_${historyInstance.id}" value="${historyInstance.longitude}" />
			</g:each>
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
	<div id="map_info">
	  <!--  <small>Distance : <span id="distance_in_km">20</span> KM</small>  -->
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
</body>
</html>
