var rendererOptions;
var directionsDisplay;
var directionsService;
var centerOfIndia = new google.maps.LatLng(23.843138,79.44171);
var defaultZoom = 13;
//var from = centerOfIndia;
var from;
var to;
var mapOptions;
var map;			
var marker;
var geocoder;
function init() {
	clearFields();
//	rendererOptions = {
//		draggable : true
//	};
//	mapOptions = {
//		center : from,
//	    mapTypeControl: true,
//	    mapTypeControlOptions: {
//	      style: google.maps.MapTypeControlStyle.DROPDOWN_MENU
//	    },
//	    zoomControl: true,
//	    zoomControlOptions: {
//	      style: google.maps.ZoomControlStyle.SMALL
//	    },
//		zoom : 4,
//		mapTypeId : google.maps.MapTypeId.ROADMAP
//	// ROADMAP | SATELLITE | HYBRID | TERRAIN
//	};
//	directionsDisplay = new google.maps.DirectionsRenderer(rendererOptions);
	directionsService = new google.maps.DirectionsService();
//	map = new google.maps.Map(document.getElementById("map-canvas"), mapOptions);
	geocoder = new google.maps.Geocoder();
//	marker = new google.maps.Marker({
//		draggable:true,
//		position: from,
//		map: map,
//		flat : true,
//		visible : false,
//		title: 'Current Location'
//	});
//	google.maps.event.addListener(marker, 'dragend', function(){
//	    geocodePosition(marker.getPosition());
//	});
//	google.maps.event.addListener(directionsDisplay,
//			'directions_changed', function() {
//			changeDirections(directionsDisplay.directions);
//	});
//	if($('#_hFromPlace').val()) {
//		$('#fromPlace').val($('#_hFromPlace').val());
//	}
//	else {
//		$('#fromPlace').val("");
//	}
//	
//	if($('#_hToPlace').val()) {
//		$('#toPlace').val($('#_hToPlace').val());
//	}
//	else {
//		$('#toPlace').val("");
//	}
//	if($('#_hFromPlace').val() != null && $('#_hToPlace').val() != null) {
//		from = new google.maps.LatLng($('#fromLatitude').val(), $('#fromLongitude').val());
//		to = new google.maps.LatLng($('#toLatitude').val(), $('#toLongitude').val())
//	}
//	
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
	
//	directionsDisplay.setMap(map);
});
//*/		
$(function() {	
	$('#fromPlace').placesSearch({
		onSelectAddress : function(result) {
			fromPlace = result.geometry.location;
			$('#fromLatitude').val(fromPlace.lat());
			$('#fromLongitude').val(fromPlace.lng());
			if(toPlace != null && fromPlace != null) {
				calcRoute(fromPlace, toPlace);
			}
			else {
//				map.setZoom(defaultZoom);
//				map.panTo(from);
//				marker.setPosition(from);
//				marker.setVisible(true);
			}	
		}
	});
	$('#to').placesSearch({
		onSelectAddress : function(result) {
			toPlace = result.geometry.location;
			$('#toLatitude').val(toPlace.lat());
			$('#toLongitude').val(toPlace.lng());
			if(fromPlace != null && toPlace != null) {
				calcRoute(fromPlace, toPlace);
			}
			else {
//				map.setZoom(defaultZoom);
//				map.panTo(to);
			}
		}
	});
});

function calcRoute(from, to) {
//	marker.setVisible(false);
	var request = {
		origin : from,
		destination : to,
		travelMode : google.maps.DirectionsTravelMode.DRIVING
	};
	directionsService.route(request, function(results, status) {
		if (status == google.maps.DirectionsStatus.OK) {
			computeTotalDistance(results);
			//directionsDisplay.setDirections(response);
		}
	});
//	setMapHelp();
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
	var resultString = JSON.stringify(result);
	console.log("result : " + resultString);
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
		console.log(i + " changeDirections : " + sla + ":" + start_location.lat() + ", " + slo + ":" + start_location.lng() + ", " +ela + ":" + end_location.lat() + ", " + elo + ":" + end_location.lng())
	}
	if(numberOfLegs != 1) {
		console.log("numberOfLegs : " + numberOfLegs);
	}
	total = total / 1000.
	$('#tripUnit').val('KM');
	$('#tripDistance').val(total + '');
}

function setMapHelp() {
	//$('#map-help').text("Drag start and end point to set the exact address.");
}

var now = new Date();
var reserveTime = 25;//in minutes
var timeLimitInDays = 7;//in days
var validStartTime = new Date(now.getTime() + reserveTime * 60000);
var initialTime = new Date(now.getTime() + (reserveTime + 15) * 60000);
//console.log(validStartTime);
var validEndTime = new Date(now.getTime() + timeLimitInDays * 24 * 60 * 60000);
//console.log(validEndTime);

// for configration of time picker
var uiValidStartTime = new Date(now.getTime() + 15 * 60000); // 15 minutes from now
var uiValidEndTime = validEndTime; // 15 minutes from now

//Date format for date.js library - dd MMMM yyyy    hh:mm tt - map.js
//This is different from that of datetime plugin which is - dd MM yyyy    HH:ii P - search.gsp
//This in turn is different from Joda date format - dd MMMM yyyy    hh:mm a - JourneyController.groovy
$('#validStartTimeString').val(validStartTime.toString('dd MMMM yyyy    hh:mm tt'));

$(function() {
	$('#dateOfJourneyString').datetimepicker({
        weekStart: 1,
        //todayBtn:  true,
        startDate: uiValidStartTime,
        endDate: uiValidEndTime,
        todayHighlight: true,
		autoclose: true,
        minuteStep: 15,
        showMeridian: true,
        pickerPosition: "top-left",
        startDate: new Date(),
        format: 'dd M yy HH:ii P'
        //format: 'dd MM yyyy    HH:ii P'
    }).on('changeDate', function(ev){
    	//Nothing
    });

});



$(function() {
	$('#offer').click(function() {	
		var errorMessage = getErrorMessage();		
		if(errorMessage){
//			$('#errorMessage').text(errorMessage);					
//			$('#myModal').modal({show:true});
			$("#error-message").text(errorMessage);
			$('#form-error').fadeIn(1000);
            $('#form-success').fadeOut(500);
		}
		else {
			$('#isDriver').val('true');
			$('#search-form').submit();
		}
	});
	$('#ask').click(function() {
		var errorMessage = getErrorMessage();
		if(errorMessage){
			$("#error-message").text(errorMessage);
			$('#form-error').fadeIn(1000);
            $('#form-success').fadeOut(500);
//			$('#errorMessage').text(errorMessage);			
//			$('#myModal').modal({show:true});
		}
		else {
			$('#isDriver').val('false');
			$('#search-form').submit();
		}
	});
	$('#clear').click(function() {
		clearFields();
	});
});

function clearFields() {
	$('#dateOfJourneyString').val("");
	$('#fromPlace').val("");
	$('#toPlace').val("");
	$('#fromLatitude').val("");
	$('#fromLongitude').val("");
	$('#toLatitude').val("");
	$('#toLongitude').val("");
	$('#tripDistance').val("");
	$('#tripUnit').val("");
	$('#tripDistance').val("");
	$('#_hFromPlace').val("");
	$('#_hToPlace').val("");
}

$( ".input-append" ).change(function() {
	$(this).removeClass("control-group").removeClass("error");
});

function getErrorMessage() {
	var errorMessage = '';
	var travelDateText = $('#dateOfJourneyString').val();
	if(travelDateText) {
		if(travelDateText != "Date and Time") {
			var selectedDate = new Date(travelDateText);
			if(selectedDate < now) {
				//alert(selectedDate.toString('dd MMMM yyyy    hh:mm tt'));
				//$('#dateOfJourneyString').addClass("control-group").addClass("error");
				errorMessage = "You have selected past date/time";
				return errorMessage;
			}
			else if(selectedDate < validStartTime) {
				//$('#dateOfJourneyString').addClass("control-group").addClass("error");
				errorMessage = "You can select time only after 30 minutes from now";
			}
			else if(selectedDate > validEndTime) {
				//$('#dateOfJourneyString').addClass("control-group").addClass("error");
				errorMessage = "You can not select time more than seven days in future";
			}
		}
		else {
			errorMessage = "Date and Time";
		}
	}
	else {
		//$('#dateOfJourneyString').addClass("control-group").addClass("error");
		errorMessage = "Date and Time";
	}
	var startLatitude = parseFloat($('#fromLatitude').val());
	var startLongitude = parseFloat($('#fromLongitude').val());
	var endLatitude = parseFloat($('#toLatitude').val());
	var endLongitude = parseFloat($('#toLongitude').val());
	
	if(startLatitude <= 0 || startLongitude <= 0) {
		//$('#fromPlace').addClass("control-group").addClass("error");
		if(errorMessage) {
			errorMessage = errorMessage + ", From : Landmark";
		}
		else {
			errorMessage = "From : Landmark";
		}		
	}
	else {		
		var startPointText = $('#fromPlace').val();
		if(startPointText) {
			
		}
		else {
			//$('#fromPlace').addClass("control-group").addClass("error");
			if(errorMessage) {
				errorMessage = errorMessage + ", From : Landmark";
			}
			else {
				errorMessage = "From : Landmark";
			}			
		}		
	}
	
	if(endLatitude <= 0 || endLongitude <= 0) {
		//$('#toPlace').addClass("control-group").addClass("error");
		if(errorMessage) {
			errorMessage = errorMessage + ", To : Landmark"
		}
		else {
			errorMessage = "To : Landmark";
		}			
	}
	else {
		var toPointText = $('#to').val();
		if(toPointText) {
			
		}
		else {
			//$('#toPlace').addClass("control-group").addClass("error");
			if(errorMessage) {
				errorMessage = errorMessage + ", To : Landmark"
			}
			else {
				errorMessage = "To : Landmark";
			}				
		}		
	}
	var tripDistanceInKm = $('#tripDistance').val();
	if(tripDistanceInKm) {
		if(tripDistanceInKm <= 1) {
			if(errorMessage) {
				errorMessage = errorMessage + ", Distance you want to travel is very less"
			}
			else {
				errorMessage = "Distance you want to travel is very less"
			}
		}
	}
	if(errorMessage) {
		errorMessage = "Error Fields: - " + errorMessage;
	}
	return errorMessage;
}

jQuery(document).ready(function($) {

    "use strict";
    //set your google maps parameters
    var $latitude = 30.758332, //If you unable to find latitude and longitude of your address. Please visit http://www.latlong.net/convert-address-to-lat-long.html you can easily generate.
        $longitude = 76.633361,
        $map_zoom = 12; /* ZOOM SETTING */

    //google map custom marker icon - .png fallback for IE11
    var is_internetExplorer11 = navigator.userAgent.toLowerCase().indexOf('trident') > -1;
    var $marker_url = (is_internetExplorer11) ? 'images/map-marker.png' : 'images/map-marker.svg';

    //we define here the style of the map
    var style = [{
        "stylers": [{
            "hue": "#00aaff"
        }, {
            "saturation": -100
        }, {
            "gamma": 2.15
        }, {
            "lightness": 12
        }]
    }];

    //set google map options
    var map_options = {
        center: new google.maps.LatLng($latitude, $longitude),
        zoom: $map_zoom,
        panControl: true,
        zoomControl: true,
        mapTypeControl: false,
        streetViewControl: true,
        mapTypeId: google.maps.MapTypeId.ROADMAP,
        scrollwheel: false,
        styles: style
    }
    //inizialize the map
    var map = new google.maps.Map(document.getElementById('google-container'), map_options);
    //add a custom marker to the map				
    var marker = new google.maps.Marker({
        position: new google.maps.LatLng($latitude, $longitude),
        map: map,
        visible: true,
        icon: $marker_url
    });

  
});