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
			<g:if test="${flash.message != null && flash.message.length() > 0}">
			<div class="alert alert-error">
                <a class="close" data-dismiss="alert" href="#">×</a>
                <n:flashembed/>
            </div>
            </g:if>
			<g:form action="saveuser" name="signup-form" method="post">
				<label>Username</label>
				<input type="text" id="username" name="username" value="${fieldValue(bean: user, field: 'username')}" placeholder="Username" class="span3"/> 
				<label>Password</label>
				<input type="password" size="30" id="pass" name="pass" value="${user.pass?.encodeAsHTML()}" placeholder="Password" class="span3"/>
				<label>Password Confirm</label>
				<input type="password" size="30" id="passConfirm" name="passConfirm" value="${user.passConfirm?.encodeAsHTML()}" placeholder="Confirm Password" class="span3"/>
				<label>Name</label> 
				<input type="text" size="30" id="fullName" name="fullName" value="${user.profile?.fullName?.encodeAsHTML()}" placeholder="Full Name" class="span3"> 
				<label class="radio inline">
					<input type="radio" name="sex" value="${user?.profile?.isMale}">Male
				</label>
				<label class="radio inline">
					<input type="radio" name="sex" value="${user?.profile?.isMale}">Female
				</label>
				<label>Email Address</label>
				<input type="text" size="30" id="email" name="email" value="${user.profile?.email?.encodeAsHTML()}" placeholder="Email" class="span3"/>
				<label>Mobile 
				<input type="text" size="30" id="mobile" name="mobile" value="${user.profile?.mobile}" placeholder="Mobile Number" class="span3"/> 
				<label class="checkbox">
				  <input type="checkbox" value="terms">
				  I agree with the <a href="${request.contextPath}/terms" target="_blank">Terms</a> and <a href="${request.contextPath}/privacy" target="_blank">Privacy</a></label>
				</label>
				<input type="submit" value="Sign up" class="btn btn-primary pull-right">
			</g:form>
		</div>
	</div>
</body>
</html>