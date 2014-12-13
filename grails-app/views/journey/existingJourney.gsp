<%@ page import="grails.converters.JSON" %>
<html>
<head>
<meta name="layout" content="static" />
<title>Existing Journey Found</title>

</head>
<body>
	<g:if test="${flash.error}">
	<div class="alert alert-danger">	
		${flash.error}
	</div>
	</g:if>
	<g:form name="existingJourneyFound" controller="journey" action="searchWithExistingJourney" class="form-inline">
	<div class="row user-infos cyruxx">
	
            <div class="col-md-10 offset1">
                <div class="panel panel-primary">
                    <div class="panel-heading">
                        <h3 class="panel-title">Existing Journey Found!</h3>
                    </div>
                    <div class="panel-body">
                        <div class="row-fluid">
                            
                            <div class="col-md-12">
                            	
                                <table class="table table-condensed table-responsive table-user-information">
                                    <thead>
	                                    <tr>
	                                        <th class="col-md-1"></th>
	                                        <th class="col-md-4">New Journey</th>
	                                        <th class="col-md-4">Existing Journey</th>
	                                    </tr>
                                    </thead>
                                    <tbody>
	                                    <tr>
	                                        <td class="text-left">From : </td>
	                                        <td class="text-left">${currentJourney.fromPlace}</td>
	                                        <td class="text-left">${existingJourney.fromPlace}</td>
	                                    </tr>
	                                    <tr>
	                                        <td class="text-left">To : </td>
	                                        <td class="text-left">${currentJourney.toPlace}</td>
	                                        <td class="text-left">${existingJourney.toPlace}</td>
	                                    </tr>
	                                    <tr>
	                                        <td class="text-left">Date : </td>
	                                        <td class="text-left">${currentJourney.dateOfJourney}</td>
	                                        <td class="text-left">${existingJourney.dateOfJourney}</td>
	                                    </tr>
	                                    <tr>
	                                        <td class="text-left">Driving : </td>
	                                        <td class="text-left">${currentJourney.isDriver?'Yes ':'No '}</td>
	                                        <td class="text-left">${existingJourney.isDriver?'Yes ':'No '}</td>
	                                    </tr>
                                    
                                    </tbody>
                                </table>
                            </div>
                            
                        </div>
                        
                        
                    </div>
                    <div class="row-fluid">
                    	 <div class="col-md-12">
                    	 	<div class="panel-body">
                    	 	<strong>Please select your options</strong><br><br>
                    		
							  <label  class="radio-inline">
							    <input type="radio" name="optionsRadios" id="optionsRadios1" value="existingJourney" checked >
							    Please use existing journey
							  </label>
							
							
							  <label  class="radio-inline">
							    <input type="radio" name="optionsRadios" id="optionsRadios2" value="newJourney">
							    No, I want to put in a new journey request.
							  </label>
							
							</div>
						</div>
					</div>
					
					<div class="col-md-5 text-left">
						<ul class="text-left">
		                    <li>
		                    	<a id="sendRequest" href="#"  data-target="#myModal">
			                        <button id ="sendRequest"class="btn  btn-primary" type="button"
			                                data-toggle="tooltip"
			                                data-original-title="Send message to user"><i class="icon-aim"></i> Send Request</button>
								</a> 
		                    	 <g:link action="search" id="backToSearchResult" controller="userSession">                        	
	                            	<button id ="back" class="btn btn-danger" type="button"
	                                    data-toggle="tooltip"
	                                    data-original-title="Edit this user"><i class="icon-arrows-remove"></i>Back to Search</button>
	                            </g:link> 
		                    	
		                    </li>
	                	</ul>
					</div>
                    
                    
                </div>
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
    
    $('#sendRequest').click(function(){
    	var val = $("input[name=optionsRadios]:checked").val();
    	if(val=='newJourney'){	
    		$('#myModal').modal('show');
			
		}
		else {
			$('#existingJourneyFound').submit();
		}

    });
    $('#Confirmed').click(function(){
      $('#existingJourneyFound').submit();
    });
      
});
</script>	        
        
</body>

</html>