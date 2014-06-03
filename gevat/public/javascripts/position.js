var svgContainer = d3.select("div.position")
	.append("svg")
	.attr("width", "100%")
	.attr("height", 200);

var bar = svgContainer.append("rect")
	.attr("x", 5)
	.attr("y", 100)
	.attr("rx", 20)
	.attr("ry", 20)
	.attr("width", "95%")
	.attr("height", 25)
	.attr("stroke-width", 3)
	.attr("stroke", "black")
	.attr("fill-opacity", 0.1);

var marker = svgContainer.append("line")
	.attr("x1", "50%")
	.attr("y1", 100)
	.attr("x2", "50%")
	.attr("y2", 125)
	.attr("stroke-width", 10)
	.attr("stroke", "red");
	
var edge1 = svgContainer.append("line")
	.attr("x1", "40%")
	.attr("y1", 100)
	.attr("x2", "40%")
	.attr("y2", 125)
	.attr("stroke-width", 5)
	.attr("stroke", "black");
	
var edge2 = svgContainer.append("line")
	.attr("x1", "70%")
	.attr("y1", 100)
	.attr("x2", "70%")
	.attr("y2", 125)
	.attr("stroke-width", 5)
	.attr("stroke", "black");