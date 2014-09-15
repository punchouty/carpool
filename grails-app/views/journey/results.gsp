<%@ page import="grails.converters.JSON" %>
<%@ page import="com.racloop.journey.workkflow.WorkflowState" %>
<html>
<head>
<meta name="layout" content="static" />
<title>Search Results -  ${searchResults.numberOfRecords} records returned</title>
<script type="text/javascript">
        $(document).ready(function () {
            $("#results").dataTable({
            	"aaSorting": []
            });
        });
        </script>
</head>
<body>
	<g:set var="isHome" value="true" scope="request" />
	<g:set var="currentJourney" value="${searchResults.currentJourney}"/>
	<g:if test="${flash.message}">
		<div class="message" role="status">${flash.message}</div>
	</g:if>
	<div class="row para well well-large">
		<div class="col-md-9">
			You want to travel from <em>${currentJourney.fromPlace}</em> to <em>${currentJourney.toPlace}</em> 
			on <strong><g:formatDate format="dd MMM HH:mm" date="${currentJourney.dateOfJourney}"/></strong> 
		</div>
		<div class="col-md-3">
			<g:if test ="${session?.currentJourney?.id }">
				<g:link controller="journey" action="redoSearch" class="btn btn-info">Search again</g:link>
			</g:if>
			<g:else>
				<g:link controller="journey" action="newJourney" class="btn btn-info">Save Request</g:link>&nbsp;<a href="${request.contextPath}/" class="btn">Cancel</a>
			</g:else>
			
		</div>
	</div>
	
		<g:if test="${searchResults.numberOfRecords != 0}">
		<div class="row">
			<table id="results" class="table table-striped" >
				<thead>
					<tr>
						<th>Name</th>
						<th>From</th>
						<th>To</th>
						<th>Time</th>
						<th></th>
					</tr>
				</thead>
				<tbody>
				<g:each in="${searchResults.matchedJourneys}" status="i" var="matchedResult">
				<g:set var="journeyInstance" value="${matchedResult.matchedJourney}"/>
					<tr>
						<td>${journeyInstance.name}</td>	
						<td id="${i}_from">${journeyInstance.fromPlace}</td>
						<td id="${i}_to">${journeyInstance.toPlace}</td>		
						<td><g:formatDate format="dd MMM HH:mm" date="${journeyInstance.dateOfJourney}"/></td>
						<g:if test = "${matchedResult.workflow}">
							<g:set var="workflow" value="${matchedResult.workflow}"/>
							<g:if test = "${workflow}">
								<td>
									<g:if test="${WorkflowState.INITIATED.state.equals(workflow.state)}">
										<g:if test = "${workflow && workflow.requestJourneyId == currentJourney.id }">
											<div class="btn-group">
												<button class="btn btn-primary">Requested</button>
												<button class="btn btn-primary dropdown-toggle"
													data-toggle="dropdown">
													<span class="caret"></span>
												</button>
												<ul class="dropdown-menu">
													<li><g:link action="cancelJourneyRequest" id="cancelled" params="[requestedJourneyId: currentJourney.id, matchedJourneyId: journeyInstance.id]">Cancel</g:link></li>
												</ul>
											</div>
										</g:if>
										<g:else>
											<div class="btn-group">
												<button class="btn btn-primary">New</button>
												<button class="btn btn-primary dropdown-toggle"
													data-toggle="dropdown">
													<span class="caret"></span>
												</button>
												<ul class="dropdown-menu">
													<li><g:link action="acceptIncomingRequest" id="accept" params="[workflowId: workflow.id, redirectToSearch: true]">Accept</g:link></li>
													<li><g:link action="rejectIncomingRequest" id="reject" params="[workflowId: workflow.id, redirectToSearch: true]">Reject</g:link></li>
												</ul>
											</div>
										</g:else>
									</g:if>
									<g:elseif test="${WorkflowState.ACCEPTED.state.equals(workflow.state)}">
										<div class="btn-group">
											<button class="btn btn-primary">Accepted</button>
											<button class="btn btn-primary dropdown-toggle"
												data-toggle="dropdown">
												<span class="caret"></span>
											</button>
											<ul class="dropdown-menu">
												<li><g:link action="cancelJourneyRequest" id="cancelled" params="[requestedJourneyId: currentJourney.id, matchedJourneyId: journeyInstance.id]">Cancel</g:link></li>
											</ul>
										</div>
									</g:elseif>
									<g:elseif test="${WorkflowState.REJECTED.state.equals(workflow.state)}">
										<div class="btn-group">
											<button class="btn btn-primary">Rejected</button>
											<button class="btn btn-primary dropdown-toggle"
												data-toggle="dropdown">
												<span class="caret"></span>
											</button>
										</div>
									</g:elseif>
									<g:elseif test="${WorkflowState.CANCELLED.state.equals(workflow.state) || WorkflowState.CANCELLED_BY_REQUESTER.state.equals(workflow.state)}">
										<div class="btn-group">
											<button class="btn btn-primary">Cancelled</button>
											<button class="btn btn-primary dropdown-toggle"
												data-toggle="dropdown">
												<span class="caret"></span>
											</button>
										</div>
									</g:elseif>
								</td>
							</g:if> 
						</g:if> 
						<g:else>
							<g:if test="${currentJourney.isDriver}">
								<td><g:link action="selectedJourney" id="request_${i}"  params="[matchedJourneyId: journeyInstance.id, dummy:searchResults.isDummyData]" class="btn btn-success">Ask for Drive</g:link></td>		
							</g:if>
							<g:else>
								<td><g:link action="selectedJourney" id="requestService_${i}" params="[matchedJourneyId: journeyInstance.id, dummy:searchResults.isDummyData]" class="btn btn-success">Request a Ride</g:link></td>
							</g:else>
						</g:else>
						<input type="hidden" id="${i}_from_lattitude" value="${journeyInstance.fromLatitude}">
						<input type="hidden" id="${i}_from_longitude" value="${journeyInstance.fromLongitude}">
						<input type="hidden" id="${i}_to_lattitude" value="${journeyInstance.toLatitude}">
						<input type="hidden" id="${i}_to_longitude" value="${journeyInstance.toLongitude}">
					</tr>
				</g:each>
				</tbody>
			</table>
		</div>
		</g:if>
		<g:else>
			<div class="row">
				<p class="text-error">Sorry your search did not match any results</p>
			</div>
		</g:else>
	<g:hiddenField name="dummy" value="${searchResults.isDummyData}" />
	<g:hiddenField name="user_mobile" value="${currentUser?.profile?.mobile}" />
	<g:hiddenField name="user_email" value="${currentUser?.profile?.email}" />
	<g:hiddenField name="numberOfRecords" value="${numberOfRecords}" />
</body>

</html>