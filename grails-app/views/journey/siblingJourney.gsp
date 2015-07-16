<%@ page import="grails.converters.JSON" %>
<html>
<body>
	<g:if test="${flash.error}">
	<div class="alert alert-danger">	
		${flash.error}
	</div>
	</g:if>
	<section class="white-bg" id="section10">
        <div class="container-fluid">

            <!-- SECTION HEADER -->
            <div class="section-header-racloop">
                
                <h2 class="dark-text"><strong>Additional</strong> Buddy Information</h2>
                <div class="colored-line">
                </div>
            </div>
        </div>
    </section>
	<div class="container-fluid">
	 	<g:each in="${journeys}" status="i" var="result">
			<g:set var="journeyInstance" value="${result}"/>
			<g:if test = "${journeyInstance.getMyStatus() == null || (journeyInstance.getMyStatus()?.startsWith('Cancelled') == false && journeyInstance.getMyStatus()?.startsWith('Rejected') == false)}">
			
			<article id="request-${i}" class="row <g:if test="${i%2 == 0}">grey-bg</g:if><g:else>white-bg</g:else>">
 				<div class="row">
 				
		             <div class="col-md-8 col-md-offset-2 text-left">
		                <div>
		                    <ul class="active-journey-list text-left">
		                    	<li><%-- 
		                        	<g:if test = "${journeyInstance.isDriver == true}">
		                        		<span class="label label-primary">Car Owner <i class="fa fa-car"></i></span> 
		                        	</g:if>
		                        	<g:else>
		                        		<span class="label label-primary">Ride Seeker <i class="fa fa-male"></i></span> 
		                        	</g:else>--%>
		                        	<g:if test = "${journeyInstance.isTaxi == true}">
		                        		<span class="label label-primary">Taxi</span> 
		                        	</g:if>
		                        	<g:else>
		                        		<span class="label label-primary">Auto Rickshaw</span> 
		                        	</g:else> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span><strong>${journeyInstance.name} </strong></span>
		                        </li>
		                        <li>	
		                        	<i class="icon-basic-calendar"></i> <span><g:formatDate format="dd/MMM/yyyy" date="${journeyInstance.dateOfJourney}"/></span> 
		                        	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i class="icon-clock-alt"></i> <span><g:formatDate format="hh:mm a" date="${journeyInstance.dateOfJourney}"/></span>
		                        </li>
		                       
		                        <li><i class="icon-basic-geolocalize-01"></i> <strong>From : </strong> ${journeyInstance.from}</li>
		                        <li><i class="icon-weather-wind-s"></i> <strong>To : </strong> ${journeyInstance.to}</li>
		                        <li>
		                        	
		                        </li>
		                        
		                    </ul>
		                </div>
		            </div>
	            </div>
	             
    		</article>
    		</g:if>
		</g:each>	
     </div>
</body>

</html>