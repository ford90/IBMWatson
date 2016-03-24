package com.acm;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Author {
	@JsonProperty("explanation_tags")
	private List<ExplanationTags> explanationTags;
	
	@JsonProperty("user_fields")
	private UserField userField;
	
	public List<ExplanationTags> getExplanationTags() {
		return explanationTags;
	}

	public void setExplanationTags(List<ExplanationTags> explanationTags) {
		this.explanationTags = explanationTags;
	}

	public UserField getUserField() {
		return userField;
	}

	public void setUserField(UserField userField) {
		this.userField = userField;
	}

}
