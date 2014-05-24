package models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.GenotypesContext;
import org.broadinstitute.variant.variantcontext.VariantContext;

public class Mutation extends VariantContext {

    public String mutationType;
/*
    protected Mutation(String source, String ID, String contig, long start, long stop, Collection<Allele> alleles, GenotypesContext genotypes,
                         double log10PError, Set<String> filters, Map<String,Object> attributes,
                         boolean fullyDecoded, EnumSet<VariantContext.Validation> validationToPerform) {
        super(source, ID, contig, start, stop, alleles, genotypes, log10PError, filters, attributes, fullyDecoded, validationToPerform);
    }*/

	/**
	 * Store information about a mutation
	 * 
	 * @param id
	 * @param sort
	 * @param rsID
	 * @param chromosome
	 * @param alleles
	 */
	public Mutation(int id, String sort, String rsID, int chromosome,
			Collection<Allele> alleles) {
        super(null, rsID, Integer.toString(chromosome), id, id, null, null, 0, null, null, false, null);
        this.mutationType=mutationType;
	}

    public Mutation(VariantContext vc) {
        super(vc);
    }
    
    public String getMutationType()
    {
        return mutationType;
    }
    
    public String getRsID()
    {
        return getID();
    }
    
    public long getId()
    {
        return start;
    }
    
    public int getChromosome()
    {
        return Integer.parseInt(contig);
    }

	/**
	 * Return the alleles of the child
	 * 
	 * @return String
	 */
	public String child() {
        return getAlleles().toString();
	}

	/**
	 * Return the alleles of the father
	 * 
	 * @return String
	 */
	public String father() {
        return getGenotype("FATHER").getAlleles().toString();
	}

	/**
	 * Return the alleles of the mother
	 * 
	 * @return String
	 */
	public String mother() {
        return getGenotype("MOTHER").getAlleles().toString();
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
			Collection<Allele> alleles = toAlleleCollection(rs.getString("alleles"));
			mutations.add(new Mutation(id, sort, rsID, chromosome, alleles));
		}
		return mutations;
	}
	
	public static Collection<Allele> toAlleleCollection(String allelesString)
	{
	    Collection<Allele> alleles = new ArrayList<Allele>();
	    alleles.add(toAllele(allelesString.substring(0,2)));
	    return alleles;
	}
	
	public static Allele toAllele(String s)
	{
	    return Allele.create(s);
	}
}
