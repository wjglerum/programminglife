SELECT DISTINCT 
	snp_id, 
	allele, 
	chr_cnt,
	freq 
FROM 
	snpallelefreq 
join 
	allele ON 
	snpallelefreq.allele_id = allele.allele_id
WHERE 
	snp_id = ? AND 
	allele = ? AND 
	freq < 0.005 AND 
	freq > 0;
