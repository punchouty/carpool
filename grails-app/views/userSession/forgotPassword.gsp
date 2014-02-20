<html>
<head>
<meta name="layout" content="static" />
<title>About raC looP</title>
</head>

<body>
	<div class="row">
       	<g:if test="${flash.message != null && flash.message.length() > 0}">
			<div class="alert alert-error">
		         <a class="close" data-dismiss="alert" href="#">×</a>
		         <n:flashembed/>            
		     </div>
		</g:if>
		<div class="span9">
   			<div class="thumbnail center well well-small text-center">
                <h2>Forgot Password</h2>
                
                <p>Email address register with us.</p>
                
                <g:form controller="userSession" action="forgotPasswordProcess" method="POST">
	                <div class="control-group">
	                	<div class="input-prepend">
	                		<span class="add-on"><i class="icon-envelope"></i></span>
							<input name="email" id="email" type="email" placeholder="your@email.com" required>
	                    </div>
						<p class="help-block"></p> 
					</div>
                    <br />
                    <input type="submit" value="Send Email!" class="btn btn-large" />
              </g:form>
           	</div>    
       	</div>
	</div>
</body>
</html>