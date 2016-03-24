package com.acm;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserField {
	@JsonProperty("author_data")
	private AuthorData authorData;

	public AuthorData getAuthorData() {
		return authorData;
	}

	@JsonProperty("paper_urls")
	private List<String> relatedPapers;
	
	public List<String> getRelatedPapers() {
		return relatedPapers;
	}

	public void setRelatedPapers(List<String> relatedPapers) {
		this.relatedPapers = relatedPapers;
	}

	public void setAuthorData(AuthorData authorData) {
		this.authorData = authorData;
	}

}
