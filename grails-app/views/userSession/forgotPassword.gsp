<html>
<head>
<meta name="layout" content="static" />
<title>Forgot Password</title>
<r:require module="core" />
</head>
<body>
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
	        
            
            <!-- VERTICAL REGISTRATION FORM -->
            <div class="row">
             <g:form controller="userSession" action="forgotPasswordProcess" method="POST" class="registration-form" id="contact-form">
                    <input type="email" id="email" name="email" class="form-control input-box" placeholder="Email used during Sign Up" required>
                    <button class="btn btn-primary standard-button" type="submit" >Forgot Password</button>
              </g:form>
            </div>
        </div>
    </div>
    <br>

</body>
</html>