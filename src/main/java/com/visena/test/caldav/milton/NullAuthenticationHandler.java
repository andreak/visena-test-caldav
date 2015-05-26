package com.visena.test.caldav.milton;

import io.milton.http.AuthenticationHandler;
import io.milton.http.Request;
import io.milton.resource.Resource;

import java.util.List;

public class NullAuthenticationHandler implements AuthenticationHandler {
	public boolean supports(Resource r, Request request) {
		return true;
	}

	public Object authenticate(Resource resource, Request request) {
		return new Object();
	}

	public void appendChallenges(Resource resource, Request request, List<String> challenges) {

	}

	public boolean isCompatible(Resource resource, Request request) {
		return false;
	}

	public boolean credentialsPresent(Request request) {
		return false;
	}
}
