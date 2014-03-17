<html>
<head>
<meta name="layout" content="static" />
<title>My Active Journeys</title>
</head>
<body>
	<g:set var="isActiveJourneys" value="true" scope="request" />
	<h2>Active Journeys</h2>
	<div class="row-fluid">
		<div class="accordion" id="accordion1">
		
		<div class="accordion-group"> <!-- Repete Element -->
   			<div class="accordion-heading ">
                 	<a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion1" href="#journey1">
                 		<i class="icon-calendar"></i> March 16th, 4:20 pm
					| <i class="icon-bell"></i> Incoming Requests <strong>(4)</strong>
				  	| <i class="icon-comment"></i> Outgoing Responses <strong>(4)</strong>
				  	| Show Details
                 	</a>        				
   			</div> <!-- accordion-heading ENDS  -->
   			<div id="journey1" class="accordion-body collapse">
   				<div class="accordion-inner">
				   <div>
				     <blockquote>
				       <p>Driving <span class="label label-info">Active</span></p>
				       <small><i class="icon-home"></i> From : <cite title="Source Title">Rajiv Chownk, New Delhi, India </cite></small>
				       <small><i class="icon-map-marker"></i> To : <cite title="Source Title">Sector 47, Chandigarh India </cite></small>
				       <button class="btn btn-success"><i class="icon-refresh icon-white"></i> Search Again</button>
				       <button class="btn btn-danger"><i class="icon-trash icon-white"></i> Cancel</button>
				     </blockquote>
				   </div>
		   		<div>
			    <ul class="nav nav-tabs">
				  <li class="active"><a href="#journey1_notfications"  data-toggle="tab"><i class="icon-bell"></i> Incoming Requests <span class="badge badge-info">4</span></a></li>
				  <li><a href="#journey1_responses"  data-toggle="tab"><i class="icon-comment"></i> Outgoing Responses <span class="badge badge-info">4</span></a></li>
				</ul>
				<div class="tab-content">
  					<div class="tab-pane active in" id="journey1_notfications">
  						<table class="table table-striped table-condensed">
       						<thead>
       							<tr>
       								<th>Name <i class="icon-user"></i></th>
       								<th>Route <i class="icon-road"></i></th>
       								<th>Time <i class="icon-calendar"></i></th>
       								<th>Status <i class=" icon-info-sign"></i></th>
       								<th></th>
       							</tr>
       						</thead>
	       						<tbody>
	       							<tr>
	       								<td>Sample User 1</td>
	       								<td>
	       									<div>
										      <blockquote>
										        <small><i class="icon-home"></i> From : <cite title="Source Title">Rajiv Chownk, New Delhi, India  </cite></small>
										        <small><i class="icon-map-marker"></i> To : <cite title="Source Title">Sector 47, Chandigarh India  </cite></small>
										      </blockquote>
										    </div>
	       								</td>
	       								<td>Sept 16th, 4:30 pm</td>
	       								<td><span class="label label-info">New</span></td>
	       								<td>
	       									<button class="btn btn-success"><i class="icon-ok icon-white"></i> Accept</button>
	       									<button class="btn btn-warning"><i class="icon-ban-circle icon-white"></i> Reject</button>
	       								</td>
	       							</tr>
	       							<tr>
	       								<td>Sample User 2</td>
	       								<td>
	       									<div>
										      <blockquote>
										        <small><i class="icon-home"></i> From : <cite title="Source Title">Rajiv Chownk, New Delhi, India  </cite></small>
										        <small><i class="icon-map-marker"></i> To : <cite title="Source Title">Sector 47, Chandigarh India  </cite></small>
										      </blockquote>
										    </div>
										</td>
	       								<td>Sept 16th, 4:30 pm</td>
	       								<td><span class="label label-inverse">Rejected</span></td>
	       								<td>
	       								</td>
	       							</tr>
		       							<tr>
		       								<td>Sample User 3</td>
		       								<td>
		       									<div>
											      <blockquote>
											        <small><i class="icon-home"></i> From : <cite title="Source Title">Rajiv Chownk, New Delhi, India  </cite></small>
											        <small><i class="icon-map-marker"></i> To : <cite title="Source Title">Sector 47, Chandigarh India  </cite></small>
											      </blockquote>
											    </div>
		       								</td>
		       								<td>Sept 16th, 4:30 pm</td>
		       								<td><span class="label label-success">Accepted</span><p><strong>9717744392</strong></p></td>
		       								<td>			            									
		       									<button class="btn btn-danger"><i class="icon-trash icon-white"></i> Cancel</button>
		       								</td>
		       							</tr>
		       							<tr>
		       								<td>Sample User 4</td>
		       								<td>
		       									<div>
											      <blockquote>
											        <small><i class="icon-home"></i> From : <cite title="Source Title">Rajiv Chownk, New Delhi, India  </cite></small>
											        <small><i class="icon-map-marker"></i> To : <cite title="Source Title">Sector 47, Chandigarh India  </cite></small>
											      </blockquote>
								   				</div>
		       								</td>
		       								<td>Sept 16th, 4:30 pm</td>
		       								<td><span class="label">Cancelled</span><p><strong>9717744392</strong></p></td>
		       								<td>	
		       								</td>
		       							</tr>
		       						</tbody>
		       					</table>
           					</div>
           					<div class="tab-pane in" id="journey1_responses">
           						<table class="table table-striped table-condensed">
		       						<thead>
		       							<tr>
		       								<th>Name <i class="icon-user"></i></th>
		       								<th>Route <i class="icon-road"></i></th>
		       								<th>Time <i class="icon-calendar"></i></th>
		       								<th>Status <i class=" icon-info-sign"></i></th>
		       								<th></th>
		       							</tr>
		       						</thead>
		       						<tbody>
		       							<tr>
		       								<td>Sample User 5</td>
		       								<td>
		       									<div>
											      <blockquote>
											        <small><i class="icon-home"></i> From : <cite title="Source Title">Rajiv Chownk, New Delhi, India  </cite></small>
											        <small><i class="icon-map-marker"></i> To : <cite title="Source Title">Sector 47, Chandigarh India  </cite></small>
											      </blockquote>
											    </div>
		       								</td>
		       								<td>Sept 16th, 4:30 pm</td>
		       								<td><span class="label label-warning">Sent</span></td>
		       								<td>
		       									<button class="btn btn-danger"><i class="icon-trash icon-white"></i> Cancel</button>
		       								</td>
		       							</tr>
		       							<tr>
		       								<td>Sample User 6</td>
		       								<td>
		       									<div>
											      <blockquote>
											        <small><i class="icon-home"></i> From : <cite title="Source Title">Rajiv Chownk, New Delhi, India  </cite></small>
											        <small><i class="icon-map-marker"></i> To : <cite title="Source Title">Sector 47, Chandigarh India  </cite></small>
											      </blockquote>
											    </div>
											</td>
		       								<td>Sept 16th, 4:30 pm</td>
		       								<td><span class="label label-inverse">Rejected</span></td>
		       								<td>
		       								</td>
		       							</tr>
		       							<tr>
		       								<td>Sample User 7</td>
		       								<td>
		       									<div>
											      <blockquote>
											        <small><i class="icon-home"></i> From : <cite title="Source Title">Rajiv Chownk, New Delhi, India  </cite></small>
											        <small><i class="icon-map-marker"></i> To : <cite title="Source Title">Sector 47, Chandigarh India  </cite></small>
											      </blockquote>
											    </div>
		       								</td>
		       								<td>Sept 16th, 4:30 pm</td>
		       								<td><span class="label label-success">Accepted</span><p><strong>9717744392</strong></p></td>
		       								<td>			            									
		       									<button class="btn btn-danger"><i class="icon-trash icon-white"></i> Cancel</button>
		       								</td>
		       							</tr>
		       							<tr>
		       								<td>Sample User 8</td>
		       								<td>
		       									<div>
											      <blockquote>
											        <small><i class="icon-home"></i> From : <cite title="Source Title">Rajiv Chownk, New Delhi, India  </cite></small>
											        <small><i class="icon-map-marker"></i> To : <cite title="Source Title">Sector 47, Chandigarh India  </cite></small>
											      </blockquote>
											    </div>
		       								</td>
		       								<td>Sept 16th, 4:30 pm</td>
		       								<td><span class="label">Canceled</span><p><strong>9717744392</strong></p></td>
		       								<td>			            									
		       									
		       								</td>
		       							</tr>
		       						</tbody>
		       					</table>
           					</div>
           				</div>
					</div>
   				</div>
   			</div> <!-- accordion-body ENDS  -->
   		</div><!-- accordian-group ENDS -->
   		
   		
		<div class="accordion-group"> <!-- Repete Element -->
   			<div class="accordion-heading ">
                 	<a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion1" href="#journey2">
                 		<i class="icon-calendar"></i> March 17th, 4:20 pm
					| <i class="icon-bell"></i> Incoming Requests <strong>(4)</strong>
				  	| <i class="icon-comment"></i> Outgoing Responses <strong>(4)</strong>
				  	| Show Details
                 	</a>        				
   			</div> <!-- accordion-heading ENDS  -->
   			<div id="journey2" class="accordion-body collapse">
   				<div class="accordion-inner">
				   <div>
				     <blockquote>
				       <p>Need a Ride <span class="label">Cancelled</span></p>
				       <small><i class="icon-home"></i> From : <cite title="Source Title">Rajiv Chownk, New Delhi, India </cite></small>
				       <small><i class="icon-map-marker"></i> To : <cite title="Source Title">Sector 47, Chandigarh India </cite></small>
				     </blockquote>
				   </div>
		   		<div>
			    <ul class="nav nav-tabs">
				  <li class="active"><a href="#journey2_notfications"  data-toggle="tab"><i class="icon-bell"></i> Incoming Requests <span class="badge badge-info">4</span></a></li>
				  <li><a href="#journey2_responses"  data-toggle="tab"><i class="icon-comment"></i> Outgoing Responses <span class="badge badge-info">4</span></a></li>
				</ul>
				<div class="tab-content">
  					<div class="tab-pane active in" id="journey2_notfications">
  						<table class="table table-striped table-condensed">
       						<thead>
       							<tr>
       								<th>Name <i class="icon-user"></i></th>
       								<th>Route <i class="icon-road"></i></th>
       								<th>Time <i class="icon-calendar"></i></th>
       								<th>Status <i class=" icon-info-sign"></i></th>
       								<th></th>
       							</tr>
       						</thead>
	       						<tbody>
	       							<tr>
	       								<td>Sample User 1</td>
	       								<td>
	       									<div>
										      <blockquote>
										        <small><i class="icon-home"></i> From : <cite title="Source Title">Rajiv Chownk, New Delhi, India  </cite></small>
										        <small><i class="icon-map-marker"></i> To : <cite title="Source Title">Sector 47, Chandigarh India  </cite></small>
										      </blockquote>
										    </div>
	       								</td>
	       								<td>Sept 16th, 4:30 pm</td>
	       								<td><span class="label">Cancelled</span></td>
	       								<td>
	       								</td>
	       							</tr>
	       							<tr>
	       								<td>Sample User 2</td>
	       								<td>
	       									<div>
										      <blockquote>
										        <small><i class="icon-home"></i> From : <cite title="Source Title">Rajiv Chownk, New Delhi, India  </cite></small>
										        <small><i class="icon-map-marker"></i> To : <cite title="Source Title">Sector 47, Chandigarh India  </cite></small>
										      </blockquote>
										    </div>
										</td>
	       								<td>Sept 16th, 4:30 pm</td>
	       								<td><span class="label">Cancelled</span></td>
	       								<td>
	       								</td>
	       							</tr>
	       							<tr>
	       								<td>Sample User 4</td>
	       								<td>
	       									<div>
										      <blockquote>
										        <small><i class="icon-home"></i> From : <cite title="Source Title">Rajiv Chownk, New Delhi, India  </cite></small>
										        <small><i class="icon-map-marker"></i> To : <cite title="Source Title">Sector 47, Chandigarh India  </cite></small>
										      </blockquote>
							   				</div>
	       								</td>
	       								<td>Sept 16th, 4:30 pm</td>
	       								<td><span class="label">Cancelled</span><p><strong>9717744392</strong></p></td>
	       								<td>	
	       								</td>
	       							</tr>
		       						</tbody>
		       					</table>
           					</div>
           					<div class="tab-pane in" id="journey2_responses">
           						<table class="table table-striped table-condensed">
		       						<thead>
		       							<tr>
		       								<th>Name <i class="icon-user"></i></th>
		       								<th>Route <i class="icon-road"></i></th>
		       								<th>Time <i class="icon-calendar"></i></th>
		       								<th>Status <i class=" icon-info-sign"></i></th>
		       								<th></th>
		       							</tr>
		       						</thead>
		       						<tbody>
		       							<tr>
		       								<td>Sample User 5</td>
		       								<td>
		       									<div>
											      <blockquote>
											        <small><i class="icon-home"></i> From : <cite title="Source Title">Rajiv Chownk, New Delhi, India  </cite></small>
											        <small><i class="icon-map-marker"></i> To : <cite title="Source Title">Sector 47, Chandigarh India  </cite></small>
											      </blockquote>
											    </div>
		       								</td>
		       								<td>Sept 16th, 4:30 pm</td>
		       								<td><span class="label">Cancelled</span></td>
		       								<td>
		       								</td>
		       							</tr>
		       							<tr>
		       								<td>Sample User 6</td>
		       								<td>
		       									<div>
											      <blockquote>
											        <small><i class="icon-home"></i> From : <cite title="Source Title">Rajiv Chownk, New Delhi, India  </cite></small>
											        <small><i class="icon-map-marker"></i> To : <cite title="Source Title">Sector 47, Chandigarh India  </cite></small>
											      </blockquote>
											    </div>
											</td>
		       								<td>Sept 16th, 4:30 pm</td>
		       								<td><span class="label">Cancelled</span></td>
		       								<td>
		       								</td>
		       							</tr>
		       							<tr>
		       								<td>Sample User 8</td>
		       								<td>
		       									<div>
											      <blockquote>
											        <small><i class="icon-home"></i> From : <cite title="Source Title">Rajiv Chownk, New Delhi, India  </cite></small>
											        <small><i class="icon-map-marker"></i> To : <cite title="Source Title">Sector 47, Chandigarh India  </cite></small>
											      </blockquote>
											    </div>
		       								</td>
		       								<td>Sept 16th, 4:30 pm</td>
		       								<td><span class="label">Cancelled</span><p><strong>9717744392</strong></p></td>
		       								<td>			            									
		       									
		       								</td>
		       							</tr>
		       						</tbody>
		       					</table>
           					</div>
           				</div>
					</div>
   				</div>
   			</div> <!-- accordion-body ENDS  -->
   		</div><!-- accordian-group ENDS -->
   		
   		<div class="accordion-group"> <!-- Repete Element -->
   			<div class="accordion-heading ">
                 	<a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion1" href="#journey3">
                 		<i class="icon-calendar"></i> March 18th, 4:20 pm
					| <i class="icon-bell"></i> Incoming Requests <strong>(4)</strong>
				  	| <i class="icon-comment"></i> Outgoing Responses <strong>(4)</strong>
				  	| Show Details
                 	</a>        				
   			</div> <!-- accordion-heading ENDS  -->
   			<div id="journey3" class="accordion-body collapse">
   				<div class="accordion-inner">
				   <div>
				     <blockquote>
				       <p>Driving <span class="label label-info">Active</span></p>
				       <small><i class="icon-home"></i> From : <cite title="Source Title">Rajiv Chownk, New Delhi, India </cite></small>
				       <small><i class="icon-map-marker"></i> To : <cite title="Source Title">Sector 47, Chandigarh India </cite></small>
				       <button class="btn btn-success"><i class="icon-refresh icon-white"></i> Search Again</button>
				       <button class="btn btn-danger"><i class="icon-trash icon-white"></i> Cancel</button>
				     </blockquote>
				   </div>
		   		<div>
			    <ul class="nav nav-tabs">
				  <li class="active"><a href="#journey3_notfications"  data-toggle="tab"><i class="icon-bell"></i> Incoming Requests <span class="badge badge-info">4</span></a></li>
				  <li><a href="#journey3_responses"  data-toggle="tab"><i class="icon-comment"></i> Outgoing Responses <span class="badge badge-info">4</span></a></li>
				</ul>
				<div class="tab-content">
  					<div class="tab-pane active in" id="journey3_notfications">
  						<table class="table table-striped table-condensed">
       						<thead>
       							<tr>
       								<th>Name <i class="icon-user"></i></th>
       								<th>Route <i class="icon-road"></i></th>
       								<th>Time <i class="icon-calendar"></i></th>
       								<th>Status <i class=" icon-info-sign"></i></th>
       								<th></th>
       							</tr>
       						</thead>
	       						<tbody>
	       							<tr>
	       								<td>Sample User 1</td>
	       								<td>
	       									<div>
										      <blockquote>
										        <small><i class="icon-home"></i> From : <cite title="Source Title">Rajiv Chownk, New Delhi, India  </cite></small>
										        <small><i class="icon-map-marker"></i> To : <cite title="Source Title">Sector 47, Chandigarh India  </cite></small>
										      </blockquote>
										    </div>
	       								</td>
	       								<td>Sept 16th, 4:30 pm</td>
	       								<td><span class="label label-info">New</span></td>
	       								<td>
	       									<button class="btn btn-success"><i class="icon-ok icon-white"></i> Accept</button>
	       									<button class="btn btn-warning"><i class="icon-ban-circle icon-white"></i> Reject</button>
	       								</td>
	       							</tr>
	       							<tr>
	       								<td>Sample User 2</td>
	       								<td>
	       									<div>
										      <blockquote>
										        <small><i class="icon-home"></i> From : <cite title="Source Title">Rajiv Chownk, New Delhi, India  </cite></small>
										        <small><i class="icon-map-marker"></i> To : <cite title="Source Title">Sector 47, Chandigarh India  </cite></small>
										      </blockquote>
										    </div>
										</td>
	       								<td>Sept 16th, 4:30 pm</td>
	       								<td><span class="label label-inverse">Rejected</span></td>
	       								<td>
	       								</td>
	       							</tr>
		       							<tr>
		       								<td>Sample User 3</td>
		       								<td>
		       									<div>
											      <blockquote>
											        <small><i class="icon-home"></i> From : <cite title="Source Title">Rajiv Chownk, New Delhi, India  </cite></small>
											        <small><i class="icon-map-marker"></i> To : <cite title="Source Title">Sector 47, Chandigarh India  </cite></small>
											      </blockquote>
											    </div>
		       								</td>
		       								<td>Sept 16th, 4:30 pm</td>
		       								<td><span class="label label-success">Accepted</span><p><strong>9717744392</strong></p></td>
		       								<td>			            									
		       									<button class="btn btn-danger"><i class="icon-trash icon-white"></i> Cancel</button>
		       								</td>
		       							</tr>
		       							<tr>
		       								<td>Sample User 4</td>
		       								<td>
		       									<div>
											      <blockquote>
											        <small><i class="icon-home"></i> From : <cite title="Source Title">Rajiv Chownk, New Delhi, India  </cite></small>
											        <small><i class="icon-map-marker"></i> To : <cite title="Source Title">Sector 47, Chandigarh India  </cite></small>
											      </blockquote>
								   				</div>
		       								</td>
		       								<td>Sept 16th, 4:30 pm</td>
		       								<td><span class="label">Cancelled</span><p><strong>9717744392</strong></p></td>
		       								<td>	
		       								</td>
		       							</tr>
		       						</tbody>
		       					</table>
           					</div>
           					<div class="tab-pane in" id="journey3_responses">
           						<table class="table table-striped table-condensed">
		       						<thead>
		       							<tr>
		       								<th>Name <i class="icon-user"></i></th>
		       								<th>Route <i class="icon-road"></i></th>
		       								<th>Time <i class="icon-calendar"></i></th>
		       								<th>Status <i class=" icon-info-sign"></i></th>
		       								<th></th>
		       							</tr>
		       						</thead>
		       						<tbody>
		       							<tr>
		       								<td>Sample User 5</td>
		       								<td>
		       									<div>
											      <blockquote>
											        <small><i class="icon-home"></i> From : <cite title="Source Title">Rajiv Chownk, New Delhi, India  </cite></small>
											        <small><i class="icon-map-marker"></i> To : <cite title="Source Title">Sector 47, Chandigarh India  </cite></small>
											      </blockquote>
											    </div>
		       								</td>
		       								<td>Sept 16th, 4:30 pm</td>
		       								<td><span class="label label-warning">Sent</span></td>
		       								<td>
		       									<button class="btn btn-danger"><i class="icon-trash icon-white"></i> Cancel</button>
		       								</td>
		       							</tr>
		       							<tr>
		       								<td>Sample User 6</td>
		       								<td>
		       									<div>
											      <blockquote>
											        <small><i class="icon-home"></i> From : <cite title="Source Title">Rajiv Chownk, New Delhi, India  </cite></small>
											        <small><i class="icon-map-marker"></i> To : <cite title="Source Title">Sector 47, Chandigarh India  </cite></small>
											      </blockquote>
											    </div>
											</td>
		       								<td>Sept 16th, 4:30 pm</td>
		       								<td><span class="label label-inverse">Rejected</span></td>
		       								<td>
		       								</td>
		       							</tr>
		       							<tr>
		       								<td>Sample User 7</td>
		       								<td>
		       									<div>
											      <blockquote>
											        <small><i class="icon-home"></i> From : <cite title="Source Title">Rajiv Chownk, New Delhi, India  </cite></small>
											        <small><i class="icon-map-marker"></i> To : <cite title="Source Title">Sector 47, Chandigarh India  </cite></small>
											      </blockquote>
											    </div>
		       								</td>
		       								<td>Sept 16th, 4:30 pm</td>
		       								<td><span class="label label-success">Accepted</span><p><strong>9717744392</strong></p></td>
		       								<td>			            									
		       									<button class="btn btn-danger"><i class="icon-trash icon-white"></i> Cancel</button>
		       								</td>
		       							</tr>
		       							<tr>
		       								<td>Sample User 8</td>
		       								<td>
		       									<div>
											      <blockquote>
											        <small><i class="icon-home"></i> From : <cite title="Source Title">Rajiv Chownk, New Delhi, India  </cite></small>
											        <small><i class="icon-map-marker"></i> To : <cite title="Source Title">Sector 47, Chandigarh India  </cite></small>
											      </blockquote>
											    </div>
		       								</td>
		       								<td>Sept 16th, 4:30 pm</td>
		       								<td><span class="label">Canceled</span><p><strong>9717744392</strong></p></td>
		       								<td>			            									
		       									
		       								</td>
		       							</tr>
		       						</tbody>
		       					</table>
           					</div>
           				</div>
					</div>
   				</div>
   			</div> <!-- accordion-body ENDS  -->
   		</div><!-- accordian-group ENDS -->
   		
   		</div><!-- accordian ENDS -->
	</div><!-- row ENDS  -->
</body>
</html>