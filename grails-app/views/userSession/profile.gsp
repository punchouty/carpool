<html>
<head>
<meta name="layout" content="static" />
<title>My Profile</title>
<r:require module="core" />
</head>

<body>  
    <%--<div class="col-md-6"><span class="pull-left">
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

          <div class="form-group">
            <div class="col-sm-offset-2 col-sm-10">
              <div class="pull-right">
                <button type="submit" class="btn btn-default">Cancel</button>
                <button type="submit" class="btn btn-primary">Save</button>
              </div>
            </div>
          </div>

        </fieldset>
      </g:form>
      </span>
    </div><!-- /.col-lg-12 -->
    
   <g:render template="/templates/shared/racloop" />
  
</div><!-- /.row -->


--%>

  <section class="white-bg" id="section10">
        <div class="container">

            <!-- SECTION HEADER -->
            <div class="section-header-racloop">
                <div class="small-text-medium uppercase colored-text">
                    Personal information.
                </div>
                <h2 class="dark-text">Edit <strong>Profile</strong></h2>
                <div class="colored-line">
                </div>
            </div>
        </div>
    </section>
    <div class="container">
        <div class="col-md-6 col-md-offset-3">
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
	                  <n:flashembed/> 
	             </div>
	            </g:else>
			</g:if>
			<g:hasErrors bean="${user}">			
				<div class="alert alert-danger alert-dismissible" role="alert">
	                    <button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
	                    <strong>Warning!</strong> <g:renderErrors bean="${user}" as="list" />
	                </div>
			</g:hasErrors>
	       </div>
            <!-- VERTICAL REGISTRATION FORM -->
            <div class="row">
                  
             <g:form controller="userSession" action="editProfile" method="POST"  class="registration-form" name="edit-form">

                    <div class="text-left form-group">
					    <label for="cf-email">Email :</label>
						<input type="email" id="cf-email" name="email" class="form-control input-box zero-margin" placeholder="Email" type="email" value="${user.username.encodeAsHTML()}" placeholder="Email" readonly />
  					</div>
                   
                    <div class="text-left form-group">
					    <label for="cf-name">Name :</label>
						<input type="text" id="cf-name" name="fullName" class="form-control input-box zero-margin" placeholder="Name" value="${user.profile?.fullName?.encodeAsHTML()}"  maxlength="100" minlength="3" required />
					</div>
                   
                    <div class="text-left form-group">
					    <label for="cf-mobile">Mobile :</label>
					    <g:if test="${user?.profile?.mobile == '0000000000'}">
					    	<input type="text" id="cf-mobile" name="mobile" class="form-control input-box zero-margin" placeholder="Mobile" pattern="^[6789]\d{9}$" required data-validation-pattern-message="Invalid Phone Number">
					    </g:if>
					    <g:else>
					    	<input type="text" id="cf-mobile" name="mobile" class="form-control input-box zero-margin" placeholder="Mobile" value="${user.profile?.mobile}" pattern="^[6789]\d{9}$" required data-validation-pattern-message="Invalid Phone Number">
					    </g:else>
						
              		</div>
                   
              		<div class="text-left form-group">
					    <label for="cf-emergencyContact">Emergency Contact 1 :</label>
						<input type="text" id="cf-emergencyContactOne" name="emergencyContactOne" class="form-control input-box zero-margin" placeholder="Emergency Contact 1" value="${user.profile?.emergencyContactOne}" pattern="^[6789]\d{9}$" data-validation-pattern-message="Invalid Phone Number">
              		</div>
                   
              		<div class="text-left form-group">
					    <label for="cf-emergencyContact">Emergency Contact 2 :</label>
						<input type="text" id="cf-emergencyContactTwo" name="emergencyContactTwo" class="form-control input-box zero-margin" placeholder="Emergency Contact 2" value="${user.profile?.emergencyContactTwo}" pattern="^[6789]\d{9}$" data-validation-pattern-message="Invalid Phone Number">
              		</div>
                   
              		<div class=" text-left">
                        <label class="radio-inline">
                            <input type="radio" name="sex" id="inlineRadio1" value="male" <g:if test="${user?.profile?.isMale}">checked</g:if>> Male
                        </label>
                        <label class="radio-inline">
                            <input type="radio" name="sex" id="inlineRadio2" value="female" <g:if test="${!user?.profile?.isMale}">checked</g:if> /> Female
                        </label>
                    </div>
                    <button id="edit-profile-button" type="submit" value="Update Profile"  class="btn btn-primary standard-button">UPDATE</button>
                </g:form>
            </div>
        </div>
    </div>
    <br>
</body>
</html>