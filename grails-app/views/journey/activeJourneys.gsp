<%@ page import="com.racloop.journey.workkflow.WorkflowState" %>
<html>
<head>
<meta name="layout" content="static" />
<title>My Active Journeys</title>
<style>
.modal-header {
    background-color: #f2dede;
 }
</style>
</head>
<section class="white-bg" id="section10">
    <div class="container">

        <!-- SECTION HEADER -->
        <div class="section-header-racloop">
            <div class="small-text-medium uppercase colored-text">
                My Journeys
            </div>
            <h2 class="dark-text"><strong>Active</strong> Requests</h2>
            <div class="colored-line">
            </div>
        </div>
    </div>
</section>
 <div class="container">
 	<g:if test="${journeys?.size > 0}">
 		<g:each in="${journeys}" status="i" var="journeyInstance">
 			<article id="request-${i}" class="row grey-bg">
 				<div class="row">
 				
		             <div class="col-md-5 col-md-offset-1 text-left">
		                <div>
		                    <ul class="text-left">
		                        <li>
		                        	<g:if test = "${journeyInstance.isDriver == true}">
		                        		<span class="label label-primary">Car Owner</span> 
		                        	</g:if>
		                        	<g:else>
		                        		<span class="label label-primary">Ride Seeker</span> 
		                        	</g:else>
		                        	<i class="icon-basic-calendar"></i> <span><g:formatDate format="dd/MMM/yyyy" date="${journeyInstance.dateOfJourney}"/></span> <i class="icon-clock-alt"></i> <span><g:formatDate format="hh:mm a" date="${journeyInstance.dateOfJourney}"/></span>
		                        </li>
		                        <li><i class="icon-basic-geolocalize-01"></i> <strong>From :</strong> ${journeyInstance.fromPlace}</li>
		                        <li><i class="icon-basic-map"></i> <strong>To :</strong> ${journeyInstance.toPlace}</li>
		                        <li>
		                        	<g:link action="searchAgain"id="searchAgain"  params="[journeyId: journeyInstance.journeyId, indexName:journeyInstance.dateOfJourney.format(grailsApplication.config.grails.journeyIndexNameFormat), isDriver:journeyInstance.isDriver?true:false]">
					       				<button class="btn btn-primary">Search Again</button>
					       			</g:link>&nbsp; 
		                        	<button id="incoming-btn-1" data-requestId="1" class="btn btn-success incoming-btn">Incoming (${journeyInstance.incomingRequests.size()})</button> &nbsp; 
		                        	<button id="outgoing-btn-1" data-requestId="1" class="btn btn-info outgoing-btn">Outgoing (${journeyInstance.outgoingRequests.size()})</button> &nbsp; 
		                        	<a name ="delete" id="delete${i}" href="#"  data-target="#myModal" data-id="${journeyInstance.journeyId}">
					       				<button class="btn btn-danger">Delete</button>
					       			</a>
		                        </li>
		                        
		                    </ul>
		                </div>
		            </div>
	            </div>
	            <div id="incoming-requests-${i}" class="incoming-requests">
                    <div class="well  well-sm">
                   		<h4>Incoming Requests</h4>
               		</div>
	                   
                    <g:each in="${journeyInstance.incomingRequests}" status="k" var="matchedWorkflowInstance">
	                    <div class="row">
	                    	<div class="col-md-7 col-md-offset-2 text-left">
	                    		<h5>${matchedWorkflowInstance?.otherUser?.profile?.fullName}<g:if test = "${matchedWorkflowInstance.showContactInfo}"> &nbsp;&nbsp; <i class="icon-icon-mobile"></i> ${matchedWorkflowInstance.otherUser?.profile?.mobile}</g:if> </h5>
	                    		<ul class="text-left">
	                                <li>
	                                	<g:if test = "${matchedWorkflowInstance?.workflow?.isRequesterDriving == true}">
	                                		<span class="label label-primary">Car Owner</span> 
	                                	</g:if>
	                                	<g:else>
	                                		<span class="label label-primary">Ride Seeker</span> 
	                                	</g:else>
	                                	<span class="${matchedWorkflowInstance.state=='Accepted'?'label label-success':(matchedWorkflowInstance.state=='New'?'label label-info':(matchedWorkflowInstance.state=='Rejected'?'label label-info':'label-info'))}">${matchedWorkflowInstance.state}</span>
	                                </li>
	                                <li> <i class="icon-basic-calendar"></i> <span><g:formatDate format="dd/MMM/yyyy" date="${matchedWorkflowInstance.workflow.matchedDateTime}"/></span> <i class="icon-clock-alt"></i> <span><g:formatDate format="hh:mm a" date="${matchedWorkflowInstance.workflow.matchedDateTime}"/></span></li>
	                                <li><i class="icon-basic-geolocalize-01"></i> <strong>From :</strong>${matchedWorkflowInstance.workflow.matchedFromPlace}</li>
	                                <li><i class="icon-basic-map"></i> <strong>To :</strong>${matchedWorkflowInstance.workflow.matchedToPlace}</li>
	                                <li>
		                                <g:each in ="${matchedWorkflowInstance.actionButtons}" var="action">
											<g:if test = "${action=='Accept'}">
												<g:link action="acceptIncomingRequest" id="acceptIncomingRequest"  params="[workflowId: matchedWorkflowInstance.workflow.id]">
													<button class="btn btn-info">Accept</button>
												</g:link>
											</g:if>
											<g:if test = "${action=='Reject'}">
												<g:link action="rejectIncomingRequest" id="rejectIncomingRequest"  params="[workflowId: matchedWorkflowInstance.workflow.id]">
													<button class="btn btn-warning">Reject</button>
												</g:link>
											</g:if>
											<g:if test = "${action=='Cancel'}">
												<button class="btn btn-danger">Cancel</button>
											</g:if>
										</g:each>
	                                </li>
	                            </ul>
	                         </div> 
	                         <div class="col-md-1">
	                        	<g:if test = "${currentUser?.profile?.gravatarUri}">
	                            	<img src="${currentUser.profile.gravatarUri}?s=64" alt="profile image" class="img-thumbnail"> </img>
	                            </g:if>
	                        </div>  
	                   	</div>     
                    </g:each>
	             </div>
	             
	             <div id="outgoing-requests-${i}" class="outgoing-requests">
	             	<div class="well well-sm">
	             		<h4>Outgoing Requests</h4>
	                </div>
                	<g:each in="${journeyInstance.outgoingRequests}" status="j" var="requestWorkflowInstance">
                		<div class="row">
                   			<div class="col-md-7 col-md-offset-2 text-left">
	                		    <h5>${requestWorkflowInstance.otherUser?.profile?.fullName} <g:if test = "${requestWorkflowInstance.showContactInfo}"> &nbsp;&nbsp; <i class="icon-icon-mobile"></i> ${requestWorkflowInstance.otherUser?.profile?.mobile}</g:if> </h5>
	                            <ul class="text-left">
	                                <li>
		                                <g:if test = "${matchedWorkflowInstance?.workflow?.isRequesterDriving == true}">
	                                		<span class="label label-primary">Ride Seeker</span> 
	                                	</g:if>
	                                	<g:else>
	                                		<span class="label label-primary">Car Owner</span> 
	                                	</g:else>
		                                <span class="label label-info">${requestWorkflowInstance.state}</span>
	                                </li>
	                                <li> <i class="icon-basic-calendar"></i> <span><g:formatDate format="dd/MMM/yyyy" date="${requestWorkflowInstance.workflow.matchedDateTime}"/></span> <i class="icon-clock-alt"></i> <span><g:formatDate format="hh:mm a" date="${requestWorkflowInstance.workflow.matchedDateTime}"/></span></li>
	                                <li><i class="icon-basic-geolocalize-01"></i> <strong>From :</strong>${requestWorkflowInstance.workflow.matchedFromPlace}</li>
	                                <li><i class="icon-basic-map"></i> <strong>To :</strong>${requestWorkflowInstance.workflow.matchedToPlace}</li>
	                                <li>
	                                	<g:each in ="${requestWorkflowInstance.actionButtons}" var="action">
	   										<g:if test = "${action=='Accept'}">
	   											<button class="btn btn-success">Accept</button>
	   										</g:if>
	   										<g:if test = "${action=='Reject'}">
	   											<button class="btn btn-warning">Reject</button>
	   										</g:if>
	   										<g:if test = "${action=='Cancel'}">
	   											<g:link action="cancelOutgoingRequest" id="cancelOutgoingRequest"  params="[workflowId: requestWorkflowInstance.workflow.id]">
	   												<button class="btn btn-danger">Cancel</button>
	   											</g:link>
	   										</g:if>
	   									</g:each>
	                                </li>
	                            </ul>
                        	</div>
	                        <div class="col-md-1">
	                        	<g:if test = "${currentUser?.profile?.gravatarUri}">
	                            	<img src="${currentUser.profile.gravatarUri}?s=64" alt="profile image" class="img-thumbnail"> </img>
	                            </g:if>
	                        </div>
	                   </div>     
                	</g:each>
	             </div>
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