<html>
<body>

	<p>
		<g:message code="nimble.template.mail.forgottenpassword.descriptive" />
	</p>
	<p>
		<g:message code="nimble.template.mail.forgottenpassword.instructions" />
	</p>
	<table>
	  	<tbody>
			  <tr>
			    <th><g:message code="nimble.label.username" /></th>
			    <td>${user.username}</td>
			  </tr>
			  <tr>
			    <th><g:message code="nimble.label.password" /></th>
			    <td>${user.pass}</td>
			  </tr>
		  </tbody>
	</table>
	<p>
		Click <a href="${baseUrl}/signin">here</a> to login into raC looP or go to ${baseUrl}/signin
	</p>

</body>
</html>