<html>
<head>
<meta name="layout" content="signin" />
<title>Sign In</title>
<r:require module="core" />
</head>
<body>
	<g:set var="isSignin" value="true" scope="request"/>	
	<!-- CONTAINER -->
    <div class="container">
		<div class="row">
			<g:if test="${flash.message != null && flash.message.length() > 0}">
				<div class="alert alert-danger alert-dismissible" role="alert">
	            	<button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
	                <n:flashembed/>
	            </div>		
	        </g:if>
		</div>
		<div class="row">
			<div class="col-md-7 text-left">
				<!-- HEADING AND BUTTONS -->
				<div class="intro-section intro-section-from">
					<!-- WELCOM MESSAGE -->
					<h1 class="intro white-text">Search, Connect and Carpool</h1>
					<p class="transparent-text">Make new friends and transform a dull ride into an unforgettable journey. Better way to get Social. If you are not member of CabShare community, click below to Sign Up.</p>
					<!-- BUTTON -->
					<div class="button hidden-xs">
		                <a href="${request.contextPath}/signup" class="btn btn-primary secondary-button">SIGN UP</a>
		            </div>
		            <!-- /END BUTTON -->
		        </div>
			</div>
			<div class="col-md-5 pull-right">
				<div class="vertical-registration-form">                   
		        <g:form action="signin"  controller="auth" name="login-form" method="post" class="registration-form" id="login-form">
					<input type="hidden" name="targetUri" value="${targetUri}" />
			        <input type="email" id="cf-email" name="username"  class="form-control input-box" placeholder="Email" value="${user?.profile?.email?.encodeAsHTML()}" data-validation-required-message="Email is required field" required>
					<input type="password" name="password" id="cf-password" class="form-control input-box" placeholder="Password" data-validation-required-message="Password is required field"  required />
			     	<div class="checkbox  text-left">
						<label>
						    <g:checkBox name="rememberme" value="${rememberme}" /> Remember Me
						</label>
					</div>	          
					<button type="submit" name="submit" class="btn standard-button btn-primary">Sign In</button>
				</g:form>
				   
				<p>
				<!-- ####### Disable Facebook login for time being##########
				<br>
				<label>OR Sign in with</label><br> 
				<facebook:loginLink appPermissions="${facebookContext.app.permissions}" returnUrl="${request.contextPath}/userSession/signinUsingFacebook?targetUri=${targetUri}" elementClass="btn standard-button btn-primary"><span class="icon-social-facebook"></span>Facebook</facebook:loginLink>
				<br>
				-->
				<br>
				 	
				<a href="${request.contextPath}/password/forgot" >Forgot Password</a> <br><a href="${request.contextPath}/verifyMobile"  class="red-text">Verify Mobile</a> 
				</p>
				
				<p id="form-success" class="dark-text small-text"><span class="icon-check-alt2 colored-text"></span> <span id="success-message">Thanks!</span></p>
                <p id="form-error" class="dark-text small-text"><span class="icon-close-alt2 red-text"></span> <span id="error-message" >Error!</span> </p> 
		     	</div>
			</div> 
		</div>
	</div>
	
</body>
</html>