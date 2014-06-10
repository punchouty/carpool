<html>
<head>
<meta name="layout" content="signin" />
<title>Sign In</title>
</head>
<body>
	<g:set var="isSignin" value="true" scope="request"/>	
	<div class="row">
		<div class="col-md-8 jumbobox">
	        <h1 id="logo">Car Pool</h1>
	        <p class="lead">Share your Car, Taxi and Auto rides. Save money, Make friends and Contribute to Greener Environment.</p>
	         <a class="btn btn-large btn-primary" href="${request.contextPath}/signup">Sign up today</a>
             <div class="row">
				<div class="col-md-4">
				<br>
				    <h4><b>Save Money</b></h4>
				    <p>It is pocket friendly. Nothing better than some one sharing your commute cost. </p>
				</div>
				<div class="col-md-4">
				<br>
				    <h4><b>Environment Friendly</b></h4>
				    <p>Having fewer cars on the road means reduced air pollution and improved air quality. Benefits for generations to come. It also mean reduced traffic congestion that will save time for all of us.</p>
				</div>
				<div class="col-md-4">
				    <br>
				    <h4><b>Make New Friends</b></h4>
				    <p>Car pool provide you new avenue of friendships and company for your commute. Drive Together.</p>
				</div></br>
		      </div>
		</div>
		<div class="col-md-4 well">
			<h2>Sign In</h2>
			<g:if test="${flash.message != null && flash.message.length() > 0}">
			<div id="flash-message" class="alert alert-danger">
                <a class="close" data-dismiss="alert" href="#">×</a>
                <n:flashembed/>
            </div>
            </g:if>
			<g:form action="signin"  controller="auth" name="login-form" method="post">
				<input type="hidden" name="targetUri" value="${targetUri}" />
	            <div class="control-group">
					<label class="control-label">Username</label>
					<input type="text" name="username" id="username" placeholder="Username" value="${username}" data-validation-required-message="Username is required field" class="form-control" required />
	      			<p class="help-block"></p>
	      		</div>
				<div class="control-group">
					<label class="control-label">Password</label>
					<input type="password" name="password" id="password" placeholder="Password" data-validation-required-message="Password is required field" class="form-control" required />
	      			<p class="help-block"></p>
	      		</div>
	            <label class="checkbox">
	            	<g:checkBox name="rememberme" value="${rememberme}" /> Remember Me
	            </label>
				<button type="submit" name="submit" class="btn btn-large btn-primary">Sign In</button>
			</g:form>    
			<br>       
            <a href="${request.contextPath}/password/forgot">Forgot Password</a></br>
		</div>
	</div>
</body>
</html>