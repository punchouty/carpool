<html>
<head>
<meta name="layout" content="static" />
<title>Verify Mobile Number</title>
<r:require module="core" />
</head>
<body>
	<section class="white-bg" id="section10">
        <div class="container">
			<!-- SECTION HEADER -->
            <div class="section-header-racloop">
                <div class="small-text-medium uppercase colored-text">
                    Please check your mobile for SMS
                </div>
                <h2 class="dark-text">Mobile <strong>Verification</strong></h2>
                <div class="colored-line">
                </div>
            </div>
        </div>
    </section>
    <div class="container">
    	<div class="col-md-12">
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
	                  <strong>Success!</strong> <n:flashembed/> 
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
		<div class="container">
        <div class="col-md-8 col-md-offset-2">
            <!-- VERTICAL REGISTRATION FORM -->
            <div class="row">
            	<g:form controller="userSession" action="processMobileVerification" method="POST" class="form-inline" name="verificationForm">
		             <input type="text" id="mobile" name="mobile" value="${mobile}" class="form-control input-box" placeholder="Mobile used during Sign Up" required>
	                 <input type="text" id="verificationCode" name="verificationCode" class="form-control input-box" placeholder="Verification Code" >
	                 <button id="verify" class="btn standard-button" type="button" >Verify</button>
	                 <button id="sendSms" class="btn standard-button standard-red-button" type="button">Send SMS Again</button>
	                 <div id = "error" class="hidden">
		               	 <p class="dark-text small-text">
			               	 <span class="icon-close-alt2 red-text"></span>&nbsp;&nbsp;<span id="error-message" >Error!</span>
		               	 </p>
	               	 </div>
	               	 <div id = "errorCode" class="hidden">
		               	 <p class="dark-text small-text">
		               	 	<span class="icon-close-alt2 red-text"></span>&nbsp;&nbsp;<span id="error-message-code" >Error!</span>
		               	 </p>
	               	 </div>
		            <input type="hidden" id="formAction" name="formAction" value="verify">
              </g:form>
            </div>
        </div>
        </div>
    </div>
    <br>
	<script>
	$(function() {
		var phonePattren = /^[6789]\d{9}$/;
		var verificationCodePattren = /\d{6}$/;
		$('#sendSms').click(function() {	
			$('#form-error').fadeOut(500);
			var errorMessage = null;
			var errorMessageForCode = null;
			var mobile = $('#mobile').val();
			if(mobile) {
				if(!phonePattren.test(mobile)) {
					errorMessage = "Invalid phone number.";
				}
			}
			else {
				errorMessage = "Please provide mobile number for SMS verification.";
			}
			if(errorMessage != null) {
				$('#error-message').text(errorMessage);
				$('#form-error').fadeIn(1000);
				$("#error").attr("class", "show");
				$("#errorCode").attr("class", "hide");
			}
			else {
				$('#formAction').val('sendSms');
				 $('form[name=verificationForm]').submit();
			}
		});
		$('#verify').click(function(event) {	
			$('#form-error').fadeOut(500);
			var errorMessage = null;
			var errorMessageForCode = null;
			var mobile = $('#mobile').val();
			var verificationCode = $('#verificationCode').val();
			if(mobile) {
				if(!phonePattren.test(mobile)) {
					errorMessage = "Invalid phone number.";
				}
			}
			else {
				errorMessage = "Please provide mobile number for SMS verification.";
			}
			if(verificationCode) {
				if(!verificationCodePattren.test(verificationCode)) {
					errorMessageForCode = "Invalid verification code.";
				}
			}
			else {
				errorMessageForCode = "Please provide the Verification Code.";
			}
			if(errorMessage != null || errorMessageForCode !=null) {
				$('#error-message').text(errorMessage);
				$('#error-message-code').text(errorMessageForCode);
				$('#form-error').fadeIn(1000);
				if(errorMessage) {
					$("#error").attr("class", "show");
				}
				if(errorMessageForCode){
					$("#errorCode").attr("class", "show");
				}
				event.preventDefault();
			}
			else {
				$('#formAction').val('verifyMobile');
				$('form[name=verificationForm]').submit();
			}
		});
	});
	</script>
</body>
</html>