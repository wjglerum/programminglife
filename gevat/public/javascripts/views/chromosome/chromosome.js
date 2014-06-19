// amount of basepairs per chromosome
var chromosome = '{"1": 249250621, "2": 243199373, "3": 198022430, "4": 191154276, "5": 180915260, "6": 171115067, "7": 159138663, "8": 146364022, "9": 141213431, "10": 135534747, "11": 135006516, "12": 133851895, "13": 115169878, "14": 107349540, "15": 102531392, "16": 90354753, "17": 81195210, "18": 78077248, "19": 59128983, "20": 63025520, "21": 48129895, "22": 51304566, "X": 155270560, "Y": 59373566}';
chromosome = JSON.parse(chromosome);

// the id of the current chromosome
var cid = $(".position").data("cid");

// make a SVG container
var svgContainer = d3.select("div.position")
	.append("svg")
	.attr("width", "100%")
	.attr("height", 150);

svgContainer.append("rect")
	.attr("x", "5%")
	.attr("y", 100)
	.attr("rx", 10)
	.attr("ry", 10)
	.attr("width", "90%")
	.attr("height", 25)		
	.attr("stroke", "black")
	.attr("fill", "gray")
	.attr("fill-opacity", 0.5)
	.attr("stroke-width", 3);

// convert to relative percentage
function relativePercentage(value) {
	value = value / chromosome[cid];
	value = value * 100 * 0.9 + 5;
	return value += "%";
}

// add a line for each mutation
for(i in mutationData) {
	//compute percentage
	var line = relativePercentage(mutationData[i].position);

	// make a container for the mutation
	var g = svgContainer.append("g")
		.attr("original-title",
			mutationData[i].rsid
			+ "<br>Position: "
			+ mutationData[i].position
			+ "<br>Sort: "
			+ mutationData[i].sort
		);
	
	// add a line for the mutation
	g.append("svg:a")
		.attr("xlink:href",
			"/patients/"
			+ mutationData[i].pid
			+ "/mutation/"
			+ mutationData[i].mid)
		.append("line")
		.attr("x1", line)
		.attr("y1", 100)
		.attr("x2", line)
		.attr("y2", 125)
		.attr("stroke", "red")
		.attr("stroke-width", 2);

	// add tooltips
	$('svg g').tipsy({
		gravity: 's',
		html: true,
		fallback: "No information found about this mutation!",
		delayOut: 250,
		fade: true
	});
}

// Start the table sorter
    $(".table-mutations").tablesorter({
        // Automatically sort on the phred quality score
        sortList:  [[1,0]]
    });
