package models.mutation;

import java.sql.SQLException;
import java.util.List;

public interface MutationRepository {
	public List<Mutation> getMutations(final int pId) throws SQLException;

	public List<Mutation> getMutations(final int pId, final String cId)
			throws SQLException;

	public float getScore(Mutation m) throws SQLException;
}
