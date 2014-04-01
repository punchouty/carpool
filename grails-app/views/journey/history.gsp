<html>
<head>
<meta name="layout" content="static" />
<title>My Active Journeys</title>
</head>
<body>
	<g:set var="isHistory" value="true" scope="request" />
	<h2>History</h2>
	<g:if test="${journeys?.size > 0}">
	<div class="row-fluid">
		<div class="accordion" id="accordion1">
		
		<div class="accordion-group"> <!-- Repete Element -->
   			<div class="accordion-heading ">
                 	<a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion1" href="#journey1">
                 		<i class="icon-calendar"></i> Feb 16th, 4:20 pm
					| <i class="icon-bell"></i> Incoming Requests <strong>(3)</strong>
				  	| <i class="icon-comment"></i> Outgoing Responses <strong>(2)</strong>
				  	| Show Details
                 	</a>        				
   			</div> <!-- accordion-heading ENDS  -->
   			<div id="journey1" class="accordion-body collapse">
   				<div class="accordion-inner">
				   <div>
				     <blockquote>
				       <p>Driving <span class="label label-inverse">Expired</span></p>
				       <small><i class="icon-home"></i> From : <cite title="Source Title">Rajiv Chownk, New Delhi, India </cite></small>
				       <small><i class="icon-map-marker"></i> To : <cite title="Source Title">Sector 47, Chandigarh India </cite></small>
				       <button class="btn btn-success"><i class="icon-refresh icon-white"></i> Search Again </button>
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
	       								<td><span class="label label-inverse">Expired</span></td>
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
	       								<td><span class="label label-inverse">Expired</span></td>
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
		       								<td><span class="label label-inverse">Expired</span></td>
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
		       								<td><span class="label label-inverse">Expired</span></td>
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
		       								<td><span class="label label-inverse">Expired</span></td>
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
                 		<i class="icon-calendar"></i> Feb 26th, 4:20 pm
					| <i class="icon-bell"></i> Incoming Requests <strong>(3)</strong>
				  	| <i class="icon-comment"></i> Outgoing Responses <strong>(2)</strong>
				  	| Show Details
                 	</a>        				
   			</div> <!-- accordion-heading ENDS  -->
   			<div id="journey2" class="accordion-body collapse">
   				<div class="accordion-inner">
				   <div>
				     <blockquote>
				       <p>Driving <span class="label label-inverse">Expired</span></p>
				       <small><i class="icon-home"></i> From : <cite title="Source Title">Rajiv Chownk, New Delhi, India </cite></small>
				       <small><i class="icon-map-marker"></i> To : <cite title="Source Title">Sector 47, Chandigarh India </cite></small>
				       <button class="btn btn-success"><i class="icon-refresh icon-white"></i> Search Again </button>
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
	       								<td><span class="label label-inverse">Expired</span></td>
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
	       								<td><span class="label label-inverse">Expired</span></td>
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
		       								<td><span class="label label-inverse">Expired</span></td>
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
		       								<td><span class="label label-inverse">Expired</span></td>
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
		       								<td><span class="label label-inverse">Expired</span></td>
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
	</g:if>
	<g:else>
		<div class="well">
			<h4>No Records Found</h4>
		</div>
		<g:if env="development">
			<div class="alert">
			  <button type="button" class="close" data-dismiss="alert">&times;</button>
			  <strong>Important Note!</strong> Look at history.gsp for sample html code and then remove this html snippet
			</div>
		</g:if>
	</g:else>
</body>
</html>