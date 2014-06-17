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
	/**
	 * Gets the annotations of the protein.
	 * 
	 * @param protein The name of the protein
	 * @return Returns the annotations associated with the protein.
	 * @throws SQLException SQL Exception
	 */
	String getAnnotations(String protein) throws SQLException;

	/**
	 * 
	 * @param p The patient
	 * @param m The mutation
	 * @return A list of related mutations
	 * @throws SQLException SQL Exception
	 */
	ArrayList<Mutation> getRelatedMutations(Patient p, Mutation m)
			throws SQLException;
}
