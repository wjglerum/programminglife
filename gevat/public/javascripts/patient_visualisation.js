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
            chromosomesWithMutations.push("chromosome" + table.rows[r].cells[2].innerHTML);
        }
    }

    function markMutations() {
        for (var i = 0; i < chromosomesWithMutations.length; i++) {
            d3.selectAll("." + chromosomesWithMutations[i]).attr("class", function (d) {
                return "chromosome-visualisation mutation chromosome" + d;
            });
        }
    }

    d3.select("#patient_visualisation").selectAll("svg")
        .data(chromosomes)
        .enter()
        .append("svg")
        .attr("width", 15)
        .attr("height", 100)
        .append("rect")
        .attr("class", function (d) { return "chromosome-visualisation chromosome" + d;})
        .attr("data-chr-id", function (d) { return d;})
        .attr("x", 5)
        .attr("rx", 10)
        .attr("ry", 10)
        .attr("width", 10)
        .attr("height", 100);

    getChromosomesWithMutations();
    markMutations();

    // Highlight mutations in the table while hovering over a chromosome
    $(".chromosome-visualisation").hover(function() {
        var chrId = $(this).data("chr-id");
        $(".table-mutations tr[data-chr-id=" + chrId + "]").addClass("highlight");
        d3.selectAll(".chromosome" + chrId).attr("class", function(d){return "chromosome-visualisation highlight chromosome" + d;});
    }, function() {
        // Make sure the chromosomes get their normal color back when you stop hovering over them
        var chrId = $(this).data("chr-id");
        $(".table-mutations tr[data-chr-id=" + chrId + "]").removeClass("highlight");
        d3.selectAll(".chromosome" + chrId).attr("class", function(d){return "chromosome-visualisation chromosome" + d;});
        markMutations();
    });

    // Highlight chromosomes when hovering over the table
    $(".table-mutation").hover(function() {
        var chrId = $(this).data("chr-id");
        d3.selectAll(".chromosome" + chrId).attr("class", "chromosome-visualisation highlight chromosome" + chrId);
    }, function() {
        // Make sure the chromosomes get their normal color back when you stop hovering over them
        var chrId = $(this).data("chr-id");
        d3.selectAll(".chromosome" + chrId).attr("class", "chromosome-visualisation mutation chromosome" + chrId);
    });
});