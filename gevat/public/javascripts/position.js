//retrieve data from html
var left = $(".position").data("left");
var right = $(".position").data("right");
var marker = $(".position").data("marker");	

var leftBar;
var rightBar;
var markerBar;


//check where the marker is, on the left, right or middle
if(marker < left) {
	markerBar = "10%";
	leftBar = Math.floor((left - marker) / (right - marker) * 100);
	leftBar += "%";
} else {
	leftBar = "10%";
	markerBar = Math.floor((marker - left) / (right - left) * 100);
	markerBar += "%";
}

if(marker > right) {
	markerBar = "90%";
	rightBar = Math.floor((right - left) / (marker - left) * 100);
	rightBar += "%";
} else {
	rightBar = "90%";
	markerBar = Math.floor((marker - left) / (right - left) * 100);
	markerBar += "%";
}

//make a container for svg
var svgContainer = d3.select("div.position")
	.append("svg")
	.attr("width", "100%")
	.attr("height", 200);


//make the bar
var bar = svgContainer.append("rect")
	.attr("x", "5%")
	.attr("y", 100)
	.attr("rx", 20)
	.attr("ry", 20)
	.attr("width", "95%")
	.attr("height", 25)
	.attr("stroke-width", 3)
	.attr("stroke", "black")
	.attr("fill-opacity", 0.1);


//draw the red mutation
var mutation = svgContainer.append("line")
	.attr("x1", markerBar)
	.attr("y1", 100)
	.attr("x2", markerBar)
	.attr("y2", 125)
	.attr("stroke-width", 10)
	.attr("stroke", "red");
	
//draw the left edge of gene
var edge1 = svgContainer.append("line")
	.attr("x1", leftBar)
	.attr("y1", 100)
	.attr("x2", leftBar)
	.attr("y2", 125)
	.attr("stroke-width", 5)
	.attr("stroke", "black");
	
	
//draw the right edge of gene
var edge2 = svgContainer.append("line")
	.attr("x1", rightBar)
	.attr("y1", 100)
	.attr("x2", rightBar)
	.attr("y2", 125)
	.attr("stroke-width", 5)
	.attr("stroke", "black");