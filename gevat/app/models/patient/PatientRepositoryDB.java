package models.patient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import play.Logger;
import play.db.DB;
import models.database.Database;

/**
 * Class for DB access for patients.
 * 
 * @author willem
 * 
 */
public class PatientRepositoryDB implements PatientRepository {

    private static PreparedStatement getAll;
    private static PreparedStatement get;
    private static PreparedStatement add;
    private static PreparedStatement remove;

    /**
     * Constructs a new PatientRepositoryDB.
     */
    public PatientRepositoryDB() {
        // Initialize all queries
        try {
            prepareQueries("data");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Lookup patient in the database by p_id.
     * 
     * @param pId
     *            The patient-id
     * @param uId
     *            The user-id
     * @return
     * 
     * @return Patient Returns the patient
     * 
     * @throws SQLException
     *             In case SQL goes wrong
     */
    @Override
    public Patient get(final int pId, final int uId) throws SQLException {
        get.setInt(1, pId);
        get.setInt(2, uId);
        ResultSet rs = get.executeQuery();
        if (rs.next()) {
            String name = rs.getString("name");
            String surname = rs.getString("surname");
            String vcfFile = rs.getString("vcf_file");
            Long vcfLength = rs.getLong("vcf_length");
            boolean processed = rs.getBoolean("processed");
            boolean female = rs.getBoolean("female");

            return new Patient(pId, name, surname, vcfFile, vcfLength,
                    processed, female);
        }
        return null;
    }

    /**
     * Make a list of all the patients in the database.
     * 
     * @param uId
     *            The user-id
     * 
     * @return List<Patient> A list of all the patients
     * 
     * @throws SQLException
     *             In case SQL goes wrong
     * @throws IOException IO Exception
     */
    @Override
    public List<Patient> getAll(final int uId) throws SQLException, IOException {
        getAll.setInt(1, uId);
        ResultSet rs = getAll.executeQuery();

        List<Patient> patients = new ArrayList<Patient>();

        while (rs.next()) {
            int id = rs.getInt("p_id");
            String name = rs.getString("name");
            String surname = rs.getString("surname");
            String vcfFile = rs.getString("vcf_file");
            Long vcfLength = rs.getLong("vcf_length");
            boolean processed = rs.getBoolean("processed");
            boolean female = rs.getBoolean("female");

            patients.add(new Patient(id, name, surname, vcfFile, vcfLength,
                    processed, female));
        }
        return patients;
    }

    /**
     * Add patient to the database, id's will be auto incremented.
     * 
     * @param uId
     *            The user-id
     * @param name
     *            The name
     * @param surname
     *            The surname
     * @param vcfFile
     *            The file
     * @param vcfLength
     *            The length of the VCF-file
     * @param female True when patient is female
     * 
     * @return Return the patient
     * 
     * @throws SQLException
     *             In case the SQL goes wrong
     */
    @Override
    public Patient add(final int uId, final String name, final String surname,
            final String vcfFile, final Long vcfLength, boolean female)
            throws SQLException {
    	int counter = 1;
    	
        add.setInt(counter++, uId);
        add.setString(counter++, name);
        add.setString(counter++, surname);
        add.setString(counter++, vcfFile);
        add.setLong(counter++, vcfLength);
        add.setBoolean(counter++, female);
        int i = add.executeUpdate();
        System.out.println("Dit ging goed: " + i);
        // TODO get id of added patient efficiently
        List<Patient> patients = null;
        try {
            patients = getAll(uId);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return patients.get(patients.size() - 1);
    }

    /**
     * Remove patient and linked mutations from database.
     * 
     * @param patient
     *            The patient to be deleted
     * @throws SQLException SQL Exception
     */
    @Override
    public void remove(final Patient patient) throws SQLException {
        remove.setInt(1, patient.getId());
        String queryDeleteMutations = "DELETE FROM mutations"
                + " WHERE p_id = " + patient.getId();
        Database.delete("data", queryDeleteMutations);
        remove.executeUpdate();
    }

    /**
     * Initialize prepared queries.
     * 
     * @param database The database to query from
     * @throws IOException
     *             If the query is not accessible
     */
    public static void prepareQueries(final String database) throws IOException {
        String getAllQuery = new String(Files.readAllBytes(Paths
                .get("public/sql/patients/getAll.sql")));
        String getQuery = new String(Files.readAllBytes(Paths
                .get("public/sql/patients/get.sql")));
        String addQuery = new String(Files.readAllBytes(Paths
                .get("public/sql/patients/add.sql")));
        String removeQuery = new String(Files.readAllBytes(Paths
                .get("public/sql/patients/delete.sql")));
        try (Connection connection = DB.getConnection(database);) {
            getAll = connection.prepareStatement(getAllQuery);
            get = connection.prepareStatement(getQuery);
            add = connection.prepareStatement(addQuery);
            remove = connection.prepareStatement(removeQuery);
        } catch (SQLException e) {
            Logger.error((e.toString()));
        }
    }
}
