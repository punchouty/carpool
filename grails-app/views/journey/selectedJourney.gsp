<%@ page import="grails.converters.JSON" %>
<html>
<head>
<meta name="layout" content="static" />
<title>Search Results -  ${numberOfRecords} records returned</title>
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
</style>
</head>
<body>
	<g:if test="${flash.message}">
	<div class="message" role="status">${flash.message}</div>
	</g:if>
	<div class="row user-infos cyruxx">
            <div class="col-md-10 offset1">
                <div class="panel panel-primary">
                    <div class="panel-heading">
                        <h3 class="panel-title">Driver Information</h3>
                    </div>
                    <div class="panel-body">
                        <div class="row-fluid">
                            <div class="col-md-3">
                            	<g:img dir="images" file="driver.png" width="100" height="100" class="img-circle"/>
                                
                            </div>
                            <div class="col-md-6">
                            	<g:if test="${matchedUser}">
                            		<strong>${matchedUser.profile.fullName}</strong><br>
                            	</g:if>
                            	<g:else>
                            		<strong>${matchedJourney.name}</strong><br>
                            	</g:else>
                                
                                <table class="table table-condensed table-responsive table-user-information">
                                    <tbody>
                                    <tr>
                                        <td>From : </td>
                                        <td>${matchedJourney.fromPlace}</td>
                                    </tr>
                                    <tr>
                                        <td>To : </td>
                                        <td>${matchedJourney.toPlace}</td>
                                    </tr>
                                    <tr>
                                        <td>Date :</td>
                                        <td>${matchedJourney.dateOfJourney}</td>
                                    </tr>
                                    <tr>
                                        <td>Cost :</td>
                                        <td>Approximate INR 200</td>
                                    </tr>
                                    <tr>
                                        <td>Rating : </td>
                                        <td>
                                        	<g:img dir="images" file="star-full.png" width="25" height="25"/>
                                        	<g:img dir="images" file="star-full.png" width="25" height="25"/>
                                        	<g:img dir="images" file="star-half.png" width="25" height="25"/>
                                        	<g:img dir="images" file="star-empty.png" width="25" height="25"/>
                                        	<g:img dir="images" file="star-empty.png" width="25" height="25"/>
                                        	&nbsp;&nbsp;&nbsp; 12 people rated &nbsp;&nbsp;&nbsp;
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>Reviews:</td>
                                        <td>
                                        	3 <a href="#reviews" role="button" data-toggle="modal">Reviews</a> Available
                                        </td>
                                    </tr>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                    <div class="panel-footer">
                    	<g:link action="requestService" id="requestService"  params="[matchedJourneyId: matchedJourney.id, dummy:isDummy]">
	                        <button class="btn  btn-primary" type="button"
	                                data-toggle="tooltip"
	                                data-original-title="Send message to user">Send Request <i class="icon-envelope icon-white"></i></button>
						</g:link>                                
                        <span class="pull-right">
			<g:link action="backToSearchResult" id="backToSearchResult">                        	
                            <button class="btn btn-warning" type="button"
                                    data-toggle="tooltip"
                                    data-original-title="Edit this user">Back to Results <i class="icon-share-alt icon-white"></i></button>
                            </g:link>        
                            
                        </span>
                    </div>
                </div>
            </div>
        </div>
        
        <div id="reviews" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		  <div class="modal-header">
		    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
		    <h3 id="myModalLabel">Last 3 Reviews</h3>
		  </div>
		  <div class="modal-body">
		  	<div>
		        <p>
		          Lorem ipsum dolor sit amet, id nec conceptam conclusionemque. Et eam tation option. Utinam salutatus ex eum. Ne mea dicit tibique facilisi, ea mei omittam explicari conclusionemque, ad nobis propriae quaerendum sea.
		        </p>
		        <p>
                  	<g:img dir="images" file="star-full.png" width="25" height="25"/>
                  	<g:img dir="images" file="star-full.png" width="25" height="25"/>
                  	<g:img dir="images" file="star-full.png" width="25" height="25"/>
                  	<g:img dir="images" file="star-empty.png" width="25" height="25"/>
                  	<g:img dir="images" file="star-empty.png" width="25" height="25"/>
		          | <i class="icon-user"></i> by <a href="#">John</a> 
		          | <i class="icon-calendar"></i> Sept 16th, 2012
		        </p>
		    </div>
		    <hr/>
		  	<div>
		        <p>
		          Lorem ipsum dolor sit amet, id nec conceptam conclusionemque. Et eam tation option. Utinam salutatus ex eum. Ne mea dicit tibique facilisi, ea mei omittam explicari conclusionemque, ad nobis propriae quaerendum sea.
		        </p>
		        <p>
                  	<g:img dir="images" file="star-full.png" width="25" height="25"/>
                  	<g:img dir="images" file="star-full.png" width="25" height="25"/>
                  	<g:img dir="images" file="star-half.png" width="25" height="25"/>
                  	<g:img dir="images" file="star-empty.png" width="25" height="25"/>
                  	<g:img dir="images" file="star-empty.png" width="25" height="25"/>
		          | <i class="icon-user"></i> by <a href="#">John</a> 
		          | <i class="icon-calendar"></i> Sept 16th, 2012
		        </p>
		    </div>
		    <hr/>
		    
		  	<div>
		        <p>
		          Lorem ipsum dolor sit amet, id nec conceptam conclusionemque. Et eam tation option. Utinam salutatus ex eum. Ne mea dicit tibique facilisi, ea mei omittam explicari conclusionemque, ad nobis propriae quaerendum sea.
		        </p>
		        <p>
                  	<g:img dir="images" file="star-full.png" width="25" height="25"/>
                  	<g:img dir="images" file="star-full.png" width="25" height="25"/>
                  	<g:img dir="images" file="star-empty.png" width="25" height="25"/>
                  	<g:img dir="images" file="star-empty.png" width="25" height="25"/>
                  	<g:img dir="images" file="star-empty.png" width="25" height="25"/>
		          | <i class="icon-user"></i> by <a href="#">John</a> 
		          | <i class="icon-calendar"></i> Sept 16th, 2012
		        </p>
		    </div>
		  </div>
		  <div class="modal-footer">
		    <button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
		  </div>
		</div>
</body>

</html>