<%@ page import="com.racloop.journey.workkflow.WorkflowState" %>
<html>
<head>
<meta name="layout" content="static" />
<title>My Active Journeys</title>
</head>
<body>
	<g:set var="isActiveJourneys" value="true" scope="request" />
	<h2>Active Journeys</h2>
	<g:if test="${journeys?.size > 0}">
		<div class="row-fluid">
		<div class="accordion" id="accordion1">
		<g:each in="${journeys}" status="i" var="journeyInstance">
			<div class="accordion-group"> <!-- Repete Element -->
	   			<div class="accordion-heading ">
	                 	<a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion1" href= "#journey${i}">
	                 		<i class="icon-calendar"></i> <g:formatDate format="dd MMM HH:mm" date="${journeyInstance.journey.dateOfJourney}"/>
						| <i class="icon-bell"></i> Incoming Requests <strong>(${journeyInstance.matchedJourneys.size()})</strong>
					  	| <i class="icon-comment"></i> Outgoing Responses <strong>(${journeyInstance.requestedJourneys.size()})</strong>
					  	| Show Details
	                 	</a>        				
	   			</div> <!-- accordion-heading ENDS  -->
	   			<div id="journey${i}" class="accordion-body collapse">
	   				<div class="accordion-inner">
					   <div>
					     <blockquote>
					       <p>${journeyInstance.journey.isDriver?'Driving ':'Riding '}<span class="label label-info">Active</span></p>
					       <small><i class="icon-home"></i> From : <cite title="Source Title">${journeyInstance.journey.fromPlace}</cite></small>
					       <small><i class="icon-map-marker"></i> To : <cite title="Source Title">${journeyInstance.journey.toPlace}</cite></small>
					       <g:link action="searchAgain"id="searchAgain"  params="[journeyId: journeyInstance.journey.id, indexName:journeyInstance.journey.dateOfJourney.format(grailsApplication.config.grails.journeyIndexNameFormat), isDriver:journeyInstance.journey.isDriver?true:false]">
					       		<button class="btn btn-success"><i class="icon-refresh icon-white"></i> Search Again</button>
					       </g:link>
				       	   <button class="btn btn-danger"><i class="icon-trash icon-white"></i> Cancel</button>
					     </blockquote>
					   </div>
			   		<div>
				    <ul class="nav nav-tabs">
					  <li class="active"><a href="#journey${i}_notification"  data-toggle="tab"><i class="icon-bell"></i> Incoming Requests <span class="badge badge-info">${journeyInstance.matchedJourneys.size()}</span></a></li>
					  <li><a href="#journey${i}_responses"  data-toggle="tab"><i class="icon-comment"></i> Outgoing Responses <span class="badge badge-info">${journeyInstance.requestedJourneys.size()}</span></a></li>
					</ul>
					<div class="tab-content">
						<div class="tab-pane active in" id="journey${i}_notification">
           					<table class="table table-striped table-condensed">
	       						<thead>
	       							<tr>
	       								<th>Name <i class="icon-user"></i></th>
	       								<th>Route <i class="icon-road"></i></th>
	       								<th>Time <i class="icon-calendar"></i></th>
	       								<th>Status <i class=" icon-info-sign"></i></th>
	       								<th></th>
	       							</tr>
	       						</thead>
	       						<tbody>
	       							<g:each in="${journeyInstance.matchedJourneys}" status="k" var="matchedWorkflowInstance">
		       							<tr>
		       								<td>${matchedWorkflowInstance?.otherUser?.profile?.fullName}</td>
		       								<td>
		       									<div>
											      <blockquote>
											        <small><i class="icon-home"></i> From : <cite title="Source Title">${matchedWorkflowInstance.workflow.matchedFromPlace}</cite></small>
											        <small><i class="icon-map-marker"></i> To : <cite title="Source Title">${matchedWorkflowInstance.workflow.matchedToPlace}</cite></small>
											      </blockquote>
											    </div>
		       								</td>
		       								<td><g:formatDate format="dd MMM HH:mm" date="${matchedWorkflowInstance.workflow.matchedDateTime}"/></td>
		       								<td>
		       									<span class="${matchedWorkflowInstance.state=='Accepted'?'label label-success':(matchedWorkflowInstance.state=='New'?'label label-info':(matchedWorkflowInstance.state=='Rejected'?'label label-inverse':'label'))}">${matchedWorkflowInstance.state}</span>
		       									<g:if test = "${matchedWorkflowInstance.showContactInfo}">
		       										<p><strong>${matchedWorkflowInstance.otherUser?.profile?.mobile}</strong></p>
		       									</g:if>
		       								</td>
		       								<td>
		       									<g:each in ="${matchedWorkflowInstance.actionButtons}" var="action">
		       										<g:if test = "${action=='Accept'}">
		       											<g:link action="acceptIncomingRequest" id="acceptIncomingRequest"  params="[workflowId: matchedWorkflowInstance.workflow.id]">
		       												<button class="btn btn-success"><i class="icon-ok icon-white"></i> Accept</button>
		       											</g:link>
		       										</g:if>
		       										<g:if test = "${action=='Reject'}">
		       											<g:link action="rejectIncomingRequest" id="rejectIncomingRequest"  params="[workflowId: matchedWorkflowInstance.workflow.id]">
		       												<button class="btn btn-warning"><i class="icon-ban-circle icon-white"></i> Reject</button>
		       											</g:link>
		       										</g:if>
		       										<g:if test = "${action=='Cancel'}">
		       											<button class="btn btn-danger"><i class="icon-trash icon-white"></i> Cancel</button>
		       										</g:if>
		       									</g:each>
		       								</td>
		       							</tr>
		       						</g:each>
	       						</tbody>
		       				</table>
           				</div>
	  					<div class="tab-pane in" id="journey${i}_responses">
	  						<table class="table table-striped table-condensed">
	       						<thead>
	       							<tr>
	       								<th>Name <i class="icon-user"></i></th>
	       								<th>Route <i class="icon-road"></i></th>
	       								<th>Time <i class="icon-calendar"></i></th>
	       								<th>Status <i class=" icon-info-sign"></i></th>
	       								<th></th>
	       							</tr>
	       						</thead>
	       						<tbody>
			       					<g:each in="${journeyInstance.requestedJourneys}" status="j" var="requestWorkflowInstance">
		       							<tr>
		       								<td>${requestWorkflowInstance.otherUser?.profile?.fullName}</td>
		       								<td>
		       									<div>
											      <blockquote>
											        <small><i class="icon-home"></i> From : <cite title="Source Title">${requestWorkflowInstance.workflow.matchedFromPlace} </cite></small>
											        <small><i class="icon-map-marker"></i> To : <cite title="Source Title">${requestWorkflowInstance.workflow.matchedToPlace}</cite></small>
											      </blockquote>
											    </div>
		       								</td>
		       								<td><g:formatDate format="dd MMM HH:mm" date="${requestWorkflowInstance.workflow.matchedDateTime}"/></td>
		       								<td>
		       									<span class="label label-info">${requestWorkflowInstance.state}</span>
		       									<g:if test = "${requestWorkflowInstance.showContactInfo}">
		       										<p><strong>matchedWorkflowInstance.otherUser?.profile?.mobile</strong></p>
		       									</g:if>
		       								</td>
       										<td>
		       									<g:each in ="${requestWorkflowInstance.actionButtons}" var="action">
		       										<g:if test = "${action=='Accept'}">
		       											<button class="btn btn-success"><i class="icon-ok icon-white"></i> Accept</button>
		       										</g:if>
		       										<g:if test = "${action=='Reject'}">
		       											<button class="btn btn-warning"><i class="icon-ban-circle icon-white"></i> Reject</button>
		       										</g:if>
		       										<g:if test = "${action=='Cancel'}">
		       											<g:link action="cancelOutgoingRequest" id="cancelOutgoingRequest"  params="[workflowId: requestWorkflowInstance.workflow.id]">
		       												<button class="btn btn-danger"><i class="icon-trash icon-white"></i> Cancel</button>
		       											</g:link>
		       										</g:if>
		       										
		       									</g:each>
		       								</td>
		       							</tr>
		       						</g:each>
		       					</tbody>
			       				</table>
	           					</div>
	           				</div>
						</div>
	   				</div>
	   			</div> <!-- accordion-body ENDS  -->
	   		</div><!-- accordian-group ENDS -->
   		</g:each>
   		</div><!-- accordian-group ENDS -->
   		
   		</div><!-- accordian ENDS -->
		</g:if>
		<g:else>
			<div class="well">
				<h4>No Records Found</h4>
			</div>
		</g:else>
</body>
</html>