<html>
<head>
<meta name="layout" content="static" />
<title>Register</title>
<style>
  .center {text-align: center; margin-left: auto; margin-right: auto; margin-bottom: auto; margin-top: auto;}
</style>
</head>
<body>
	<g:set var="isSignup" value="true" scope="request"/>
	<div class="row">
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
	
		
		<div class="col-md-5">
		
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
		</div>
	</div>
	
</body>
</html>