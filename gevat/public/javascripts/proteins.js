var load, draw;
var limit, threshold;
var proteins;
var relations;

function proteinsGraph(proteins, relations) {
	var g = new Graph();

	var render = function(r, node) {
        var ellipse = r.ellipse(0, 0, 30, 20).attr({fill: "#7af", stroke: "#7af", "stroke-width": 5});
        
        ellipse.node.id = node.label || node.id;
        
        shape = r.set().
            push(ellipse).
            push(r.text(0, 30, node.label || node.id));
        
        return shape;
    }
	
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
	
	for (i = 0; i < relations.length; i++) {
		var from = relations[i].from;
		var to = relations[i].to;
		
		var strokeWidth = Math.round(2.5 * Math.pow((relations[i].score / relationsScoreAvg), 2));
		
		g.addEdge(from, to, {label: relations[i].score, "width": strokeWidth, "background": "#eee"});
	}
	
	var layouter = new Graph.Layout.Spring(g);
	var renderer = new Graph.Renderer.Raphael("canvas", g, $("#canvas").width(), 400);
	
	draw = function() {
        layouter.layout();
    	renderer.draw();
    	
		$("input#limit").val(limit);
		$("input#threshold").val(threshold);
    };
    
    draw();
}

function proteinsTable(proteins) {
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

$(document).ready(function() { if (typeof proteinsData !== 'undefined') {
	load = function(proteins, relations) {
		proteinsGraph(proteins, relations);
		proteinsTable(proteins);
	}
	
	proteins = proteinsData.proteins;
	relations = proteinsData.relations;
	limit = proteinsData.limit;
	threshold = proteinsData.threshold;
	
	load(proteins, relations);
	
	$(".visualisation-proteins-relations .redraw").click(function() {;
		if (typeof draw == 'function')
			draw();
	});
	
	$(".visualisation-proteins-relations .reload").click(function() {
		var newProteins, newRelations;
		var newLimit, newThreshold;
		
		newLimit = $("input#limit").val();
		newThreshold = $("input#threshold").val();
		
		// Check if limit and threshold has changed
		if (newLimit == limit && newThreshold == threshold) {
			// Just use the old values if not changed
			newProteins = proteins;
			newRelations = relations;
		} else {
			// TODO Get new values via ajax if changed
			newProteins = proteins;
			newRelations = relations;
		}
		
		load(newProteins, newRelations);
	});
}});