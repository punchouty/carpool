
<html>

<head>
  <meta name="layout" content="static"/>
  <title><g:message code="nimble.view.account.registeraccount.complete.title" /></title>
</head>

<body>

	<h2><g:message code="nimble.view.account.registeraccount.complete.heading" /></h2>
	<p>
	  Please check your mail and activate your account from there.
	</p>
	<div class="jumbotron">
	        <h1>Car Pool Finder</h1>
	        <p class="lead">Share your Car, Taxi and Auto rides. Save money, Make friends and Contribute to Greener Environment.</p>
	        <g:link controller="account" action="createuser" class="btn btn-large btn-primary">
                Sign up today
             </g:link>
		</div>
	
	<%-- 
	<g:if test="${useractive}">
		<p>
			<g:message code="nimble.view.account.registeraccount.complete.sentemail" />
	    </p>
	</g:if>
	<g:else>
		<p>
			<a href="${createLink(uri:'/')}">login</a> page
		</p>
	</g:else>
	--%>
</body>

</html>