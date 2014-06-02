$(document).ready(function() {
	var g = new Graph();
	
	var relations = proteinsData.relations;
	
	for (i = 0; i < relations.length; i++) {
		var from = relations[i].from;
		var to = relations[i].to;
		
		g.addEdge(from, to, {label: relations[i].score});
	}
	
	var layouter = new Graph.Layout.Spring(g);
	layouter.layout();
	 
	var renderer = new Graph.Renderer.Raphael("canvas", g, 800, 400);
	renderer.draw();
});