package models.reader;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import models.database.QueryProcessor;
import models.mutation.Mutation;

import org.broad.tribble.AbstractFeatureReader;
import org.broad.tribble.FeatureReader;
import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.VariantContext;
import org.broadinstitute.variant.vcf.VCFCodec;

/**
 * This class reads the VCF-files.
 *
 * @author rhvanstaveren
 *
 */
public final class VCFReader {

    /**
     * Done because it is a utility-class.
     */
    private VCFReader() {
        // not called
    }

    /**
     * Gets the mutations from the file.
     *
     * @param fileName
     *            The name of the VCF-file.
     *
     * @return Returns a list of the SNP's
     */
    public static List<Mutation> getMutations(final String fileName) {
        FeatureReader<VariantContext> fr = AbstractFeatureReader
                .getFeatureReader(fileName, new VCFCodec(), false);
        fr.getHeader();
        List<Mutation> listSNP = getMutations(fr);
        try {
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listSNP;
    }

    /**
     * Gets the mutations from the FeatureReader.
     *
     * @param fr Reads the VCF-file
     *
     * @return Returns a list of SNP's
     */
    protected static List<Mutation> getMutations(
            final FeatureReader<VariantContext> fr) {
        List<Mutation> listSNP = new ArrayList<Mutation>();
        ArrayList<Mutation> output = new ArrayList<Mutation>();
        HashMap<Integer, Mutation> output2 = new HashMap<Integer, Mutation>();
        
        try {
            Iterator<VariantContext> it = fr.iterator();
            while (it.hasNext()) {
                VariantContext vc = it.next();
                checkVariantContext(listSNP, output2, vc);
            }
            output = QueryProcessor.filterOnFrequency(output2, split(output2));
        } catch (Exception e) {
            e.printStackTrace();
        }
        listSNP.addAll(output);
        return listSNP;
    }

	private static void checkVariantContext(List<Mutation> listSNP,
			HashMap<Integer, Mutation> output2, VariantContext vc)
			throws SQLException {
		String[] idAsString = vc.getID().split(";");
		if (idAsString[0].length() > 2) {
		    int id = Integer.parseInt(idAsString[0].substring(2));
		    checkForMutation(listSNP, output2, vc, id);
		}
	}

	private static void checkForMutation(List<Mutation> listSNP,
			HashMap<Integer, Mutation> output2, VariantContext vc, int id)
			throws SQLException {
		if (hasMutation(vc)) {
		    ArrayList<String> list = QueryProcessor.findGenesAssociatedWithSNP(id);
		    if (!list.isEmpty()) {
		        listSNP.add(toMutation(vc, "De Novo"));
		    }
		} else if (isPotentialHomozygousRecessive(vc)) {
		    Mutation m = toMutation(vc, "Recessive Homozygous");
		    output2.put(id, m);
		}
	}

    /**
     * Takes alleles from the child and parents and checks whether a mutation
     * must have taken place in order to get this allele combination.
     *
     * @param vc
     *            The VCF-data
     *
     * @return Returns whether or not it is a valid set.
     */
    public static boolean hasMutation(final VariantContext vc) {
        List<Allele> d = vc.getGenotype("DAUGHTER").getAlleles();
        List<Allele> f = vc.getGenotype("FATHER").getAlleles();
        List<Allele> m = vc.getGenotype("MOTHER").getAlleles();
        return !possibleAlleleSet(d, f, m);
    }

    /**
     * Store variantcontext with a mutation as a mutation.
     *
     * @param vc
     *            The VCF-data
     * @param mutationType
     *            The type of mutation
     *
     * @return Returns a new @link{Mutation}
     */
    public static Mutation toMutation(final VariantContext vc,
            final String mutationType) {
        return new Mutation(vc, mutationType);
    }

    /**
     * Checks whether this is a possible allele set: one of the childs alleles
     * is from the father and one is from the mother.
     *
     * @param d The set of alleles of the daughter
     * @param f The set of alleles of the father
     * @param m The set of alleles of the mother
     *
     * @return Returns true if it is a valid set
     */
    private static boolean possibleAlleleSet(final List<Allele> d,
            final List<Allele> f, final List<Allele> m) {
        return ((f.contains(d.get(0)) && m.contains(d.get(1))) || (f.contains(d
                .get(1)) && m.contains(d.get(0))));
    }

    private static boolean isPotentialHomozygousRecessive(
            final VariantContext vc) {
        List<Allele> d = vc.getGenotype("DAUGHTER").getAlleles();
        List<Allele> f = vc.getGenotype("FATHER").getAlleles();
        List<Allele> m = vc.getGenotype("MOTHER").getAlleles();
        return (isHeterozygous(f) && isHeterozygous(m) && isHomozygous(d));
    }

    private static boolean isHeterozygous(final List<Allele> input) {
        if (input.size() == 1) {
            return true;
        }
        return (!input.get(0).equals(input.get(1)));
    }

    private static boolean isHomozygous(final List<Allele> input) {
        if (input.size() == 1) {
            return false;
        }
        return (input.get(0).equals(input.get(1)));
    }

    /**
     * Splits a hashmap into four lists based on the bases.
     *
     * @param hm The hashmap to be splitted
     *
     * @return The splitted list
     */
    public static ArrayList<ArrayList<Mutation>> split(final HashMap<Integer, Mutation> hm) {
        ArrayList<ArrayList<Mutation>> splitted = new ArrayList<ArrayList<Mutation>>();
        for (int i = 0; i < 4; i++) {
            splitted.add(new ArrayList<Mutation>());
        }

        for (Entry<Integer, Mutation> m : hm.entrySet()) {
            switch (m.getValue().child().charAt(1)) {
                case 'A':   splitted.get(0)
                .add(m.getValue());
                            break;
                case 'T':   splitted.get(1)
                .add(m.getValue());
                            break;
                case 'C':   splitted.get(2)
                .add(m.getValue());
                            break;
                case 'G':   splitted.get(3)
                .add(m.getValue());
                            break;
                default : break;
            }
        }
        return splitted;
    }
}
