package com.acm;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthorData {
	@JsonProperty("author_profile_id")
	private String authorProfileID;
		
	public String getAuthorProfileID() {
		return authorProfileID;
	}

	public void setAuthorProfileID(String authorProfileID) {
		this.authorProfileID = authorProfileID;
	}

}
