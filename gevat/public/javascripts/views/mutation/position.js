//retrieve data from html
var mutation = $(".position").data("mutation");	
var id = $(".position").data("id");

// lowest and highest value in the dataset
var low = Number.MAX_VALUE;
var high = 0;

// compute lowest and highest value
for(i in initPositionsData) {
	if(initPositionsData[i].start < low) low = initPositionsData[i].start;
	if(initPositionsData[i].end > high) high = initPositionsData[i].end;
}

// also check the location of the mutation
if(mutation < low) low = mutation;
if(mutation > high) high = mutation;

// convert to relative percentage
var marker = (mutation - low)/(high - low)*100*0.9+5;
marker += "%";

//make a container for svg
var SVGheight = 100 + initPositionsData.length*20
var svgContainer = d3.select("div.position")
	.append("svg")
	.attr("width", "100%")
	.attr("height", SVGheight + 100);

//draw the red mutation marker
//var mutation = svgContainer.append("svg:image")
//	.attr("x", markerBar)
//	.attr("y", 50)
//	.attr("width", 50)
//	.attr("height", 50)
//	.attr("xlink:href","/assets/images/marker.png");

// draw the line for the mutation
var mutationLine = svgContainer.append("line")
	.attr("x1", marker)
	.attr("y1", 50)
	.attr("x2", marker)
	.attr("y2", SVGheight)
	.attr("stroke", "red")
	.attr("stroke-width", 2);

// append a label to the mutation
var text = svgContainer.append("svg:text") 
	.attr("x", marker) 
	.attr("y", 45) 
	.text(id); 

// add a line per gene
var height = 75;
for(i in initPositionsData) {
	// increment the height for each new line
	height += 20;

	// compute the relative position to the lowest and highest value
	var left = (initPositionsData[i].start - low)/(high - low)*100*0.9+5;
	var right = (initPositionsData[i].end - low)/(high - low)*100*0.9+5;
	var middle = (left + right)/2;

	// make the visualisation larger for very small genes
	if(right-left < 0.1) right = left + 0.1;

	// make everything a percentage for nice scaling
	left += "%";
	right += "%";
	middle += "%";

	// add the line per gene
	var line = svgContainer.append("line")
		.attr("x1", left)
		.attr("y1", height)
		.attr("x2", right)
		.attr("y2", height)
		.attr("stroke", "black")
		.attr("stroke-width", 5);

	// append a text label per gene
	var label = svgContainer.append("svg:text")
		.attr("x", middle)
		.attr("y", height - 5)
		.text(initPositionsData[i].name);
}

console.log(low);
console.log(high);
console.log(marker);
// draw a scale
height += 50;
var scale = svgContainer.append("line")
	.attr("x1", "5%")
	.attr("y1", height)
	.attr("x2", "95%")
	.attr("y2", height)
	.attr("stroke", "black")
	.attr("stroke-width", 1);

var scaleLeft = svgContainer.append("svg:text")
	.attr("x", "2.5%")
	.attr("y", height + 20)
	.text(low);

var scaleRight = svgContainer.append("svg:text")
	.attr("x", "92.5%")
	.attr("y", height + 20)
	.text(high);

var scaleLeftTick = svgContainer.append("line")
	.attr("x1", "5%")
	.attr("y1", height - 5)
	.attr("x2", "5%")
	.attr("y2", height + 5)
	.attr("stroke", "black")
	.attr("stroke-width", 1);

var scaleRightTick = svgContainer.append("line")
	.attr("x1", "95%")
	.attr("y1", height - 5)
	.attr("x2", "95%")
	.attr("y2", height + 5)
	.attr("stroke", "black")
	.attr("stroke-width", 1);

if(marker != "5%" && marker != "95%"){
	var scaleMarker = svgContainer.append("svg:text")
		.attr("x", marker)
		.attr("y", height + 20)
		.text(mutation);
	var scaleMarkerTick = svgContainer.append("line")
		.attr("x1", marker)
		.attr("y1", height - 5)
		.attr("x2", marker)
		.attr("y2", height + 5)
		.attr("stroke", "black")
		.attr("stroke-width", 1);
}
