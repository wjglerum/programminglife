package models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
		//not called
	}

	/**
	 * Gets the mutations from the file.
	 *
	 * @param fileName The name of the VCF-file.
	 *
	 * @return Returns a list of the SNP's
	 */
	public static List<Mutation> getMutations(final String fileName) {
		FeatureReader<VariantContext> fr = AbstractFeatureReader
				.getFeatureReader(fileName, new VCFCodec(),
						false);
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
		try {
			Iterator<VariantContext> it;
			it = fr.iterator();
			while (it.hasNext()) {
				VariantContext vc = it.next();
				if (hasMutation(vc)) {
					listSNP.add(toMutation(vc, "SNP"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listSNP;
	}

	/**
	 * Takes alleles from the child and parents and checks whether a
	 * mutation must have taken place in order to get this allele
	 * combination.
	 *
	 * @param vc The VCF-data
	 *
	 * @return Returns whether or not it is a valid set.
	 */
	public static boolean hasMutation(final VariantContext vc) {
		// TODO: take names like "DAUGHTER" from file
		List<Allele> d = vc.getGenotype("DAUGHTER").getAlleles();
		List<Allele> f = vc.getGenotype("FATHER").getAlleles();
		List<Allele> m = vc.getGenotype("MOTHER").getAlleles();
		return !possibleAlleleSet(d, f, m);
	}

	/**
	 * Store variantcontext with a mutation as a mutation.
	 *
	 * @param vc The VCF-data
	 * @param mutationType The type of mutation
	 *
	 * @return Returns a new @link{Mutation}
	 */
	public static Mutation toMutation(final VariantContext vc,
			final String mutationType) {
		return new Mutation(vc, mutationType);
	}

	/**
	 * Checks whether this is a possible allele set: one of the childs
	 * alleles is from the father and one is from the mother.
	 *
	 * @param d The set of alleles of the daughter
	 * @param f The set of alleles of the father
	 * @param m The set of alleles of the mother
	 *
	 * @return Returns true if it is a valid set
	 */
	public static boolean possibleAlleleSet(final List<Allele> d,
			final List<Allele> f, final List<Allele> m) {
		return ((f.contains(d.get(0)) && m.contains(d.get(1)))
				|| (f.contains(d.get(1))
						&& m.contains(d.get(0))));
	}
}
