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
