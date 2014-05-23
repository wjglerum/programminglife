package models;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import play.Logger;
import play.db.DB;

public class Patient {

	public int id;
	public String name;
	public String surname;
	
	public String vcf_file;
	public Long vcf_length;

	public Patient(int id, String name, String surname, String vcf_file, Long vcf_length) {
		this.id = id;
		this.name = name;
		this.surname = surname;
		this.vcf_file = vcf_file;
		this.vcf_length = vcf_length;
	}

	/*
	 * Lookup patient in the database by p_id
	 */
	public static Patient get(int p_id, int u_id) {
		try (Connection con = DB.getConnection("data");) {
			String query = "SELECT * FROM patient WHERE p_id=" + p_id + " AND u_id=" + u_id + ";";
			ResultSet rs = con.createStatement().executeQuery(query);

			if (rs.next()) {
				String name = rs.getString("name");
				String surname = rs.getString("surname");
        String vcf_file = rs.getString("vcf_file");
        Long vcf_length = rs.getLong("vcf_length");
				
				return new Patient(p_id, name, surname, vcf_file, vcf_length);
			}
		} catch (SQLException e) {
			Logger.info((e.toString()));
		}
		return null;
	}

	/*
	 * Make a list of all the patients in the database
	 */
	public static List<Patient> getAll(int u_id) {
		try (Connection con = DB.getConnection("data");) {
			String query = "SELECT * FROM patient WHERE u_id = '" + u_id + "';";
			ResultSet rs = con.createStatement().executeQuery(query);
			List<Patient> patients = new ArrayList<Patient>();

			while (rs.next()) {
				int id = rs.getInt("p_id");
				String name = rs.getString("name");
				String surname = rs.getString("surname");
        String vcf_file = rs.getString("vcf_file");
        Long vcf_length = rs.getLong("vcf_length");
        
				patients.add(new Patient(id, name, surname, vcf_file, vcf_length));
			}
			return patients;
		} catch (SQLException e) {
			Logger.info((e.toString()));
		}
		return null;
	}

  /*
   * Add users to the database, id's will be auto incremented
   */
  public static void add(int u_id, String name, String surname, String vcf_File, Long vcf_length) {
    try (Connection con = DB.getConnection("data");) {
      String query = "INSERT INTO patient VALUES (nextval('p_id_seq'::regclass),"
          + u_id + ",'" + name + "', '" + surname + "', '" + vcf_File + "', " + vcf_length + ");";
      Statement st = con.createStatement();
      st.executeUpdate(query);
    } catch (SQLException e) {
      Logger.info((e.toString()));
    }
  }

  /*
   * Remove a patient and it's mutations from the database
   */
  public static void remove(Patient p) {
    try (Connection con = DB.getConnection("data");) {
      String queryDeletePatient = "DELETE FROM patient WHERE p_id = " + p.id;
      String queryDeleteMutations = "DELETE FROM mutations WHERE p_id = " + p.id;
      
      Statement st = con.createStatement();
      
      st.execute(queryDeletePatient);
      st.execute(queryDeleteMutations);
    } catch (SQLException e) {
      Logger.info((e.toString()));
    }
  }
  
  public Double vcf_lengthMB() {
    return ((double) (Math.round(vcf_length.doubleValue()/1024/1024 * 10))) / 10;
  }
}
