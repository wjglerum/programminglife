//retrieve data from html
var left = $(".position").data("left");
var right = $(".position").data("right");
var marker = $(".position").data("marker");	
var id = $(".position").data("id");

var leftBar;
var rightBar;
var markerBar;


//check where the marker is, on the left, right or middle
if (marker < left) {
	markerBar = "10%";
	leftBar = Math.floor((left - marker) / (right - marker) * 100);
	leftBar += "%";
	rightBar = "90%";
} else if (marker > right) {
	markerBar = "90%";
	leftBar = "10%";
	rightBar = Math.floor((right - left) / (marker - left) * 100);
	rightBar += "%";
} else {
	markerBar = Math.floor((marker - left) / (right - left) * 100);
	markerBar += "%";
	leftBar = "10%";
	rightBar = "90%";
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


var mutation = svgContainer.append("svg:image")
	.attr('x', markerBar)
	.attr('y', 50)
	.attr('width', 50)
	.attr('height', 50)
	.attr("xlink:href","/assets/images/marker.png");

var text = svgContainer.append("svg:text") 
	.attr("x", markerBar) 
	.attr("y", 50) 
	.text(id); 
	
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