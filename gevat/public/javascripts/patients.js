$(document).ready(function () {
    // Start the table sorter
    $(".table-patients").tablesorter({
        // Automatically sort on the phred quality score
        sortList:  [[2,0]]
    });
});