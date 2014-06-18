//retrieve data from html
var startPoint = $(".position").data("startpoint");
var id = $(".position").data("mid");
var mutationType = $(".position").data("sort");


// lowest and highest value in the dataset
var low = Number.MAX_VALUE;
var high = 0;

// compute lowest and highest value
for(i in positionsData) {
	if(positionsData[i].start < low) low = positionsData[i].start;
	if(positionsData[i].end > high) high = positionsData[i].end;
}

// also check the location of the startPoint
if(startPoint < low) low = startPoint;
if(startPoint > high) high = startPoint;

// convert to relative percentage
var marker = (startPoint - low)/(high - low)*100*0.9+5;
marker += "%";

//make a container for svg
var SVGheight = 100 + positionsData.length*20
var svgContainer = d3.select("div.position")
	.append("svg")
	.attr("width", "100%")
	.attr("height", SVGheight + 100);

// draw the line for the mutation
svgContainer.append("line")
	.attr("x1", marker)
	.attr("y1", 50)
	.attr("x2", marker)
	.attr("y2", SVGheight)
	.attr("stroke", "red")
	.attr("stroke-width", 3)
	.attr("original-title", 
		id
		+ "<br>Position: "
		+ startPoint
		+ "<br>Sort: "
		+ mutationType
	);

// append a label to the mutation
svgContainer.append("svg:text") 
	.attr("x", marker) 
	.attr("y", 45) 
	.attr("original-title", "mutation")
	.text(id);

// add nearby mutations
for(i in nearbyData) {
	// compute the relative position
	var mark = (nearbyData[i].position - low)/(high - low)*100*0.9+5;
	mark += "%";

	// draw the line for the mutation and add href to the mutation
	svgContainer.append("svg:a")
		.attr("xlink:href",
			"/patients/"
			+ nearbyData[i].pid
			+ "/mutation/"
			+ nearbyData[i].mid)
		.append("line")
		.attr("x1", mark)
		.attr("y1", 50)
		.attr("x2", mark)
		.attr("y2", SVGheight)
		.attr("stroke", "red")
		.attr("stroke-width", 2)
		.attr("original-title",
			nearbyData[i].rsid
			+ "<br>Position: "
			+ nearbyData[i].position
			+ "<br>Sort: "
			+ nearbyData[i].sort
		);

	// append a hoverbox with basic info
	$('svg line').tipsy({
		gravity: 'w',
		html: true,
		fallback: "gene"
	});
}

// add a line per gene
var height = 75;
for(i in positionsData) {
	// increment the height for each new line
	height += 20;

	// compute the relative position to the lowest and highest value
	var left = (positionsData[i].start - low)/(high - low)*100*0.9+5;
	var right = (positionsData[i].end - low)/(high - low)*100*0.9+5;
	var middle = (left + right)/2;

	// make the visualisation larger for very small genes
	if(right-left < 0.1) right = left + 0.1;

	// make everything a percentage for nice scaling
	left += "%";
	right += "%";
	middle += "%";

	// add the line per gene
	svgContainer.append("line")
		.attr("x1", left)
		.attr("y1", height)
		.attr("x2", right)
		.attr("y2", height)
		.attr("stroke", "black")
		.attr("stroke-width", 5)
		.attr("id", "gene");

	// append a text label per gene
	svgContainer.append("svg:text")
		.attr("x", middle)
		.attr("y", height - 5)
		.attr("original-title",
			positionsData[i].name 
			+ "<br>Start: "
			+ positionsData[i].start
			+ "<br>End: "
			+ positionsData[i].end
			+ "<br>Annotation: "
			+ positionsData[i].annotation
			+ "<br>Disease: "
			+ positionsData[i].disease)
		.text(positionsData[i].name);

	// append a hoverbox with basic info
	$('svg text').tipsy({
		gravity: 'nw',
		html: true,
		fallback: "gene"
	});
}

// draw a scale
height += 50;
svgContainer.append("line")
	.attr("x1", "5%")
	.attr("y1", height)
	.attr("x2", "95%")
	.attr("y2", height)
	.attr("stroke", "black")
	.attr("stroke-width", 1);

svgContainer.append("svg:text")
	.attr("x", "2.5%")
	.attr("y", height + 20)
	.text(low);

svgContainer.append("svg:text")
	.attr("x", "92.5%")
	.attr("y", height + 20)
	.text(high);

svgContainer.append("line")
	.attr("x1", "5%")
	.attr("y1", height - 5)
	.attr("x2", "5%")
	.attr("y2", height + 5)
	.attr("stroke", "black")
	.attr("stroke-width", 1);

svgContainer.append("line")
	.attr("x1", "95%")
	.attr("y1", height - 5)
	.attr("x2", "95%")
	.attr("y2", height + 5)
	.attr("stroke", "black")
	.attr("stroke-width", 1);

// only draw tick when marker is not the same as left or right
if(marker != "5%" && marker != "95%"){
	svgContainer.append("svg:text")
		.attr("x", marker)
		.attr("y", height + 20)
		.text(startPoint);
	svgContainer.append("line")
		.attr("x1", marker)
		.attr("y1", height - 5)
		.attr("x2", marker)
		.attr("y2", height + 5)
		.attr("stroke", "black")
		.attr("stroke-width", 1);
}
