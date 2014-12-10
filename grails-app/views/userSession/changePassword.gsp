<html>
<head>
<meta name="layout" content="static" />
<title>Change Password</title>
<style>
  .center {text-align: center; margin-left: auto; margin-right: auto; margin-bottom: auto; margin-top: auto;}
</style>
</head>

<body>
	
			
	
 <section class="white-bg" id="section10">
        <div class="container">

            <!-- SECTION HEADER -->
            <div class="section-header-racloop">
                <div class="small-text-medium uppercase colored-text">
                    Keep Your password secret
                </div>
                <h2 class="dark-text">Change <strong>Password</strong></h2>
                <div class="colored-line">
                </div>
            </div>
        </div>
    </section>
    <div class="container">
        <div class="col-md-6 col-md-offset-3">
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
            
            
            
               
            </div>
            <!-- VERTICAL REGISTRATION FORM -->
            <div class="row">
			<g:form id="change-password-form"  name="change-password-form"  controller="userSession" action="updatePassword" method="POST" class="registration-form" >

                    <input type="password" id="currentPassword" name="currentPassword" class="form-control input-box" placeholder="Old Password" required />

                    <input type="password" id="pass" name="pass" class="form-control input-box" placeholder="New Password" required data-validation-regex-regex="((?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})" data-validation-regex-message="Password must be at least 8 character long, contains one digit, one lower case, one uppercase and one special character" />

                    <input type="password" id="passConfirm" name="passConfirm" class="form-control input-box" placeholder="Repeat" required data-validation-match-match="pass" data-validation-match-message="Two passwords don't match" />
							
							
                    <button class="btn btn-primary standard-button" id="change-help-block" type="submit">Change Password</button>
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






</body>
</html>