var Proteins = {};

Proteins.graph = function (proteins, relations) {
	var g = new Graph();

	// Function to render the ellipses representing proteins
	var render = function(r, node, mutated) {
		var fill = "#67B3DD";
		
		if (mutated)
			fill = "#ff0000";
		
        var ellipse = r.ellipse(0, 0, 30, 20).attr({fill: fill, stroke: "#4D9CC7", "stroke-width": 5});
        
        ellipse.node.id = node.label || node.id;
        
        shape = r.set().
            push(ellipse).
            push(r.text(0, 30, node.label || node.id));
        
        return shape;
    }
	
	var renderEmpty = function(r, node) {
		return render(r, node, false);
	}
	var renderMutated = function(r, node) {
		return render(r, node, true);
	}
	
	// Add the proteins to the graph
	for (i = 0; i < proteins.length; i++) {
		var protein = proteins[i];
		
		// Commented, can be used if .hasMutation is given through JSON
		if (protein.hasMutation)
			g.addNode(protein.name, {render: renderMutated});
		else
			g.addNode(protein.name, {render: renderEmpty});
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
		
		var x = Math.pow((relations[i].score / relationsScoreAvg), 2);
		
		var strokeWidth = Math.round(2.5 * x);
		
		var intensity = Math.round(Math.min(230, Math.max(0, 255 - 128 * x)));
		
		// Convert intensity to hex
		intensity = intensity.toString(16);
		
		g.addEdge(from, to, {label: relations[i].score, "stroke": "#" + intensity + intensity + intensity, "width": strokeWidth, "background": "#eee"});
	}
	
	var layouter = new Graph.Layout.Spring(g);
	var renderer = new Graph.Renderer.Raphael("canvas", g, $("#canvas").width(), 400);
	
	Proteins.g = g;
	Proteins.draw = function() {
        layouter.layout();
    	renderer.draw();
    	
    	// Update the input fields matching to the recently drawn graph 
		$("input#limit").val(Proteins.data.limit);
		$("input#threshold").val(Proteins.data.threshold);
		
		// Set mouse handlers
		for (var id in g.nodes) {
		   g.nodes[id].shape.mouseover(function() {
			   $(".table-proteins tr[data-protein-id='" + this[0].id + "']").addClass("highlight");
		   });
		   g.nodes[id].shape.mouseout(function() {
			   $(".table-proteins tr[data-protein-id='" + this[0].id + "']").removeClass("highlight");
		   });
		   g.nodes[id].shape.mousedown(function() {
			   // Remove selected class in graph
			   for (var id2 in Proteins.g.nodes) {
				   $("ellipse#" + id2).attr("class", "");
			   }
			   
			   // Give the svg protein the selected class
			   $("ellipse#" + this[0].id).attr("class", "selected");
			   
			   // Remove selected class in table
			   $(".table-proteins tr").removeClass("selected");
			   
			   // Remove the protein row in table...
			   var row = $(".table-proteins tr[data-protein-id='" + this[0].id + "']").remove();
			   
			   row = $(row).addClass("selected");
			   
			   // ...and add it to the top with the selected class
			   $('.table-proteins tbody tr:first').before(row);
		   });
		}
    };
    
    // Doe initial drawing
    Proteins.draw();
}

Proteins.table = function (proteins) {
	// First remove old rows
	$(".table-proteins tr.protein").remove();
	
	// Add the new proteins
	for (var i = 0; i < proteins.length; i++) {
		var protein = proteins[i];
		var related = "";
		
		console.log(protein.related);
		
		for (var j = 0; j < protein.related.length; j++) {
			var mutation = protein.related[j];
			
			if (j > 0)
				related += ", ";
			
			related += "<a href=\"/patients/" + mutation.patient + "/mutation/" + mutation.id + "\">" + mutation.rsid + "<a/>";
		}
		
		$(".table-proteins tbody").append("<tr data-protein-id=\"" + protein.name + "\" class=\"protein\"><td>" + protein.name + "</td><td>" + protein.annotations + "</td><td>" + protein.disease + "</td><td>" + related + "</td></tr>");
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

		// Show/hide right stuff
		if (proteins.length > 0) {
			$(".visualisation-proteins-relations .no-proteins").hide();
			
			// Setup both the graph and table with info about the proteins
			Proteins.graph(proteins, relations);

			$(".visualisation-proteins-relations #canvas").show();
		} else {
			$(".visualisation-proteins-relations #canvas").hide();
			$(".visualisation-proteins-relations .no-proteins").show();
		}
		
		Proteins.table(proteins);
	}
	
	// Save and visualise initial data
	Proteins.load(initProteinsData);
	
	// Revisualise the graph, only reload data via Ajax if the limit/threshold are changed
	$(".visualisation-proteins-relations .revisualise").click(function() {
		if (typeof Proteins.draw == 'function') {
			var limit = $("input#limit").val();
			var threshold = $("input#threshold").val();
			
			if (isNaN(limit) || isNaN(threshold)) {
				alert("Please enter limit and threshold as numeric values");
			} else if (limit < 1 || limit > 50 || threshold < 1 || threshold > 10000) {
				alert("Please enter limit in range [1, 50] and threshold in range [1, 10000].");
			} else {
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
		}
	});
}});