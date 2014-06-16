var patientTimers = {};

$(document).ready(function () {
    // Start the table sorter
    $(".table-patients").tablesorter({
        // Automatically sort on the phred quality score
        sortList:  [[2,0]]
    });
    // Remove a patient via Ajax
    $(".table-patients .remove-patient").click(function() {
        var p_id = $(this).parent().parent().data('patient');

        jsRoutes.controllers.Patients.remove(p_id).ajax({
            success: function() {
                // Remove the patient data from the table
                $(".table-patients tr[data-patient=" + p_id + "]").remove();

                // Display the 'no patients found' message if there are no patients left
                if ($(".table-patients tr.patient").length == 0) {
                    $(".table-patients tr.no-patients").show();
                }
            }
        });
    });
    
    $(".table-patients .patient.processing").each(function() {
    	var p_id = $(this).data('patient');
    	
    	// Check if processing finished every 3 seconds
    	patientTimers[p_id] = setInterval(function(){
    		jsRoutes.controllers.Patients.isProcessed(p_id).ajax({
                success: function(data) {
                    if (data != '0') {
                    	$(".table-patients .patient[data-patient=" + p_id + "]").html(data);
                    	
                    	window.clearInterval(patientTimers[p_id]);
                    }
                }
            });
    	}, 3000);
    });
});