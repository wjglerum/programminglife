package models.dna;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import models.database.Database;
import models.database.QueryProcessor;

import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.GenotypesContext;
import org.broadinstitute.variant.variantcontext.Genotype;
import org.broadinstitute.variant.variantcontext.GenotypeBuilder;
import org.broadinstitute.variant.variantcontext.VariantContext;

/**
 * 
 * @author rhvanstaveren
 * 
 */
public class Mutation extends VariantContext {

	public String mutationType;
	public int id;
	private int positionGRCH37;

	/**
	 * Stores information about a mutation.
	 * 
	 * @param id
	 *            The location of the allele
	 * @param mutationType
	 *            The type of mutation
	 * @param rsID
	 *            The id of the SNP
	 * @param chromosome
	 *            The chromosome the SNP is on
	 * @param alleles
	 *            The basepair of the child
	 * @param startPoint
	 *            The startpoint of the mutation
	 * @param endPoint
	 *            The endpoint of the mutation
	 * @param genotypes
	 *            The genotype of the parents
	 */
	public Mutation(final int id, final String mutationType, final String rsID,
			final int chromosome, final Collection<Allele> alleles,
			final int startPoint, final int endPoint,
			final GenotypesContext genotypes, final int positionGRCH37) {
		super(null, rsID, Integer.toString(chromosome), startPoint, endPoint,
				alleles, genotypes, 0, null, null, false, EnumSet
						.noneOf(Validation.class));
		this.mutationType = mutationType;
		this.id = id;
		this.positionGRCH37 = positionGRCH37;
	}

	/**
	 * Stores information about a mutation.
	 * 
	 * @param id
	 *            The location of the allele
	 * @param mutationType
	 *            The type of mutation
	 * @param rsID
	 *            The id of the SN
	 * @param chromosome
	 *            The chromosome the SNP is on
	 * @param alleles
	 *            The basepairs which need to be converted
	 * @param startPoint
	 *            The startpoint of the mutation
	 * @param endPoint
	 *            The endpoint of the mutation
	 */
	public Mutation(final int id, final String mutationType, final String rsID,
			final int chromosome, final char[] alleles, final int startPoint,
			final int endPoint, final int positionGRCH37) {
		super(null, rsID, Integer.toString(chromosome), id, id,
				toAlleleCollection(new String(alleles)),
				toGenotypesContext(new String(alleles)), 0, null, null, false,
				EnumSet.noneOf(Validation.class));
		this.mutationType = mutationType;
		this.id = id;
		this.positionGRCH37 = positionGRCH37;
	}

	/**
	 * Stores information about a mutation.
	 * 
	 * @param vc
	 *            The VariantContext without a MutationType
	 * @param mutationType
	 *            The MutationType as String
	 */
	public Mutation(final VariantContext vc, final String mutationType) {
		super(vc);
		this.mutationType = mutationType;
		String[] splitGP = ((String) vc.getAttribute("GP")).split(":");
		this.positionGRCH37 = Integer.parseInt(splitGP[1]);
	}

	/**
	 * Gets the mutationType.
	 * 
	 * @return Returns the String MutationType
	 */
	public final String getMutationType() {
		return this.mutationType;
	}

	/**
	 * Gets the rsID.
	 * 
	 * @return String rsID
	 */
	public final String getRsID() {
		return this.getID();
	}

	/**
	 * Gets the id number as a long.
	 * 
	 * @return The id
	 */
	public final long getId() {
		return this.id;
	}

	/**
	 * Gets the chromosome number.
	 * 
	 * @return Returns the chromosome number.
	 */
	public final int getChromosome() {
		return Integer.parseInt(this.contig);
	}

	/**
	 * Gets the startpoint of the mutation.
	 * 
	 * @return Returns the startpoint of a mutation
	 */
	public final int getStartPoint() {
		return this.getStart();
	}

	/**
	 * Gets the mutation score
	 * 
	 * @return mutation score
	 * @throws SQLException
	 */
	public float getScore() throws SQLException {
		return QueryProcessor.executeScoreQuery(this);
	}
	
	public float getFrequency() throws SQLException {
		return QueryProcessor.getFrequency(this);
	}

	/**
	 * Gets the endpoint of the mutation.
	 * 
	 * @return Returns the endpoint of a mutation
	 */
	public final int getEndPoint() {
		return this.getEnd();
	}

	public final int getPositionGRCH37() {
		return this.positionGRCH37;
	}
	/**
	 * Return the alleles of the child.
	 * 
	 * @return String with alleles of the child
	 */
	public final String child() {
		return this.toBaseString(this.getAlleles());
	}

	/**
	 * Return the alleles of the father.
	 * 
	 * @return String
	 */
	public final String father() {
		return this.toBaseString(this.getGenotype("FATHER").getAlleles());
	}

	/**
	 * Return the alleles of the mother.
	 * 
	 * @return String
	 */
	public final String mother() {
		return this.toBaseString(this.getGenotype("MOTHER").getAlleles());
	}

	/**
	 * Looks at the alleles of the child, mother and father, and returns the
	 * unique base of the child that the parents don't have
	 * 
	 * @return Unique base of the child that the parents don't have
	 */
	public final String getUniqueBase() {
		String output = "";
		// Get all the individual base pairs
		char p1 = child().charAt(0);
		char p2 = child().charAt(1);
		char f1 = father().charAt(0);
		char f2 = father().charAt(1);
		char m1 = mother().charAt(0);
		char m2 = mother().charAt(1);
		// Check if the mutation is in the first, or the second basepair
		if (p1 != f1 && p1 != f2 && p1 != m1 && p1 != m2) {
			output = "" + p1;
		}
		if (p2 != f1 && p2 != f2 && p2 != m1 && p2 != m2) {
			output = "" + p2;
		}
		return output;
	}

	/**
	 * Gives the basepair alleles as a String.
	 * 
	 * @param alleles
	 *            The list of alleles
	 * 
	 * @return Returns the String representation of the basepair
	 */
	protected final String toBaseString(final List<Allele> alleles) {
		return "[" + alleles.get(0).getBaseString() + ", "
				+ alleles.get(1).getBaseString() + "]";
	}

	/**
	 * Gives the alleles as a String.
	 * 
	 * @return Returns the String representation of the basepair
	 */
	public final String toAllelesString() {
		List<Allele> childAlleles = this.getAlleles();
		List<Allele> fatherAlleles = this.getGenotype("FATHER").getAlleles();
		List<Allele> motherAlleles = this.getGenotype("MOTHER").getAlleles();

		return childAlleles.get(0).getBaseString()
				+ childAlleles.get(1).getBaseString()
				+ fatherAlleles.get(0).getBaseString()
				+ fatherAlleles.get(1).getBaseString()
				+ motherAlleles.get(0).getBaseString()
				+ motherAlleles.get(1).getBaseString();
	}

	/**
	 * Retrieve a list of all the mutations of a patient.
	 * 
	 * @param pId
	 *            The id
	 * 
	 * @return Returns List<Mutation>, a list of all mutations
	 * 
	 * @throws SQLException
	 *             In case SQL goes wrong
	 */
	public static List<Mutation> getMutations(final int pId)
			throws SQLException {
		String query = "SELECT * FROM mutations WHERE p_id = '" + pId + "';";
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
			int startPoint = rs.getInt("startpoint");
			int endPoint = rs.getInt("endpoint");
			int positionGRCH37 = rs.getInt("GRCH37_pos");
			mutations.add(new Mutation(id, sort, rsID, chromosome, alleles,
					startPoint, endPoint, genotypescontext, positionGRCH37));
		}
		return mutations;
	}

	/**
	 * Retrieve a list of all the mutations of a patient in a certain chromosome.
	 * 
	 * @param pId
	 *            The id
	 * @param cId
	 *            The chromosome number
	 * 
	 * @return Returns List<Mutation>, a list of all mutations in the chromosome.
	 * 
	 * @throws SQLException
	 *             In case SQL goes wrong
	 */
	public static List<Mutation> getMutations(final int pId, final int cId)
			throws SQLException {
		String query = "SELECT * FROM mutations WHERE p_id = '" + pId + "' and chromosome = '" + cId + "';";
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
			int startPoint = rs.getInt("startpoint");
			int endPoint = rs.getInt("endpoint");
			int positionGRCH37 = rs.getInt("GRCH37_pos");
			mutations.add(new Mutation(id, sort, rsID, chromosome, alleles,
					startPoint, endPoint, genotypescontext, positionGRCH37));
		}
		return mutations;
	}
	/**
	 * Makes an allele collection of the alleles of the child (the first two
	 * alleles in the string).
	 * 
	 * @param allelesString
	 *            The String of alleles
	 * 
	 * @return Returns a collection of alleles
	 */
	protected static Collection<Allele> toAlleleCollection(
			final String allelesString) {
		Collection<Allele> alleles = new ArrayList<Allele>();
		alleles.add(toAllele(allelesString.substring(0, 1), true));
		alleles.add(toAllele(allelesString.substring(1, 2), false));
		return alleles;
	}

	/**
	 * Makes a GenotypesContext of the alleles of the parents using the 3rd to
	 * 6th alleles in the string.
	 * 
	 * @param s
	 *            The string used
	 * 
	 * @return Returns as a GenotypesContext
	 */
	protected static GenotypesContext toGenotypesContext(final String s) {
		GenotypesContext gc = GenotypesContext.create();
		gc.add(toGenotype(s.substring(2, 4), "FATHER"));
		gc.add(toGenotype(s.substring(4, 6), "MOTHER"));
		return gc;
	}

	/**
	 * Makes a Genotype of a 2-letter string representing two alleles.
	 * 
	 * @param allelesString
	 *            The string of the alleles
	 * @param name
	 *            The name
	 * 
	 * @return Returns a Genotype
	 */
	protected static Genotype toGenotype(final String allelesString,
			final String name) {
		List<Allele> alleles = new ArrayList<Allele>();
		alleles.add(toAllele(allelesString.substring(0, 1), false));
		alleles.add(toAllele(allelesString.substring(1, 2), false));
		return GenotypeBuilder.create(name, alleles);
	}

	/**
	 * Creates an allele from the given string.
	 * 
	 * @param s
	 *            The give string
	 * @param isref
	 *            If it is ref or not
	 * 
	 * @return Returns an Allele
	 */
	protected static Allele toAllele(final String s, final boolean isref) {
		return Allele.create(s, isref);
	}
	
	public ArrayList<Integer> getPositions() {
		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(103843421);
		list.add(186633831);
		return list;
	}
}