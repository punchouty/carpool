<html>
<head>
<meta name="layout" content="static" />
<title>My Profile</title>
</head>

<body>
	<div class="row">
		<g:if test="${flash.message != null && flash.message.length() > 0}">
			<div class="alert alert-error">
		         <a class="close" data-dismiss="alert" href="#">×</a>
		         <n:flashembed/>            
		     </div>
		</g:if>
		<g:hasErrors bean="${user}">
			<div class="alert alert-error">
				<a class="close" data-dismiss="alert" href="#">×</a>
				<h5 class='alert-heading'>
					<g:message code="nimble.label.error" />
				</h5>
				<g:renderErrors bean="${user}" as="list" />
			</div>
		</g:hasErrors>
		<div class="span9">
			<div class="span2"></div>
			<div class="well well-large span5">
                <h2>Edit Profile</h2>
                <h3>User Name : ${fieldValue(bean: user, field: 'username')}</h3>
                <g:form controller="userSession" action="editProfile" method="POST">
                <div class="control-group">
					<label class="control-label">Name</label>
					<input type="text" size="30" id="fullName" name="fullName" value="${user.profile?.fullName?.encodeAsHTML()}" placeholder="Full Name" maxlength="100" minlength="3" required class="span3"> 
					<p class="help-block"></p> 
				</div>
				<div class="control-group">
					<label class="radio inline">
						<input type="radio" name="sex" value="male" <g:if test="${user?.profile?.isMale}">checked</g:if> />Male
					</label>
					<label class="radio inline">
						<input type="radio" name="sex" value="female" <g:if test="${!user?.profile?.isMale}">checked</g:if> />Female
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
				
                <br />
                <input type="submit" value="Update Profile" class="btn btn-large" />
            	</g:form>
            </div>
            
			<div class="span2"></div>
		</div>
	</div>
</body>
</html>