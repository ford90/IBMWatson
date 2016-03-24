package com.acm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class WatsonDao {

	private Connection conn;
	
	public WatsonDao(){
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.
                getConnection("jdbc:oracle:thin:@172.16.10.89:1521:RAC2"
                    ,"opsdl","passthrough");
		} catch(Exception e){
			e.printStackTrace();
		}	
	}
	
	public List<String> getIDs(){
		/**/	
		String sql = "select id"
				+ "       from esearch.ibm_watson_ok "
				+ "       where id not in ( " 
				+ "       select id "
				+ "       from opsdl.citation_watson_concepts"
				+ "       ) and rownum < 1000";
		
		/* 
			String sql = "select id, json "
					+ "       from esearch.ibm_watson_ok "
					+ "       where id = '998955' ";
	*/
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<String> id_l = new ArrayList<String>();
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				id_l.add(rs.getString("id"));
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			try { ps.close(); } catch(Exception e){}
			try { rs.close(); } catch(Exception e){}
			ps = null;
			rs = null;
		}
		return id_l;
	}
	
	public String getFullJson(String id){
		String sql = " select json "
				+ "    from esearch.ibm_watson_ok "
				+ "    where id = '" + id + "' ";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		String json = null;
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next())
				json = rs.getString("json");
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			try { ps.close(); } catch(Exception e){}
			try { rs.close(); } catch(Exception e){}
			ps = null;
			rs = null;
		}
		return json;
	}
	
	public void insertConceptWatson(QueryConcept concept, String id) {
        
		String insertSQL = " insert into opsdl.watson_concepts ( CONCEPT_ID, CONCEPT, DESCRIPTION, URI ) "
				+ "         values ( ?, ?, ?, ? )";
		PreparedStatement ps = null;
		String conceptID = conceptExsit(concept.getLabel()); 
		if( conceptID == null  ) {

			try {
				conceptID = getConceptID();
				ps = conn.prepareStatement(insertSQL);
				ps.setString(1, conceptID);
				ps.setString(2, concept.getLabel());
				ps.setString(3, concept.getAbs());
				ps.setString(4, concept.getId());
				
				ps.executeUpdate();

				ps.close();
				ps = null;
				
				String insertCitWatsonConcept = "insert into opsdl.citation_watson_concepts(id, concept_id) "
						+ "                      values ( ?, ? )";
				ps = conn.prepareStatement(insertCitWatsonConcept);
				ps.setString(1, id);
				ps.setString(2, conceptID);
				
				ps.executeUpdate();
				ps.close();
				ps = null;
				
			} catch(Exception e){
				e.printStackTrace();
			}finally {
				try { ps.close(); } catch(Exception e){}
				ps = null;
			}
		}
		
	}

	private String getConceptID() {
		String sql = "select to_char(concept_seq.nextval) from dual";
		PreparedStatement ps = null;
		ResultSet rs = null;
		String conceptID = null;
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			if(rs.next())
				conceptID = rs.getString(1);
			else
				throw new Exception();
			
		} catch(Exception e){
			e.printStackTrace();
		}finally {
			try { ps.close(); } catch(Exception e){}
			try { rs.close(); } catch(Exception e){}
			ps = null;
			rs = null;
		}
		return conceptID;
	}

	public void insertProfileData(Author author, String id){
		String sql = "insert into citation_watson_profile( id, profile_id ) values ( ?, ? )";
		PreparedStatement ps = null;
		
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, id);
			ps.setString(2, author.getUserField().getAuthorData().getAuthorProfileID());
			
			ps.executeUpdate();

		} catch(Exception e){
			e.printStackTrace();
		}finally {
			try { ps.close(); } catch(Exception e){}
			ps = null;
		}
		
		String profileID = author.getUserField().getAuthorData().getAuthorProfileID();
		for(ExplanationTags tag : author.getExplanationTags()){
			insertProfileConceptWork(profileID, tag.getConcept());
		}
		
		for(String paperURL : author.getUserField().getRelatedPapers()) {
			insertRelatedPaper(id, profileID, paperURL);
//			System.out.println(paperURL);
		}
		
//		insertProfileConceptWork(author);
	}

	private void insertRelatedPaper(String id, String profileID, String paperURL) {
		String sql = "insert into watson_evidence( ID, PROFILE_ID, PAPER_URL ) values ( ?, ?, ? )";
		PreparedStatement ps = null;
		
		try {
			ps = conn.prepareStatement(sql);

			ps.setString(1, id);
			ps.setString(2, profileID);
			ps.setString(3, paperURL);
			
			ps.executeUpdate();

		} catch(Exception e){
			e.printStackTrace();
		}finally {
			try { ps.close(); } catch(Exception e){}
			ps = null;
		}		
		
	}

	private void insertProfileConceptWork(String profileID, QueryConcept concept) {
		String sql = "insert into profile_watson_concept_work( profile_id, concept, uri ) values ( ?, ?, ? )";
		PreparedStatement ps = null;
		
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, profileID);
			ps.setString(2, concept.getLabel());
			ps.setString(3, concept.getId());
			
			ps.executeUpdate();

		} catch(Exception e){
			e.printStackTrace();
		}finally {
			try { ps.close(); } catch(Exception e){}
			ps = null;
		}		
	}

	private String conceptExsit(String conceptStr){
		String tempStr = conceptStr.toLowerCase().trim();
		tempStr= tempStr.replaceAll("\\s+", " ");
		
		String sql = "select concept_id "
				+ "   from opsdl.watson_concepts "
				+ "   where trim(lower(concept)) = ?";
		String conceptID = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			ps = conn.prepareStatement(sql);
			ps.setString(1, tempStr);
			
			rs = ps.executeQuery();
			if(rs.next())
				conceptID = rs.getString("concept_id");
		} catch(Exception e){
			e.printStackTrace();
		}
		finally {
			try { ps.close(); } catch(Exception e){}
			try { rs.close(); } catch(Exception e){}
			ps = null;
			rs = null;
		}
		return conceptID;
	}
	
}
