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

	public ArrayList<Mutation> getRelatedMutations(Patient p, Mutation m)
			throws SQLException;
}
