<html>
<head>
<meta name="layout" content="static" />
<title>Sign In</title>
</head>
<body>
	<g:set var="isSignup" value="true" scope="request"/>
	<div class="row">
		<div class="span6 jumbotron">
			<h1>Car Pool</h1>
	        <p class="lead">Share your Car, Taxi and Auto rides. Save money, Make friends and Contribute to Greener Environment.</p>
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
			<h2>Sign Up</h2>
			<form>
				<label>First Name</label> <input type="text" name="firstname"
					class="span3"> <label>Last Name</label> <input type="text"
					name="lastname" class="span3"> <label>Email Address</label>
				<input type="email" name="email" class="span3"> <label>Username</label>
				<input type="text" name="username" class="span3"> <label>Password</label>
				<input type="password" name="password" class="span3"> <label><input
					type="checkbox" name="terms"> I agree with the <a href="#">Terms
						and Conditions</a>.</label> <input type="submit" value="Sign up"
					class="btn btn-primary pull-right">
				<div class="clearfix"></div>
			</form>
		</div>
	</div>
</body>
</html>