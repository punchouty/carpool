<html>
<head>
<meta name="layout" content="static" />
<title>My Profile</title>
<style>
  .center {text-align: center; margin-left: auto; margin-right: auto; margin-bottom: auto; margin-top: auto;}
</style>
</head>

<body>
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
    <div class="col-md-6"><span class="pull-left">
      <g:form controller="userSession" action="editProfile" method="POST"  class="form-horizontal">
        <fieldset>

          <!-- Form Name -->
          <legend>Update Profile</legend>
          
         

          <!-- Text input-->
          
         <div class="form-group">
			<label class="col-sm-5 control-label" for="email">Email</label>
			<div class="col-sm-7">
				<input disabled size="50" id="email" class="form-control" name="email" type="email" value="${user.username.encodeAsHTML()}" placeholder="Email" required/>
				<p id="email-help-block" class="help-block"></p> 
			</div>
		</div>

          <!-- Text input-->
          <div class="form-group">
            <label class="col-sm-5 control-label" for="fullName">Name</label>
            <div class="col-sm-7">
              <input type="text"id="fullName" size="30" class="form-control" name="fullName" value="${user.profile?.fullName?.encodeAsHTML()}" placeholder="Full Name" maxlength="100" minlength="3" required>
              <p id="name-help-block" class="help-block"></p>
               
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
 
 

         
          <!-- Text input-->
          <div class="form-group">
            <label class="col-sm-5 control-label" for="mobile">Mobile Number <a href="${request.contextPath}/privacy" target="_blank">Privacy</a></label>
            <div class="col-sm-7">
              <input type="text"  id="mobile" class="form-control" name="mobile" value="${user.profile?.mobile}" pattern="^[6789]\d{9}$" required data-validation-pattern-message="Invalid Phone Number" placeholder="Mobile Number"/>
              <p id="mobile-help-block" class="help-block"></p> 
            </div>
          </div>
          
          <br />
	            <div class="center">
                	<input id="edit-profile-button" type="submit" value="Update Profile" class="btn btn-large btn-info" />
                </div>

          <%--<div class="form-group">
            <div class="col-sm-offset-2 col-sm-10">
              <div class="pull-right">
                <button type="submit" class="btn btn-default">Cancel</button>
                <button type="submit" class="btn btn-primary">Save</button>
              </div>
            </div>
          </div>

        --%></fieldset>
      </g:form>
      </span>
    </div><!-- /.col-lg-12 -->
    
   <g:render template="/templates/shared/racloop" />
  
</div><!-- /.row -->














</body>
</html>