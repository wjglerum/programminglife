$(document).ready(function () {

    var chromosomesWithMutations = [];
    var table = document.getElementById("table-mutations");

    //Make a table for our chromosomes, and initiate the first table row (for the svg's)
    d3.select("#patient_visualisation")
        .append("table")
        .attr("class", "chromosome-pair-table")
        .append("tr")
        .attr("class", "chromosome-pair-table-row");

    //If the patient is a man, 24 cells should be shown (23 should be an x and an y chromosome)
    var numberOfCells = (female == "false") ? 25 : 24;

    // Add 2 chromosomes per div
    var tablerow = d3.select(".chromosome-pair-table-row");
    for (var chromosomenr = 1; chromosomenr < numberOfCells; chromosomenr++) {
        var tableCel = tablerow.append("td");
        for (var i = 1; i < 3; i++) {
            if (chromosomenr == numberOfCells-2 && female == "false") {
                appendChromosome(tableCel, 100, "X");
                i++;
            }
            else if (chromosomenr == numberOfCells-1 && female == "false") {
                appendChromosome(tableCel, 80, "Y");
                i++;
            }
            else {
                appendChromosome(tableCel, 100, chromosomenr);
            }
        }
    }

    function appendChromosome(tableCel, height, id) {
        tableCel.attr("class", "chromosome-pair-table-cel")
            .attr("data-chr-id", id)
            .append("svg")
            .attr("width", 15)
            .attr("height", 100)
            .append("rect")
            .attr("class", "chromosome-visualisation chromosome" + id)
            .attr("data-chr-id", id)
            .attr("x", 5)
            .attr("rx", 10)
            .attr("ry", 10)
            .attr("width", 10)
            .attr("height", height);
    }

    // Add numbers to the chromosomes
    var tableDescription = d3.select(".chromosome-pair-table")
        .append("tr")
        .attr("class", "chromosome-pair-table-description");

    for (var n = 1; n < numberOfCells; n++) {
        if (n == numberOfCells-2 && female == "false") {
            tableDescription.append("td").html("X");
        }
        else if (n == numberOfCells-1 && female == "false") {
            tableDescription.append("td").html("Y");
        }
        else if (n == numberOfCells-1 && female == "true") {
            tableDescription.append("td").html("X");
        }
        else
            tableDescription.append("td").html(n);
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

    // Actions when hovering over table
    $(".table-mutation").hover(function () {
        var chrId = $(this).data("chr-id");
        d3.selectAll(".chromosome" + chrId).attr("class", "chromosome-visualisation highlight chromosome" + chrId);
    }, function () {
        // Make sure the chromosomes get their normal color back when you stop hovering over them
        var chrId = $(this).data("chr-id");
        d3.selectAll(".chromosome" + chrId).attr("class", "chromosome-visualisation mutation chromosome" + chrId);
    });

    // Actions when hovering over the mutations
    $(".page-content").on({
        mouseenter: function () {
            var id = $(this)[0].getAttribute("data-mutation-id");
            $(".table-mutations tr[data-mutation-id=" + id + "]").addClass("highlight");
        },
        mouseleave: function() {
            var id = $(this)[0].getAttribute("data-mutation-id");
            $(".table-mutations tr[data-mutation-id=" + id + "]").removeClass("highlight");
        }
    }, ".mutation-list-item");

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

    // Start the table sorter
    $("#table-mutations").tablesorter({
        // Automatically sort on the phred quality score
       sortList:  [[3,1]]
    });

    // Make the chromosomes clickable, redirect the page to the chromosome view
    $(".chromosome-pair-table").on("click", "td", function() {
        var chrId = $(this).data("chr-id");
        window.document.location = "/patients/" + patient + "/chromosome/" + chrId;
    });

});