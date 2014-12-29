<html>
<head>
<meta name="layout" content="static" />
<title>Change Password</title>
<r:require module="core" />
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
				<g:form id="change-password-form"  name="change-password-form"  controller="userSession" action="updatePassword" method="POST" class="registration-form" >
	
	                    <input type="password" id="currentPassword" name="currentPassword" class="form-control input-box" placeholder="Old Password" required />
	
	                    <input type="password" id="pass" name="pass" class="form-control input-box" placeholder="New Password" />
	
	                    <input type="password" id="passConfirm" name="passConfirm" class="form-control input-box" placeholder="Repeat"  />
								
								
	                    <button class="btn btn-primary standard-button" id="change-help-block" type="submit">Change Password</button>
	            </g:form>
	        </div>
        </div>
    </div>
    <br>
</body>
</html>