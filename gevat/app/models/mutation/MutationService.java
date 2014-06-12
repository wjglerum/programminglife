package models.mutation;

import java.sql.SQLException;
import java.util.List;

public class MutationService {
	private final MutationRepository mutationRepository;

	public MutationService(final MutationRepository mutationRepository) {
		this.mutationRepository = mutationRepository;
	}

	public List<Mutation> getMutations(final int pId) throws SQLException {
		return this.mutationRepository.getMutations(pId);
	}

	public List<Mutation> getMutations(final int pId, final int cId)
			throws SQLException {
		return this.mutationRepository.getMutations(pId, cId);
	}

	public float getScore(Mutation m) throws SQLException {
		return this.mutationRepository.getScore(m);
	}
}
