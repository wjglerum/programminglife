$(document).ready(function() { if (typeof proteinsData !== 'undefined') {
	var g = new Graph();

	var proteins = proteinsData.proteins;
	var relations = proteinsData.relations;

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
		
		g.addNode(protein, {render: render});
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
	layouter.layout();
	 
	var renderer = new Graph.Renderer.Raphael("canvas", g, 800, 400);
	renderer.draw();
}});