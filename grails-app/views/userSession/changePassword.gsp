<html>
<head>
<meta name="layout" content="static" />
<title>Change Password</title>
<style>
  .center {text-align: center; margin-left: auto; margin-right: auto; margin-bottom: auto; margin-top: auto;}
</style>
</head>

<body>
	<div class="row">
		<g:if test="${flash.message != null && flash.message.length() > 0}">
			<div id="flash-message" class="alert alert-error">
		         <a class="close" data-dismiss="alert" href="#">×</a>
		         <h4>Error!</h4>
		         <n:flashembed/>            
		     </div>
		</g:if>
		<g:hasErrors bean="${user}">
			<div id="flash-error" class="alert alert-error">
				<a class="close" data-dismiss="alert" href="#">×</a>
				<h4>Error!</h4>
				<g:renderErrors bean="${user}" as="list" />
			</div>
		</g:hasErrors>	
	</div>
	
		<div class="col-md-5 ">
		
		<g:form controller="userSession" action="updatePassword" method="POST" class="form-horizontal">
	    <fieldset>
	          <legend>Change Password</legend>
		     		
	                
	                    <div class="form-group">
	                	<label class="col-sm-5 control-label" for="textinput">Old Password</label>
	                    <div class="col-sm-7">
	                        <input id="currentPassword" class="form-control" name="currentPassword" placeholder="Current Password" type="password" required></input>
							<p id="current-help-block" class="help-block"></p>
	                    </div> 
					</div>
					 <div class="form-group">
	                	<label class="col-sm-5 control-label" for="pass">New Password</label>
	                    <div class="col-sm-7">
	                        <input id="pass" class="form-control" name="pass" placeholder="New Password" type="password" required data-validation-regex-regex="((?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})" data-validation-regex-message="Password must be at least 8 character long, contains one digit, one lower case, one uppercase and one special character"></input>
							<p id="password-help-block" class="help-block"></p>
	                    </div>
					</div>
					 <div class="form-group">
	                	<label class="col-sm-5 control-label" for="passConfirm">Confirm New Password</label>
	                    <div class="col-sm-7">
	                        <input id="passConfirm" class="form-control" name="passConfirm" placeholder="Confirm New Password" type="password" required data-validation-match-match="pass" data-validation-match-message="Two passwords don't match"></input>
							<p id="confirm-help-block" class="help-block"></p>
	                    </div> 
					</div>
	                
	                <br />
	                <div class="center">
	                	<input id="change-help-block" type="submit" value="Change Password" class="btn btn-large btn-info" />
	                </div>
	            	
	        </fieldset>
	        </g:form>
	     </div>
    </div>
</body>
</html>