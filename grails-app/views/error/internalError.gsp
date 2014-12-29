<html>
<head>
<meta name="layout" content="static" />
<title>Error on server side</title>
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
					This is embarrassing :(
				</h1>
				<br />
				<p>
					We have error at server and We will try to resolve this soon.
				</p>
				<a href="${request.contextPath}/" class="standard-button"><i
					class="icon-home-house-streamline"></i> Take Me Home</a>
			</div>				
		</div>
	</div>
	<g:if env="development">
	<div class="container">
		<g:renderException exception="${exception}" />
	</div>
	</g:if>
</body>
</html>
