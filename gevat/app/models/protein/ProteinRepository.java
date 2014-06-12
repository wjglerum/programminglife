package models.protein;

import java.sql.SQLException;
import java.util.ArrayList;

import models.mutation.Mutation;
import models.patient.Patient;

/**
 * Interface for protein.
 * 
 * @author willem
 * 
 */
public interface ProteinRepository {
	public String getAnnotations(String name) throws SQLException;

	public void addConnectionsOfSnp(ProteinGraph pg, int snp, int limit,
			int threshold) throws SQLException;

	public void addConnectionsOfProteine(ProteinGraph pg, String proteine)
			throws SQLException;

	public ArrayList<Mutation> getRelatedMutations(Patient p, Mutation m) throws SQLException;
}
