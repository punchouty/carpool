<!DOCTYPE html>
<html>
<head>
<meta name="layout" content="main" />
<title>Welcome to raC looP</title>
</head>
<body>
	<g:set var="isHome" value="true" scope="request" />
	<div class="jumbotron" id="jumbotron">
		<h1>Car Pool Finder</h1>
		<p class="lead">Travel together, Make friends, Save money and
			Contribute to Greener environment
		</p>
	</div>

	<div id="main-controls">
		<form id="search-form" class="form-inline" role="form">
			<div id="travelDateDiv" class="input-append">
				<input id="travelDate" type="text" class="search-field" placeholder="Date and Time" data-format="dd/MM/yyyy hh:mm"></input> 
				<a href="#" class="add-on"> 
					<i data-time-icon="icon-time" data-date-icon="icon-calendar"> </i>
				</a>
			</div>			
			
			<div id="fromDiv" class="input-append dropdown">
				<input id="from" type="text" placeholder="From - Start Typing"></input> 
				<a href="#" class="add-on dropdown-toggle"  data-toggle="dropdown"> 
					<i class="icon-map-marker"> </i>
				</a>
				<ul class="dropdown-menu">
					<li><a href="#"> Caunaught place, Delhi </a></li>
					<li><a href="#"> Sector 47-D,  Chandigarh </a></li>
					<li><a href="#"> IFFCO chowk, Gurgaon </a></li>
				</ul>
			</div>			
			
			<input type="hidden" id="fromLatitude" value=""> 
			<input type="hidden" id="fromLongitude" value="">
			
			<div id="toDiv" class="input-append dropdown"> 
				<input id="to" type="text" placeholder="To - Start Typing"></input>
				<a href="#" class="add-on dropdown-toggle"  data-toggle="dropdown"> 
					<i class="icon-map-marker"></i>
				</a>
				<ul class="dropdown-menu">
					<li><a href="#"> IFFCO chowk, Gurgaon </a></li>
					<li><a href="#"> Sector 47-D,  Chandigarh </a></li>
					<li><a href="#"> Caunaught place, Delhi </a></li>
				</ul>
			</div>
			
			<input type="hidden" id="toLatitude" value=""> 
			<input type="hidden" id="toLongitude" value="">
			
			<div class="btn-group">
				<button class="btn btn-primary">Car?</button>
				<button class="btn btn-primary dropdown-toggle"
					data-toggle="dropdown">
					<span class="caret"></span>
				</button>
				<ul class="dropdown-menu">
					<li><a id="driver" href="#">Yes, I am driving</a></li>
					<li><a id="rider" href="#">No, I need a ride</a></li>
				</ul>
			</div>
			<input type="hidden" id="tripDistance" value="" /> 
			<input type="hidden" id="tripUnit" value="" />
			
			<input type="hidden" id="isDriver" value="" />
		</form>
	</div>
	<div id="map-canvas"></div>
	<small id="map-help"></small>
	<div id="myModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	  <div class="modal-header">
	    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
	    <h3 id="myModalLabel">Error Message</h3>
	  </div>
	  <div class="modal-body">
	    <p id="errorMessage"></p>
	  </div>
	  <div class="modal-footer">
	    <button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
	  </div>
	</div>
	<script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?v=3&key=AIzaSyD-2SVsFAN8CLCAU7gU7xdbF2Xdkox9JoI&sensor=false&libraries=places">		
	</script>
	<script type="text/javascript">
		var rendererOptions = {
			draggable : true
		};
		var directionsDisplay = new google.maps.DirectionsRenderer(
				rendererOptions);
		var directionsService = new google.maps.DirectionsService();
		var userLocation = new google.maps.LatLng(23.843138,79.44171);
		var map;
		var from;
		var to;
		$('#from').val("");
		$('#to').val("");
		$.getJSON('http://freegeoip.net/json/', function(location) {
			userLocation = new google.maps.LatLng(parseFloat(location.latitude), parseFloat(location.longitude));
			var mapOptions = {
				center : userLocation,
				zoom : 10,
				mapTypeId : google.maps.MapTypeId.ROADMAP
			// ROADMAP | SATELLITE | HYBRID | TERRAIN
			};
			map = new google.maps.Map(document.getElementById("map-canvas"),
					mapOptions);
			directionsDisplay.setMap(map);
			google.maps.event.addListener(directionsDisplay, 'directions_changed', function() {
				computeTotalDistance(directionsDisplay.directions);
			});
		});
		$(function() {
			$('#from').placesSearch({
				onSelectAddress : function(result) {
					from = result.geometry.location;
					$('#fromLatitude').val(from.lat());
					$('#fromLongitude').val(from.lng());
					if(to) {
						calcRoute(from, to);
					}
					else {
						map.setZoom(12);
						map.panTo(from);
					}	
				}
			});
			$('#to').placesSearch({
				onSelectAddress : function(result) {
					to = result.geometry.location;
					$('#toLatitude').val(to.lat());
					$('#toLongitude').val(to.lng());
					if(from) {
						calcRoute(from, to);
					}
					else {
						map.setZoom(12);
						map.panTo(to);
					}
				}
			});
		});
		
		function calcRoute(from, to) {
			var request = {
				origin : from,
				destination : to,
				travelMode : google.maps.DirectionsTravelMode.DRIVING
			};
			directionsService.route(request, function(response, status) {
				if (status == google.maps.DirectionsStatus.OK) {
					directionsDisplay.setDirections(response);
				}
			});
			distance(from.lat(), from.lng(), to.lat(), to.lng());
			setMapHelp();
		}

		function distance(lat1, lon1, lat2, lon2) {
			var R = 6371; // km (change this constant to get miles)
			var dLat = (lat2 - lat1) * Math.PI / 180;
			var dLon = (lon2 - lon1) * Math.PI / 180;
			var a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
					+ Math.cos(lat1 * Math.PI / 180)
					* Math.cos(lat2 * Math.PI / 180) * Math.sin(dLon / 2)
					* Math.sin(dLon / 2);
			var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
			var d = R * c;
			if (d > 1) {
				d = Math.round(d);
				$('#tripUnit').val('KM');
				$('#tripDistance').val(d + '');
			} else if (d <= 1) {
				d = Math.round(d * 1000);
				$('#tripUnit').val('M');
				$('#tripDistance').val(d + '');
			}
		}

		function computeTotalDistance(result) {
			var total = 0;
			var myroute = result.routes[0];
			var numberOfLegs = 0;
			for ( var i = 0; i < myroute.legs.length; i++) {
				total += myroute.legs[i].distance.value;
				var start_location = myroute.legs[i].start_location
				var end_location = myroute.legs[i].end_location
				var start_address = myroute.legs[i].start_address
				var end_address = myroute.legs[i].end_address
				$('#fromLatitude').val(start_location.lat());
				$('#fromLongitude').val(start_location.lng());
				$('#toLatitude').val(end_location.lat());
				$('#toLongitude').val(end_location.lng());
				$('#from').val(start_address);
				$('#to').val(end_address);
				numberOfLegs++;
			}
			if(numberOfLegs != 1) {
				alert("numberOfLegs : " + numberOfLegs);
			}
			total = total / 1000.
			$('#tripUnit').val('KM');
			$('#tripDistance').val(total + '');
		}

		function setMapHelp() {
			$('#map-help').text("Drag start and end point to set the exact address.");
		}
	</script>
	<script type="text/javascript">	
		$(function() {
			var now = new Date();
			$('#travelDateDiv').val(now);
			var dp = $('#travelDateDiv').datetimepicker({
				language : 'pt-BR',
				pickSeconds : false,
				startDate : now
			});
		});
		
		$(function() {
			$('#driver').click(function() {
				var errorMessage = getErrorMessage();
				if(errorMessage){
					$('#errorMessage').text(errorMessage);
					$('#myModal').modal();
				}
				else {
					$('#isDriver').val('true');
					$('#search-form').submit();
				}
			});
			$('#rider').click(function() {
				var errorMessage = getErrorMessage();
				if(errorMessage){
					$('#errorMessage').text(errorMessage);
					$('#myModal').modal();
				}
				else {
					$('#isDriver').val('false');
					$('#search-form').submit();
				}
			});
		});

		$( ".input-append" ).change(function() {
			$(this).removeClass("control-group").removeClass("error");
		});

		function getErrorMessage() {
			var errorMessage = '';
			var travelDateText = $('#travelDate').val();
			if(travelDateText) {
				
			}
			else {
				$('#travelDate').addClass("control-group").addClass("error");
				errorMessage = "Travel Date/Time";
			}
			var startPointText = $('#from').val();
			if(startPointText) {
				
			}
			else {
				$('#fromDiv').addClass("control-group").addClass("error");
				if(errorMessage) {
					errorMessage = errorMessage + ", From Location";
				}
				else {
					errorMessage = "From Location";
				}
				
			}
			var toPointText = $('#to').val();
			if(toPointText) {
				
			}
			else {
				$('#toDiv').addClass("control-group").addClass("error");
				if(errorMessage) {
					errorMessage = errorMessage + ", To Location"
				}
				else {
					errorMessage = "To Location";
				}				
			}
			if(errorMessage) {
				errorMessage = "Empty Fields : " + errorMessage;
			}
			return errorMessage;
		}
	</script>
</body>
</html>
