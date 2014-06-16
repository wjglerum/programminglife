package models.chromosome;

/**
 * The chromosome for the chromosome view.
 */
public class Chromosome {

	private String id;

	/**
	 * The constructor for chromosome.
	 *
	 * @param cId The id of the chromosome
	 */
	public Chromosome(final String cId) {
		this.id = cId;
	}

	public final String getID() {
		return this.id;
	}
}
