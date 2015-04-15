<html>
<head>
<meta name="layout" content="static" />
<title>Query</title>
<r:require module="core" />
</head>


  <section class="white-bg" id="section10">
        <div class="container">

            <!-- SECTION HEADER -->
            <div class="section-header-racloop">
                <div class="small-text-medium uppercase colored-text">
                    Query
                </div>
               
                <div class="colored-line">
                </div>
            </div>
        </div>
    </section>
    <div class="container">
        <div class="col-md-6 col-md-offset-3">
	        <div class="row">
	        <g:if test="${flash.message != null && flash.message.length() > 0}">	
	        	<g:if test="${flash.type == 'error'}">			
			     <div class="alert alert-danger alert-dismissible" role="alert">
	                  <button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
	                  <strong>Error!</strong> <n:flashembed/> 
	             </div>
	            </g:if>
	            <g:else>
	            <div class="alert alert-info alert-dismissible" role="alert">
	                  <button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
	                  <n:flashembed/> 
	             </div>
	            </g:else>
			</g:if>
			
	       </div>
            <!-- VERTICAL REGISTRATION FORM -->
            <div class="row">
                  
             <g:form controller="elasticsearch" action="executeAddIndex" method="POST"  class="query-form" name="query-form">
					<input type="text" id="indexUptoString" name="indexUptoString" size="17" autocomplete="off" class="form-control input-box form_datetime" placeholder="Date and Time">
                    <span class="clearer glyphicon glyphicon-remove-circle form-control-feedback"></span>
                    <button id="addIndex" type="submit" value="addIndex"  class="btn btn-primary standard-button">Add Index</button>
                    
                </g:form>
            </div>
        </div>
    </div>
    <br>
</body>
<script>
$(function() {
	$('#indexUptoString').datetimepicker({
        weekStart: 1,
        //todayBtn:  true,
        //startDate: uiValidStartTime,
        //endDate: uiValidEndTime,
        todayHighlight: true,
		autoclose: true,
        minuteStep: 15,
        showMeridian: true,
        //pickerPosition: "top-left",
        startDate: new Date(),
        format: 'dd M yy HH:ii P'
        //format: 'dd MM yyyy    HH:ii P'
    }).on('changeDate', function(ev){
    	
    });

});

</script>
</html>