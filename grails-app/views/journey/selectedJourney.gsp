<%@ page import="grails.converters.JSON" %>
<html>
<head>
<meta name="layout" content="static" />
<title>Search Results -  ${numberOfRecords} records returned</title>
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
                <div class="small-text-medium uppercase colored-text">
                    Confirm Request
                </div>
                <h2 class="dark-text"><strong>Passenger</strong> Information</h2>
                <div class="colored-line">
                </div>
            </div>
        </div>
    </section>
	
	<div class="container">
        <article class="row white-bg-border racloop-search-fonts">
            <div class="col-md-2 col-md-offset-2">
                <ul class="feature-list text-left">
                    <li>
                        <g:if test = "${matchedJourney.isDriver == true}">
                        	<span class="hidden-lg hidden-md visible-sm visible-xs">
                            	<span class="label label-primary">Car Owner</span>
                            	<g:img dir="images" file="racloop/driver.png"  alt="Ride Seeker" class="img-thumbnail"/>
                        	</span>
	                        <span class="hidden-sm hidden-xs visible-lg visible-md">
	                            <span class="label label-primary">Car Owner</span>
	                        </span>
                        </g:if>
                        <g:else>
                        	<span class="hidden-lg hidden-md visible-sm visible-xs">
                            	<span class="label label-primary">Ride Seeker</span>
                            	<g:img dir="images" file="racloop/rider.png"  alt="Ride Seeker" class="img-thumbnail"/>
                        	</span>
	                        <span class="hidden-sm hidden-xs visible-lg visible-md">
	                            <span class="label label-primary">Ride Seeker</span>
	                        </span>
                        </g:else>
                        
                    </li>
                    <li><i class="icon-basic-calendar"></i> <span><g:formatDate format="dd/MMM/yyyy" date="${matchedJourney.dateOfJourney}"/></span></li>
                    <li><i class="icon-clock-alt"></i> <span><g:formatDate format="hh:mm a" date="${matchedJourney.dateOfJourney}"/></span></li>
                </ul>
            </div>
            <div class="col-md-5 text-left">
                <h5>
	                <g:if test="${matchedUser}">
	                    <strong>${matchedUser.profile.fullName}</strong><br>
	                    Rating ${matchedUser.getUserRating()}
	                    <i class="icon-star-alt"></i> <i class="icon-star-alt"></i> <i class="icon-star-half-alt"></i> &nbsp; <small>${matchedUser.getIncomingReviews().size()} <a href ="#" data-target="#reviews" role="button" data-toggle="modal">Reviews</a>  available</small>
	                </g:if>
	               	<g:else>
	               		<strong>${matchedJourney.name}</strong><br>
	               		<i class="icon-star-alt"></i> <i class="icon-star-alt"></i> <i class="icon-star-half-alt"></i> &nbsp; <small>4 <a href="#">Reviews</a> available</small>
	               	</g:else>
					
				</h5>
                <ul class="text-left">
                    <li><i class="icon-basic-geolocalize-01"></i> <strong>From :</strong>${matchedJourney.fromPlace}</li>
                    <li><i class="icon-basic-geolocalize-05"></i> <strong>To :</strong>${matchedJourney.toPlace}</li>
                    <li><i class="icon-ecommerce-dollar"></i> <strong>Approximate Cost :</strong> 250/150/100 INR for 2/3/4 Passengers (By Car)</li>
                    <li>
                    	<g:if test = "${showRequestButton}">
                    		<g:link action="requestService" id="requestService"  params="[matchedJourneyId: matchedJourney.id, dummy:isDummy]">
                    			<button class="btn btn-info"><i class="icon-aim"></i> Send Request</button> 
                    		</g:link>	
                    	</g:if>
                    	<g:else>
                    		<button class="btn btn-info disabled"><i class="fa fa-mail-forward"></i> Send Request</button> 
                    	</g:else>
                    	<g:link action="backToSearchResult" id="backToSearchResult">                        	
                            <button class="btn btn-danger"><i class="fa fa-trash"></i> Cancel</button>
                         </g:link> 
                    	
                    </li>
                </ul>
            </div>
            <div class="col-md-2 text-left">
            	<g:if test = "${matchedUser?.profile?.gravatarUri}">
	                <div>
	                    <img src="${matchedUser?.profile?.gravatarUri}" alt="profile image" class="img-thumbnail"> </img>
	                </div>
                </g:if>
                <g:else>
                	<div>
	                	<img src="http://www.gravatar.com/avatar/205e460b479c07710c08d50?s=64&d=mm" alt="profile image" class="img-thumbnail"> </img>
	                </div>		
	            </g:else>
            </div>
            <span class="clearfix"></span>
        </article>
    </div>
    <div id="reviews" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
          <div class="modal-dialog">
   			 <div class="modal-content">
				  <div class="modal-header">
				    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
				    <h3 id="myModalLabel">User Reviews</h3>
				  </div>
				  <div class="modal-body">
				  <g:if test="${matchedUser?.getIncomingReviews()?.size() > 0}">
				  	<div>
				  		<g:each in="${matchedUser?.getIncomingReviews()}" status="i" var="review">
				  			<p>
				  				${review.comment}
				  			</p>
				  		</g:each>
				  	
				  	</div>
				  </g:if>
				  <g:else>
				  	<div><p>No Reviews available.</p>
				  	</div>
				  </g:else>
				  <div class="modal-footer">
				    <button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
				  </div>
				</div>
			</div>
		</div>
	</div>
</body>

</html>