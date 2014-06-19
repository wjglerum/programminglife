package models.reader;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import models.database.Database;
import models.database.QueryProcessor;
import models.mutation.Mutation;
import models.mutation.MutationRepositoryDB;
import models.mutation.MutationService;
import models.patient.Patient;
import models.protein.ProteinConnection;
import models.protein.ProteinGraph;

/**
 * Separate thread for reading VCF files.
 */
public class ReaderThread implements Runnable {

    private Thread thread;
    private Patient patient;
    private String filePath;

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
            findProteinConnections(
            		mutations);
        } catch (
        		SQLException e) {
            // TODO Auto-generated catch
        	// block
            e.printStackTrace();
        }

        updatePatientToProcessed();
    }

    private List<Mutation> getMutations() {
        return VCFReader.getMutations(filePath);
    }

    private void addMutationsToDatabase(List<Mutation> mutations)
    		throws SQLException {
        // Add each mutation to the database
        MutationRepositoryDB mutationRepository = new MutationRepositoryDB();
        MutationService mutationService = new MutationService(
                mutationRepository);
        for (Mutation m : mutations) {
            insertMutationToDatabase(mutationService, m);
        }
    }

	private void insertMutationToDatabase(MutationService mutationService,
			Mutation m) throws SQLException {
		QueryProcessor.insertMutation(patient.getId(), m.getMutationType(),
				m.getRsID(), m.getChromosome(), m.toAllelesString(),
				m.getStart(), m.getEnd(), m.getPositionGRCH37(),
				mutationService.getScore(m),
				QueryProcessor.getFrequency(m.getID(),
						m.getAlleles().get(0).getBaseString()));
	}

	private void updatePatientToProcessed() {
        String query = "UPDATE patient SET processed = 'true' WHERE p_id = "
        		+ patient.getId() + ";";

        Database.insert("data", query);
    }
	
	private void findProteinConnections(List<Mutation> mutations) {
		final int limit = 30;
		final int threshold = 300;
		for (Mutation m : mutations) {
			try {
				ProteinGraph pg = new ProteinGraph();
				pg.addConnectionsOfSnp(Integer.parseInt(m.getRsID()
						.substring(2)), limit, threshold);
				for (ProteinConnection pc : pg.getConnections()) {
					pc.insertIntoDB(patient.getId());
				}
			} catch (NumberFormatException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
