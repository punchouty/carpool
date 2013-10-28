<html>
<head>
<meta name="layout" content="main" />
<title>Search Results -  ${numberOfRecords} records returned</title>
</head>
<body>
	<g:set var="isHome" value="true" scope="request" />
	<g:if test="${flash.message}">
	<div class="message" role="status">${flash.message}</div>
	</g:if>
	<g:if test="${numberOfRecords == 0}">
	<div class="row-fluid para">
		<p class="muted">
				No matching records found
		</p>
	</div>
	<div class="row-fluid para well well-large">
		<div class="span9">
			You want to travel from <em>${currentJourney.fromPlace}</em> to <em>${currentJourney.toPlace}</em> 
			on <strong><g:formatDate format="dd MMM HH:mm" date="${currentJourney.dateOfJourney}"/></strong> 
		</div>
		<div class="span3">
			<g:link controller="journey" action="newJourney" class="btn btn-info">Save Request</g:link>&nbsp;<g:link controller="staticPage" action="search" class="btn">Cancel</g:link>
		</div>
	</div>
	</g:if>
	<g:else>
	<div class="row-fluid para well well-large">
		<div class="span9">
			You want to travel from <em>${currentJourney.fromPlace}</em> to <em>${currentJourney.toPlace}</em> 
			on <strong><g:formatDate format="dd MMM HH:mm" date="${currentJourney.dateOfJourney}"/></strong> 
		</div>
		<div class="span3">
			<g:link controller="journey" action="newJourney" class="btn btn-info">Save Request</g:link>&nbsp;<g:link controller="staticPage" action="search" class="btn">Cancel</g:link>
		</div>
	</div>
	<div class="row-fluid">
		<table id="results" class="table table-striped">
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
			<g:each in="${journeys}" status="i" var="journeyInstance">
				<tr>
					<td>${journeyInstance.name}</td>	
					<td>${journeyInstance.fromPlace}</td>
					<td>${journeyInstance.toPlace}</td>		
					<td><g:formatDate format="dd MMM HH:mm" date="${journeyInstance.dateOfJourney}"/></td>
					<g:if test="${currentJourney.isDriver}">
					<td><g:link action="show" id="${journeyInstance.id}" class="btn btn-success">Ask for Drive</g:link></td>		
					</g:if>
					<g:else>
						<td><g:link action="show" id="${journeyInstance.id}" class="btn btn-success">Request a Ride</g:link></td>
					</g:else>	
				</tr>
			</g:each>
			</tbody>
		</table>
	</div>
	</g:else>	
</body>
</html>