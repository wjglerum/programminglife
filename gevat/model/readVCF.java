import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.broad.tribble.AbstractFeatureReader;
import org.broad.tribble.FeatureReader;
import org.broadinstitute.sting.gatk.walkers.phasing.PhaseByTransmission;
import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.VariantContext;
import org.broadinstitute.variant.vcf.VCFCodec;

public class readVCF {
	public static void main(String args[]) throws IOException
	{
		FeatureReader<VariantContext> fr = AbstractFeatureReader.getFeatureReader("H:/vakken/programming life/trio/anonymous_trio1.vcf", new VCFCodec(), false);
		fr.getHeader();
		System.out.println(fr.getHeader());
		analyse(fr);
	}
	
	public static void analyse(FeatureReader<VariantContext> fr) throws IOException
	{
		long i=0;
		Iterator<VariantContext> it = fr.iterator();
		PhaseByTransmission pbs = new PhaseByTransmission();
		System.out.println(pbs.toString());
		while( it.hasNext() )
		{
			try {
				VariantContext vc = it.next();
				List<Allele> D = vc.getGenotype("DAUGHTER").getAlleles();
				List<Allele> F = vc.getGenotype("FATHER").getAlleles();
				List<Allele> M = vc.getGenotype("MOTHER").getAlleles();
				if(!possibleAlleleSet(D, F, M))
					System.out.println(vc.getGenotype("DAUGHTER").getAlleles() + "\t" + vc.getGenotype("FATHER").getAlleles() + "\t" + vc.getGenotype("MOTHER").getAlleles() + "\t" + vc);
				i++;
			} catch(Exception e) {
			    e.printStackTrace();
			}
		}
	}
	
	public static boolean possibleAlleleSet(List<Allele> D, List<Allele> F, List<Allele> M)
	{
	    return ((F.contains(D.get(0)) && M.contains(D.get(1))) || (F.contains(D.get(1)) && M.contains(D.get(0))));
	}
}
