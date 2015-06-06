<%@ page import="com.racloop.journey.workkflow.WorkflowState" %>
<html>
<head>
<meta name="layout" content="dynamic" />
<title>My Requests</title>
<r:require module="core" />
<style>
.modal-header {
    background-color: #f2dede;
 }
</style>
</head>
<body>
<section class="white-bg" id="section10">
    <div class="container">

        <!-- SECTION HEADER -->
        <div class="section-header-racloop">
            <div class="small-text-medium uppercase colored-text">
                My upcoming Journeys
            </div>
            <h2 class="dark-text"><strong>My</strong> Requests</h2>
            <div class="colored-line">
            </div>
        </div>
    </div>
</section>
 <div class="container">
 	<g:if test="${journeys?.size > 0}">
 		<g:each in="${journeys}" status="i" var="journeyInstance">
 			<article id="request-${i}" class="row <g:if test="${i%2 == 0}">grey-bg</g:if><g:else>white-bg</g:else>">
 				<div class="row">
 				
		             <div class="col-md-8 col-md-offset-2 text-left">
		                <div>
		                    <ul class="active-journey-list text-left">
		                        <li>
		                        	<g:if test = "${journeyInstance.isDriver == true}">
		                        		<span class="label label-primary">Car Owner <i class="fa fa-car"></i></span> 
		                        	</g:if>
		                        	<g:else>
		                        		<span class="label label-primary">Ride Seeker <i class="fa fa-male"></i></span> 
		                        	</g:else>
		                        	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i class="icon-basic-calendar"></i> <span><g:formatDate format="dd/MMM/yyyy" date="${journeyInstance.dateOfJourney}"/></span> 
		                        	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i class="icon-clock-alt"></i> <span><g:formatDate format="hh:mm a" date="${journeyInstance.dateOfJourney}"/></span>
		                        </li>
		                        <li><i class="icon-basic-geolocalize-01"></i> <strong>From :</strong> ${journeyInstance.from}</li>
		                        <li><i class="icon-weather-wind-s"></i> <strong>To :</strong> ${journeyInstance.to}</li>
		                        <li>
		                        	<g:link action="searchAgain"id="searchAgain"  class="btn btn-warning" params="[journeyId: journeyInstance.id, indexName:journeyInstance.dateOfJourney.format(grailsApplication.config.grails.journeyIndexNameFormat), isDriver:journeyInstance.isDriver?true:false]">
					       				<i class="fa fa-search"></i> Search Again
					       			</g:link>&nbsp; 
		                        	<button id="incoming-btn-${i}" data-requestId="${i}" class="btn btn-success incoming-btn"><i class="fa fa-download"></i> Travel Buddies (${journeyInstance.numberOfCopassengers}) <span class="caret"></span></button> &nbsp; 
		                        	<a name ="delete" id="delete${i}" href="#"  data-target="#myModal" data-id="${journeyInstance.id}">
					       				<button class="btn btn-danger"><i class="fa fa-trash"></i> Delete</button>
					       			</a>
		                        </li>
		                        
		                    </ul>
		                </div>
		            </div>
	            </div>
	            
	            <g:if test="${journeyInstance.numberOfCopassengers > 0}">
	            <div id="incoming-requests-${i}" class="incoming-requests">
                    <div class="well  well-sm">
                   		<h4>Travel Buddies</h4>
               		</div>
	                   
                    <g:each in="${journeyInstance.relatedJourneys}" status="k" var="matchedWorkflowInstance">
	                    <div class="row">
	                    	<div class="col-md-7 col-md-offset-2 text-left">
	                    		<h5>${matchedWorkflowInstance?.name}</h5>
	                    		<ul class="text-left">
	                                <li>
	                                	<g:if test = "${matchedWorkflowInstance?.isDriver== true}">
	                                		<span class="label label-primary">Car Owner</span> 
	                                	</g:if>
	                                	<g:else>
	                                		<span class="label label-primary">Ride Seeker</span> 
	                                	</g:else>
	                                	<span class="${matchedWorkflowInstance.getMyStatus()=='Accepted'?'label label-success':(matchedWorkflowInstance.getMyStatus()=='Cancelled'?'label label-danger':(matchedWorkflowInstance.getMyStatus()=='Rejected'?'label label-info':'label label-info'))}">${matchedWorkflowInstance.getMyStatus()}</span>
	                                	
	                                </li>
	                                <li> <i class="icon-basic-calendar"></i> <span><g:formatDate format="dd/MMM/yyyy" date="${matchedWorkflowInstance.dateOfJourney}"/></span> <i class="icon-clock-alt"></i> <span><g:formatDate format="hh:mm a" date="${matchedWorkflowInstance.dateOfJourney}"/></span></li>
	                                <li><i class="icon-basic-geolocalize-01"></i> <strong>From :</strong>${matchedWorkflowInstance.from}</li>
	                                <li><i class="icon-basic-map"></i> <strong>To :</strong>${matchedWorkflowInstance.to}</li>
	                                <li>
	                                	<g:if test = "${matchedWorkflowInstance.getMyStatus()=='Requested'}">
	                                		<g:link action="cancelRequest" id="cancelRequest"  params="[pairId: matchedWorkflowInstance.getMyPairId(), myJourneyId:journeyInstance.id]">
	   												<button class="btn btn-danger"><i class="fa fa-trash"></i> Cancel Request</button>
	   										</g:link>
	                                	</g:if>
	                                	<g:elseif test = "${matchedWorkflowInstance.getMyStatus()=='Request Recieved'}">
	                                		<g:link action="acceptIncomingRequest" id="acceptIncomingRequest"  params="[pairId: matchedWorkflowInstance.getMyPairId()]">
													<button class="btn btn-primary"><i class="fa fa-check-circle"></i> Accept</button>
											</g:link>
											<g:link action="rejectIncomingRequest" id="rejectIncomingRequest"  params="[pairId: matchedWorkflowInstance.getMyPairId()]">
													<button class="btn btn-warning"><i class="fa fa-ban"></i> Reject</button>
											</g:link>
	                                	</g:elseif>
	                                	<g:elseif test = "${matchedWorkflowInstance.getMyStatus()=='Accepted'}">
	                                		<g:link action="cancelRequest" id="cancelIncomingRequest"  params="[pairId: matchedWorkflowInstance.getMyPairId(), myJourneyId:journeyInstance.id]">
													<button class="btn btn-danger"><i class="fa fa-trash"></i> Cancel Request</button>
											</g:link>
	                                	</g:elseif>
	                                	<g:elseif test = "${matchedWorkflowInstance.getMyStatus()=='Rejected'}">
	                                		<button class="btn btn-warning"><i class="fa fa-ban"></i> Reject</button>
	                                	</g:elseif>
	                                	<g:elseif test = "${matchedWorkflowInstance.getMyStatus()=='Cancelled'}">
	                                		<button class="btn btn-warning"><i class="fa fa-ban"></i> Cancelled</button>
	                                	</g:elseif>
	                                	<g:elseif test = "${matchedWorkflowInstance.getMyStatus()=='Cancelled by Requester'}">
	                                		<button class="btn btn-warning"><i class="fa fa-ban"></i> Cancelled by Requester</button>
	                                	</g:elseif>
	                                	
	                                </li>
	                                
	                            </ul>
	                         </div> 
	                         <div class="col-md-1">
	                         	
	                			<img src="http://www.gravatar.com/avatar/205e460b479c07710c08d50?s=64&d=mm" alt="profile image" class="img-thumbnail"> </img>
	                        </div>  
	                   	</div>     
                    </g:each>
	             </div>
	             </g:if>
	             
    		</article>
    	</g:each>
       
   </g:if>
     <g:else>
	  <div class="well">
		<h4>No Records Found</h4>
	 </div>
    </g:else>
 </div>
        
 	<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	  <div class="modal-dialog">
	    <div class="modal-content">
	      <div class="modal-header">
	        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	        <h4 class="modal-title modal-primary" id="myModalLabel">Warning</h4>
	      </div>
	      <div class="modal-body">
	      <g:form name="deleteJourneyForm" controller="journey" action="deleteJourney" class="form-inline">
	        <p id="errorMessage">This action will cancel your existing journey and all of its associated notifications.
	        </p>
	        <g:hiddenField id="journeyIdToBeDeleted" name ="journeyIdToBeDeleted" value=""/>
	       </g:form>
	       
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
			$('a[name=delete]').click(function(){
		        var jourenyId = $(this).data('id');
		        $("#journeyIdToBeDeleted").val(jourenyId);
		    	$('#myModal').modal({show:true});
		    });
		   $('#Confirmed').click(function(){
		    	$('#deleteJourneyForm').submit();
		    });    	
		});
	</script>
</body>
</html>