SELECT 
	annotation
FROM 
	items.proteins
WHERE 
	preferred_name = ? AND
	species_id = 9606;
