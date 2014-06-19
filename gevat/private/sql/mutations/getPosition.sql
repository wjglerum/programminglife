SELECT 
	* 
FROM 
	genes 
WHERE 
	chromosome = ?
ORDER BY 
	ABS((endpoint + startpoint)/2 - ?)
ASC 
	LIMIT ?;
