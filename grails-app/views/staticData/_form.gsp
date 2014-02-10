<%@ page import="com.racloop.staticdata.StaticData" %>



<div class="fieldcontain ${hasErrors(bean: staticDataInstance, field: 'data', 'error')} ">
	<label for="data">
		<g:message code="staticData.data.label" default="Data" />
		
	</label>
	<g:textField name="data" value="${staticDataInstance?.data}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: staticDataInstance, field: 'key', 'error')} ">
	<label for="key">
		<g:message code="staticData.key.label" default="Key" />
		
	</label>
	<g:textField name="key" value="${staticDataInstance?.key}"/>
</div>

