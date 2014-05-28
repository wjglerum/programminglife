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

    getChromosomesWithMutations();

    d3.select("#patient_visualisation").selectAll("span")
        .data(chromosomes)
        .enter()
        .append("span")
        .append("svg")
        .attr("width", 15)
        .attr("height", 100)
        .append("rect")
        .attr("class", function (d) {
            return "chromosome-visualisation chromosome" + d;
        })
        .attr("data-chr-id", function (d) {
            return d;
        })

        .attr("x", 5)
        .attr("rx", 10)
        .attr("ry", 10)
        .attr("width", 10)
        .attr("height", 100)

    ;

    function markMutations() {
        for (var i = 0; i < chromosomesWithMutations.length; i++) {
            d3.selectAll("." + chromosomesWithMutations[i]).attr("class", function (d) {
                return "chromosome-visualisation mutation chromosome" + d;
            });
        }
    }
    markMutations();

    // Highlight mutations in the table while hovering over a chromosome
    $(".chromosome-visualisation").hover(function() {
        var chrId = $(this).data("chr-id");
        //alert("hoi" + chrId);
        $(".table-mutations tr[data-chr-id=" + chrId + "]").addClass("highlight");
        d3.selectAll(".chromosome" + chrId).attr("class", function(d){return "chromosome-visualisation highlight chromosome" + d;});
        //alert("chromosome" + chrId);
    }, function() {
        var chrId = $(this).data("chr-id");
        $(".table-mutations tr[data-chr-id=" + chrId + "]").removeClass("highlight");
        d3.selectAll(".chromosome" + chrId).attr("class", function(d){return "chromosome-visualisation chromosome" + d;});
        markMutations();
    });
});