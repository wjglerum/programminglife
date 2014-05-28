$( document ).ready(function() {

    var chromosomes = [];
    for (var i = 1; i < 24; i++) {
        chromosomes.push(i);
        chromosomes.push(i);
    }
    var chromosomesWithMutations = [];
    var table = document.getElementById("table-mutations");


    function getChromosomesWithMutations() {
        for (var r = 1, n = table.rows.length; r < n; r++) {
            chromosomesWithMutations.push("chromosome"+table.rows[r].cells[2].innerHTML);
        }
    }

    getChromosomesWithMutations();

    d3.select("#patient_visualisation").selectAll("span")
        .data(chromosomes)
        .enter()
        .append("span")
        .append("svg")
        .attr("width", 25)
        .attr("height", 100)
        .append("rect")
        .attr("class", function(d){return "chromosome-visualisation chromosome" + d;})
        .attr("data-chr-id", function(d){return d;})
        .style("fill", "black")
        .attr("x", 5)
        .attr("rx", 10)
        .attr("ry", 10)
        .attr("width", 10)
        .attr("height", 100)
        .on("mouseover", function(){d3.select(this).style("fill", "aliceblue");})
        .on("mouseout", function(){d3.select(this).style("fill", "black");});

    for(var i = 0; i < chromosomesWithMutations.length; i++) {
        d3.selectAll("." + chromosomesWithMutations[i]).style("fill", "red")
        .on("mouseout", function(){d3.select(this).style("fill", "red");});
    }

    // Highlight mutations in the table while hovering over a chromosome
    $(".chromosome-visualisation").hover(function() {
        var chrId = $(this).data("chr-id");
        //alert("hoi" + chrId);
        $(".table-mutations tr[data-chr-id=" + chrId + "]").addClass("highlight");
        //$("chromosome" + chrId).style("fill", "black");
        d3.selectAll(".chromosome" + chrId).style("fill", "aliceblue");
        //alert("chromosome" + chrId);
    }, function() {
        var chrId = $(this).data("chr-id");
        $(".table-mutations tr[data-chr-id=" + chrId + "]").removeClass("highlight");
        //TODO: niet zo netjes...
        var kleur = "black";
        if(chromosomesWithMutations.indexOf("chromosome" + chrId) != -1)  {
            kleur = "red";
        }
        d3.selectAll(".chromosome" + chrId).style("fill", kleur);
    });
});