<html>
<head>
<meta name="layout" content="static" />
<title>Error on server side</title>
<style>
  .center {text-align: center; margin-left: auto; margin-right: auto; margin-bottom: auto; margin-top: auto;}
</style>
</head>

<body>
	<div class="row">
		<div>
			<div class="jumbotron center">
				<h1>
					This is embarrassing :(
				</h1>
				<br />
				<p>
					We have error at server and We will try to resolve this soon.
				</p>
				<a href="${request.contextPath}/" class="btn btn-large btn-info"><i
					class="icon-home icon-white"></i> Take Me Home</a>
			</div>				
		</div>
	</div>
	<g:if env="development">
			<g:renderException exception="${exception}" />
	</g:if>
</body>
</html>
