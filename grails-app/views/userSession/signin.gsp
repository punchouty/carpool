<html>
<head>
<meta name="layout" content="signin" />
<title>Sign In</title>
</head>
<body>
	<g:set var="isSignin" value="true" scope="request"/>	
	<%--<div class="row">
		<div class="col-md-8 jumbobox">
	        <h1 id="logo">Car Pool</h1>
	        <p class="lead">Share your Car, Taxi and Auto rides. Save money, Make friends and Contribute to Greener Environment.</p>
	         <a class="btn btn-large btn-primary" href="${request.contextPath}/signup">Sign up today</a>
             <div class="row">
				<div class="col-md-4">
				<br>
				    <h4><b>Save Money</b></h4>
				    <p>It is pocket friendly. Nothing better than some one sharing your commute cost. </p>
				</div>
				<div class="col-md-4">
				<br>
				    <h4><b>Environment Friendly</b></h4>
				    <p>Having fewer cars on the road means reduced air pollution and improved air quality. Benefits for generations to come. It also mean reduced traffic congestion that will save time for all of us.</p>
				</div>
				<div class="col-md-4">
				    <br>
				    <h4><b>Make New Friends</b></h4>
				    <p>Car pool provide you new avenue of friendships and company for your commute. Drive Together.</p>
				</div></br>
		      </div>
		</div>
		<div class="col-md-4 well">
			<h2>Sign In</h2>
			<g:if test="${flash.message != null && flash.message.length() > 0}">
			<div id="flash-message" class="alert alert-danger">
                <a class="close" data-dismiss="alert" href="#">×</a>
                <n:flashembed/>
            </div>
            </g:if>
			<g:form action="signin"  controller="auth" name="login-form" method="post">
				<input type="hidden" name="targetUri" value="${targetUri}" />
	            <div class="control-group">
					<label class="control-label">Email</label>
					<input type="text" name="username" id="username" placeholder="email" value="${user?.profile?.email?.encodeAsHTML()}" data-validation-required-message="Username is required field" class="form-control" required />
	      			<p class="help-block"></p>
	      		</div>
	      		
				<div class="control-group">
					<label class="control-label">Password</label>
					<input type="password" name="password" id="password" placeholder="Password" data-validation-required-message="Password is required field" class="form-control" required />
	      			<p class="help-block"></p>
	      		</div>
	            <label class="checkbox">
	            	<g:checkBox name="rememberme" value="${rememberme}" /> Remember Me
	            </label>
				<button type="submit" name="submit" class="btn btn-large btn-primary">Sign In</button>
			</g:form>    
			<br>       
            <a href="${request.contextPath}/password/forgot">Forgot Password</a></br>
		</div>
	</div>
--%>



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
					<div class="vertical-registration-form">
                    <!-- VERTICAL REGISTRATION FORM 
                    <div class="vertical-registration-form">

                        <form class="registration-form" id="contact-form">

                            <input type="email" id="cf-email" name="email" class="form-control input-box" placeholder="Email">

                            <input type="password" id="cf-password" name="name" class="form-control input-box" placeholder="Password">

                            <div class="checkbox  text-left">
                                <label>
                                    <input type="checkbox" value="">
                                    Remember me
                                </label>
                            </div>
                            <button class="btn btn-primary standard-button">Login</button>

                        </form>-->
                        
                        
                        
               <g:form action="signin"  controller="auth" name="login-form" method="post" class="registration-form" id="contact-form">
				<input type="hidden" name="targetUri" value="${targetUri}" />
	           
	            <input type="email" id="username" name="username"  class="form-control input-box" placeholder="Email" value="${user?.profile?.email?.encodeAsHTML()}" data-validation-required-message="Username is required field" required>
	      			<p class="help-block"></p>            		
				
					<input type="password" name="password" id="password" class="form-control input-box" placeholder="Password" data-validation-required-message="Password is required field"  required />
	      			<p class="help-block"></p>
	      		
	      		
	      		<div class="checkbox  text-left">
                                <label>
                                    <g:checkBox name="rememberme" value="${rememberme}" /> Remember Me
                                </label>
                            </div>	          
				<button type="submit" name="submit" class="btn standard-button btn-primary">Sign In</button>
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