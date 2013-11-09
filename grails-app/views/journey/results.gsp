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
	<div class="row-fluid para well well-large">
		<div class="span9">
			You want to travel from <em>${currentJourney.fromPlace}</em> to <em>${currentJourney.toPlace}</em> 
			on <strong><g:formatDate format="dd MMM HH:mm" date="${currentJourney.dateOfJourney}"/></strong> 
		</div>
		<div class="span3">
			<g:link controller="journey" action="newJourney" class="btn btn-info">Save Request</g:link>&nbsp;<g:link controller="staticPage" action="search" class="btn">Cancel</g:link>
		</div>
	</div>
	<g:if test="${isDummyData}">
		<g:if test="${numberOfRecords != 0}">
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
						<td id="${i}_from"></td>
						<td id="${i}_to"></td>		
						<td><g:formatDate format="dd MMM HH:mm" date="${journeyInstance.dateOfJourney}"/></td>
						<g:if test="${currentJourney.isDriver}">
							<td><g:link action="pseudoRequest" id="pseudo_${i}" class="btn btn-success">Ask for Drive</g:link></td>		
						</g:if>
						<g:else>
							<td><g:link action="pseudoRequest" id="pseudo_${i}" class="btn btn-success">Request a Ride</g:link></td>
						</g:else>
						<input type="hidden" id="${i}_from_lattitude" value="${journeyInstance.fromLatitude}">
						<input type="hidden" id="${i}_from_longitude" value="${journeyInstance.fromLongitude}">
						<input type="hidden" id="${i}_to_lattitude" value="${journeyInstance.toLatitude}">
						<input type="hidden" id="${i}_to_longitude" value="${journeyInstance.toLongitude}">
					</tr>
				</g:each>
				</tbody>
			</table>
			<g:hiddenField name="dummy" value="true" />
		</div>
		</g:if>
		<g:else>
			<div class="row-fluid">
				<p class="text-error">Sorry your search did not match any results</p>
			</div>
		</g:else>
	</g:if>
	<g:else>
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
					<td><g:link action="request" id="${journeyInstance.id}" class="btn btn-success">Ask for Drive</g:link></td>		
					</g:if>
					<g:else>
						<td><g:link action="request" id="${journeyInstance.id}" class="btn btn-success">Request a Ride</g:link></td>
					</g:else>	
				</tr>
			</g:each>
			</tbody>
		</table>
		<g:hiddenField name="dummy" value="false" />
	</div>
	</g:else>	
	<g:hiddenField name="user_mobile" value="${currentUser?.profile?.mobile}" />
	<g:hiddenField name="user_email" value="${currentUser?.profile?.email}" />
	<g:hiddenField name="numberOfRecords" value="${numberOfRecords}" />
</body>
</html>