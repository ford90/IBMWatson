package com.acm;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExplanationTags {
	@JsonProperty("concept")
	private QueryConcept concept;

	public QueryConcept getConcept() {
		return concept;
	}

	public void setConcept(QueryConcept concept) {
		this.concept = concept;
	}

}
