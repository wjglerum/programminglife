SELECT 
	protein_b.preferred_name, combined_score
FROM 
	network.node_node_links, 
	items.proteins AS protein_a,	
	items.proteins AS protein_b, 
	items.species
WHERE
	protein_a.species_id = items.species.species_id AND 
	official_name = 'Homo sapiens' AND
	protein_a.preferred_name = ? AND
	node_id_a = protein_a.protein_id AND
	node_id_b = protein_b.protein_id AND
	combined_score > ?
ORDER BY 
	combined_score DESC LIMIT ?;
