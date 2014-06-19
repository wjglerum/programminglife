INSERT INTO patient 
	(p_id,
	u_id,
	name,
	surname,
	vcf_file,
	vcf_length,
	processed,
	female)			
VALUES
	(nextval('p_id_seq'::regclass),
	?,
	?,
	?,
	?,
	?,
	false,
	?);
