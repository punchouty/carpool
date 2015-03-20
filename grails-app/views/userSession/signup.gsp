<html>
<head>
<meta name="layout" content="signin" />
<title>Sign Up</title>
<r:require module="core" />
</head>
<body>
	<g:set var="isSignup" value="true" scope="request"/>

	<!-- CONTAINER -->
    <div class="container">
		<div class="row">
			<g:if test="${flash.message != null && flash.message.length() > 0}">	
	        	<g:if test="${flash.type == 'error'}">			
			     <div class="alert alert-danger alert-dismissible" role="alert">
	                  <button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
	                  <strong>Error!</strong> <n:flashembed/> 
	             </div>
	            </g:if>
	            <g:else>
	            <div class="alert alert-info alert-dismissible" role="alert">
	                  <button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
	                  <strong>Success!</strong> <n:flashembed/> 
	             </div>
	            </g:else>
			</g:if>
	        <g:hasErrors bean="${user}">			
				 <div class="alert alert-danger alert-dismissible" role="alert">
                    <button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                    <strong>Error!</strong> <g:renderErrors bean="${user}" as="list" />
                </div>	
			</g:hasErrors>
		</div>
		<div class="row">
			<div class="col-md-7 text-left">
				<!-- HEADING AND BUTTONS -->
				<div class="intro-section intro-section-from">
					<!-- WELCOM MESSAGE -->
			        <h1 class="intro white-text">Signup to Save Money and Environment</h1>
					<p class="transparent-text">Every time you carpool you are not only saving money but also reducing traffic on roads. This is helping in reducing air and noise pollution as well as lowering the need of Oil. If you are already member of Racloop community, click on login.</p>
					<!-- BUTTON -->
					<div class="button hidden-xs">
					    <a href="${request.contextPath}/signin" class="btn btn-primary secondary-button">Login</a>
					</div>
			        <!-- /END BUTTON -->
				</div>
			</div>
			<div class="col-md-5 pull-right">
				<!-- VERTICAL REGISTRATION FORM -->
            	<div id="registration-form" class="vertical-registration-form">                    
                	<g:form action="saveuser" name="signup-form" method="post"  class="registration-form" id="signup-form">
						<input type="email" id="cf-email" name="email" class="form-control input-box" value="${user.profile?.email?.encodeAsHTML()}" placeholder="Email" required/>
						<input type="password" id="cf-password" name="pass" class="form-control input-box" placeholder="Password" value="${user.pass?.encodeAsHTML()}"  required />
						<input type="password" id="cf-passConfirm" name="passConfirm" class="form-control input-box" placeholder="Confirm Password" value="${user.pass?.encodeAsHTML()}"  required />
						<%--data-validation-regex-regex="((?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})" data-validation-regex-message="Password must be at least 8 character long, contains one digit, one lower case, one uppercase and one special character" --%>
                        <input type="text" id="cf-name" name="fullName" class="form-control input-box" placeholder="Name" value="${user.profile?.fullName?.encodeAsHTML()}" placeholder="Full Name" maxlength="100" minlength="3" required />
                        <input type="text" id="cf-mobile" name="mobile" class="form-control input-box" placeholder="Mobile Number" value="${user.profile?.mobile}" pattern="^[6789]\d{9}$" required title="10 digit mobile number">
                        <div class=" text-left">
                            <label class="radio-inline">
                                <input type="radio" name="sex" id="inlineRadio1" value="male" <g:if test="${user?.profile?.isMale}">checked</g:if> /> Male
                            </label>
                            <label class="radio-inline">
                                <input type="radio" name="sex" value="female" id="inlineRadio2" <g:if test="${!user?.profile?.isMale}">checked</g:if> /> Female
                            </label>
                        </div>
                        <p id="sex-help-block" class="help-block"></p>
                        <div class="checkbox  text-left">
	                        <label>
	                            <input type="checkbox" id="terms"  name="terms" required data-validation-required-message="You must agree to the terms and conditions">
	                            I agree to <a href="${request.contextPath}/terms" target="_blank">Terms</a> and <a href="${request.contextPath}/privacy" target="_blank">Privacy</a>
	                        </label>
                        </div>
                        <p id="terms-help-block" class="help-block"></p> 
                        <button id="signup-button" type="submit" class="btn btn-primary standard-button">Register</button>
                  	</g:form>
                  	<p>
                  	<!-- ####### Disable Facebook login for time being##########
                  	<br>
					<label>OR Sign up with</label><br> 
						<facebook:loginLink appPermissions="${facebookContext.app.permissions}" returnUrl="${request.contextPath}/userSession/signinUsingFacebook?targetUri=${targetUri}" elementClass="btn standard-button btn-primary"><span class="icon-social-facebook"></span>Facebook</facebook:loginLink>
					</p>	
					-->
					<p id="form-success" class="dark-text small-text"><span class="icon-check-alt2 colored-text"></span> <span id="success-message">Thanks!</span></p>
	                <p id="form-error" class="dark-text small-text"><span class="icon-close-alt2 red-text"></span> <span id="error-message" >Error!</span> </p> 
                </div>
            </div>
        </div>
	</div>
</body>
</html>