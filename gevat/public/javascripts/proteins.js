$(document).ready(function() {
	var g = new Graph();
	 
	g.addEdge("DCLRE1B", "RSBN1");
	g.addEdge("C1orf64", "RSBN1");
	g.addEdge("QARS", "RSBN1");
	g.addEdge("SUMO1", "RSBN1");
	g.addEdge("DUX4L4", "RSBN1");
	g.addEdge("DUX4L9", "RSBN1");
	g.addEdge("SMG7", "RSBN1");
	g.addEdge("PRKACA", "RSBN1");
	g.addEdge("SMC4", "RSBN1");
	g.addEdge("SMC4", "RSBN1");
	g.addEdge("SMC4", "RSBN1");
	 
	var layouter = new Graph.Layout.Spring(g);
	layouter.layout();
	 
	var renderer = new Graph.Renderer.Raphael('canvas', g, 400, 300);
	renderer.draw();
});