<%@ page import="grails.converters.JSON" %>
<html>
<head>
<meta name="layout" content="main" />
<title>My Requested Journeys</title>
<r:require module="core" />
</head>
<body>
	<g:set var="isHome" value="true" scope="request" />
	<g:if test="${flash.message}">
	<div class="message" role="status">${flash.message}</div>
	</g:if>
	<g:if test="${numberOfRecords != 0}">
	<div class="row">
		<table id="results" class="table table-striped">
			<thead>
				<tr>
					<th>My From Place</th>
					<th>My To Place</th>
					<th>My Requested Date</th>
					<th>Am I Driving</th>
					<th>Matched From Place</th>
					<th>Matched To Place</th>
					<th>Matched Date</th>
					<th>State</th>
				</tr>
			</thead>
			<tbody>
			<g:each in="${workflows}" status="i" var="workflowInstance">
				<tr>
					<td id="${i}_from">${workflowInstance.requestedFromPlace}</td>
					<td id="${i}_to">${workflowInstance.requestedToPlace}</td>		
					<td><g:formatDate format="dd MMM HH:mm" date="${workflowInstance.requestedDateTime}"/></td>
					<g:if test="${workflowInstance.isRequesterDriving}">
						<td id="${i}_driving">Yes</td>			
					</g:if>
					<g:else>
						<td id="${i}_driving">No</td>
					</g:else>
					<td id="${i}_matchedfrom">${workflowInstance.matchedFromPlace}</td>
					<td id="${i}_macthedto">${workflowInstance.matchedToPlace}</td>		
					<td><g:formatDate format="dd MMM HH:mm" date="${workflowInstance.matchedDateTime}"/></td>
					<td id="${i}_state">${workflowInstance.state}</td>	
					
				</tr>
			</g:each>
			</tbody>
		</table>
	</div>
	</g:if>
	<g:else>
		<div class="row">
			<p class="text-error">Sorry matching request found</p>
		</div>
	</g:else>
</body>

</html>