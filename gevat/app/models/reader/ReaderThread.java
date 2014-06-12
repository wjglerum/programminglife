package models.reader;

import java.util.List;

import models.database.Database;
import models.dna.Mutation;
import models.patient.Patient;

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
      List<Mutation> mutations = VCFReader.getMutations(filePath);

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

}