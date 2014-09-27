<html>
<head>
<meta name="layout" content="static" />
<title>Forgot Password</title>
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
		<div class="jumbotron center">
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
</body>
</html>