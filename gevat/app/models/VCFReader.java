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

public class VCFReader {
	/**
	 * Return a list of mutations found in the file.
	 */
	public static List<Mutation> getMutations(String fileName) {
		FeatureReader<VariantContext> fr = AbstractFeatureReader
				.getFeatureReader(fileName, new VCFCodec(), false);
		fr.getHeader();
		List<Mutation> SNPList = getMutations(fr);
		try {
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return SNPList;
	}

	protected static List<Mutation> getMutations(
			FeatureReader<VariantContext> fr) {
		List<Mutation> SNPList = new ArrayList<Mutation>();
		try {
			Iterator<VariantContext> it;
			it = fr.iterator();
			while (it.hasNext()) {
				VariantContext vc = it.next();
				if (hasMutation(vc)) {
					SNPList.add(toMutation(vc, "SNP"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SNPList;
	}

	/**
	 * Takes alleles from the child and parents and checks whether a mutation
	 * must have taken place in order to get this allele combination
	 */
	public static boolean hasMutation(VariantContext vc) {
		// TODO: take names like "DAUGHTER" from file
		List<Allele> D = vc.getGenotype("DAUGHTER").getAlleles();
		List<Allele> F = vc.getGenotype("FATHER").getAlleles();
		List<Allele> M = vc.getGenotype("MOTHER").getAlleles();
		return !possibleAlleleSet(D, F, M);
	}

	/**
	 * Store variantcontext with a mutation as a mutation.
	 */
	public static Mutation toMutation(VariantContext vc, String mutationType) {
		return new Mutation(vc, mutationType);
	}

	/**
	 * Checks whether this is a possible allele set: one of the childs alleles
	 * is from the father and one is from the mother.
	 */
	public static boolean possibleAlleleSet(List<Allele> D, List<Allele> F,
			List<Allele> M) {
		return ((F.contains(D.get(0)) && M.contains(D.get(1))) || (F.contains(D
				.get(1)) && M.contains(D.get(0))));
	}
}
