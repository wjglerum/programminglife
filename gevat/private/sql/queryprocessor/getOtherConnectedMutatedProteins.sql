SELECT *
FROM protein_connections
WHERE p_id = ?
AND protein_a_id NOT IN ( ? )
AND protein_b_id IN ( ? );
