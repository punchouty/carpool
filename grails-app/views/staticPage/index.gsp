<!DOCTYPE html>
<html>
<head>
<meta name="layout" content="signin" />
<title>Welcome to raC looP - A Car pool Finder</title>
<style type="text/css">
      body {
        padding-top: 20px;
        padding-bottom: 40px;
      }

      /* Custom container */
      .container-narrow {
        margin: 0 auto;
        max-width: 700px;
      }
      .container-narrow > hr {
        margin: 30px 0;
      }

      /* Main marketing message and sign up button */
      .jumbotron {
        margin: 60px 0;
        text-align: center;
      }
      .jumbotron h1 {
        font-size: 72px;
        line-height: 1;
      }
      .jumbotron .btn {
        font-size: 21px;
        padding: 14px 24px;
      }

      /* Supporting marketing content */
      .marketing {
        margin: 25px 0;
      }
      .marketing p + h4 {
        margin-top: 28px;
      }
</style>
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
