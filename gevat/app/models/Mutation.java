package models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.GenotypesContext;
import org.broadinstitute.variant.variantcontext.Genotype;
import org.broadinstitute.variant.variantcontext.GenotypeBuilder;
import org.broadinstitute.variant.variantcontext.VariantContext;

public class Mutation extends VariantContext {

	public String mutationType;

	/**
	 * Store information about a mutation
	 * 
	 * @param id
	 * @param sort
	 * @param rsID
	 * @param chromosome
	 * @param alleles
	 */
	public Mutation(int id, String mutationType, String rsID, int chromosome,
			Collection<Allele> alleles, GenotypesContext genotypes) {
		super(null, rsID, Integer.toString(chromosome), id, id, alleles,
				genotypes, 0, null, null, false, EnumSet
						.noneOf(Validation.class));
		this.mutationType = mutationType;
	}

	public Mutation(int id, String mutationType, String rsID, int chromosome,
			char[] alleles) {
		super(null, rsID, Integer.toString(chromosome), id, id,
				toAlleleCollection(new String(alleles)),
				toGenotypesContext(new String(alleles)), 0, null, null, false,
				EnumSet.noneOf(Validation.class));
		this.mutationType = mutationType;
	}

	public Mutation(VariantContext vc, String mutationType) {
		super(vc);
		this.mutationType = mutationType;
	}

	public String getMutationType() {
		return mutationType;
	}

	public String getRsID() {
		return getID();
	}

	public long getId() {
		return start;
	}

	public int getChromosome() {
		return Integer.parseInt(contig);
	}

	/**
	 * Return the alleles of the child
	 * 
	 * @return String
	 */
	public String child() {
		return toBaseString(getAlleles());
	}

	/**
	 * Return the alleles of the father
	 * 
	 * @return String
	 */
	public String father() {
		return toBaseString(getGenotype("FATHER").getAlleles());
	}

	/**
	 * Return the alleles of the mother
	 * 
	 * @return String
	 */
	public String mother() {
		return toBaseString(getGenotype("MOTHER").getAlleles());
	}

	protected String toBaseString(List<Allele> alleles) {
		return "[" + alleles.get(0).getBaseString() + ", "
				+ alleles.get(1).getBaseString() + "]";
	}

	public String toAllelesString() {
		List<Allele> childAlleles = getAlleles();
		List<Allele> fatherAlleles = getGenotype("FATHER").getAlleles();
		List<Allele> motherAlleles = getGenotype("MOTHER").getAlleles();

		return childAlleles.get(0).getBaseString()
				+ childAlleles.get(1).getBaseString()
				+ fatherAlleles.get(0).getBaseString()
				+ fatherAlleles.get(1).getBaseString()
				+ motherAlleles.get(0).getBaseString()
				+ motherAlleles.get(1).getBaseString();
	}

	/**
	 * Retrive a list of all the mutations of a patient
	 * 
	 * @param p_id
	 * @return List<Mutation>
	 * @throws SQLException
	 */
	public static List<Mutation> getMutations(int p_id) throws SQLException {
		String query = "SELECT * FROM mutations WHERE p_id = '" + p_id + "';";
		ResultSet rs = Database.select("data", query);
		List<Mutation> mutations = new ArrayList<Mutation>();

		while (rs.next()) {
			int id = rs.getInt("m_id");
			String sort = rs.getString("sort");
			String rsID = rs.getString("rsID");
			int chromosome = rs.getInt("chromosome");
			Collection<Allele> alleles = toAlleleCollection(rs
					.getString("alleles"));
			GenotypesContext genotypescontext = toGenotypesContext(rs
					.getString("alleles"));
			mutations.add(new Mutation(id, sort, rsID, chromosome, alleles,
					genotypescontext));
		}
		return mutations;
	}

	/**
	 * Makes an allele collection of the alleles of the child (the first two
	 * alleles in the string)
	 */
	protected static Collection<Allele> toAlleleCollection(String allelesString) {
		Collection<Allele> alleles = new ArrayList<Allele>();
		alleles.add(toAllele(allelesString.substring(0, 1), true));
		alleles.add(toAllele(allelesString.substring(1, 2), false));
		return alleles;
	}

	/**
	 * Makes a GenotypesContext of the alleles of the parents using the 3rd to
	 * 6th alleles in the string
	 */
	protected static GenotypesContext toGenotypesContext(String s) {
		GenotypesContext gc = GenotypesContext.create();
		gc.add(toGenotype(s.substring(2, 4), "FATHER"));
		gc.add(toGenotype(s.substring(4, 6), "MOTHER"));
		return gc;
	}

	/**
	 * Makes a Genotype of a 2-letter string representing two alleles
	 */
	protected static Genotype toGenotype(String allelesString, String name) {
		List<Allele> alleles = new ArrayList<Allele>();
		alleles.add(toAllele(allelesString.substring(0, 1), false));
		alleles.add(toAllele(allelesString.substring(1, 2), false));
		return GenotypeBuilder.create(name, alleles);
	}

	/**
	 * Creates an allele from the given string
	 */
	protected static Allele toAllele(String s, boolean isref) {
		return Allele.create(s, isref);
	}
}
