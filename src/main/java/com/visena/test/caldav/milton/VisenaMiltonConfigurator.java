package com.visena.test.caldav.milton;

import io.milton.http.*;
import io.milton.http.fs.NullSecurityManager;
import io.milton.servlet.Config;
import io.milton.servlet.DefaultMiltonConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VisenaMiltonConfigurator extends DefaultMiltonConfigurator {
	private static final Logger log = LoggerFactory.getLogger(VisenaMiltonConfigurator.class);

	@Override
	public HttpManager configure(Config config) throws ServletException {
		String davPath = config.getInitParameter("davPath");
		String contextPath = config.getServletContext().getContextPath() + (davPath != null ? davPath : "");
		log.trace(String.format("Using contextPath: %s", contextPath));
		builder.setContextPath(contextPath);
		return super.configure(config);
	}

	@Override
	protected void build() {
		NullSecurityManager securityManager = new NullSecurityManager();
		securityManager.setRealm("Visena WebDAV-server");
		builder.setSecurityManager(securityManager);
		/*
You can "turn it off' a few different ways. Assuming you are using authentication externally to milton
the best thing is to implement an AuthenticationHandler which returns information about the current user.
This will be used for locking (if you're using locking) and there are some special edge cases in the MS Office
support that need to know if there is a user or not.
Once you have implemented your auth handler you should inject it into the HttpManagerBuilder and disable
basic and digest auth, prior to building the HttpManager. How you do this depends on how you're configuring the system,
 ie with spring or web.xml etc
		 */
		List<AuthenticationHandler> authenticationHandlers = new ArrayList<AuthenticationHandler>();
		authenticationHandlers.add(new NullAuthenticationHandler());
		builder.setAuthenticationHandlers(authenticationHandlers);
		super.build();

	}
}
