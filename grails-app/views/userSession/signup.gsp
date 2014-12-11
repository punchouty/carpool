<html>
<head>
<meta name="layout" content="signin" />
<title>Register</title>
<style>
  .center {text-align: center; margin-left: auto; margin-right: auto; margin-bottom: auto; margin-top: auto;}
</style>
</head>
<body>
	<g:set var="isSignup" value="true" scope="request"/>
<%--	<div class="row">
		<g:if test="${flash.message != null && flash.message.length() > 0}">
			<div id="flash-message" class="alert alert-info">
		         <a class="close" data-dismiss="alert" href="#">×</a>
		         <h4>Success!</h4>
		         <n:flashembed/>            
		     </div>
		</g:if>
		<g:hasErrors bean="${user}">
			<div id="flash-error" class="alert alert-danger">
				<a class="close" data-dismiss="alert" href="#">×</a>
				<h4>Error!</h4>
				<g:renderErrors bean="${user}" as="list" />
			</div>
		</g:hasErrors>	
	
		
		<div class="col-md-6"><span class="pull-left">
		
		 <g:form action="saveuser" name="signup-form" method="post"  class="form-horizontal">
			<fieldset>
	          <legend>Registration Form</legend>
               
				<!-- <div class="form-group" >
					<label class="col-sm-5 control-label" for="username">User name</label>
					<div class="col-sm-7">
						<input type="text" id="username" class="form-control" name="username" value="${fieldValue(bean: user, field: 'username')}" placeholder="Username" data-validation-regex-regex="[a-z0-9_.-]{5,15}" data-validation-regex-message="Minimum 5 chracters - matching a-z, 0-9, underscore, hyphen" class="span3" required/>
						<p id="username-help-block" class="help-block"></p> 
					</div>
				</div> -->
				<div class="form-group">
					<label class="col-sm-5 control-label" for="email">Email</label>
					<div class="col-sm-7">
						<input size="50" id="email" class="form-control" name="email" type="email" value="${user.profile?.email?.encodeAsHTML()}" placeholder="Email" required/>
						<p id="email-help-block" class="help-block"></p> 
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-5 control-label" for="pass">Password</label>
					<div class="col-sm-7">
						<input type="password" size="30" id="pass" class="form-control" name="pass" value="${user.pass?.encodeAsHTML()}" placeholder="Password" class="span3" data-validation-regex-regex="((?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})" data-validation-regex-message="Password must be at least 8 character long, contains one digit, one lower case, one uppercase and one special character" required />
						<p id="pass-help-block" class="help-block"></p> 
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-5 control-label" for="passConfirm">Confirm Password</label>
					<div class="col-sm-7">
						<input type="password" size="30" id="passConfirm" class="form-control" name="passConfirm" value="${user.passConfirm?.encodeAsHTML()}" placeholder="Confirm Password" required data-validation-match-match="pass" data-validation-match-message="Two passwords don't match" class="span3"/>
						<p id="passConfirm-help-block" class="help-block"></p>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-5 control-label" for="fullName">Name</label>
					<div class="col-sm-7">
						<input type="text" size="30" id="fullName" class="form-control" name="fullName" value="${user.profile?.fullName?.encodeAsHTML()}" placeholder="Full Name" maxlength="100" minlength="3" required class="span3"> 
						<p id="fullName-help-block" class="help-block"></p> 
					</div>
				</div>
				
			
						
				<div class="form-group">
					<label class="col-sm-5 control-label" for="sex">Sex</label>
					<div class="col-sm-7">
						<label class="radio-inline">
							<input type="radio" name="sex" value="male" <g:if test="${user?.profile?.isMale}">checked</g:if> />Male
						</label>
						<label class="radio-inline">
							<input type="radio" name="sex" value="female" <g:if test="${!user?.profile?.isMale}">checked</g:if> />Female
						</label>
						<p id="sex-help-block" class="help-block"></p>
					</div>
				</div>
				
				<div class="form-group">
					<label class="col-sm-5 control-label" for="mobile">Mobile Number</label>
					<div class="col-sm-7">
						<input type="text" size="30" id="mobile" class="form-control" name="mobile" value="${user.profile?.mobile}" pattern="^[6789]\d{9}$" required data-validation-pattern-message="Invalid Phone Number" placeholder="Mobile Number"/> 
						<p id="mobile-help-block" class="help-block"></p> 
					</div>
				</div>
				<div class="form-group" >
					<label class="col-sm-5 control-label" for="trems"></label>
					<div class="col-sm-7" >
						<input type="checkbox" id="terms"  name="terms" required data-validation-required-message="You must agree to the terms and conditions">
					  	I agree with the <a href="${request.contextPath}/terms" target="_blank">Terms</a> and <a href="${request.contextPath}/privacy" target="_blank">Privacy</a> 
						<p id="terms-help-block" class="help-block"></p> 
					</div>
				</div>
                <br />
	            <div class="center">
                	<input id="signup-button" type="submit" value="Sign Up" class="btn btn-large btn-info" />
                </div>
            	
	        </fieldset>
	        </g:form>
	        </span>
		</div>
		 <g:render template="/templates/shared/racloop" />
	</div>
	
--%>







<!-- CONTAINER -->
            <div class="container">
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
					<div class="row">
		<g:if test="${flash.message != null && flash.message.length() > 0}">			
		     <div class="alert alert-info alert-dismissible" role="alert">
                    <button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                    <strong>Success!</strong> <n:flashembed/>
                </div>		
		</g:if>
		<g:hasErrors bean="${user}">			
				 <div class="alert alert-danger alert-dismissible" role="alert">
                    <button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                    <strong>Warning!</strong> <g:renderErrors bean="${user}" as="list" />
                </div>	
		</g:hasErrors>	
                    <!-- VERTICAL REGISTRATION FORM -->
                    <div class="vertical-registration-form">                    
                      <g:form action="saveuser" name="signup-form" method="post"  class="registration-form" id="signup-form">

                            <input type="email" id="cf-email" name="email" class="form-control input-box" value="${user.profile?.email?.encodeAsHTML()}" placeholder="Email" required/>
							
                            <input type="password" id="cf-password" name="pass" class="form-control input-box" placeholder="Password" value="${user.pass?.encodeAsHTML()}"  required />
							
							<input type="password" id="cf-passConfirm" name="passConfirm" class="form-control input-box" placeholder="Confirm Password" value="${user.pass?.encodeAsHTML()}"  required />
						<%--data-validation-regex-regex="((?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})" data-validation-regex-message="Password must be at least 8 character long, contains one digit, one lower case, one uppercase and one special character" --%>
                            <input type="text" id="cf-name" name="fullName" class="form-control input-box" placeholder="Name" value="${user.profile?.fullName?.encodeAsHTML()}" placeholder="Full Name" maxlength="100" minlength="3" required />
                            
						    <input type="text" id="cf-mobile" name="mobile" class="form-control input-box" placeholder="Mobile" value="${user.profile?.mobile}" pattern="^[6789]\d{9}$" required data-validation-pattern-message="Invalid Phone Number" placeholder="Mobile Number">
                            
                                                        
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
                        

                        <!-- FORM SUBMIT SUCCESS / ERROR MESSAGES -->
                        <p class="email-success dark-text small-text"><span class="icon-check-alt2 colored-text"></span>Email sent seuccessfully</p>
                        <p class="email-error dark-text small-text"><span class="icon-close-alt2"></span>Error! Please check all fields filled correctly</p>
                        <!-- MAILCHIMP ALERTS
                <p class="mailchimp-success dark-text"><span class="icon-check-alt2 colored-text"></span>We sent the confirmation to your email</p>
                <p class="mailchimp-error dark-text"><span class="icon-close-alt2"></span>Error! Please check all fields filled correctly</p>
                -->
                    </div>
                </div>
            </div>
        </div>
    </header>


    <!-- =========================
     SECTION: CONTACT INFO  
    ============================== -->
    <div class="contact-info white-bg">
        <div class="container">

            <!-- CONTACT INFO -->
            <div class="row contact-links">

                <div class="col-sm-4">
                    <div class="icon-container">
                        <span class="icon-basic-mail colored-text"></span>
                    </div>
                    <a href="mailto:hey@designlab.co" class="strong">help@racloop.com</a>
                </div>

                <div class="col-sm-4">
                    <div class="icon-container">
                        <span class="icon-basic-geolocalize-01 colored-text"></span>
                    </div>
                    <a href="" class="strong">New Delhi, India</a>
                </div>

                <div class="col-sm-4">
                    <div class="icon-container">
                        <span class="icon-basic-tablet colored-text"></span>
                    </div>
                    <a href="tel:44-12-3456-7890" class="strong">+91 9780242630</a>
                </div>
            </div>

        </div>
    </div>












</body>
</html>