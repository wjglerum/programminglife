package models;

public class Mutation {

	private String type;
	private int rs;
	private int chromosome;
	private char[] allele;
	
	public Mutation(String type, int rs, int chromosome, char[] allele) {
		this.type = type;
		this.rs = rs;
		this.chromosome = chromosome;
		this.allele = allele;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getRs() {
		return rs;
	}
	public void setRs(int rs) {
		this.rs = rs;
	}
	public int getChromosome() {
		return chromosome;
	}
	public void setChromosome(int chromosome) {
		this.chromosome = chromosome;
	}
	public char[] getAllele() {
		return allele;
	}
	public void setAllele(char[] allele) {
		this.allele = allele;
	}
}
