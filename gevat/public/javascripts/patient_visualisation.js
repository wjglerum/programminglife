$( document ).ready(function() {

    var chromosomes = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23];
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
        .attr("width", 40)
        .attr("height", 100)
        .append("rect")
        .attr("id", function(d){return "chromosome" + d;})
        .attr("class", "chromosome-visualisation")
        .attr("data-chr-id", function(d){return d;})
        .style("stroke", "gray")
        .style("fill", "white")
        .attr("rx", 10)
        .attr("ry", 10)
        .attr("width", 20)
        .attr("height", 100)
        .on("mouseover", function(){d3.select(this).style("fill", "aliceblue");})
        .on("mouseout", function(){d3.select(this).style("fill", "white");});

    for(var i = 0; i < chromosomesWithMutations.length; i++) {
        d3.select("#" + chromosomesWithMutations[i]).style("fill", "red")
        .on("mouseout", function(){d3.select(this).style("fill", "red");});
    }

    // Highlight mutations in the table while hovering over a chromosome
    $(".chromosome-visualisation").hover(function() {
        var chrId = $(this).data("chr-id");
        //alert("hoi" + chrId);
        $(".table-mutations tr[data-chr-id=" + chrId + "]").addClass("highlight");
    }, function() {
        var chrId = $(this).data("chr-id");

        $(".table-mutations tr[data-chr-id=" + chrId + "]").removeClass("highlight");
    });
});