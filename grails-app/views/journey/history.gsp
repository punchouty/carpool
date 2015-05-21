<%@ page import="com.racloop.journey.workkflow.WorkflowState" %>
<html>
<head>
<meta name="layout" content="dynamic" />
<title>My Past Journeys</title>
<r:require module="core" />
</head>
<body>
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
	<g:if test="${messageMap?.message}">
		<div class="alert alert-success">			
		  <button type="button" class="close" data-dismiss="alert">&times;</button>
		  ${messageMap?.message}
		</div>
	</g:if>	
	<g:if test="${messageMap?.error}">
		<div class="alert alert-danger">			
		  <button type="button" class="close" data-dismiss="alert">&times;</button>
		  ${messageMap?.error}
		</div>
	</g:if>
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
		                        <li><i class="icon-basic-map"></i> <strong>To :</strong> ${journeyInstance.to}</li>
		                        <li>
		                        	<g:link action="searchRouteAgain"id="searchRouteAgain"  class="btn btn-warning" params="[journeyId: journeyInstance.journeyId]">
					       				<i class="fa fa-search"></i> Search Again
					       			</g:link>&nbsp; 
		                        	<button id="incoming-btn-${i}" data-requestId="${i}" class="btn btn-success incoming-btn"><i class="fa fa-download"></i> Incoming (${journeyInstance.incomingRequests.size()}) <span class="caret"></span></button> &nbsp; 
		                        	<button id="outgoing-btn-${i}" data-requestId="${i}" class="btn btn-info outgoing-btn"><i class="fa fa-upload"></i> Outgoing (${journeyInstance.outgoingRequests.size()}) <span class="caret"></span></button> &nbsp;
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
	                    		<h5>${matchedWorkflowInstance?.otherUser?.profile?.fullName?:matchedWorkflowInstance?.workflow?.requestUser}<g:if test = "${matchedWorkflowInstance.showContactInfo}"> &nbsp;&nbsp; <i class="icon-icon-mobile"></i> ${matchedWorkflowInstance.otherUser?.profile?.mobile}</g:if> </h5>
	                    		<ul class="text-left">
	                                <li>
	                                	<g:if test = "${matchedWorkflowInstance?.workflow?.isMatchedUserDriving == true}">
	                                		<span class="label label-primary">Car Owner</span> 
	                                	</g:if>
	                                	<g:else>
	                                		<span class="label label-primary">Ride Seeker</span> 
	                                	</g:else>
	                                	<span class="${matchedWorkflowInstance.state=='Accepted'?'label label-success':(matchedWorkflowInstance.state=='New'?'label label-info':(matchedWorkflowInstance.state=='Rejected'?'label label-info':'label label-info'))}">${matchedWorkflowInstance.state}</span>
	                                </li>
	                                <li> <i class="icon-basic-calendar"></i> <span><g:formatDate format="dd/MMM/yyyy" date="${matchedWorkflowInstance.workflow.requestedDateTime}"/></span> <i class="icon-clock-alt"></i> <span><g:formatDate format="hh:mm a" date="${matchedWorkflowInstance.workflow.requestedDateTime}"/></span></li>
	                                <li><i class="icon-basic-geolocalize-01"></i> <strong>From :</strong>${matchedWorkflowInstance.workflow.requestedFromPlace}</li>
	                                <li><i class="icon-basic-map"></i> <strong>To :</strong>${matchedWorkflowInstance.workflow.requestedToPlace}</li>
	                                <g:if test = "${matchedWorkflowInstance?.workflow?.state == 'Accepted'}">
		                                <li>
											<g:link action="loadReviewPage" controller ="review" params="[workflowId: matchedWorkflowInstance.workflow.id]">
						       					<button class="btn btn-primary">Review</button>
						       				</g:link>
		                                </li>
	                                </g:if>
	                            </ul>
	                         </div> 
	                         <div class="col-md-1">
	                        	<g:if test = "${matchedWorkflowInstance.otherUser?.profile?.gravatarUri}">
	                            	<img src="${matchedWorkflowInstance.otherUser.profile.gravatarUri}" alt="profile image" class="img-thumbnail"> </img>
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
	                		    <h5>${requestWorkflowInstance.otherUser?.profile?.fullName?:requestWorkflowInstance?.workflow?.matchingUser} <g:if test = "${requestWorkflowInstance.showContactInfo}"> &nbsp;&nbsp; <i class="icon-icon-mobile"></i> ${requestWorkflowInstance.otherUser?.profile?.mobile}</g:if> </h5>
	                            <ul class="text-left">
	                                <li>
		                                <g:if test = "${requestWorkflowInstance?.workflow?.isMatchedUserDriving == true}">
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
	                                <g:if test = "${requestWorkflowInstance?.workflow?.state == 'Accepted'}">
		                                <li>
											<g:link action="loadReviewPage" controller ="review" params="[workflowId: requestWorkflowInstance.workflow.id]">
						       					<button class="btn btn-primary">Review</button>
						       				</g:link>
		                                </li>
	                                </g:if>
	                            </ul>
                        	</div>
	                        <div class="col-md-1">
	                        	<g:if test = "${requestWorkflowInstance.otherUser?.profile?.gravatarUri}">
	                            	<img src="${requestWorkflowInstance.otherUser.profile.gravatarUri}" alt="profile image" class="img-thumbnail"> </img>
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