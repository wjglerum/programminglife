package models.protein;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

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

	public void addConnectionsOfSnp(int snp, int limit,
			int threshold) throws SQLException;

	public void addDistantConnectionsOfSnp(int snp, int limit,
			int threshold, int distance) throws SQLException;

	public void addDistantConnectionsOfProtein(String protein,
			int limit, int threshold, int distance) throws SQLException;

	public Collection<Protein> addConnectionsOfProteine(String protein,
			int limit, int threshold) throws SQLException;

	public Collection<Protein> addConnections(String p1, String connections);

	public void add(String p1, String p2, int combinedScore);
}
