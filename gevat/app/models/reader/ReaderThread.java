package models.reader;

import java.sql.SQLException;
import java.util.List;

import models.database.Database;
import models.database.QueryProcessor;
import models.mutation.Mutation;
import models.patient.Patient;

/**
 * Separate thread for reading VCF files.
 */
public class ReaderThread implements Runnable {

    private Thread thread;
    private Patient patient;
    private String filePath;    
    private static QueryProcessor qp;

    /**
     * @param patient The patient
     * @param filePath The filepath
     */
    public ReaderThread(Patient patient, String filePath) {
        this.patient = patient;
        this.filePath = filePath;
    }

    /**
     * Starts the thread.
     */
    public void start() {
        qp = new QueryProcessor();
       if (thread == null) {
          thread = new Thread(this);
          thread.start();
       }
    }

    /**
     * runs the thread.
     */
    public void run() {
        // Process VCF file
        List<Mutation> mutations = getMutations();

        try {
            addMutationsToDatabase(mutations);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        updatePatientToProcessed();
    }

    private List<Mutation> getMutations() {
        return VCFReader.getMutations(filePath);
    }

    private void addMutationsToDatabase(List<Mutation> mutations) throws SQLException {
        // Add each mutation to the database
        for (Mutation m : mutations) {
            String query = "INSERT INTO mutations VALUES (nextval('m_id_seq'::regclass),"
                    + patient.getId()
                    + ",'"
                    + m.getMutationType()
                    + "','"
                    + m.getRsID()
                    + "','"
                    + m.getChromosome()
                    + "','"
                    + m.toAllelesString()
                    + "',"
                    + m.getStart()
                    + ","
                    + m.getEnd()
                    + ","
                    + m.getPositionGRCH37()
                    + ","
                    + m.getScore()
                    + ","
                    + QueryProcessor.getFrequency(m.getID(), m.getAlleles().get(0).getBaseString())
                    + ");";
            System.out.println(query);
            Database.insert("data", query);
        }
    }

    private void updatePatientToProcessed() {
        String query = "UPDATE patient SET processed = 'true' WHERE p_id = "
        		+ patient.getId() + ";";

        Database.insert("data", query);
    }

}