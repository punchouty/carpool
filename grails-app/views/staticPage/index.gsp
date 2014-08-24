<!DOCTYPE html>
<html>
<head>
<meta name="layout" content="static" />
<title>Welcome to raC looP - A Car pool Finder</title>
</head>
<body>
	<g:set var="isHome" value="true" scope="request" />
		<div class="jumbotron well">
	        <h1>Car Pool Finder</h1>${authenticatedUser?.id}
	        <p class="lead">Share your Car, Taxi and Auto rides. Save money, Make friends and Contribute to Greener Environment.</p>
	        <g:if test="${user == null}">
	        	<a class="btn btn-large btn-success" href="${request.contextPath}/signup">Sign Up Today!</a>
	        </g:if>
	        <g:else>
	        	<a class="btn btn-large btn-success" href="${request.contextPath}/etiquettes">Car Pool Etiquettes</a>
	        </g:else>
		</div>

      <hr>

      <div class="row marketing">
		<div class="col-md-4">
		    <h4>Save Money</h4>
		    <p>It is pocket friendly. Nothing better than some one sharing your commute cost. </p>
		</div>
		<div class="col-md-4">
		    <h4>Environment Friendly</h4>
		    <p>Having fewer cars on the road means reduced air pollution and improved air quality. Benefits for generations to come. It also mean reduced traffic congestion that will save time for all of us.</p>
		</div>
		<div class="col-md-4">
		    <h4>Make New Friends</h4>
		    <p>Car pool provide you new avenue of friendships and company for your commute. Drive Together.</p>
		</div>
      </div>
	
</body>
</html>
