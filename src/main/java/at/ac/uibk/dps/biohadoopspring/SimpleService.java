package at.ac.uibk.dps.biohadoopspring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.yarn.am.AppmasterService;

public class SimpleService implements AppmasterService {

	private static final Log log = LogFactory.getLog(SimpleService.class);
	
	@Override
	public int getPort() {
		log.error("#######getPort");
		return 31001;
	}

	@Override
	public boolean hasPort() {
		log.error("#######hasPort");
		return true;
	}

	@Override
	public String getHost() {
		log.error("#######getHost");
		return "master";
	}
	
	public SimpleResponse getEcho(SimpleRequest request) {
		log.error("######got request " + request.getText());
		SimpleResponse response = new SimpleResponse();
		response.setText("ECHO: " + request.getText());
		return response;
	}

}
