package models.patient;

import models.reader.ReaderThread;

/**
 * @author wjglerum
 */
public class Patient {

	private int id;
	private String name;
	private String surname;
	private String vcfFile;
	private Long vcfLength;
	private boolean processed;
	private boolean female;

	/**
	 * Basic Patient information.
	 *
	 * @param id The id of the patient
	 * @param name The first name of the patient
	 * @param surname The last name of the patient
	 * @param vcfFile The vcf-file associated with the patient
	 * @param vcfLength The lenght of the file
	 * @param processed True if the vcf-file is processed
	 * @param female True if the patient is female
	 */
	public Patient(final int id, final String name, final String surname,
			final String vcfFile, final Long vcfLength,
			final boolean processed, final boolean female) {
		this.id = id;
		this.name = name;
		this.surname = surname;
		this.vcfFile = vcfFile;
		this.vcfLength = vcfLength;
		this.processed = processed;
		this.female = female;
	}

	/**
	 * Gives the size of the VCF file in MB.
	 * @return A string representation of the file size
	 */
	public final String getVcfLengthMB() {
		final int megaSize = 1024 * 1024;
		return String.format("%.1f", vcfLength.doubleValue() / megaSize);
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

	public final boolean isFemale() {
		return female;
	}

	public final boolean isProcessed() {
		return processed;
	}

	/**
	 * Sets up a separate thread to read the VCF file.
	 * @param filePath The path of the VCF file
	 */
	public final void setupReaderThread(final String filePath) {
		// Setup a thread for processing the VCF
		ReaderThread readerThread = new ReaderThread(this, filePath);

		// Let the thread process the file in the background
		readerThread.start();
	}

}