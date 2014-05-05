package at.ac.uibk.dps.biohadoopspring;

import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.yarn.am.AppmasterServiceClient;
import org.springframework.yarn.container.YarnContainer;

public class SimpleYarnContainer implements YarnContainer, AppmasterServiceClient {

	private static final Log log = LogFactory.getLog(SimpleYarnContainer.class);
	@Autowired
	SimpleService client;
	
	@Override
	public void run() {
		log.error("Hello from MyCustomYarnContainer!!");
//		SimpleRequest request = new SimpleRequest();
//		request.setText(new Date().toString());
//		SimpleResponse response = client.getEcho(request);
//		log.error("######got response " + response);
	}

	@Override
	public void setEnvironment(Map<String, String> environment) {
		log.error("Hello from setEnvironment!!");

	}

	@Override
	public void setParameters(Properties parameters) {
		log.error("Hello from setParameters122!!");
	}

}