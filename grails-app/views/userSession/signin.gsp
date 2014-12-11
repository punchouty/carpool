<html>
<head>
<meta name="layout" content="signin" />
<title>Sign In</title>
</head>
<body>
	<g:set var="isSignin" value="true" scope="request"/>	

<!-- CONTAINER -->
            <div class="container">
                <div class="col-md-7 text-left">

                    <!-- HEADING AND BUTTONS -->
                    <div class="intro-section intro-section-from">

                        <!-- WELCOM MESSAGE -->
                        <h1 class="intro white-text">Search, Connect and Carpool</h1>

                        <p class="transparent-text">Make new friends and transform a dull ride into an unforgettable journey. Better way to Get Social. If you are not member of racloop community, click below to Sign Up.</p>

                        <!-- BUTTON -->
                        <div class="button hidden-xs">
                            <a href="${request.contextPath}/signup" class="btn btn-primary secondary-button">Register</a>
                        </div>
                        <!-- /END BUTTON -->

                    </div>

                </div>
                <div class="col-md-5 pull-right">
                <div class="row">
                 <g:if test="${flash.message != null && flash.message.length() > 0}">
         		   <div class="alert alert-info alert-dismissible" role="alert">
                    <button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                    <strong>Warning!</strong> <n:flashembed/>
                  </div>		
        		    </g:if>
                </div>
					<div class="vertical-registration-form">                   
                        
                        
                        
               <g:form action="signin"  controller="auth" name="login-form" method="post" class="registration-form" id="contact-form">
				
				<input type="hidden" name="targetUri" value="${targetUri}" />
	           
	            <input type="email" id="cf-email" name="username"  class="form-control input-box" placeholder="Email" value="${user?.profile?.email?.encodeAsHTML()}" data-validation-required-message="Username is required field" required>
				
				<input type="password" name="password" id="cf-password" class="form-control input-box" placeholder="Password" data-validation-required-message="Password is required field"  required />
	      		
	      		
	      		<div class="checkbox  text-left">
                                <label>
                                    <g:checkBox name="rememberme" value="${rememberme}" /> Remember Me
                                </label>
                            </div>	          
				<button type="submit" name="submit" class="btn standard-button btn-primary">Sign In</button>
			</g:form>    
                        

                        <!-- FORM SUBMIT SUCCESS / ERROR MESSAGES -->
                   
                        <p class="email-success dark-text small-text"><span class="icon-check-alt2 colored-text"></span>Email sent successfully</p>
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