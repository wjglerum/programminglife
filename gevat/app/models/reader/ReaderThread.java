package models.reader;

import java.util.List;

import play.Logger;
import models.database.Database;
import models.mutation.Mutation;
import models.patient.Patient;
import models.protein.ProteinConnection;
import models.protein.ProteinGraph;

public class ReaderThread implements Runnable {

    private Thread thread;
    private Patient patient;
    private String filePath;    
  
    public ReaderThread(Patient patient, String filePath) {
      this.patient = patient;
      this.filePath = filePath;
    }
    
    public void start () {
       if (thread == null) {
          thread = new Thread (this);
          thread.start ();
       }
    }
  
    public void run() {
      // Process VCF file
      List<Mutation> mutations = getMutations();
      
      addMutationsToDatabase(mutations);
      
      updatePatientToProcessed();
    }
    
    private List<Mutation> getMutations() {
      return VCFReader.getMutations(filePath);
    }
    
    private void addMutationsToDatabase(List<Mutation> mutations) {
      // Add each mutation to the database
      for (Mutation m : mutations) {
        String query = "INSERT INTO mutations VALUES (nextval('m_id_seq'::regclass),"
            + patient.getId()
            + ",'"
            + m.getMutationType()
            + "','"
            + m.getRsID()
            + "',"
            + m.getChromosome()
            + ",'"
            + m.toAllelesString()
            + "',"
            + m.getStart()
            + ","
            + m.getEnd()
            + ","
            + m.getPositionGRCH37()
            + ");";
        
        Database.insert("data", query);
      }
    }
    
    private void updatePatientToProcessed() {
      String query = "UPDATE patient SET processed = 'true' WHERE p_id = " + patient.getId() + ";";
      
      Database.insert("data", query);
    }
    
/* Unused until protein package is updated 
	public static void findProteinConnections(List<Mutation> mutations, Patient p)
	{
		Commented until package protein is updated
		Logger.info(p.getName() + " " + p.getSurname() + ": " + p.getId());
		ProteinGraph pg = new ProteinGraph();
		for(Mutation m: mutations)
		{
			pg.addConnectionsOfSnp(Integer.parseInt(m.getRsID().substring(2)), 10, 300);
		}
		for(ProteinConnection pc : pg.getConnections())
		{
			pc.insertIntoDB(p.getId());
		}
	}
	*/
}