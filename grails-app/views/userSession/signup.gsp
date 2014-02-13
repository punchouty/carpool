<html>
<head>
<meta name="layout" content="static" />
<title>Register</title>
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
				<div class="control-group">
					<label class="control-label">Username</label>
					<input type="text" id="username" name="username" value="${fieldValue(bean: user, field: 'username')}" placeholder="Username" data-validation-regex-regex="[a-z0-9_-]{5,15}" data-validation-regex-message="Minimum 5 chracters - matching a-z, 0-9, underscore, hyphen" class="span3"/>
					<p class="help-block"></p> 
				</div>
				<div class="control-group">
					<label class="control-label">Password</label>
					<input type="password" size="30" id="pass" name="pass" value="${user.pass?.encodeAsHTML()}" placeholder="Password" class="span3" maxlength="30" minlength="6" />
					<p class="help-block"></p> 
				</div>
				<div class="control-group">
					<label class="control-label">Confirm Password</label>
					<input type="password" size="30" id="passConfirm" name="passConfirm" value="${user.passConfirm?.encodeAsHTML()}" placeholder="Confirm Password" required data-validation-match-match="pass" data-validation-match-message="Two passwords don't match" class="span3"/>
					<p class="help-block"></p> 
				</div>
				<div class="control-group">
					<label class="control-label">Name</label>
					<input type="text" size="30" id="fullName" name="fullName" value="${user.profile?.fullName?.encodeAsHTML()}" placeholder="Full Name" maxlength="100" minlength="3" required class="span3"> 
					<p class="help-block"></p> 
				</div>
				<div class="control-group">
					<label class="radio inline">
						<input type="radio" name="sex" value="${user?.profile?.isMale}"/>Male
					</label>
					<label class="radio inline">
						<input type="radio" name="sex" value="${user?.profile?.isMale}"/>Female
					</label>
				</div>
				<div class="control-group">
					<label class="control-label">Email</label>
					<input size="50" id="email" name="email" type="email" value="${user.profile?.email?.encodeAsHTML()}" placeholder="Email" class="span3" required/>
					<p class="help-block"></p> 
				</div>
				<div class="control-group">
					<label class="control-label">Mobile Number <a href="${request.contextPath}/privacy" target="_blank">Privacy</a></label>
					<input type="text" size="30" id="mobile" name="mobile" value="${user.profile?.mobile}" pattern="^[6789]\d{9}$" required data-validation-pattern-message="Invalid Phone Number" placeholder="Mobile Number" class="span3"/> 
					<p class="help-block"></p> 
				</div>
				<div class="control-group">
					<label class="checkbox">
					  <input type="checkbox" id="terms" name="terms" required data-validation-required-message="You must agree to the terms and conditions">
					  I agree with the <a href="${request.contextPath}/terms" target="_blank">Terms</a> and <a href="${request.contextPath}/privacy" target="_blank">Privacy</a>
					</label>
                    <p class="help-block"></p>
				</div>
				<input type="submit" value="Sign up" class="btn btn-primary pull-right">
			</g:form>
		</div>
	</div>
</body>
</html>