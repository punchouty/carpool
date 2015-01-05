<%@ page import="grails.converters.JSON" %>
<html>
<head>
<meta name="layout" content="static" />
<title>Existing Journey Found</title>
<r:require module="core" />
</head>
<body>
	<g:if test="${flash.error}">
	<div class="alert alert-danger">	
		${flash.error}
	</div>
	</g:if>
	<g:form name="existingJourneyFound" controller="journey" action="searchWithExistingJourney" class="form-inline">
		<section class="white-bg" id="section10">
	        <div class="container">
	
	            <!-- SECTION HEADER -->
	            <div class="section-header-racloop">
	                <div class="small-text-medium uppercase colored-text">
	                    Search Results
	                </div>
	                <h2 class="dark-text"><strong>Similar</strong> Request Found</h2>
	                <div class="colored-line">
	                </div>
	            </div>
	        </div>
    	</section>
    	<div class="container">
            <div class="row">
                 <div class="col-md-5 col-md-offset-1 text-left">
                    <h4>Previous</h4>
                    <div>
                        <ul class="text-left">
                            <li>
	                            <g:if test = "${existingJourney.isDriver == true}">
			                		<span class="label label-primary">Car Owner <i class="fa fa-car"></i></span>
			                	</g:if>
			                	<g:else>
			                		<span class="label label-primary">Ride Seeker <i class="fa fa-male"></i></span>
			                	</g:else> 
	                            <i class="icon-basic-calendar"></i> <span><g:formatDate format="dd/MMM/yyyy" date="${existingJourney.dateOfJourney}"/></span>
	                            <i class="icon-clock-alt"></i> <span><g:formatDate format="hh:mm a" date="${existingJourney.dateOfJourney}"/></span>
                            </li>
                            <li><i class="icon-basic-geolocalize-01"></i> <strong>From :</strong>${existingJourney.fromPlace}</li>
                            <li><i class="icon-basic-map"></i> <strong>To :</strong>${existingJourney.toPlace}</li>
                        </ul>
                    </div>
                </div>
                <div class="col-md-5 text-left">
                    <h4>New</h4>
                    <div>
                        <ul class="text-left">
                            <li>
	                            <g:if test = "${currentJourney.isDriver == true}">
			                		<span class="label label-primary">Car Owner <i class="fa fa-car"></i></span>
			                	</g:if>
			                	<g:else>
			                		<span class="label label-primary">Ride Seeker <i class="fa fa-male"></i></span>
			                	</g:else>
	                            <i class="icon-basic-calendar"></i> <span><g:formatDate format="dd/MMM/yyyy" date="${currentJourney.dateOfJourney}"/></span>
	                            <i class="icon-clock-alt"></i> <span><g:formatDate format="hh:mm a" date="${currentJourney.dateOfJourney}"/></span>
                            </li>
                            <li><i class="icon-basic-geolocalize-01"></i> <strong>From :</strong>${currentJourney.fromPlace}</li>
                            <li><i class="icon-basic-map"></i> <strong>To :</strong>${currentJourney.toPlace}</li>
                        </ul>
                    </div>
                </div>
            </div>
            <div class="row">
            	<button id ="useExisting" class="btn btn-primary">Use Existing</button>
            	<a id="useNew" href="#"  data-target="#myModal" class="btn  btn-info" type="button" data-toggle="tooltip" data-original-title="Confirm Cancellation">
            	Use New    
				</a> 
            	<g:link action="search" id="backToSearchResult" controller="userSession">                        	
	               	<button id ="back" class="btn btn-warning" type="button">Back to Search</button>
                </g:link> 
            </div>
    	</div>
    
    <g:hiddenField name="existingJourneyId" value="${existingJourney.id}"/>
	
	<g:hiddenField name="toPlace" value="${currentJourney.toPlace}"/>
	<g:hiddenField name="fromPlace" value="${currentJourney.fromPlace}"/>
	<g:hiddenField name="dateOfJourneyString" value="${currentJourney.dateOfJourneyString}"/>
	<g:hiddenField name="user" value="${currentJourney.user}"/>
	
	<g:hiddenField name="isMale" value="${currentJourney.isMale}"/>
	<g:hiddenField name="validStartTimeString" value="${currentJourney.validStartTimeString}"/>
	<g:hiddenField name="fromLatitude" value="${currentJourney.fromLatitude}"/>
	<g:hiddenField name="fromLongitude" value="${currentJourney.fromLongitude}"/>
	<g:hiddenField name="toLatitude" value="${currentJourney.toLatitude}"/>
	<g:hiddenField name="toLongitude" value="${currentJourney.toLongitude}"/>
	<g:hiddenField name="isDriver" value="${currentJourney.isDriver}"/>
	<g:hiddenField name="tripDistance" value="${currentJourney.tripDistance}"/>
	<g:hiddenField name="tripUnit" value="${currentJourney.tripUnit}"/>
	<g:hiddenField name="newJourney"/>
	
	</g:form>
	<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	  <div class="modal-dialog modal-lg">
	    <div class="modal-content">
	      <div class="modal-header">
	        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	        <h4 class="modal-title modal-primary" id="myModalLabel">Warning</h4>
	      </div>
	      <div class="modal-body">
	        <p id="errorMessage"><strong>This action will cancel your existing journey and all of its associated notifications.</strong><br>
	        <br>Below are the existing journey details: -
	        </p>
	        <table  class="table table-condensed table-responsive table-user-information">
		        <tbody>
			        <tr>
	                     <td>From : </td>
	                     <td>${existingJourney.fromPlace}</td>
	                 </tr>
	                 <tr>
	                     <td>To : </td>
	                     <td>${existingJourney.toPlace}</td>
	                 </tr>
	                 <tr>
	                     <td>Date :</td>
	                     <td>${existingJourney.dateOfJourney}</td>
	                 </tr>
		      	</tbody>
	      	</table>
	      </div>
	      <div class="modal-footer">
	        <button type="button" class="btn btn-default" id ="cancel" data-dismiss="modal">Cancel</button>  
	        <button class="btn btn-primary" data-dismiss="modal" id="Confirmed">Continue</button>      
	      </div>
	    </div>
	  </div>
	</div>

<script type="text/javascript">
$(document).ready(function($){
    //var val = $(':radio[name=rdo_1]:checked').val();
    
    $('#useExisting').click(function(){
    	$('#existingJourneyFound').submit();	

    });
    $('#useNew').click(function(){
    	$('#myModal').modal('show');

    });
    $('#Confirmed').click(function(){
   		$('#newJourney').val("newJourney");  
      $('#existingJourneyFound').submit();
    });
      
});
</script>	        
        
</body>

</html>