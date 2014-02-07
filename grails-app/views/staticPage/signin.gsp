<html>
<head>
<meta name="layout" content="static" />
<title>Sign In</title>
</head>
<body>
	<g:set var="isSignin" value="true" scope="request"/>
	<div class="row">
		<div class="span6 jumbotron">
	        <h1>Car Pool</h1>
	        <p class="lead">Share your Car, Taxi and Auto rides. Save money, Make friends and Contribute to Greener Environment.</p>
	        <g:link controller="staticPage" action="signup" class="btn btn-large btn-primary">
                Sign up today
             </g:link>
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
		</div>
		<div class="span3 well">
			<h2>Sign In</h2>
			<%-- TODO need to implement
          	<div class="alert alert-error">
                <a class="close" data-dismiss="alert" href="#">×</a>Incorrect Username or Password!
            </div>
            --%>
			<g:form action="signin"  controller="auth" name="login-form" method="post">
			<input type="hidden" name="targetUri" value="${targetUri}"/>
            <input type="text" name="username" id="username" placeholder="user@example.com">
			<input type="password" name="password" id="password" placeholder="password">
            <label class="checkbox">
            	<input type="checkbox" name="rememberme"> Remember Me
            </label>
			<button type="submit" name="submit" class="btn btn-large btn-primary">Sign in</button>
			</g:form>  
            <g:link controller="account" action="forgottenpassword">
                     Forgot Password
            </g:link>
		</div>
	</div>
</body>
</html>