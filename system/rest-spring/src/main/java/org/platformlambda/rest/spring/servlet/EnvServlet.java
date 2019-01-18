package org.platformlambda.rest.spring.servlet;

import org.platformlambda.core.serializers.SimpleMapper;
import org.platformlambda.core.util.AppConfigReader;
import org.platformlambda.core.util.Utility;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/env")
public class EnvServlet extends HttpServlet {

	private static final long serialVersionUID = 3495394964619652075L;
	private static final String SHOW_ENV = "show.env.variables";
	private static final String SHOW_PROPERTIES = "show.application.properties";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		Map<String, Object> result = new HashMap<>(HealthServlet.getBasicInfo());

		Utility util = Utility.getInstance();
		AppConfigReader reader = AppConfigReader.getInstance();

		List<String> envVars = util.split(reader.getProperty(SHOW_ENV, ""), ", ");
		List<String> properties = util.split(reader.getProperty(SHOW_PROPERTIES, ""), ", ");

		Map<String, Object> eMap = new HashMap<>();
		if (!envVars.isEmpty()) {
			for (String var: envVars) {
				String v = System.getenv(var);
				eMap.put(var, v == null? "?" : v);
			}
		}
		result.put("systemEnvironment", eMap);

		Map<String, Object> pMap = new HashMap<>();
		if (!properties.isEmpty()) {
			for (String var: properties) {
				String v = reader.getProperty(var);
				pMap.put(var, v == null? "?" : v);
			}
		}
		result.put("applicationProperties", pMap);
		
		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");
		response.getWriter().write(SimpleMapper.getInstance().getMapper().writeValueAsString(result));
	}

}
