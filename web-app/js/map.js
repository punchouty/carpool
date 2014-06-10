var rendererOptions;
var directionsDisplay;
var directionsService;
var centerOfIndia = new google.maps.LatLng(23.843138,79.44171);
var defaultZoom = 13;
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
		visible : false,
		title: 'Current Location'
	});
	google.maps.event.addListener(marker, 'dragend', function(){
	    geocodePosition(marker.getPosition());
	});
	google.maps.event.addListener(directionsDisplay,
			'directions_changed', function() {
			changeDirections(directionsDisplay.directions);
	});
	$('#fromPlace').val("");
	$('#toPlace').val("");
}

function geocodePosition(position) {
	geocoder.geocode({'latLng': position}, function(results, status) {
	   if (status == google.maps.GeocoderStatus.OK) {
			if (results[1]) {
				$('#fromLatitude').val(position.lat());
				$('#fromLongitude').val(position.lng());
		    	$('#fromPlace').val(results[1].formatted_address);
	    	} 
	   } 
	});
}
///*
$(function() {
	init();
	/*
	$.getJSON('http://freegeoip.net/json/', function(location) {
		from = new google.maps.LatLng(parseFloat(location.latitude), parseFloat(location.longitude));
		map.setCenter(from);
		map.setZoom(defaultZoom);
		marker.setPosition(from);
		marker.setVisible(true);
		geocodePosition(from);
	});
	*/
	directionsDisplay.setMap(map);
});
//*/		
$(function() {	
	$('#fromPlace').placesSearch({
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
				marker.setVisible(true);
			}	
		}
	});
	$('#toPlace').placesSearch({
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
		$('#fromPlace').val(start_address);
		$('#toPlace').val(end_address);
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

var now = new Date();
var reserveTime = 30;//in minutes
var timeLimitInDays = 7;//in days
var validStartTime = new Date(now.getTime() + reserveTime * 60000);
var initialTime = new Date(now.getTime() + (reserveTime + 15) * 60000);
//console.log(validStartTime);
var validEndTime = new Date(now.getTime() + timeLimitInDays * 24 * 60 * 60000);
//console.log(validEndTime);

//Date format for date.js library - dd MMMM yyyy    hh:mm tt - map.js
//This is different from that of datetime plugin which is - dd MM yyyy    HH:ii P - search.gsp
//This in turn is different from Joda date format - dd MMMM yyyy    hh:mm a - JourneyController.groovy
$('#validStartTimeString').val(validStartTime.toString('dd MMMM yyyy    hh:mm tt'));

var picker;
$(function() {
//	$('#travelDateDiv').datetimepicker({
//		language : 'pt-BR',
//	    pick12HourFormat: false,
//		pickSeconds : false,
//		startDate : initialTime,
//		format : 'dd/MM/yyyy hh:mm'
//	});
//	picker = $('#travelDateDiv').data('datetimepicker');
//	picker.setLocalDate(initialTime);
	$('#travelDateDiv').datetimepicker({
        //language:  'fr',
        weekStart: 1,
        todayBtn:  1,
		autoclose: 1,
		todayHighlight: 1,
		startView: 2,
		forceParse: 0,
        showMeridian: 1
    });
});

$(function() {
	$('#driver').click(function() {		
		var errorMessage = getErrorMessage();
		alert(errorMessage.length);
		if(errorMessage){	
			alert("if "+errorMessage);
			$('#errorMessage').text(errorMessage);
			$('#myModal').modal('show')
			$('#myModal').modal({show:true})
		}
		else {
			alert("else");
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
	var travelDateText = $('#dateOfJourneyString').val();
	if(travelDateText) {
		var selectedDate = new Date(travelDateText);
		if(selectedDate < now) {
			$('#dateOfJourneyString').addClass("control-group").addClass("error");
			errorMessage = "You have selected past date/time";
			return errorMessage;
		}
		else if(selectedDate < validStartTime) {
			$('#dateOfJourneyString').addClass("control-group").addClass("error");
			errorMessage = "You can select time only after 30 minutes from now";
		}
		else if(selectedDate > validEndTime) {
			$('#dateOfJourneyString').addClass("control-group").addClass("error");
			errorMessage = "You can not select time more than seven days in future";
		}
	}
	else {
		$('#dateOfJourneyString').addClass("control-group").addClass("error");
		errorMessage = "Travel Date/Time";
	}
	var startLatitude = parseFloat($('#fromLatitude').val());
	var startLongitude = parseFloat($('#fromLongitude').val());
	var endLatitude = parseFloat($('#toLatitude').val());
	var endLongitude = parseFloat($('#toLongitude').val());
	
	if(startLatitude <= 0 || startLongitude <= 0) {
		$('#fromPlace').addClass("control-group").addClass("error");
		if(errorMessage) {
			errorMessage = errorMessage + ", From Location";
		}
		else {
			errorMessage = "From Location";
		}		
	}
	else {		
		var startPointText = $('#fromPlace').val();
		if(startPointText) {
			
		}
		else {
			$('#fromPlace').addClass("control-group").addClass("error");
			if(errorMessage) {
				errorMessage = errorMessage + ", From Location";
			}
			else {
				errorMessage = "From Location";
			}			
		}		
	}
	
	if(endLatitude <= 0 || endLongitude <= 0) {
		$('#toPlace').addClass("control-group").addClass("error");
		if(errorMessage) {
			errorMessage = errorMessage + ", To Location"
		}
		else {
			errorMessage = "To Location";
		}			
	}
	else {
		var toPointText = $('#toPlace').val();
		if(toPointText) {
			
		}
		else {
			$('#toPlace').addClass("control-group").addClass("error");
			if(errorMessage) {
				errorMessage = errorMessage + ", To Location"
			}
			else {
				errorMessage = "To Location";
			}				
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