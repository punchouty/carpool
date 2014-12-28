<%@ page import="com.racloop.JourneyRequestCommand" %>
<html>
<head>
<meta name="layout" content="main" />
<title>Welcome to raC looP</title>
<r:require module="core" />
</head>
<body>
	<g:set var="isLandingPage" value="true" scope="request" />
	<g:if test="${flash.message}">
		<div class="alert alert-success">			
		  <button type="button" class="close" data-dismiss="alert">&times;</button>
		  ${flash.message}
		</div>
	</g:if>
	<g:if test="${flash.error}">
		<div class="alert alert-danger">			
		  <button type="button" class="close" data-dismiss="alert">&times;</button>
		  ${flash.error}
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
	
	<!-- ONLY LOGO ON HEADER -->
      <div class="only-logo">
          <div class="navbar">
              <div class="navbar-header">
                  <h2 class="white-text racloop-heading-big">Racloop <span class="fa fa-leaf green-text"></span></h2>
              </div>
          </div>
      </div>
      
      <div class="row">
          <div class="col-md-12">

              <!-- HEADING AND BUTTONS -->
              <div class="intro-section">

                  <!-- WELCOM MESSAGE -->
                  <h1 class="intro white-text">Share Car and Auto Rides Safely</h1>
                  <h5 class="white-text">Offer a ride or Ask people for lift. Save money, Make friends and Contribute to <span class="green-text">Greener</span> Environment. </h5>

                  <!-- MAILCHIMP SUBSCRIBE FORM -->
                  <div class="row">
                      <div class="col-sm-12">
                          <div class="horizontal-subscribe-form">
                          	<g:form class="form-inline" name="search-form" controller="journey" action="findMatching">
                          		<input type="text" id="dateOfJourneyString" name="dateOfJourneyString" size="16" autocomplete="off" class="form-control input-box form_datetime" placeholder="Date and Time">
                          		<span class="clearer glyphicon glyphicon-remove-circle form-control-feedback"></span>
                          		<input type="text" id="fromPlace" name="fromPlace" size="25" autocomplete="off" value="${commandInstance?.fromPlace}" class="form-control input-box" placeholder="From : Landmark">
                          		<g:hiddenField name="_hFromPlace" value="${commandInstance?.fromPlace}" />
                          		<g:hiddenField name="fromLatitude" value="${commandInstance?.fromLatitude}" />
								<g:hiddenField name="fromLongitude" value="${commandInstance?.fromLongitude}" />
								<input type="text" id="toPlace" name="toPlace" size="25" autocomplete="off" value="${commandInstance?.toPlace}" class="form-control input-box" placeholder="To : Landmark">
								<g:hiddenField name="_hToPlace" value="${commandInstance?.toPlace}" />
								<g:hiddenField name="toLatitude" value="${commandInstance?.toLatitude}" />
								<g:hiddenField name="toLongitude" value="${commandInstance?.toLongitude}" />
								
								<button type="button" id ="offer" value="offer" class="btn standard-button">Offer</button>
							    <button type="button" id ="ask" value="ask"class="btn standard-button">Ask</button>
							    <button type="button" id ="clear" value="clear"class="btn standard-button standard-red-button">Clear</button>
								<g:hiddenField name="validStartTimeString" value="${commandInstance?.validStartTimeString}" />
								<g:hiddenField name="tripDistance" value="${commandInstance?.tripDistance}" />
								<g:hiddenField name="tripUnit" value="${commandInstance?.tripUnit}" />
								<g:hiddenField name="isDriver" value="${commandInstance?.isDriver}" />	
							</g:form>
							 <!-- SUCCESS AND ERROR MESSAGE -->
                              <p id="form-success" class="white-text"><span class="icon-check-alt2 colored-text"></span> <span id="success-message">Thanks!</span></p>
                              <p id="form-error" class="white-text"><span class="icon-close-alt2 red-text"></span> <span id="error-message" >Error!</span> </p>
                          </div>
                      </div>
                  </div>
              </div>
          </div>
      </div>    
	<%-- 
	<div id="map_info">
	  <!--  <small>Distance : <span id="distance_in_km">20</span> KM</small>  -->
	</div>
	<!-- TODO to be removed. Not used any where. Else following java script error -->
	<!-- Uncaught TypeError: Cannot read property 'offsetWidth' of null -->
	<div id="map-canvas" class="map_canvas"></div>
	<small id="map-help"></small>
	
	--%>
	
<%--
<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h4 class="modal-title" id="myModalLabel">Error Message</h4>
      </div>
      <div class="modal-body">
        <p id="errorMessage"></p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>        
      </div>
    </div>
  </div>
</div>
 --%>


</body>
</html>
