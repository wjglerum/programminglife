jQuery(document).ready(function($) {
	// Make table rows clickable as links by using the href attribute of <tr> element
	$(".clickable-rows tbody tr").not(".not-clickable").click(function() {
		window.document.location = $(this).attr("href");
	});
	$(".clickable-rows tbody tr a").click(function(e) {
		e.stopPropagation();
	});
	
	// Remove a patient via Ajax
	$(".table-patients .remove-patient").click(function() {
		var p_id = $(this).parent().parent().data('patient');
		
		jsRoutes.controllers.Patients.remove(p_id).ajax({
			success: function() {
				$(".table-patients tr[data-patient=" + p_id + "]").remove();
			}
		});
	});
});