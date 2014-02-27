<html>
<head>
<meta name="layout" content="static" />
<title>Forget Password</title>
<style>
  .center {text-align: center; margin-left: auto; margin-right: auto; margin-bottom: auto; margin-top: auto;}
</style>
</head>

<body>
	<div class="row">
       	<g:if test="${flash.message != null && flash.message.length() > 0}">
			<div id="flash-error" class="alert alert-block">
		         <a class="close" data-dismiss="alert" href="#">×</a>
		         <h4>Wrong Email!</h4>
		         <n:flashembed/>            
		     </div>
		</g:if>
		<div class="hero-unit center">
                <h1>Forgot Password</h1>
                <br/>
                <p>Email address register with us.</p>
                
                <g:form controller="userSession" action="forgotPasswordProcess" method="POST">
	                <div class="control-group">
	                	<div class="input-prepend">
	                		<span class="add-on"><i class="icon-envelope"></i></span>
							<input name="email" id="email" type="email" placeholder="your@email.com" required>
	                    </div>
						<p id="email-help-block" class="help-block"></p> 
					</div>
                    <br />
                    <input type="submit" value="Send Email!" class="btn btn-large btn-info" />
              </g:form>
       	</div>
	</div>
</body>
</html>