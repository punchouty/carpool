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
	<div class="row-fluid para">
		<div class="span9">
			You want to travel from <em>${currentJourney.fromPlace}</em> to <em>${currentJourney.toPlace}</em> 
			on <strong><g:formatDate format="dd MMM HH:mm" date="${currentJourney.dateOfJourney}"/></strong> 
		</div>
		<div class="span3">
			<a href="#" class="btn btn-warning">Save Request</a>&nbsp;<g:link controller="staticPage" action="search" class="btn">Cancel</g:link>
		</div>
	</div>
	<div class="row-fluid para">
		<p class="muted">
			<g:if test="${numberOfRecords == 0}">
				No matching records found
			</g:if>
			<%-- 
			<g:else>
				About ${numberOfRecords} results found
			</g:else>
			--%>
		</p>
	</div>
	<div class="row-fluid">
		<g:if test="${numberOfRecords != 0}">
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
		</g:if>
	</div>
</body>
</html>