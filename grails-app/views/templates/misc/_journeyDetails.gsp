<%@ page import="com.racloop.domin.*"%>
<div id="timeline">
			<g:if test ="${wayPoints?.size()==2}">
			<div class="timeline-item">
				<div class="timeline-icon">
					<img src="/resources/icons/home.svg" alt="">
				</div>
				<div class="timeline-content">
                    <h2>Start Ride</h2>
                    <table border="0">
                    	<tr><td colspan="2"><i>${wayPoints?.get(0).user} will hire a cab</i></td></tr>
                        <!--  <tr>
                            <td><strong>Time:</strong></td>
                            <td>10:20 AM</td>
                        </tr> -->
                        <tr>
                            <td><strong>Location:</strong></td>
                            <td>${wayPoints?.get(0).place}</td>
                        </tr>
                    </table>
				</div>
			</div>

			<div class="timeline-item">
				<div class="timeline-icon">
					<img src="/resources/icons/marker.svg" alt="">
				</div>
				<div class="timeline-content right">
					<h2>Destination</h2>
					<table border="0">
						<tr><td colspan="2"><i>${wayPoints?.get(1).user} : End ride and pay fare</i></td></tr>
                        <tr>
                            <td><strong>Location:</strong></td>
                            <td>${wayPoints?.get(1).place}</td>
                        </tr>
                        <tr>
                        	<td colspan="2">
                        		<g:if test ="${priceMap?.size() > 0}">
                        		<table class="ctable">
                        			<tr>
			                            <th>Approx. Fare</th>
			                            <th>UberGo</th>
			                            <th>Ola Mini</th>
			                            <th>Meru Cab</th>
			                        </tr>
			                        <tr>
			                            <td><i>Individual</i></td>
			                            <td><g:formatNumber number="${priceMap.UberGo}" format="#.##" /></td>
			                            <td><g:formatNumber number="${priceMap.olaMini}" format="#.##" /></td>
			                            <td><g:formatNumber number="${priceMap.meruCab}" format="#.##" /></td>
			                        </tr>
                        		</table>
                        		</g:if>
                        	</td>
                        </tr>
                    </table>
				</div>
			</div>
			</g:if>
			<g:elseif test ="${wayPoints?.size()==4}">
				<div class="timeline-item">
					<div class="timeline-icon">
						<img src="/resources/icons/home.svg" alt="">
					</div>
					<div class="timeline-content">
	                    <h2>Start Ride</h2>
	                    <table border="0">
	                    	<tr><td colspan="2"><i>${wayPoints?.get(0).user} will hire a cab</i></td></tr>
	                        <!--  <tr>
	                            <td><strong>Time:</strong></td>
	                            <td>10:20 AM</td>
	                        </tr> -->
	                        <tr>
	                            <td><strong>Location:</strong></td>
	                            <td>${wayPoints?.get(0).place}</td>
	                        </tr>
	                    </table>
					</div>
				</div>
	
				<div class="timeline-item">
					<div class="timeline-icon">
						<img src="/resources/icons/marker.svg" alt="">
					</div>
					<div class="timeline-content right">
						<h2>Pick Up Point</h2>
						<table border="0">
							<tr><td colspan="2"><i>${wayPoints?.get(1).user} will join you here</i></td></tr>
	                        <tr>
	                            <td><strong>Location:</strong></td>
	                            <td>${wayPoints?.get(1).place}</td>
	                        </tr>
	                    </table>
					</div>
				</div>
				<div class="timeline-item">
					<div class="timeline-icon">
						<img src="/resources/icons/marker.svg" alt="">
					</div>
					<div class="timeline-content">
						<h2>Drop Point</h2>
						<table border="0">
							<tr><td colspan="2"><i>${wayPoints?.get(2).user} will be dropped here. ${wayPoints?.get(2).user} will pay 50% of current fare to ${wayPoints?.get(3).user}</i></td></tr>
	                        <tr>
	                            <td><strong>Location:</strong></td>
	                            <td>${wayPoints?.get(2).place}</td>
	                        </tr>
	                        <tr>
	                        	<td colspan="2">
	                        		<g:if test ="${priceMap?.size() > 0}">
	                        		<table class="ctable">
	                        			<tr>
				                            <th>Approx. Fare</th>
				                            <th>UberGo</th>
				                            <th>Ola Mini</th>
				                            <th>Meru Cab</th>
				                        </tr>
				                        <tr>
				                            <td><i>Individual</i></td>
				                            <td><g:formatNumber number="${priceMap.UberGo}" format="#.##" /></td>
				                            <td><g:formatNumber number="${priceMap.olaMini}" format="#.##" /></td>
				                            <td><g:formatNumber number="${priceMap.meruCab}" format="#.##" /></td>
				                        </tr>
				                        <tr>
				                            <td><i>Shared</i></td>
				                            <td><g:formatNumber number="${(priceMap.UberGo)/2}" format="#.##" /></td>
				                            <td><g:formatNumber number="${(priceMap.olaMini)/2}" format="#.##" /></td>
				                            <td><g:formatNumber number="${(priceMap.meruCab)/2}" format="#.##" /></td>
				                        </tr>
	                        		</table>
	                        		</g:if>
	                        	</td>
	                        </tr>
	                    </table>
					</div>
				</div>
	            <div class="timeline-item">
					<div class="timeline-icon">
						<img src="/resources/icons/star.svg" alt="">
					</div>
					<div class="timeline-content right">
						<h2>Pay and End Ride</h2>
						<table border="0">
							<tr><td colspan="2"><i>${wayPoints?.get(3).user} will pay to cab driver and complete the ride</i></td></tr>
	                        <tr>
	                            <td><strong>Location:</strong></td>
	                            <td>${wayPoints?.get(3).place}</td>
	                        </tr>
	                    </table>
					</div>
				</div>
			</g:elseif>
			<g:elseif test ="${wayPoints?.size()==6}">
				<div class="timeline-item">
					<div class="timeline-icon">
						<img src="/resources/icons/home.svg" alt="">
					</div>
					<div class="timeline-content">
	                    <h2>Start Ride</h2>
	                    <table border="0">
	                    	<tr><td colspan="2"><i>${wayPoints?.get(0).user} will hire a cab</i></td></tr>
	                        <!--  <tr>
	                            <td><strong>Time:</strong></td>
	                            <td>10:20 AM</td>
	                        </tr> -->
	                        <tr>
	                            <td><strong>Location:</strong></td>
	                            <td>${wayPoints?.get(0).place}</td>
	                        </tr>
	                    </table>
					</div>
				</div>
	
				<div class="timeline-item">
					<div class="timeline-icon">
						<img src="/resources/icons/marker.svg" alt="">
					</div>
					<div class="timeline-content right">
						<h2>Pick Up</h2>
						<table border="0">
							<tr><td colspan="2"><i>Pickup ${wayPoints?.get(1).user}</i></td></tr>
	                        <tr>
	                            <td><strong>Location:</strong></td>
	                            <td>${wayPoints?.get(1).place}</td>
	                        </tr>
	                    </table>
					</div>
				</div>
				<div class="timeline-item">
					<div class="timeline-icon">
						<img src="/resources/icons/marker.svg" alt="">
					</div>
					<div class="timeline-content right">
						<h2>Pick Up</h2>
						<table border="0">
							<tr><td colspan="2"><i>Pickup ${wayPoints?.get(2).user}</i></td></tr>
	                        <tr>
	                            <td><strong>Location:</strong></td>
	                            <td>${wayPoints?.get(2).place}</td>
	                        </tr>
	                    </table>
					</div>
				</div>
				<div class="timeline-item">
					<div class="timeline-icon">
						<img src="/resources/icons/marker.svg" alt="">
					</div>
					<div class="timeline-content">
						<h2>Drop</h2>
						<table border="0">
							<tr><td colspan="2"><i>Drop ${wayPoints?.get(3).user} here. ${wayPoints?.get(3).user} will pay 33% of current fare to ${wayPoints.get(5).user}</i></td></tr>
	                        <tr>
	                            <td><strong>Location:</strong></td>
	                            <td>${wayPoints?.get(3).place}</td>
	                        </tr>
	                        <tr>
	                        	<td colspan="2">
	                        		<g:if test ="${priceMap?.size() > 0}">
	                        		<table class="ctable">
	                        			<tr>
				                            <th>Approx. Fare</th>
				                            <th>UberGo</th>
				                            <th>Ola Mini</th>
				                            <th>Meru Cab</th>
				                        </tr>
				                        <tr>
				                            <td><i>Individual</i></td>
				                            <td><g:formatNumber number="${priceMap.UberGo}" format="#.##" /></td>
				                            <td><g:formatNumber number="${priceMap.olaMini}" format="#.##" /></td>
				                            <td><g:formatNumber number="${priceMap.meruCab}" format="#.##" /></td>
				                        </tr>
				                        <tr>
				                            <td><i>Shared</i></td>
				                            <td><g:formatNumber number="${(priceMap.UberGo)/3}" format="#.##" /></td>
				                            <td><g:formatNumber number="${(priceMap.olaMini)/3}" format="#.##" /></td>
				                            <td><g:formatNumber number="${(priceMap.meruCab)/3}" format="#.##" /></td>
				                        </tr>
	                        		</table>
	                        		</g:if>
	                        	</td>
	                        </tr>
	                    </table>
					</div>
				</div>			
				<div class="timeline-item">
					<div class="timeline-icon">
						<img src="/resources/icons/marker.svg" alt="">
					</div>
					<div class="timeline-content">
						<h2>Drop</h2>
						<table border="0">
							<tr><td colspan="2"><i>Drop ${wayPoints.get(4).user} here. ${wayPoints.get(4).user} will pay 33% of current fare to ${wayPoints.get(5).user}</i></td></tr>
	                        <tr>
	                            <td><strong>Location:</strong></td>
	                            <td>${wayPoints.get(4).place}</td>
	                        </tr>
	                        <tr>
	                        	<td colspan="2">
	                        		<g:if test ="${priceMap?.size() > 0}">
	                        		<table class="ctable">
	                        			<tr>
				                            <th>Approx. Fare</th>
				                            <th>UberGo</th>
				                            <th>Ola Mini</th>
				                            <th>Meru Cab</th>
				                        </tr>
				                        <tr>
				                            <td><i>Individual</i></td>
				                            <td><g:formatNumber number="${priceMap.UberGo}" format="#.##" /></td>
				                            <td><g:formatNumber number="${priceMap.olaMini}" format="#.##" /></td>
				                            <td><g:formatNumber number="${priceMap.meruCab}" format="#.##" /></td>
				                        </tr>
				                        <tr>
				                            <td><i>Shared</i></td>
				                            <td><g:formatNumber number="${(priceMap.UberGo)/3}" format="#.##" /></td>
				                            <td><g:formatNumber number="${(priceMap.olaMini)/3}" format="#.##" /></td>
				                            <td><g:formatNumber number="${(priceMap.meruCab)/3}" format="#.##" /></td>
				                        </tr>
	                        		</table>
	                        		</g:if>
	                        	</td>
	                        </tr>
	                    </table>
					</div>
				</div>
	            <div class="timeline-item">
					<div class="timeline-icon">
						<img src="/resources/icons/star.svg" alt="">
					</div>
					<div class="timeline-content right">
						<h2>Pay and End Ride</h2>
						<table border="0">
							<tr><td colspan="2"><i>${wayPoints.get(5).user} will pay to cab driver and complete the ride</i></td></tr>
	                        <tr>
	                            <td><strong>Location:</strong></td>
	                            <td>${wayPoints.get(5).place}</td>
	                        </tr>
	                        <tr>
	                        	<td colspan="2">
	                        		<g:if test ="${priceMap?.size() > 0}">
	                        		<table class="ctable">
	                        			<tr>
				                            <th>Approx. Fare</th>
				                            <th>UberGo</th>
				                            <th>Ola Mini</th>
				                            <th>Meru Cab</th>
				                        </tr>
				                        <tr>
				                            <td><i>Individual</i></td>
				                            <td><g:formatNumber number="${priceMap.UberGo}" format="#.##" /></td>
				                            <td><g:formatNumber number="${priceMap.olaMini}" format="#.##" /></td>
				                            <td><g:formatNumber number="${priceMap.meruCab}" format="#.##" /></td>
				                        </tr>
				                        <tr>
				                            <td><i>Shared</i></td>
				                            <td><g:formatNumber number="${(priceMap.UberGo)/3}" format="#.##" /></td>
				                            <td><g:formatNumber number="${(priceMap.olaMini)/3}" format="#.##" /></td>
				                            <td><g:formatNumber number="${(priceMap.meruCab)/3}" format="#.##" /></td>
				                        </tr>
	                        		</table>
	                        		</g:if>
	                        	</td>
	                        </tr>
	                    </table>
					</div>
				</div>			
			</g:elseif>
			<g:else>
				Sorry, Unable to display route details.
			</g:else>
		</div>