/*
$(function() {
    $(".form_datetime").datetimepicker({
        format: "dd M yy HH:ii P",
        autoclose: true,
        minuteStep: 15,
        showMeridian: true,
        pickerPosition: "top-left"
    });
    $('#fromPlace').placesSearch({
        onSelectAddress : function(result) {

        }
    });
    $('#toPlace').placesSearch({
        onSelectAddress : function(result) {

        }
    });
});
*/
var incomingDiv = null;
var outgoingDiv = null;
$(function() {
    $(".outgoing-requests").hide();
    $(".incoming-requests").hide();
    $.each ($(".incoming-btn"), function(i, button){
        var id = button.getAttribute("data-requestId");
        var buttonIdIncoming = '#' + button.getAttribute("id");
        var targetDivIdIncoming = "#incoming-requests-" + id;
        var parentDiv = '#request-' + id;
        $(buttonIdIncoming).on('click', function(){
            if($(targetDivIdIncoming).is(":visible")) {
                $(targetDivIdIncoming).slideUp();
            }
            else {
                if(incomingDiv) {
                    incomingDiv.slideUp();
                    incomingDiv = null;
                }
                if(outgoingDiv) {
                    outgoingDiv.slideUp();
                    outgoingDiv = null;
                }

                $(targetDivIdIncoming).slideDown();
                incomingDiv = $(targetDivIdIncoming);
            }
        });
    });
    $.each ($(".outgoing-btn"), function(i, button){
        var id = button.getAttribute("data-requestId");
        var buttonIdOutgoing = '#' + button.getAttribute("id");
        var targetDivIdOutgoing = "#outgoing-requests-" + id
        var parentDiv = '#request-' + id;
        $(buttonIdOutgoing).on('click', function(){
            if($(targetDivIdOutgoing).is(":visible")) {
                $(targetDivIdOutgoing).slideUp();
            }
            else {
                if(incomingDiv) {
                    incomingDiv.slideUp();
                    incomingDiv = null;
                }
                if(outgoingDiv) {
                    outgoingDiv.slideUp();
                    outgoingDiv = null;
                }

                $(targetDivIdOutgoing).slideDown();
                outgoingDiv = $(targetDivIdOutgoing);
            }
        });
    });
});
