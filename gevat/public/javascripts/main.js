jQuery(document).ready(function($) {
	// Make table rows clickable as links by using the href attribute of <tr> element
	$(".clickable-rows tbody tr").not(".not-clickable").click(function() {
		window.document.location = $(this).attr("href");
	});
});