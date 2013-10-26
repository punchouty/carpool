<!DOCTYPE html>
<html>
<head>
<meta name="layout" content="signin" />
<title>Welcome to raC looP - A Car pool Finder</title>
</head>
<body>
	<g:set var="isHome" value="true" scope="request" />
		<div class="jumbotron">
	        <h1>Car Pool Finder</h1>
	        <p class="lead">Share your Car, Taxi and Auto rides. Save money, Make friends and Contribute to Greener Environment.</p>
	        <g:link controller="account" action="createuser" class="btn btn-large btn-primary">
                Sign up today
             </g:link>
		</div>

      <hr>

      <div class="row-fluid marketing">
		<div class="span4">
		    <h4>Save Money</h4>
		    <p>It is pocket friendly. Nothing better than some one sharing your commute cost. </p>
		</div>
		<div class="span4">
		    <h4>Environment Friendly</h4>
		    <p>Having fewer cars on the road means reduced air pollution and improved air quality. Benefits for generations to come. It also mean reduced traffic congestion that will save time for all of us.</p>
		</div>
		<div class="span4">
		    <h4>Make New Friends</h4>
		    <p>Car pool provide you new avenue of friendships and company for your commute. Drive Together.</p>
		</div>
      </div>
	
</body>
</html>
