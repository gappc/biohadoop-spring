/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.yarn.examples;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.yarn.YarnSystemConstants;
import org.springframework.yarn.integration.container.AbstractIntegrationYarnContainer;
import org.springframework.yarn.integration.ip.mind.MindAppmasterServiceClient;

/**
 * Customised container with following logic:
 *
 * 1. Loops itself and does requests to appmaster
 * 2. Request is basic what-to-do/job
 * 3. Randomly just die to simulate problems
 * 4. Send back job complete notifications
 * 5. Randomly just fail jobs
 *
 * @author Janne Valkealahti
 *
 */
public class CustomContainer extends AbstractIntegrationYarnContainer {

	private final static Log log = LogFactory.getLog(CustomContainer.class);

	@Override
	protected void runInternal() {
		MindAppmasterServiceClient client = (MindAppmasterServiceClient) getIntegrationServiceClient();
		Random random = new Random();
		Long job = null;
		boolean die = false;
		
		int count = 0;
		long startTime = System.currentTimeMillis();

		// run loop, we exit if appmaster asks or randomly
		// in approximately once every 10 runs
		do {
			JobRequest request = new JobRequest();
			if(job == null) {
				request.setState(JobRequest.State.WHATTODO);
			} else {
				// we send job failed message approximately once in 4 runs
//				request.setState(random.nextInt(4) == 0 ? JobRequest.State.JOBFAILED : JobRequest.State.JOBDONE);
				request.setState(JobRequest.State.JOBDONE);
				request.job = job;
			}
			JobResponse response = (JobResponse) client.doMindRequest(request);
			if (response == null) {
				die = true;
				break;
			}
			log.debug("Response state=" + response.getState());

			if (response.getState().equals(JobResponse.State.RUNJOB)) {
				job = response.getJob();
				if (count % 1000 == 0) {
					long endTime = System.currentTimeMillis();
					log.info("Count: " + count + " took " + (endTime - startTime) + "ms");
					startTime = endTime;
//					log.debug("Running job=" + job);
				}
				count++;
//				sleep(5);
			} else if (response.getState().equals(JobResponse.State.DIE)) {
				// appmaster asked us to go away
				die = true;
			} else if (response.getState().equals(JobResponse.State.STANDBY)) {
				// we're on standy, sleep a bit more
//				sleep(5);
			}

			// set job which may be null and
			// sleep during the run loop
			job = response.getJob();
//			sleep(1);
//		} while (!die && random.nextInt(10) > 0);
		} while (!die);
		log.info("Bye, I'm done baby...");
	}

	/**
	 * Utility method sleeping one second.
	 */
	private static void sleep(int seconds) {
		try {
			Thread.sleep(1000*seconds);
		} catch (InterruptedException e) {
		}
	}

}
