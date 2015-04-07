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
                  
             <g:form controller="elasticsearch" action="execute" method="POST"  class="query-form" name="query-form">

                    <div class="text-left form-group">
					    <label for="cf-email">URL :</label>
						<input type="text" id="url" name="url" class="form-control input-box zero-margin" placeholder="URL" required/>
  					</div>
                   
                    <div class="text-left form-group">
					    <label for="cf-name">JSON :</label>
						<textarea class="form-control" rows="5" id="json" placeholder="JSON"></textarea>
					</div>
                   
                   
                    <button id="query" type="submit" value="Query"  class="btn btn-primary standard-button">Query</button>
                </g:form>
            </div>
        </div>
    </div>
    <br>
</body>
</html>