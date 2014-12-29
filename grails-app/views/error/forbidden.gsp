<html>
<head>
<meta name="layout" content="static" />
<title>Unauthorized Access</title>
<r:require module="core" />
<style>
  .center {text-align: center; margin-left: auto; margin-right: auto; margin-bottom: auto; margin-top: auto;}
</style>
</head>

<body>
	<div class="container">
		<div>
			<div class="jumbotron center">
				<h1>
					Unauthorized Access <small><font face="Tahoma" color="red">Error
							403</font></small>
				</h1>
				<br />
				<p>
					You are trying to access an unauthorise 
					 page. Use your browsers <b>Back</b> button to
					navigate to the page you have previously come from
				</p>
				<p>
					<b>Or</b>
				</p>
				<a href="${request.contextPath}/" class="standard-button"><i
					class="icon-home-house-streamline"></i> Take Me Home</a>
			</div>				
		</div>
	</div>
</body>
</html>