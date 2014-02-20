<html>
<head>
<meta name="layout" content="static" />
<title>About raC looP</title>
</head>

<body>
	<div class="row">
		<g:if test="${flash.message != null && flash.message.length() > 0}">
			<div class="alert alert-error">
		         <a class="close" data-dismiss="alert" href="#">×</a>
		         <n:flashembed/>            
		     </div>
		</g:if>
		<n:errors bean="${user}"/>		
		<div class="span9">
			<div class="span2"></div>
			<div class="well well-large span5">
                <h2>Change Password</h2>
                <p>You can change your current password to something new below. To verify your login you'll need to also provide your current password.</p>
            	<g:form controller="userSession" action="updatePassword" method="POST">
                <div class="control-group">
                	<label class="control-label" for="currentPassword">Old Password</label>
                    <div class="controls">
                        <input id="currentPassword" name="currentPassword" placeholder="Current Password" type="password" required></input>
                    </div>
					<p class="help-block"></p> 
				</div>
				 <div class="control-group">
                	<label class="control-label" for="pass">New Password</label>
                    <div class="controls">
                        <input id="pass" name="pass" placeholder="New Password" type="password" data-validation-regex-regex="((?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})" data-validation-regex-message="Password must be at least 8 character long, contains one digit, one lower case, one uppercase and one special character"></input>
                    </div>
					<p class="help-block"></p> 
				</div>
				 <div class="control-group">
                	<label class="control-label" for="passConfirm">Repeat Password</label>
                    <div class="controls">
                        <input id="passConfirm" name="passConfirm" placeholder="Repeat Password" type="password" required data-validation-match-match="pass" data-validation-match-message="Two passwords don't match"></input>
                    </div>
					<p class="help-block"></p> 
				</div>
                
                <br />
                <input type="submit" value="Change Password" class="btn btn-large" />
            	</g:form>
            </div>
            
			<div class="span2"></div>
		</div>
	</div>
</body>
</html>