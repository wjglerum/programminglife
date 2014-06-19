SELECT DISTINCT 
	snp_id,
	abbrev,
	descrip,
	is_coding,
	is_exon 
FROM
	snpfunctioncode,
	b138_snpcontiglocusid
WHERE
	snp_id = ? AND fxn_class = code;
