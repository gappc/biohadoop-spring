package at.ac.uibk.dps.biohadoopspring;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.springframework.yarn.YarnSystemConstants;
import org.springframework.yarn.am.AppmasterService;
import org.springframework.yarn.am.ContainerLauncherInterceptor;
import org.springframework.yarn.am.StaticEventingAppmaster;
import org.springframework.yarn.am.container.AbstractLauncher;

public class SimpleAppmaster extends StaticEventingAppmaster implements
		ContainerLauncherInterceptor {

	private static final Log log = LogFactory.getLog(SimpleAppmaster.class);

	/** List of job id's to run */
	private LinkedList<Long> pendingJobs = new LinkedList<Long>();

	/** Running jobs keeping info for job id and start time */
	private Hashtable<Long, Long> runningJobs = new Hashtable<Long, Long>();

	/** Job access lock */
	private final Object lock = new Object();

	@Override
	protected void onInit() throws Exception {
		log.error("####onInit");
		super.onInit();
		if (getLauncher() instanceof AbstractLauncher) {
			((AbstractLauncher) getLauncher()).addInterceptor(this);
		}

	}

	@Override
	public void submitApplication() {
		super.submitApplication();
		int jobCount = Integer.parseInt(getParameters().getProperty(
				"job-count", "10"));
		log.info("Creating " + jobCount + " jobs");
		for (int i = 1; i <= jobCount; i++) {
			pendingJobs.add((long) i);
		}
	}

	@Override
	public ContainerLaunchContext preLaunch(Container container,
			ContainerLaunchContext context) {
		log.error("####preLaunch");
		AppmasterService service = getAppmasterService();
		if (service != null) {
			int port = service.getPort();
			String address = service.getHost();
			Map<String, String> env = new HashMap<String, String>(
					context.getEnvironment());
			env.put(YarnSystemConstants.AMSERVICE_PORT, Integer.toString(port));
			env.put(YarnSystemConstants.AMSERVICE_HOST, address);
			context.setEnvironment(env);
			return context;
		} else {
			return context;
		}
	}

	@Override
	protected void onContainerCompleted(ContainerStatus status) {
		super.onContainerCompleted(status);
		log.error("####onContainerCompleted");
		if (hasJobs()) {
			getAllocator().allocateContainers(1);
		}
	}

	@Override
	protected boolean isComplete() {
		log.error("####isComplete");
		return !hasJobs();
	}

	/**
	 * Report job status.
	 * 
	 * @param job
	 *            the job id
	 * @param success
	 *            true if job execution was successful
	 */
	public void reportJobStatus(long job, boolean success) {
		synchronized (lock) {
			log.error("####reportJobStatus");
			if (success) {
				runningJobs.remove(job);
			} else {
				pendingJobs.add(job);
			}
		}
	}

	/**
	 * Gets the id of next job.
	 * 
	 * @return the next job id, null if no more jobs exist
	 */
	public Long getJob() {
		synchronized (lock) {
			log.error("####getJob");
			long now = System.currentTimeMillis();
			Iterator<Entry<Long, Long>> iterator = runningJobs.entrySet()
					.iterator();
			if (iterator.hasNext()) {
				Entry<Long, Long> entry = iterator.next();
				if ((entry.getValue() + 20000) < now) {
					pendingJobs.add(entry.getKey());
					iterator.remove();
				}
			}
			Long first = null;
			try {
				first = pendingJobs.removeFirst();
				runningJobs.put(first, System.currentTimeMillis());
			} catch (Exception e) {
			}
			log.info("Job counts: " + this + " returning " + first);
			return first;
		}
	}

	/**
	 * Checks if there is any pending jobs.
	 * 
	 * @return true, if pending jobs exist
	 */
	public boolean hasJobs() {
		synchronized (lock) {
			log.error("####hasJobs");
			return !(pendingJobs.isEmpty() && runningJobs.isEmpty());
		}
	}

	@Override
	public String toString() {
		log.error("####toString");
		return "pendingJobs=" + pendingJobs.size() + " runningJobs="
				+ runningJobs.size();
	}

}
