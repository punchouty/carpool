<%@ page import="grails.converters.JSON" %>
<html>
<head>
<meta name="layout" content="static" />
<title>Enter User Review</title>
<r:require module="core" />
</head>
<body>
	<g:if test="${flash.error}">
	<div class="alert alert-danger">	
		${flash.error}
	</div>
	</g:if>
	
		<section class="white-bg" id="section10">
	        <div class="container">
	
	            <!-- SECTION HEADER -->
	            <div class="section-header-racloop">
	                <h2 class="dark-text">Please answer the following questions</h2>
	                <div class="colored-line">
	                </div>
	            </div>
	        </div>
    	</section>
    	<div class="container">
        	<div class="col-md-6 col-md-offset-3">
         		<div class="row">
	        		<g:if test="${flash.message != null && flash.message.length() > 0}">			
				     	<div class="alert alert-info alert-dismissible" role="alert">
		                    <button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
		                    <strong>Success!</strong> <n:flashembed/> 
		                </div>
					</g:if>
					<g:hasErrors bean="${review}">			
						<div class="alert alert-danger alert-dismissible" role="alert">
			                    <button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
			                    <strong>Warning!</strong> <g:renderErrors bean="${user}" as="list" />
			                </div>
					</g:hasErrors>
        		</div>
	            <!-- VERTICAL REGISTRATION FORM -->
	            <div> 
	            	<g:form name="userReview" controller="review" action="saveUserReview">
		            	<div class="form-group">
		            		<label for="punctualty">Punctualty</label>
		            		<g:select name="punctualty" required class="form-control input-box" from="${1..5}" value="${age}"noSelection="['':'-Please Select-']"/> 
		            	</div>
		            	<div class="form-group">
		            		<label for="overAll">OverAll</label>
		            		<g:select name="overAll" required class="form-control input-box" from="${1..5}" value="${age}"noSelection="['':'-Please Select-']"/>
		            	</div>
		            	 <div class="form-group">
						    <label for="comments">Comments</label>
						    <textarea name ="comments" id ="comments" required class="form-control" rows="3" maxlength="3000"></textarea>
						</div>
		            	<button id="submit" type="submit" value="Submit"  class="btn btn-primary">Submit</button>
		            	<g:hiddenField name="pairId" value="${pairId}"/>
		            	<g:hiddenField name="journeyId" value="${journeyId}"/>
	            	</g:form>
            	</div>
        	</div>
  	  </div>
    	
    
    
	
	
	
	        
        
</body>

</html>