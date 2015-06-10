package com.visena.test.caldav.milton;

import io.milton.http.*;
import io.milton.http.fs.NullSecurityManager;
import io.milton.http.fs.SimpleSecurityManager;
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
		builder.setEnableDigestAuth(false);
		builder.setFsRealm("Visena");
		return super.configure(config);
	}

}
