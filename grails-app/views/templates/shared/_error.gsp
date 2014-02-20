<g:if test="${flash.message != null && flash.message.length() > 0}">
	<div class="alert alert-error">
         <a class="close" data-dismiss="alert" href="#">×</a>
         <n:flashembed/>            
     </div>
     <n:errors bean="${user}"/>
</g:if>
<g:hasErrors bean="${bean}">
	<div class="alert alert-error">
		<a class="close" data-dismiss="alert" href="#">×</a>
		<h5 class='alert-heading'>
			<g:message code="nimble.label.error" />
		</h5>
		<g:renderErrors bean="${bean}" as="list" />
	</div>
</g:hasErrors>
