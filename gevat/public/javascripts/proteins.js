$(document).ready(function() {
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