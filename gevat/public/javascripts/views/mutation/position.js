//retrieve data from html
var position = $(".position").data("position");
var id = $(".position").data("rsid");
var mutationType = $(".position").data("sort");


// lowest and highest value in the dataset
var low = Number.MAX_VALUE;
var high = 0;

// compute lowest and highest value
for(i in positionsData) {
	if(positionsData[i].start < low) low = positionsData[i].start;
	if(positionsData[i].end > high) high = positionsData[i].end;
}

// also check the location of the position
if(position < low) low = position;
if(position > high) high = position;

// convert to relative percentage
function relativePercentage(value) {
	value = (value - low) / (high - low);
	value = value * 100 * 0.9 + 5;
	value += "%";
	return value;
}

// compute marker
var marker = relativePercentage(position);

//make a container for svg
var SVGheight = 100 + positionsData.length*20
var svgContainer = d3.select("div.position")
	.append("svg")
	.attr("width", "100%")
	.attr("height", SVGheight + 100);

// make a container for the mutation
var g = svgContainer.append("g")
	.attr("original-title", 
		id
		+ "<br>Position: "
		+ position
		+ "<br>Sort: "
		+ mutationType
	);

// draw the line for the mutation
g.append("line")
	.attr("x1", marker)
	.attr("y1", 50)
	.attr("x2", marker)
	.attr("y2", SVGheight)
	.attr("stroke", "red")
	.attr("stroke-width", 3)

// append a label to the mutation
g.append("svg:text") 
	.attr("x", marker) 
	.attr("y", 45) 
	.text(id);

$('svg g').tipsy({
	gravity: 's',
	html: true,
	fallback: "No information found about this mutation!",
	delayOut: 250,
	fade: true
});

// add nearby mutations
for(i in nearbyData) {
	// compute the relative position
	var mark = (nearbyData[i].position - low)/(high - low)*100*0.9+5;
	mark += "%";

	// make a container for nearby mutations
	var g = svgContainer.append("g")
			.attr("original-title",
			nearbyData[i].rsid
			+ "<br>Position: "
			+ nearbyData[i].position
			+ "<br>Sort: "
			+ nearbyData[i].sort
		);

	// draw the line for the mutation and add href to the mutation
	g.append("svg:a")
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
		.attr("stroke-width", 2);

	// append a hoverbox with basic info
	$('svg g').tipsy({
		gravity: 's',
		html: true,
		fallback: "No information found about this mutation!",
		delayOut: 250,
		fade: true
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
	console.log(left + " " + right);
	console.log(right-left);
	right = right - left;
	console.log(right);

	// make the visualisation larger for very small genes
	if(right < 0.2) right += 0.1;

	// make everything a percentage for nice scaling
	left += "%";
	right += "%";
	middle += "%";
	

	// add container for gene line and text
	var geneContainer = svgContainer.append("g")
		.attr("original-title",
			positionsData[i].name 
			+ "<br>Start: "
			+ positionsData[i].start
			+ "<br>End: "
			+ positionsData[i].end
			+ "<br>Annotation: "
			+ positionsData[i].annotation
			+ "<br>Disease: "
			+ positionsData[i].disease);

	// add the line per gene
	geneContainer.append("rect")
		.attr("x", left)
		.attr("y", height)
		.attr("rx", 3)
		.attr("ry", 3)
		.attr("width", right)
		.attr("height", 3)		
		.attr("stroke", "black")
		.attr("stroke-width", 1)
		.attr("id", "gene");

	// append a text label per gene
	geneContainer.append("svg:text")
		.attr("x", middle)
		.attr("y", height - 5)
		.text(positionsData[i].name);

	// append a hoverbox with basic info
	$('svg g').tipsy({
		gravity: 's',
		html: true,
		fallback: "No information found about this gene!",
		delayOut: 250,
		fade: true
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
		.text(position);
	svgContainer.append("line")
		.attr("x1", marker)
		.attr("y1", height - 5)
		.attr("x2", marker)
		.attr("y2", height + 5)
		.attr("stroke", "black")
		.attr("stroke-width", 1);
}
