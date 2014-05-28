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
				// Remove the patient data from the table
				$(".table-patients tr[data-patient=" + p_id + "]").remove();
				
				// Display the 'no patients found' message if there are no patients left
				if ($(".table-patients tr.patient").length == 0) {
					$(".table-patients tr.no-patients").show();
				}
			}
		});
	});

	// Highlight mutations in the table while hovering over a mutation in the visualization
	$(".visualization-mutations .mutation").hover(function(){
		var mutationId = $(this).data("mutation-id");

		$(".table-mutations tr[data-mutation-id=" + mutationId + "]").addClass("highlight");
	}, function() {
		var mutationId = $(this).data("mutation-id");
		
		$(".table-mutations tr[data-mutation-id=" + mutationId + "]").removeClass("highlight");
	});
});