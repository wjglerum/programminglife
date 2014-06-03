var Proteins = {};

Proteins.graph = function (proteins, relations) {
	var g = new Graph();

	// Function to render the ellipses representing proteins
	var render = function(r, node) {
        var ellipse = r.ellipse(0, 0, 30, 20).attr({fill: "#7af", stroke: "#7af", "stroke-width": 5});
        
        ellipse.node.id = node.label || node.id;
        
        shape = r.set().
            push(ellipse).
            push(r.text(0, 30, node.label || node.id));
        
        return shape;
    }
	
	// Add the proteins to the graph
	for (i = 0; i < proteins.length; i++) {
		var protein = proteins[i];
		
		g.addNode(protein.name, {render: render});
	}
	
	// Determine the sum of all relations to get an average
	var relationsScoreSum = 0;
	
	for (i = 0; i < relations.length; i++) {
		relationsScoreSum += relations[i].score;
	}
	
	relationsScoreAvg = relationsScoreSum / relations.length;
	
	// Add all relations to the graph with stroke width based on score relative to average
	for (i = 0; i < relations.length; i++) {
		var from = relations[i].from;
		var to = relations[i].to;
		
		var strokeWidth = Math.round(2.5 * Math.pow((relations[i].score / relationsScoreAvg), 2));
		
		g.addEdge(from, to, {label: relations[i].score, "width": strokeWidth, "background": "#eee"});
	}
	
	var layouter = new Graph.Layout.Spring(g);
	var renderer = new Graph.Renderer.Raphael("canvas", g, $("#canvas").width(), 400);
	
	Proteins.draw = function() {
        layouter.layout();
    	renderer.draw();
    	
    	// Update the input fields matching to the recently drawn graph 
		$("input#limit").val(Proteins.data.limit);
		$("input#threshold").val(Proteins.data.threshold);
    };
    
    // Doe initial drawing
    Proteins.draw();
}

Proteins.table = function (proteins) {
	// First remove old rows
	$(".table-proteins tr.protein").remove();
	
	// Add the new proteins
	for (i = 0; i < proteins.length; i++) {
		var protein = proteins[i];
		
		$(".table-proteins tbody").append("<tr class=\"protein\"><td>" + protein.name + "</td><td>" + protein.annotations + "</td></tr>");
	}
	
	// Display the 'no proteins found' message if there are no proteins
	if ($(".table-proteins tr.protein").length == 0) {
		$(".table-proteins tr.no-proteins").show();
	}
}

$(document).ready(function() { if (typeof initProteinsData !== 'undefined') {
	Proteins.load = function(data) {
		Proteins.data = data;
		
		var proteins = Proteins.data.proteins;
		var relations = Proteins.data.relations;
		
		// Clear the canvas, might include an old graph
		$("#canvas").empty();
		
		// Setup both the graph and table with info about the proteins
		Proteins.graph(proteins, relations);
		Proteins.table(proteins);
	}
	
	// Save and visualise initial data
	Proteins.load(initProteinsData);
	
	// Redraw button handler
	$(".visualisation-proteins-relations .redraw").click(function() {;
		if (typeof Proteins.draw == 'function')
			Proteins.draw();
	});
	
	// Reload does the same as redraw if the limit/threshold aren't changed
	$(".visualisation-proteins-relations .reload").click(function() {
		if (typeof Proteins.draw == 'function') {
			var limit = $("input#limit").val();
			var threshold = $("input#threshold").val();
			
			// Check if limit and threshold has changed
			if (Proteins.limit == limit && Proteins.threshold == threshold) {
				// Just redraw if data hasn't changed
				Proteins.draw();
			} else {
				Proteins.limit = limit;
				Proteins.threshold = threshold;			
				
				// Get new values via ajax if changed, save data and reload/revisualise
				var p_id = $(this).data("patient");
				var m_id = $(this).data("mutation");
	
				jsRoutes.controllers.Mutations.proteinsJSON(p_id, m_id, limit, threshold).ajax({
					success: function(data) {
						Proteins.load(data);
					}
				});
			}
		}
	});
}});