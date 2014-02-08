<html>
<head>
<meta name="layout" content="static" />
<title>About raC looP</title>
</head>

<body>
	<div class="row">
        <div class="span9">
    		<div class="thumbnail center well well-small text-center">
                <h2>Forgot Password</h2>
                
                <p>Email Address regiester with us.</p>
                
                <g:form controller="userSession" action="forgotPasswordProcess" method="POST">
                    <div class="input-prepend"><span class="add-on"><i class="icon-envelope"></i></span>
                        <input type="text" name="email" id="email" placeholder="your@email.com">
                    </div>
                    <br />
                    <input type="submit" value="Send Email!" class="btn btn-large" />
              </g:form>
            </div>    
        </div>
	</div>
</body>
</html>