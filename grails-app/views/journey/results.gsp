<%@ page import="grails.converters.JSON" %>
<%@ page import="com.racloop.journey.workkflow.WorkflowState" %>
<html>
<head>
<meta name="layout" content="static" />
<title>Search Results -  ${searchResults.numberOfRecords} records returned</title>
<script type="text/javascript">
        $(document).ready(function () {
            $("#results").dataTable({
            	"aaSorting": []
            });
        });
        </script>
</head>
<body>
	<g:set var="isHome" value="true" scope="request" />
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
                    ${searchResults.numberOfRecords} matching results found.
                </div>
                <h2 class="dark-text"><strong>Search</strong> Results</h2>
                <div class="colored-line">
                </div>
                <div class="sub-heading text-left">
                    <span class="label label-primary">Car Owner</span>
                    <div>
                        <strong>From :</strong> ${currentJourney.fromPlace}
                    </div>
                    <div>
                        <strong>To :</strong> ${currentJourney.toPlace}
                    </div>
                    <div>
                        <strong>On :</strong> <g:formatDate format="dd MMMM yyyy hh:mm a" date="${currentJourney.dateOfJourney}"/>
                    </div>
                    <div>
                        <g:if test ="${session?.currentJourney?.id }">
							<g:link controller="journey" action="redoSearch" class="btn btn-info"><i class="icon-check"></i> Search again</g:link>
						</g:if>
						<g:else>
							<g:link controller="journey" action="newJourney" class="btn btn-info"><i class="icon-check"></i> Save request</g:link>
						</g:else>
						<g:link controller="userSession" action="search" class="btn btn-warning"><i class="icon-arrows-remove"></i> Back to search</g:link>
		             </div>
                </div>
            </div>
        </div>
    </section>
        
	
	<g:if test="${searchResults.numberOfRecords != 0}">
	 <div class="container">
	 	<g:each in="${searchResults.matchedJourneys}" status="i" var="matchedResult">
			<g:set var="journeyInstance" value="${matchedResult.matchedJourney}"/>
	        <article class="row white-bg-border racloop-search-fonts">
	            <div class="col-md-2 col-md-offset-1">
	                <span class="hidden-sm hidden-xs visible-lg visible-md">
	                    <g:img dir="images" file="racloop/driver.png" width="100" alt="Lorem ipsum" class="img-thumbnail"/>
	                </span>
	            </div>
	            <div class="col-md-2">
	                <ul class="feature-list text-left">
	                    <li>
	                        <span class="hidden-lg hidden-md visible-sm visible-xs">
	                            <span class="label label-primary">Car Owner</span>
	                            <g:img dir="images" file="racloop/driver.png" width="100" alt="Lorem ipsum" class="img-thumbnail"/>
	                        </span>
	                        <span class="hidden-sm hidden-xs visible-lg visible-md">
	                            <span class="label label-primary">Car Owner</span>
	                        </span>
	                    </li>
	                    <li><i class="icon-basic-calendar"></i> <span><g:formatDate format="dd/MMM/yyyy" date="${journeyInstance.dateOfJourney}"/></span></li>
	                    <li><i class="icon-clock-alt"></i> <span><g:formatDate format="hh:mm a" date="${journeyInstance.dateOfJourney}"/></span></li>
	                </ul>
	            </div>
	            <div class="col-md-5 text-left">
	                <h5>${journeyInstance.name}</h5>
	                <ul class="text-left">
	                    <li><i class="icon-basic-geolocalize-01"></i> <strong>From :</strong> ${journeyInstance.fromPlace}</li>
	                    <li><i class="icon-basic-geolocalize-05"></i> <strong>To :</strong> ${journeyInstance.toPlace}</li>
	                    <li><button class="btn btn-info"><i class="icon-aim"></i> Request</button> </li>
	                </ul>
	            </div>
	            <input type="hidden" id="${i}_from_lattitude" value="${journeyInstance.fromLatitude}">
				<input type="hidden" id="${i}_from_longitude" value="${journeyInstance.fromLongitude}">
				<input type="hidden" id="${i}_to_lattitude" value="${journeyInstance.toLatitude}">
				<input type="hidden" id="${i}_to_longitude" value="${journeyInstance.toLongitude}">
	            <div class="col-md-2 text-left">
	                <div>
	                    <img src="http://www.gravatar.com/avatar/53fd5asasa5a449e3f758b891843ac4?s=64" alt="profile image" class="img-thumbnail"> </img>
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
  	<g:hiddenField name="dummy" value="${searchResults.isDummyData}" />
	<g:hiddenField name="user_mobile" value="${currentUser?.profile?.mobile}" />
	<g:hiddenField name="user_email" value="${currentUser?.profile?.email}" />
	<g:hiddenField name="numberOfRecords" value="${numberOfRecords}" />
</body>
</html>  
      