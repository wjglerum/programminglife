SELECT 
	combined_score,
	protein_a.preferred_name AS name_a,
	protein_b.preferred_name AS name_b
FROM 
	network.node_node_links,
        items.proteins AS protein_a,
	items.proteins AS protein_b 
WHERE
	protein_a.preferred_name IN (?) AND 
	protein_a.species_id = 9606 AND
	protein_b.preferred_name IN (?) AND 
	protein_b.species_id = 9606 AND
	node_id_a = protein_a.protein_id AND 
	node_id_b = protein_b.protein_id 
ORDER BY 
	combined_score DESC;
