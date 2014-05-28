$( document ).ready(function() {

    var chromosomes = [];
    for (var i = 1; i < 24; i++) {
        chromosomes.push(i);
        //chromosomes.push(i);
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
                return "chromosome-visualisation mutation " + chromosomesWithMutations[i];
            });
        }
    }

    //Make a table for our chromosomes, and initiate the first table row (for the svg's)
    d3.select("#patient_visualisation")
        .append("table")
        .attr("class", "chromosome-pair-table")
        .append("tr")
        .attr("class", "chromosome-pair-table-row");


    // Add 2 chromosomes per div
    var tablerow = d3.select(".chromosome-pair-table-row");
    for (var langemoeilijkenaam = 1; langemoeilijkenaam < 24; langemoeilijkenaam++) {
        var tmp = tablerow.append("td")
        for (var i = 1; i < 3; i++) {
                tmp.append("svg")
                .attr("width", 15)
                .attr("height", 100)
                .append("rect")

                .attr("class", "chromosome-visualisation chromosome" + langemoeilijkenaam)
                .attr("data-chr-id", langemoeilijkenaam)
                .attr("x", 5)
                .attr("rx", 10)
                .attr("ry", 10)
                .attr("width", 10)
                .attr("height", 100);
        }
    }


    // Voeg nu alle namen toe aan te tabellen
    var tableDescription = d3.select(".chromosome-pair-table")
        .append("tr")
        .attr("class", "chromosome-pair-table-description");

    for (var langemoeilijkenaam = 1; langemoeilijkenaam < 24; langemoeilijkenaam++) {
        tableDescription.append("td")
            .html(langemoeilijkenaam);
    }

    getChromosomesWithMutations();
    markMutations();

    // Highlight mutations in the table while hovering over a chromosome
    $(".chromosome-visualisation").hover(function() {
        var chrId = $(this).data("chr-id");
        $(".table-mutations tr[data-chr-id=" + chrId + "]").addClass("highlight");
        d3.selectAll(".chromosome" + chrId).attr("class", function(d){return "chromosome-visualisation highlight chromosome" + chrId;});
        d3.select("#patient_chromosome_information").html("<h3>Browse mutations</h3>");
    }, function() {
        // Make sure the chromosomes get their normal color back when you stop hovering over them
        var chrId = $(this).data("chr-id");
        $(".table-mutations tr[data-chr-id=" + chrId + "]").removeClass("highlight");
        d3.selectAll(".chromosome" + chrId).attr("class", function(d){return "chromosome-visualisation chromosome" + chrId;});
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