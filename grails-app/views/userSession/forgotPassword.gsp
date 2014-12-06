<html>
<head>
<meta name="layout" content="static" />
<title>Forgot Password</title>
<style>
  .center {text-align: center; margin-left: auto; margin-right: auto; margin-bottom: auto; margin-top: auto;}
</style>
</head>

<body>
	
		<%--<div class="jumbotron center">
                <h1>Forgot Password</h1>
                <br/>
                <p>Email address registered with us.</p>
                
                <g:form controller="userSession" action="forgotPasswordProcess" method="POST">
	                <div class="form-group">
	                	<div class="input-group  col-md-4 col-md-offset-4">
	                		<span class="input-group-addon"><i class="glyphicon glyphicon-envelope"></i></span>
							<input name="email" id="email" class="form-control" type="email" placeholder="your@email.com" required>
	                    </div>
						<p id="email-help-block" class="help-block"></p> 
					</div>
                    <br />
                    <input type="submit" value="Send Email!" class="btn btn-large btn-info" />
              </g:form>
       	</div>
	</div>
--%>







<section class="white-bg" id="section10">
        <div class="container">

            <!-- SECTION HEADER -->
            <div class="section-header-racloop">
                <div class="small-text-medium uppercase colored-text">
                    Please provide email register with us
                </div>
                <h2 class="dark-text">Forgot <strong>Password</strong></h2>
                <div class="colored-line">
                </div>
            </div>
        </div>
    </section>
    <div class="container">
        <div class="col-md-6 col-md-offset-3">
            <div class="row">
            <g:if test="${flash.message != null && flash.message.length() > 0}">
			<div class="alert alert-danger alert-dismissible" role="alert">
                    <button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                    <strong>Wrong Email!</strong> <n:flashembed/> 
                </div>
	    	</g:if>   
         </div>
            
            <!-- VERTICAL REGISTRATION FORM -->
            <div class="row">
             <g:form controller="userSession" action="forgotPasswordProcess" method="POST" class="registration-form" id="contact-form">
                    <input type="email" id="email" name="email" class="form-control input-box" placeholder="Email" required>
                    <button class="btn btn-primary standard-button" type="submit" >Forgot Password</button>
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