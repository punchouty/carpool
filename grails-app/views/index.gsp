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
				<input id="travelDate" type="text" class="search-field" placeholder="Date and Time" data-format="dd/MM/yyyy hh:mm PP"></input> 
				<a href="#" class="add-on"> 
					<i data-time-icon="icon-time" data-date-icon="icon-calendar"> </i>
				</a>
			</div>			
			
			<div id="fromDiv" class="input-append dropdown">
				<input id="from" type="text" placeholder="Trip Start Location"></input> 
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
				<input id="to" type="text" placeholder="Trip End Location"></input>
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
	<div id="map-canvas" class="map_canvas"></div>
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
		var rendererOptions;
		var directionsDisplay;
		var directionsService;
		var centerOfIndia = new google.maps.LatLng(23.843138,79.44171);
		var defaultZoom = 13;
		var reserveTime = 30;//in minutes
		var from = centerOfIndia;
		var to;
		var mapOptions;
		var map;			
		var marker;
		var geocoder;
		function init() {
			rendererOptions = {
				draggable : true
			};
			mapOptions = {
				center : from,
			    mapTypeControl: true,
			    mapTypeControlOptions: {
			      style: google.maps.MapTypeControlStyle.DROPDOWN_MENU
			    },
			    zoomControl: true,
			    zoomControlOptions: {
			      style: google.maps.ZoomControlStyle.SMALL
			    },
				zoom : 4,
				mapTypeId : google.maps.MapTypeId.ROADMAP
			// ROADMAP | SATELLITE | HYBRID | TERRAIN
			};
			directionsDisplay = new google.maps.DirectionsRenderer(rendererOptions);
			directionsService = new google.maps.DirectionsService();
			map = new google.maps.Map(document.getElementById("map-canvas"), mapOptions);
			geocoder = new google.maps.Geocoder();
			marker = new google.maps.Marker({
				draggable:true,
				position: from,
				map: map,
				flat : true,
				title: 'Current Location'
			});
			google.maps.event.addListener(marker, 'dragend', function(){
			    geocodePosition(marker.getPosition());
			});
			google.maps.event.addListener(directionsDisplay,
					'directions_changed', function() {
					changeDirections(directionsDisplay.directions);
			});
			$('#from').val("");
			$('#from').val("");
			$('#to').val("");
		}

		function geocodePosition(position) {
			geocoder.geocode({'latLng': position}, function(results, status) {
			   if (status == google.maps.GeocoderStatus.OK) {
					if (results[1]) {
						$('#fromLatitude').val(position.lat());
						$('#fromLongitude').val(position.lng());
				    	$('#from').val(results[1].formatted_address);
			    	} 
			   } 
			});
		}
		///*
		$(function() {
			init();
			$.getJSON('http://freegeoip.net/json/', function(location) {
				from = new google.maps.LatLng(parseFloat(location.latitude), parseFloat(location.longitude));
				map.setCenter(from);
				map.setZoom(defaultZoom);
				marker.setPosition(from);
				marker.setVisible(true);
				geocodePosition(from);
			});
			directionsDisplay.setMap(map);
		});
		//*/		
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
						map.setZoom(defaultZoom);
						map.panTo(from);
						marker.setPosition(from);
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
						map.setZoom(defaultZoom);
						map.panTo(to);
					}
				}
			});
		});
		
		function calcRoute(from, to) {
			marker.setVisible(false);
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
			setMapHelp();
		}
		
		function computeTotalDistance(result) {
			var total = 0;
			var myroute = result.routes[0];
			var numberOfLegs = 0;
			for ( var i = 0; i < myroute.legs.length; i++) {
				total += myroute.legs[i].distance.value;
				var sla = $('#fromLatitude').val();
				var slo = $('#fromLongitude').val();
				var ela = $('#toLatitude').val();
				var elo = $('#toLongitude').val();
				var start_location = myroute.legs[i].start_location
				var end_location = myroute.legs[i].end_location
				var start_address = myroute.legs[i].start_address
				var end_address = myroute.legs[i].end_address
				numberOfLegs++;
				console.log(sla + ":" + start_location.lat() + ", " + slo + ":" + start_location.lng() + ", " +ela + ":" + end_location.lat() + ", " + elo + ":" + end_location.lng())
			}
			if(numberOfLegs != 1) {
				console.log("numberOfLegs : " + numberOfLegs);
			}
			total = total / 1000.
			$('#tripUnit').val('KM');
			$('#tripDistance').val(total + '');
		}

		function changeDirections(result) {
			var total = 0;
			var myroute = result.routes[0];
			var numberOfLegs = 0;
			for ( var i = 0; i < myroute.legs.length; i++) {
				total += myroute.legs[i].distance.value;
				var sla = $('#fromLatitude').val();
				var slo = $('#fromLongitude').val();
				var ela = $('#toLatitude').val();
				var elo = $('#toLongitude').val();
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
				console.log(sla + ":" + start_location.lat() + ", " + slo + ":" + start_location.lng() + ", " +ela + ":" + end_location.lat() + ", " + elo + ":" + end_location.lng())
			}
			if(numberOfLegs != 1) {
				console.log("numberOfLegs : " + numberOfLegs);
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
		var picker;
		var now;
		$(function() {
			now = new Date();
			now.setMinutes(now.getMinutes() + reserveTime);
			$('#travelDateDiv').datetimepicker({
				language : 'pt-BR',
			    //pick12HourFormat: true,
				pickSeconds : false,
				startDate : now,
				format : 'dd/MM/yyyy hh:mm PP'
			});
			picker = $('#travelDateDiv').data('datetimepicker');
			picker.setLocalDate(now);
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
				var selectedDate = picker.getLocalDate();
				if(selectedDate < new Date()) {
					$('#travelDate').addClass("control-group").addClass("error");
					errorMessage = "You have selected past date/time";
					return errorMessage;
				}
				else if(selectedDate < now) {
					$('#travelDate').addClass("control-group").addClass("error");
					errorMessage = "You can select time only after 30 minutes from now";
				}
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
			var tripDistanceInKm = $('#tripDistance').val();
			if(tripDistanceInKm) {
				if(tripDistanceInKm <= 1) {
					errorMessage = errorMessage + ", Distance you want to travel is very less"
				}
			}
			if(errorMessage) {
				errorMessage = "Error Fields : " + errorMessage;
			}
			return errorMessage;
		}
		</script>
</body>
</html>
