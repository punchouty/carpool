<%@ page import="grails.converters.JSON" %>
<html>
<head>
<meta name="layout" content="static" />
<title>Existing Journey Found</title>
<script type="text/javascript">
$(document).ready(function($){
    //var val = $(':radio[name=rdo_1]:checked').val();
    
    $('#sendRequest').click(function(){
    	var val = $("input[name=optionsRadios]:checked").val();
    	if(val=='newJourney'){				
			$('#myModal').modal({show:true});
		}
		else {
			$('#existingJourneyFound').submit();
		}

    });
    $('#Confirmed').click(function(){
      $('#existingJourneyFound').submit();
    });
      
});
</script>
<style>
.panel {
    background-color: #FFFFFF;
    border: 1px solid rgba(0, 0, 0, 0);
    border-radius: 4px 4px 4px 4px;
    box-shadow: 0 1px 1px rgba(0, 0, 0, 0.05);
    margin-bottom: 20px;
}   

.panel-primary {
    border-color: #428BCA;
}   

.panel-primary > .panel-heading {
    background-color: #ffffff;
    border-color: #428BCA;
    color: #000000;
}   

.panel-heading {
    border-bottom: 1px solid rgba(0, 0, 0, 0);
    border-top-left-radius: 3px;
    border-top-right-radius: 3px;
    padding: 10px 15px;
}   

.panel-title {
    font-size: 16px;
    margin-bottom: 0;
    margin-top: 0;
}   

.panel-body:before, .panel-body:after {
    content: " ";
    display: table;
}   

.panel-body:before, .panel-body:after {
    content: " ";
    display: table;
}   

.panel-body:after {
    clear: both;
}   

.panel-body {
    padding: 15px;
}   

.panel-footer {
    background-color: #F5F5F5;
    border-bottom-left-radius: 3px;
    border-bottom-right-radius: 3px;
    border-top: 1px solid #DDDDDD;
    padding: 10px 15px;
}

//CSS from v3 snipp
.user-row {
    margin-bottom: 14px;
}

.user-row:last-child {
    margin-bottom: 0;
}

.dropdown-user {
    margin: 13px 0;
    padding: 5px;
    height: 100%;
}

.dropdown-user:hover {
    cursor: pointer;
}

.table-user-information > tbody > tr {
    border-top: 1px solid rgb(221, 221, 221);
}

.table-user-information > tbody > tr:first-child {
    border-top: 0;
}


.table-user-information > tbody > tr > td {
    border-top: 0;
}

.table-user-information > th {
width: auto !important;
}
.modal-header {
    background-color: #f2dede;
   
 }
</style>
</head>
<body>
	<g:if test="${flash.error}">
	<div class="alert alert-danger">	
		${flash.error}
	</div>
	</g:if>
	<g:form name="existingJourneyFound" controller="journey" action="searchWithExistingJourney" class="form-inline">
	<div class="row user-infos cyruxx">
	
            <div class="col-md-10 offset1">
                <div class="panel panel-primary">
                    <div class="panel-heading">
                        <h3 class="panel-title">Existing Journey Found!</h3>
                    </div>
                    <div class="panel-body">
                        <div class="row-fluid">
                            
                            <div class="col-md-12">
                            	
                                <table class="table table-condensed table-responsive table-user-information">
                                    <thead>
	                                    <tr>
	                                        <th class="col-md-1"></th>
	                                        <th class="col-md-4">New Journey</th>
	                                        <th class="col-md-4">Existing Journey</th>
	                                    </tr>
                                    </thead>
                                    <tbody>
	                                    <tr>
	                                        <td>From : </td>
	                                        <td>${currentJourney.fromPlace}</td>
	                                        <td>${existingJourney.fromPlace}</td>
	                                    </tr>
	                                    <tr>
	                                        <td>To : </td>
	                                        <td>${currentJourney.toPlace}</td>
	                                        <td>${existingJourney.toPlace}</td>
	                                    </tr>
	                                    <tr>
	                                        <td>Date : </td>
	                                        <td>${currentJourney.dateOfJourney}</td>
	                                        <td>${existingJourney.dateOfJourney}</td>
	                                    </tr>
	                                    <tr>
	                                        <td>Driving : </td>
	                                        <td>${currentJourney.isDriver?'Yes ':'No '}</td>
	                                        <td>${existingJourney.isDriver?'Yes ':'No '}</td>
	                                    </tr>
                                    
                                    </tbody>
                                </table>
                            </div>
                            
                        </div>
                        
                        
                    </div>
                    <div class="row-fluid">
                    	 <div class="col-md-12">
                    	 	<div class="panel-body">
                    	 	<strong>Please select your options</strong><br><br>
                    		
							  <label  class="radio-inline">
							    <input type="radio" name="optionsRadios" id="optionsRadios1" value="existingJourney" checked >
							    Please use existing journey
							  </label>
							
							
							  <label  class="radio-inline">
							    <input type="radio" name="optionsRadios" id="optionsRadios2" value="newJourney">
							    No, I want to put in a new journey request.
							  </label>
							
							</div>
						</div>
					</div>
                    
                    <div class="panel-footer">
                    	
                    	<a id="sendRequest" href="#"  data-target="#myModal">
	                        <button id ="sendRequest"class="btn  btn-primary" type="button"
	                                data-toggle="tooltip"
	                                data-original-title="Send message to user">Send Request <i class="icon-envelope icon-white"></i></button>
						</a> 
						                            
                        <span class="pull-right">
							<g:link action="search" id="backToSearchResult" controller="userSession">                        	
                            <button id ="back" class="btn btn-default" type="button"
                                    data-toggle="tooltip"
                                    data-original-title="Edit this user">Back to Search <i class="icon-share-alt icon-white"></i></button>
                            </g:link>        
                            
                        </span>
                    </div>
                </div>
            </div>
          
	</div>
	<g:hiddenField name="existingJourneyId" value="${existingJourney.id}"/>
	
	<g:hiddenField name="toPlace" value="${currentJourney.toPlace}"/>
	<g:hiddenField name="fromPlace" value="${currentJourney.fromPlace}"/>
	<g:hiddenField name="dateOfJourneyString" value="${currentJourney.dateOfJourneyString}"/>
	<g:hiddenField name="user" value="${currentJourney.user}"/>
	
	<g:hiddenField name="isMale" value="${currentJourney.isMale}"/>
	<g:hiddenField name="validStartTimeString" value="${currentJourney.validStartTimeString}"/>
	<g:hiddenField name="fromLatitude" value="${currentJourney.fromLatitude}"/>
	<g:hiddenField name="fromLongitude" value="${currentJourney.fromLongitude}"/>
	<g:hiddenField name="toLatitude" value="${currentJourney.toLatitude}"/>
	<g:hiddenField name="toLongitude" value="${currentJourney.toLongitude}"/>
	<g:hiddenField name="isDriver" value="${currentJourney.isDriver}"/>
	<g:hiddenField name="tripDistance" value="${currentJourney.tripDistance}"/>
	<g:hiddenField name="tripUnit" value="${currentJourney.tripUnit}"/>
	
	</g:form>
	<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	  <div class="modal-dialog modal-lg">
	    <div class="modal-content">
	      <div class="modal-header">
	        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	        <h4 class="modal-title modal-primary" id="myModalLabel">Warning</h4>
	      </div>
	      <div class="modal-body">
	        <p id="errorMessage"><strong>This action will cancel your existing journey and all of its associated notifications.</strong><br>
	        <br>Below are the existing journey details: -
	        </p>
	        <table  class="table table-condensed table-responsive table-user-information">
		        <tbody>
			        <tr>
	                     <td>From : </td>
	                     <td>${existingJourney.fromPlace}</td>
	                 </tr>
	                 <tr>
	                     <td>To : </td>
	                     <td>${existingJourney.toPlace}</td>
	                 </tr>
	                 <tr>
	                     <td>Date :</td>
	                     <td>${existingJourney.dateOfJourney}</td>
	                 </tr>
		      	</tbody>
	      	</table>
	      </div>
	      <div class="modal-footer">
	        <button type="button" class="btn btn-default" id ="cancel" data-dismiss="modal">Cancel</button>  
	        <button class="btn btn-primary" data-dismiss="modal" id="Confirmed">Continue</button>      
	      </div>
	    </div>
	  </div>
	</div>
	        
        
</body>

</html>