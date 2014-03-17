<%@ page import="grails.converters.JSON" %>
<html>
<head>
<meta name="layout" content="main" />
<title>My Requested Journeys</title>
</head>
<body>
	<g:set var="isHome" value="true" scope="request" />
	<g:if test="${flash.message}">
	<div class="message" role="status">${flash.message}</div>
	</g:if>
	<g:if test="${numberOfRecords != 0}">
	<div class="row-fluid">
		<table id="results" class="table table-striped">
			<thead>
				<tr>
					<th>From</th>
					<th>To</th>
					<th>Date</th>
					<th>Driving</th>
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
					<td id="${i}_state">${workflowInstance.state}</td>	
					
				</tr>
			</g:each>
			</tbody>
		</table>
	</div>
	</g:if>
	<g:else>
		<div class="row-fluid">
			<p class="text-error">Sorry no active request found</p>
		</div>
	</g:else>
</body>

</html>