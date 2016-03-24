package com.acm;

import java.sql.Connection;
import java.util.List;

import acmLib.json.ACMObjectMapper;

public class ParseWatson {

	private Connection conn;
	private WatsonDao dao;
	private ACMObjectMapper mapper;

	public ParseWatson(){
		dao = new WatsonDao();
		mapper = new ACMObjectMapper("");
	}
	
	public void parse(){
		// Get id's that need to be parsed
		List<String> id_l = dao.getIDs();
		String json = null;
		for(String id : id_l){
//			System.out.println(id);
			json = dao.getFullJson(id);
			mapper.setJson(json);
			List<QueryConcept> queryConcept_l = mapper.getObjectList(QueryConcept.class, "query_concepts");
			for(QueryConcept concept : queryConcept_l) {
				dao.insertConceptWatson(concept, id);
			}
			
			
			List<Author> author_l = mapper.getObjectList(Author.class, "results");
			for(Author author : author_l){
				dao.insertProfileData(author, id);
			}

		}

	}

	private String fixString(String str){
		String result = str.toLowerCase().trim();
		result = result.replaceAll("\\s+", " ");

		return result;
	}
	
	
}
