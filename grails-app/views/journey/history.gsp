<%@ page import="com.racloop.journey.workkflow.WorkflowState" %>
<html>
<head>
<meta name="layout" content="static" />
<title>My Past Journeys</title>
</head>
<section class="white-bg" id="section10">
    <div class="container">

        <!-- SECTION HEADER -->
        <div class="section-header-racloop">
            <div class="small-text-medium uppercase colored-text">
                My Journeys
            </div>
            <h2 class="dark-text"><strong>Past</strong> Requests</h2>
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
		                        	<g:link action="searchRouteAgain"id="searchRouteAgain"  params="[journeyId: journeyInstance.journeyId]">
					       				<button class="btn btn-primary">Search Again</button>
					       			</g:link>&nbsp; 
		                        	<button id="incoming-btn-${i}" data-requestId="${i}" class="btn btn-success incoming-btn">Incoming (${journeyInstance.incomingRequests.size()})</button> &nbsp; 
		                        	<button id="outgoing-btn-${i}" data-requestId="${i}" class="btn btn-info outgoing-btn">Outgoing (${journeyInstance.outgoingRequests.size()})</button> &nbsp;
				       				<button id ="review "class="btn btn-primary">Review</button>
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
	                                	<g:if test = "${matchedWorkflowInstance?.workflow?.isMatchedUserDriving == true}">
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
	                            </ul>
	                         </div> 
	                         <div class="col-md-1">
	                        	<g:if test = "${matchedWorkflowInstance.otherUser?.profile?.gravatarUri}">
	                            	<img src="${matchedWorkflowInstance.otherUser.profile.gravatarUri}?s=64" alt="profile image" class="img-thumbnail"> </img>
	                            </g:if>
	                            <g:else>
			                		<img src="http://www.gravatar.com/avatar/205e460b479c07710c08d50?s=64&d=mm" alt="profile image" class="img-thumbnail"> </img>
			                	</g:else>
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
		                                <g:if test = "${matchedWorkflowInstance?.workflow?.isMatchedUserDriving == true}">
	                                		<span class="label label-primary">Car Owner</span> 
	                                	</g:if>
	                                	<g:else>
	                                		<span class="label label-primary">Ride Seeker</span> 
	                                	</g:else>
		                                <span class="label label-info">${requestWorkflowInstance.state}</span>
	                                </li>
	                                <li> <i class="icon-basic-calendar"></i> <span><g:formatDate format="dd/MMM/yyyy" date="${requestWorkflowInstance.workflow.matchedDateTime}"/></span> <i class="icon-clock-alt"></i> <span><g:formatDate format="hh:mm a" date="${requestWorkflowInstance.workflow.matchedDateTime}"/></span></li>
	                                <li><i class="icon-basic-geolocalize-01"></i> <strong>From :</strong>${requestWorkflowInstance.workflow.matchedFromPlace}</li>
	                                <li><i class="icon-basic-map"></i> <strong>To :</strong>${requestWorkflowInstance.workflow.matchedToPlace}</li>
	                            </ul>
                        	</div>
	                        <div class="col-md-1">
	                        	<g:if test = "${requestWorkflowInstance.otherUser?.profile?.gravatarUri}">
	                            	<img src="${requestWorkflowInstance.otherUser.profile.gravatarUri}?s=64" alt="profile image" class="img-thumbnail"> </img>
	                            </g:if>
	                            <g:else>
			                		<img src="http://www.gravatar.com/avatar/205e460b479c07710c08d50?s=64&d=mm" alt="profile image" class="img-thumbnail"> </img>
			                	</g:else>
	                            
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
	
</body>
</html>