package at.ac.uibk.dps.biohadoopspring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.yarn.am.AppmasterServiceClient;
import org.springframework.yarn.annotation.YarnContainer;

public class SimpleClient implements AppmasterServiceClient {

	private static final Log log = LogFactory.getLog(SimpleClient.class);
	
	public SimpleResponse getEcho(SimpleRequest request) {
		log.error("######got request " + request.getText());
		SimpleResponse response = new SimpleResponse();
		response.setText("ECHO: " + request.getText());
		return response;
	}

}
