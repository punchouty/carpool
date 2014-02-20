<html>
   <head>
      <meta name="layout" content="static"/>
      <title>
         <g:message code="nimble.view.account.changepassword.complete.title" />
      </title>
   </head>
   <body>
      <h3>
         <g:message code="nimble.view.account.changepassword.complete.heading" />
         <span>
            <g:message code="nimble.view.account.changepassword.complete.descriptive" />
         </span>
      </h3>
      <div class="box-generic">
         <g:hasErrors bean="${user}">
			<div class="alert alert-error">
				<a class="close" data-dismiss="alert" href="#">×</a>
				<h5 class='alert-heading'>
					<g:message code="nimble.label.error" />
				</h5>
				<g:renderErrors bean="${user}" as="list" />
			</div>
		</g:hasErrors>
      </div>
   </body>
</html>