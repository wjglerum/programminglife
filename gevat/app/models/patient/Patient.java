package models.patient;

/**
 * 
 * @author wjglerum
 * 
 */
public class Patient {

	private int id;
	private String name;
	private String surname;
	private String vcfFile;
	private Long vcfLength;

	/**
	 * Basic Patient information.
	 * 
	 * @param id
	 * @param name
	 * @param surname
	 * @param vcfFile
	 * @param vcfLength
	 */
	public Patient(final int id, final String name, final String surname,
			final String vcfFile, final Long vcfLength) {
		this.id = id;
		this.name = name;
		this.surname = surname;
		this.vcfFile = vcfFile;
		this.vcfLength = vcfLength;
	}

	public final Double getVcfLengthMB() {
		return ((double) (Math
				.round(vcfLength.doubleValue() / 1024 / 1024 * 10))) / 10;
	}

	public final String getName() {
		return name;
	}

	public final void setName(final String name) {
		this.name = name;
	}

	public final String getVcfFile() {
		return vcfFile;
	}

	public final void setVcfFile(final String vcfFile) {
		this.vcfFile = vcfFile;
	}

	public final String getSurname() {
		return surname;
	}

	public final void setSurname(final String surname) {
		this.surname = surname;
	}

	public final int getId() {
		return id;
	}

	public final void setId(int id) {
		this.id = id;
	}
}