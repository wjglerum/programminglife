$(document).ready(function () {

    var chromosomes = [];
    for (var i = 1; i < 24; i++) {
        chromosomes.push(i);
    }
    var chromosomesWithMutations = [];
    var table = document.getElementById("table-mutations");

    function getChromosomesWithMutations() {
        for (var r = 1, n = table.rows.length; r < n; r++) {
            chromosomesWithMutations.push("chromosome" + table.rows[r].cells[2].innerHTML);
        }
    }

    function getMutationsFromChromosome(i) {
        var output = "";
        // Loop over de tabel om te kijken of er mutations zijn in chromosoom i
        for (var r = 1, n = table.rows.length; r < n; r++) {
            if (table.rows[r].cells[2].innerHTML == i) {
                var url = table.rows[r].getAttribute("href");
                var chr = table.rows[r].getAttribute("data-chr-id");
                var snp = table.rows[r].cells[1].innerHTML;
                var mutationid = table.rows[r].getAttribute("data-mutation-id");
                output += "<li><a class=\"mutation-list-item\" data-chr-id=" + chr + " data-mutation-id=" + mutationid + " href=" + url + ">" + snp + "</a></li>";
            }
        }
        return output;
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
    for (var chromosomenr = 1; chromosomenr < 24; chromosomenr++) {
        var tmp = tablerow.append("td");
        for (var i = 1; i < 3; i++) {
            tmp.attr("class", "chromosome-pair-table-cel")
                .attr("data-chr-id", chromosomenr)
                .append("svg")
                .attr("width", 15)
                .attr("height", 100)
                .append("rect")
                .attr("class", "chromosome-visualisation chromosome" + chromosomenr)
                .attr("data-chr-id", chromosomenr)
                .attr("x", 5)
                .attr("rx", 10)
                .attr("ry", 10)
                .attr("width", 10)
                .attr("height", 100);
        }
    }

    // Add numbers to the chromosomes
    var tableDescription = d3.select(".chromosome-pair-table")
        .append("tr")
        .attr("class", "chromosome-pair-table-description");

    for (var n = 1; n < 24; n++) {
        tableDescription.append("td")
            .html(n);
    }

    getChromosomesWithMutations();
    markMutations();

    // Actions when hovering over a table cell containing two chromosomes
    $(".chromosome-pair-table-cel").hover(function () {
        var chrId = $(this).data("chr-id");
        // Highlight mutations in the table while hovering over a chromosome
        $(".table-mutations tr[data-chr-id=" + chrId + "]").addClass("highlight");
        // Highlight the chromosomes that you are hovering over
        d3.selectAll(".chromosome" + chrId).attr("class", function (d) {
            return "chromosome-visualisation highlight chromosome" + chrId;
        });
        // If there are mutations at a certain chromosome number, show them, else tell that there are no mutations
        var mutations = (getMutationsFromChromosome(chrId) == "") ? "<p>There are no mutations in chromosome " + chrId + "</p>" : "<ul>" + getMutationsFromChromosome(chrId) + "</ul>";
        d3.select("#patient_chromosome_information").html("<h3>Mutations in chromosome " + chrId + "</h3>" + mutations);
    }, function () {
        // Make sure the chromosomes get their normal color back when you stop hovering over them
        var chrId = $(this).data("chr-id");
        $(".table-mutations tr[data-chr-id=" + chrId + "]").removeClass("highlight");
        d3.selectAll(".chromosome" + chrId).attr("class", function (d) {
            return "chromosome-visualisation chromosome" + chrId;
        });
        markMutations();
    });

//TODO: dit aan de praat krijgen...
//    // Actions when hovering over mutations list
//    $(".mutation-list-item").hover(function(){
//        //var mutationId = $(this).data("mutation-id");
//        //$(".table-mutations tr[data-mutation-id=" + mutationId + "]").addClass("highlight");
//    }, function() {
//        var mutationId = $(this).data("mutation-id");
//
//        $(".table-mutations tr[data-mutation-id=" + mutationId + "]").removeClass("highlight");
//    });

    // Actions when hovering over table
    $(".table-mutation").hover(function () {
        var chrId = $(this).data("chr-id");
        d3.selectAll(".chromosome" + chrId).attr("class", "chromosome-visualisation highlight chromosome" + chrId);
    }, function () {
        // Make sure the chromosomes get their normal color back when you stop hovering over them
        var chrId = $(this).data("chr-id");
        d3.selectAll(".chromosome" + chrId).attr("class", "chromosome-visualisation mutation chromosome" + chrId);
    });


});