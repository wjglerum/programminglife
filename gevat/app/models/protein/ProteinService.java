package models.protein;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import models.mutation.Mutation;
import models.patient.Patient;

/**
 * Service for protein.
 * 
 * @author willem
 * 
 */
public class ProteinService {

	private final ProteinRepository proteinRepository;

	public ProteinService(final ProteinRepository proteinRepository) {
		this.proteinRepository = proteinRepository;
	}

	public String getAnnotations(String name) throws SQLException {
		return proteinRepository.getAnnotations(name);
	}

	public ArrayList<Mutation> getRelatedMutations(Patient p, Mutation m)
			throws SQLException {
		return proteinRepository.getRelatedMutations(p, m);
	}

	public void addConnectionsOfSnp(ProteinGraph pq, int snp, int limit,
			int threshold) throws SQLException {
		proteinRepository.addConnectionsOfSnp(pq, snp, limit, threshold);
	}

	public void addDistantConnectionsOfSnp(ProteinGraph pg, int snp, int limit,
			int threshold, int distance) throws SQLException {
		proteinRepository.addDistantConnectionsOfSnp(pg, snp, limit, threshold,
				distance);
	}

	public void addDistantConnectionsOfProtein(ProteinGraph pq, String protein,
			int limit, int threshold, int distance) throws SQLException {
		proteinRepository.addDistantConnectionsOfProtein(pq, protein, limit,
				threshold, distance);
	}

	public Collection<Protein> addConnectionsOfProteine(ProteinGraph pg,
			String protein, int limit, int threshold) throws SQLException {
		return proteinRepository.addConnectionsOfProteine(pg, protein, limit,
				threshold);
	}

	public Collection<Protein> addConnections(String p1, String connections) {
		return proteinRepository.addConnections(p1, connections);
	}

	public void add(String p1, String p2, int combinedScore) {
		proteinRepository.add(p1, p2, combinedScore);
	}

}
