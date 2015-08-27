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
                    Please enter your Mobile number
                </div>
                <h2 class="dark-text">Add <strong>Mobile</strong></h2>
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
	            <div class="alert alert-success alert-dismissible" role="alert">
	                  <button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
	                  <n:flashembed/> 
	             </div>
	            </g:else>
			</g:if>
	        
		</div>
		<div class="container">
        <div class="col-md-8 col-md-offset-2">
            <!-- Enter Mobile Form -->
            <div class="row">
            	<g:form controller="userSession" action="addMobile" method="POST" class="form-inline" name="addMobileForm">
		             <input type="text" id="mobile" name="mobile" value="${mobile}" class="form-control input-box" placeholder="Enter Mobile" required>
	                 <!-- <input type="text" id="cf-userCode" name="userCode" class="form-control input-box" placeholder="Referral Code" value="${user?.userCode}" maxlength="10" minlength="3" />  -->
	                 <button id="addMobile" class="btn standard-button" type="button" >Add Mobile</button>
	                 
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
		            <input type="hidden" id="fullName" name="fullName" value="${fullName}">
		            <input type="hidden" id="email" name="email" value="${email }">
		            <input type="hidden" id="gender" name="gender" value="${gender }">
		            <input type="hidden" id="facebookId" name="facebookId" value="${ facebookId}">
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
		$('#addMobile').click(function(event) {	
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
				errorMessage = "Please provide your Mobile number to create the profile";
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
				
				$('form[name=addMobileForm]').submit();
			}
		});
	});
	</script>
</body>
</html>