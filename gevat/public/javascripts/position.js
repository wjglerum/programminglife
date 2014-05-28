var svgContainer = d3.select("div.position")
	.append("svg")
	.attr("width", "100%")
	.attr("height", 200);

var line = svgContainer.append("line")
	.attr("x1", 0)
	.attr("y1", 100)
	.attr("x2", "100%")
	.attr("y2", 100)
	.attr("stroke-width", 5)
	.attr("stroke", "black");

var marker = svgContainer.append("line")
	.attr("x1", "50%")
	.attr("y1", 50)
	.attr("x2", "50%")
	.attr("y2", 100)
	.attr("stroke-width", 10)
	.attr("stroke", "red");