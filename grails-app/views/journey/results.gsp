<%@ page import="grails.converters.JSON" %>
<%@ page import="com.racloop.journey.workkflow.WorkflowAction" %>
<html>
<head>
<meta name="layout" content="dynamic" />
<title>Search Results -  ${searchResults.total} records returned</title>
<r:require module="core" />
</head>
<body>
	<g:set var="isSearch" value="true" scope="request" />
	<g:set var="currentJourney" value="${searchResults.currentJourney}"/>
	<g:if test="${flash.message}">
		<div class="alert alert-success">			
		  <button type="button" class="close" data-dismiss="alert">&times;</button>
		  ${flash.message}
		</div>
	</g:if>
	<g:if test="${flash.error}">
		<div class="alert alert-danger">			
		  <button type="button" class="close" data-dismiss="alert">&times;</button>
		  ${flash.error}
		</div>
	</g:if>
	 <section class="white-bg" id="section10">
        <div class="container">
        <!-- SECTION HEADER -->
            <div class="section-header-racloop">
                <div class="small-text-medium uppercase colored-text">
                    ${searchResults.total} matching results found.
                </div>
                <h2 class="dark-text"><strong>Search</strong> Results</h2>
                <div class="colored-line">
                </div>
                <div class="sub-heading ">
                	<%--<g:if test = "${currentJourney?.isDriver == true}">
                		<span class="label label-primary">Car Owner <i class="fa fa-car"></i></span>
                	</g:if>
                	<g:else>
                		<span class="label label-primary">Ride Seeker <i class="fa fa-male"></i></span>
                	</g:else>
                    --%>
                    <g:if test = "${currentJourney?.isTaxi == true}">
                		<span class="label label-primary">Taxi</span>
                	</g:if>
                	<g:else>
                		<span class="label label-primary">Auto Rickshaw</span>
                	</g:else>
                    <div>
                        <i class="icon-icon-house-alt"></i> <strong>From :</strong> ${currentJourney?.from}
                    </div>
                    <div>
                        <i class="icon-basic-geolocalize-01"></i> <strong>To :</strong> ${currentJourney?.to}
                    </div>
                    <div>
                        <i class="icon-basic-calendar"></i> <strong>On :</strong> <g:formatDate format="dd MMMM yyyy hh:mm a" date="${currentJourney?.dateOfJourney}"/>
                    </div>
                    <div>
                        <g:if test ="${searchResults.data.get('hideSaveButton') == true}">
							<g:link controller="journey" action="redoSearch" class="btn btn-info"><i class="fa fa-refresh"></i> Search Again</g:link>
						</g:if>
						<g:else>
							<g:link controller="journey" action="newJourney" params="[currentUser: currentUser?.id]" class="btn btn-info"><i class="fa fa-save"></i> Save Request</g:link>
						</g:else>
						<g:link controller="userSession" action="search" class="btn btn-warning"><i class="fa fa-search"></i> Back to Search</g:link>
		             </div>
                </div>
            </div>
        </div>
    </section>
        
	
	<g:if test="${searchResults.total != 0}">
	 <div class="container">
	 	<g:each in="${searchResults.data.get('journeys')}" status="i" var="matchedResult">
			<g:set var="journeyInstance" value="${matchedResult}"/>
	        <article class="row white-bg-border racloop-search-fonts">
	            <div class="col-md-2 col-md-offset-1">
	                <span class="hidden-sm hidden-xs visible-lg visible-md">
	                	<%--<g:if test = "${matchedResult.isDriver == true}">
	                    	<g:img dir="images" file="racloop/driver.png" width="100" alt="Lorem ipsum" class="img-thumbnail"/>
	                    </g:if>
	                    <g:else>
	                    	<g:img dir="images" file="racloop/rider.png" width="100" alt="Lorem ipsum" class="img-thumbnail"/>
	                    </g:else>
	                	--%>
	                	<g:if test = "${matchedResult.isTaxi == true}">
	                    	<g:img dir="images" file="racloop/taxi.png" width="100" alt="Taxi" class="img-thumbnail" style="margin-top: 10px;"/>
	                    </g:if>
	                    <g:else>
	                    	<g:img dir="images" file="racloop/auto.png" width="100" alt="Auto" class="img-thumbnail" style="margin-top: 10px;"/>
	                    </g:else>
	                </span>
	            </div>
	            <div class="col-md-2">
	                <ul class="feature-list text-left">
	                    <li>
	                        <%--<g:if test = "${matchedResult.isDriver == true}">
	                        	<span class="hidden-lg hidden-md visible-sm visible-xs">
	                            	<span class="label label-primary">Car Owner <i class="fa fa-car"></i></span>
	                            <g:img dir="images" file="racloop/driver.png" width="100" alt="Car owner" class="img-thumbnail"/>
	                        	</span>
		                        <span class="hidden-sm hidden-xs visible-lg visible-md">
		                            <span class="label label-primary">Car Owner <i class="fa fa-car"></i></span>
		                        </span>
	                        </g:if>
	                        <g:else>
	                        	<span class="hidden-lg hidden-md visible-sm visible-xs">
	                            	<span class="label label-primary">Ride Seeker <i class="fa fa-male"></i></span>
	                            <g:img dir="images" file="racloop/rider.png" width="100" alt="Ride Seeker" class="img-thumbnail"/>
	                        	</span>
		                        <span class="hidden-sm hidden-xs visible-lg visible-md">
		                            <span class="label label-primary">Ride Seeker <i class="fa fa-male"></i></span>
		                        </span>
	                        </g:else>
	                        
	                        --%>
	                       <g:if test = "${matchedResult.isTaxi == true}">
	                        	<span class="hidden-lg hidden-md visible-sm visible-xs">
	                            	<span class="label label-primary">Taxi</span>
	                            <g:img dir="images" file="racloop/taxi.png" width="100" alt="Taxi" class="img-thumbnail" style="margin-top: 10px;"/>
	                        	</span>
		                        <span class="hidden-sm hidden-xs visible-lg visible-md">
		                            <span class="label label-primary">Taxi</span>
		                        </span>
	                        </g:if>
	                        <g:else>
	                        	<span class="hidden-lg hidden-md visible-sm visible-xs">
	                            	<span class="label label-primary">Auto Rickshaw</span>
	                            <g:img dir="images" file="racloop/auto.png" width="100" alt="Auto" class="img-thumbnail" style="margin-top: 10px;"/>
	                        	</span>
		                        <span class="hidden-sm hidden-xs visible-lg visible-md">
		                            <span class="label label-primary">Auto Rickshaw</span>
		                        </span>
	                        </g:else>
	                    </li>
	                    <li><i class="icon-basic-calendar"></i> <span><g:formatDate format="dd/MMM/yyyy" date="${matchedResult.dateOfJourney}"/></span></li>
	                    <li><i class="icon-clock-alt"></i> <span><g:formatDate format="hh:mm a" date="${matchedResult.dateOfJourney}"/></span></li>
	                </ul>
	            </div>
	            <div class="col-md-5 text-left">
	                <h5>${matchedResult.name}<g:if test = "${ matchedResult.getNumberOfCopassengers()>0 && !matchedResult.getMyStatus()}"> + ${ matchedResult.getNumberOfCopassengers()}</g:if> </h5>
	                <ul class="text-left">
	                    <li><i class="icon-icon-house-alt"></i> <strong>From :</strong> ${matchedResult.from}</li>
	                    <li><i class="icon-basic-geolocalize-01"></i> <strong>To :</strong> ${matchedResult.to}</li>
	                  
						<g:if test = "${matchedResult.getMyStatus()}">
							<li>
								<g:if test = "${matchedResult.getMyStatus()=='Requested'}">
                               		<g:link action="cancelRequest" id="cancelOutgoingRequest_${i }"  params="[pairId: matchedResult.getMyPairId(), myJourneyId:currentJourney.id]">
  												<button class="btn btn-danger"><i class="fa fa-times"></i> Cancel Sent Request</button>
  									</g:link>
                               	</g:if>
                               	<g:elseif test = "${matchedResult.getMyStatus()=='Request Received'}">
                               		<g:link action="acceptIncomingRequest" id="acceptIncomingRequest${i }"  params="[pairId: matchedResult.getMyPairId(),redirectToSearch:true]">
											<button class="btn btn-primary"><i class="fa fa-check-circle"></i> Accept Request</button>
									</g:link>
									<g:link action="rejectIncomingRequest" id="rejectIncomingRequest${i }"  params="[pairId: matchedResult.getMyPairId(),redirectToSearch:true]">
											<button class="btn btn-warning"><i class="fa fa-ban"></i> Reject Request</button>
									</g:link>
                               	</g:elseif>
                               	<g:elseif test = "${matchedResult.getMyStatus()=='Accepted'}">
                               		<g:link action="cancelRequest" id="cancelRequest"  params="[pairId: matchedResult.getMyPairId(), myJourneyId:matchedResult.id]">
											<button class="btn btn-danger"><i class="fa fa-times"></i> Cancel Accepted Request</button>
									</g:link>
                               	</g:elseif>
                               	<g:elseif test = "${matchedResult.getMyStatus().startsWith('Rejected')}">
                               		<button class="btn btn-warning"><i class="fa fa-ban"></i> ${matchedResult.getMyStatus()}</button>
                               	</g:elseif>
                               	<g:elseif test = "${matchedResult.getMyStatus().startsWith('Cancelled')}">
                               		<button class="btn btn-warning"><i class="fa fa-ban"></i> ${matchedResult.getMyStatus()}</button>
                               	</g:elseif>
                               	<g:elseif test = "${matchedResult.getMyStatus()=='Available'}">
                               		<g:link action="requestAgain" id="request_${i}"  params="[pairId: matchedResult.getMyPairId(), myJourneyId:currentJourney.id]" class="btn btn-info"><i class="fa fa-mail-reply"></i> Connect Again</g:link>
									<g:link action="routeDetails" id="request_${i}"  params="[matchedJourneyId: journeyInstance.id]" class="btn btn-info"><i class="fa fa-cog"></i> Details</g:link>
                               	</g:elseif>
                               	<g:else>
                               		<button class="btn btn-primary"><i class="fa fa-ban"></i> ${matchedResult.getMyStatus()}</button>
                               	</g:else>
							</li>
						</g:if>
						<g:else>
							<li>
								<g:if test= "${searchResults.data.get('disableMoreRequests') == true  }">
									<button class="btn btn-primary" disabled="disabled"><i class="fa fa-ban"></i> Connect</button>
								</g:if>
								<g:elseif test= "${ matchedResult.getNumberOfCopassengers()>0}">
									<button class="btn btn-primary" disabled="disabled"><i class="fa fa-ban"></i> Connect</button>
									<a name ="additonal" id="additonal${i}" href="#"  data-target="#myModal" data-id="${journeyInstance.id}">
							       		<button class="btn btn-warning"> All users info</button>
								    </a>
								</g:elseif>
								<g:else>
									<g:link action="selectedJourney" id="
uest_${i}"  params="[matchedJourneyId: journeyInstance.id]" class="btn btn-info"><i class="fa fa-mail-reply"></i> Connect</g:link>
									<g:link action="routeDetails" id="request_${i}"  params="[matchedJourneyId: journeyInstance.id]" class="btn btn-info"><i class="fa fa-cog"></i> Details</g:link>
									
								</g:else>
							</li> 			
						</g:else>
			            
	                </ul>
	            </div>
	            <input type="hidden" id="${i}_from_lattitude" value="${journeyInstance.fromLatitude}">
				<input type="hidden" id="${i}_from_longitude" value="${journeyInstance.fromLongitude}">
				<input type="hidden" id="${i}_to_lattitude" value="${journeyInstance.toLatitude}">
				<input type="hidden" id="${i}_to_longitude" value="${journeyInstance.toLongitude}">
	            <div class="col-md-2 text-left">
	                <div>
	                	<g:if test = "${journeyInstance?.photoUrl}">
	                		<img src="${journeyInstance?.photoUrl}" alt="profile image" class="img-thumbnail"> </img>
	                	</g:if>
	                	<g:else>
	                		<img src="http://www.gravatar.com/avatar/205e460b479c07710c08d50?s=64&d=mm" alt="profile image" class="img-thumbnail"  style="margin-top: 10px;"> </img>
	                	</g:else>
	                </div>
	            </div>
	            <span class="clearfix"></span>
	        </article>
        </g:each>
     </div>
     </g:if>
     <g:else>
		<div class="row">
			<p class="text-error">Sorry your search did not match any results</p>
		</div>
	  </g:else> 
  	
	<g:hiddenField name="user_mobile" value="${currentUser?.profile?.mobile}" />
	<g:hiddenField name="user_email" value="${currentUser?.profile?.email}" />
	<g:hiddenField name="numberOfRecords" value="${numberOfRecords}" />
	<!-- 
	<script type="text/javascript">
        $(document).ready(function () {
            $("#results").dataTable({
            	"aaSorting": []
            });
        });
     </script>
    -->
    <div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	  <div class="modal-dialog  modal-lg">
	    <div class="modal-content">
	      <div class="modal-header">
          <h4 class="modal-title" id="gridSystemModalLabel">Buddy Information</h4>
	       
	      </div>
	      <div class="modal-body">
	      this is my body
	      </div>
	      <div class="modal-footer">
 	        <button class="btn btn-primary" data-dismiss="modal" id="Confirmed">OK</button>
	      </div>
	    </div>
	  </div>
	</div>
	<script type="text/javascript">
		$(document).ready(function($){
			$('a[name=additonal]').click(function(){
		        var jourenyId = $(this).data('id');
		        //alert(jourenyId);
		        $.ajax({
		              type: "POST",
		              url: "${createLink(controller: 'journey', action: 'getSibling')}" ,
		              data: { journeyId: $(this).data('id') }
		            }).done(function(data) {
		                $('.modal-body').html(data);
		                $('#myModal').modal({show:true});
		            });
		        $("#journeyIdToBeDeleted").val(jourenyId);
		    	
		    });
		});
	</script>
</body>
</html>  