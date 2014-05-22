package models;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
    public static void main(String args[]) throws IOException
    {
        getMutations("/home/rhvanstaveren/programming_life/anonymous_trio1.vcf");
    }
    
    public static List<Mutation> getMutations(String fileName) throws IOException
    {
        FeatureReader<VariantContext> fr = AbstractFeatureReader.getFeatureReader(fileName, new VCFCodec(), false);
        fr.getHeader();
        List<Mutation> SNPList = getMutations(fr);
        fr.close();
        return SNPList;
    }
    
    public static List<Mutation> getMutations(FeatureReader<VariantContext> fr)
    {
        try {
            Iterator<VariantContext> it;
            it = fr.iterator();
            List<Mutation> SNPList = new ArrayList<Mutation>();
            while( it.hasNext() )
            {
                VariantContext vc = it.next();
                if(hasMutation(vc))
                {
                    SNPList.add(toMutation(vc));
                }
            }
            return SNPList;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static boolean hasMutation(VariantContext vc)
    {
        List<Allele> D = vc.getGenotype("DAUGHTER").getAlleles();
        List<Allele> F = vc.getGenotype("FATHER").getAlleles();
        List<Allele> M = vc.getGenotype("MOTHER").getAlleles();
        return !possibleAlleleSet(D, F, M);
    }
    
    public static Mutation toMutation(VariantContext vc)
    {
        return new Mutation(vc);
    }
    
    public static boolean possibleAlleleSet(List<Allele> D, List<Allele> F, List<Allele> M)
    {
        return ((F.contains(D.get(0)) && M.contains(D.get(1))) || (F.contains(D.get(1)) && M.contains(D.get(0))));
    }
}
