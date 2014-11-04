package de.bund.bfr.knime.paroa.strat;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Map;

import org.knime.core.node.ExecutionContext;
import org.knime.core.node.NodeLogger;

import de.bund.bfr.knime.paroa.strat.StratosphereNodeModel.EXEC;
import eu.stratosphere.api.common.accumulators.AccumulatorHelper;
import eu.stratosphere.client.RemoteExecutor;
import eu.stratosphere.client.program.Client;
import eu.stratosphere.client.program.PackagedProgram;
import eu.stratosphere.configuration.ConfigConstants;
import eu.stratosphere.configuration.Configuration;
import eu.stratosphere.configuration.GlobalConfiguration;
import eu.stratosphere.nephele.client.JobExecutionResult;

public class StratosphereConnection {

    // the logger instance
    private static final NodeLogger logger = NodeLogger
	    .getLogger(StratosphereNodeModel.class);
    private String m_path;
    private String mJarPath;
    private boolean local;
    private String remoteAddress;
    private static final String ENV_CONFIG_DIRECTORY = "/";
    private static final String CONFIG_DIRECTORY_FALLBACK_1 = "conf";
    private String CONFIG_DIRECTORY_FALLBACK_2;
    private static final String PREFIX_FILE_FILE = "file:";
    private static final String PREFIX_FILE_HDFS = "hdfs:";
    private static final String REMOTE_USER_PATH = "hdfs://tenemhead2/user/markus.freitag/";
    private static final String REMOTE_DATA_PATH = "data/";
    private static final String REMOTE_RESULTS_PATH = "results/";

    private ExecutionContext exec;

    public StratosphereConnection(String stratospherePath, String configurationPath, String jarPath, Boolean local,
	    String remoteAddress,
	    ExecutionContext knimeContext) {
	setStratospherePath(stratospherePath);
	setStratosphereConfigurationPath(configurationPath);
	setRemoteAdress(remoteAddress);
	setJarPath(jarPath);
	setLocal(local);
	this.exec = knimeContext;
    }

    public void runParoa(EXEC mode, String paroa_input_sales, int numProducts,
	    String paroa_input_outbreaks, int numCases,
	    String paroa_output, int numScenarios, String scoringMethod,
	    String paroa_input_coordinates) {

	//		final String process_location = getPath() + "\\bin\\stratosphere.bat";
	final String process_location = getStratospherePath() + "bin/stratosphere";
	final String process_cmd = "run";
	final String arg_jar_arg = "-j";
	final String arg_jar = getJarPath();
	final String arg_arg = "-a";
	final String arg_input_outbreaks = addPrefix(paroa_input_outbreaks);
	final String arg_input_sales = addPrefix(paroa_input_sales);
	final String arg_input_coordinates = paroa_input_coordinates.equals(StratosphereNodeModel.DEFAULT_EMPTYSTRING) ?
		StratosphereNodeModel.DEFAULT_EMPTYSTRING : addPrefix(paroa_input_coordinates);
	final String arg_numProducts = String.valueOf(numProducts);
	final String arg_numCases = String.valueOf(numCases);
	final String arg_numScenarios = String.valueOf(numScenarios);
	final String arg_output = addPrefix(paroa_output);
	final String arg_method = scoringMethod;
	final String arg_paral = "8";
	final String arg_debug = "-w";



	ArrayList<String> arguments = new ArrayList<String>();

	if (mode.equals(EXEC.LOCAL)) {
	    arguments.add(process_location);
	    arguments.add(process_cmd);
	    arguments.add(arg_jar_arg);
	    arguments.add(arg_jar);
	    arguments.add(arg_arg);
	    arguments.add(arg_input_sales);
	    arguments.add(arg_numProducts);
	    arguments.add(arg_input_outbreaks);
	    arguments.add(arg_numCases);
	    arguments.add(arg_output);
	    arguments.add(arg_numScenarios);
	    arguments.add(arg_method);
	    arguments.add(arg_paral);
	    if (arg_method == StratosphereNodeModel.METHODS.SYR.name())
		arguments.add(arg_input_coordinates);
	    arguments.add(arg_debug);
	    runLocally(arguments);
	}
	else if (mode.equals(EXEC.REMOTE)) {
	    arguments.add(convertDataPath(arg_input_sales));
	    arguments.add(arg_numProducts);
	    arguments.add(convertDataPath(arg_input_outbreaks));
	    arguments.add(arg_numCases);
	    arguments.add(REMOTE_USER_PATH + REMOTE_RESULTS_PATH);
	    arguments.add(arg_numScenarios);
	    arguments.add(arg_method);
	    arguments.add(arg_paral);
	    
	    // change this if GS changes
            // arguments.add(REMOTE_USER_PATH + REMOTE_DATA_PATH + "expData_refac_201043_IRI_GS.tsv");
            // arguments.add("1384");
	    
	    if (arg_method.equals(StratosphereNodeModel.METHODS.SYR.name()))
		arguments.add(arg_input_coordinates);
	    else if (arg_method.equals(StratosphereNodeModel.METHODS.LBM.name()))
		arguments.remove(arg_method);
	    cliFrontend(arguments);
	}
	else
	    logger.error("unknown execution mode (should be " + EXEC.LOCAL + " or " + EXEC.REMOTE + ")");

    }

    private String convertDataPath(String arg_input_sales) {
	String fileName = arg_input_sales.split("/")[arg_input_sales.split("/").length - 1];
	String newPath = REMOTE_USER_PATH + REMOTE_DATA_PATH + fileName;
	return newPath;
    }

    private String addPrefix(String paroa_input_outbreaks) {
	String extendedString;
	if (isLocal())
	    extendedString = PREFIX_FILE_FILE + paroa_input_outbreaks;
	else
	    extendedString = PREFIX_FILE_HDFS + paroa_input_outbreaks;
	return extendedString;

    }

    private void runLocally(ArrayList<String> arguments) {
	logger.info("Arguments:");
	for (String argument : arguments) {
	    logger.info("\t" + argument);
	}
	
	ProcessBuilder process_b = new ProcessBuilder(arguments);
	process_b.inheritIO();
	Process p;

	try {
	    logger.info(StratosphereNodeModel.MSG_STRATO_CALL
		    + process_b.command());
	    p = process_b.start();

	    exec.setProgress(0.05, "Running Stratosphere...");
	    logger.info(StratosphereNodeModel.MSG_RUN_STRATO);

	    final int exit_status = p.waitFor();
	    if (exit_status == 0) {
		exec.setProgress(0.95, "Stratosphere finished. Output results...");
		logger.info(StratosphereNodeModel.MSG_STRATO_FIN);
	    }
	    else {
		logger.error(StratosphereNodeModel.MSG_STRATO_ERROR);
		exec.getProgressMonitor().setExecuteCanceled();
	    }
	} catch (InterruptedException | IOException e) {
	    e.printStackTrace();
	}
    }

    @SuppressWarnings("unused")
    private int cliFrontend(ArrayList<String> programArgs) {
	logger.info("Arguments:");
	for (String argument : programArgs) {
	    logger.info("\t" + argument);
	}
	String[] args = new String[programArgs.size()];
	args = programArgs.toArray(args);
	
	File jarFile = null;
	String entryPointClass = null;
//	programArgs = "hdfs://tenemhead2/user/markus.freitag/data/rewe_experiment_data.csv 580 hdfs://tenemhead2/user/markus.freitag/data/outbreak_p20_c20.csv 20 hdfs://tenemhead2/user/markus.freitag/results/ 1 -1"
//		.split(" ");
	String address = null;
	boolean wait = false;

	address = getRemoteAdress();
	jarFile = new File(getJarPath());
	//		logger.info(getClass().getResource("paroa-0.91.jar").toString());
	//		jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + getClass().getResource("paroa-0.91.jar").getFile());
	// Check if JAR file exists
	/*if (!jarFile.exists()) {
		System.out.println("Error: Jar file does not exist.");
		logger.error("Error: Jar file does not exist.");
	} else if (!jarFile.isFile()) {
		System.out.println("Error: Jar file is not a file.");
		logger.error("Error: Jar file is not a file.");
	}*/

	boolean yarnMode = false;

	// get wait flag
	wait = true;

	// Try to get load plan
	PackagedProgram program;
	try {
	    if (entryPointClass == null) {
		program = new PackagedProgram(jarFile, args);
	    } else {
		program = new PackagedProgram(jarFile, entryPointClass,
			args);
	    }
	} catch (Exception e) {
	    return handleError(e);
	}

	Configuration configuration = getConfiguration();
	Client client = null;

	InetSocketAddress socket = null;
	try {
	    if (address != null && !address.isEmpty()) {
		socket = RemoteExecutor.getInetFromHostport(address);
		client = new Client(socket, configuration);

	    } else {
		client = new Client(configuration);
	    }
	} catch (Exception e) {
	    logger.error("Connection could not be established.");
	    exec.getProgressMonitor().setExecuteCanceled();
	    return handleError(e);
	}
	client.setPrintStatusDuringExecution(true);

	JobExecutionResult execResult;
	try {
	    exec.setProgress(0.05d, "Working...");
	    execResult = client.run(program.getPlanWithJars(), wait);
	} catch (Exception e) {
	    return handleError(e);
	} finally {
	    program.deleteExtractedLibraries();
	}

	if (wait && execResult != null) {
	    System.out.println("Job Runtime: " + execResult.getNetRuntime());
	    Map<String, Object> accumulatorsResult = execResult
		    .getAllAccumulatorResults();
	    if (accumulatorsResult.size() > 0) {
		System.out.println("Accumulator Results: ");
		System.out.println(AccumulatorHelper
			.getResultsFormated(accumulatorsResult));
	    }
	} else {
	    if (!yarnMode) {
		if (address != null && !address.isEmpty()) {
		    System.out
			    .println("Job successfully submitted. Use -w (or --wait) option to track the progress here.\n"
				    + "JobManager web interface: http://"
				    + socket.getHostName()
				    + ":"
				    + configuration
					    .getInteger(
						    ConfigConstants.JOB_MANAGER_WEB_PORT_KEY,
						    ConfigConstants.DEFAULT_JOB_MANAGER_WEB_FRONTEND_PORT));
		} else {
		    System.out
			    .println("Job successfully submitted. Use -w (or --wait) option to track the progress here.\n"
				    + "JobManager web interface: http://"
				    + configuration
					    .getString(
						    ConfigConstants.JOB_MANAGER_IPC_ADDRESS_KEY,
						    null)
				    + ":"
				    + configuration
					    .getInteger(
						    ConfigConstants.JOB_MANAGER_WEB_PORT_KEY,
						    ConfigConstants.DEFAULT_JOB_MANAGER_WEB_FRONTEND_PORT));
		}
	    } else {
		System.out
			.println("Job successfully submitted. Use -w (or --wait) option to track the progress here.\n");
	    }
	}
	return 0;
    }

    /**
     * Displays exceptions.
     * 
     * @param e
     *            the exception to display.
     */
    private int handleError(Throwable t) {
	System.out.println("Error: " + t.getMessage());
	logger.error("Error: " + t.getMessage());
	t.printStackTrace();
	return 1;
    }

    private String getConfigurationDirectory() {
	String location = null;
	if (System.getenv(ENV_CONFIG_DIRECTORY) != null) {
	    location = System.getenv(ENV_CONFIG_DIRECTORY);
	} else if (new File(CONFIG_DIRECTORY_FALLBACK_1).exists()) {
	    location = CONFIG_DIRECTORY_FALLBACK_1;
	} else if (new File(CONFIG_DIRECTORY_FALLBACK_2).exists()) {
	    location = CONFIG_DIRECTORY_FALLBACK_2;
	} else {
	    throw new RuntimeException(
		    "The configuration directory was not found. Please configure the '"
			    + ENV_CONFIG_DIRECTORY
			    + "' environment variable properly.");
	}
	return location;
    }

    /**
     * Reads configuration settings. The default path can be overridden by
     * setting the ENV variable "STRATOSPHERE_CONF_DIR".
     * 
     * @return Stratosphere's global configuration
     */
    private Configuration getConfiguration() {
	final String location = getConfigurationDirectory();
	GlobalConfiguration.loadConfiguration(location);
	Configuration config = GlobalConfiguration.getConfiguration();

	return config;
    }

    private void setStratosphereConfigurationPath(String configurationPath) {
	CONFIG_DIRECTORY_FALLBACK_2 = configurationPath;
    }

    private String getStratospherePath() {
	return m_path;
    }

    private void setStratospherePath(String path) {
	logger.info("Setting stratosphere path: " + path);
	this.m_path = path;
    }

    public String getJarPath() {
	return mJarPath;
    }

    public void setJarPath(String jarPath) {
	logger.info("Setting jar path: " + jarPath);
	this.mJarPath = jarPath;
    }

    private boolean isLocal() {
	return local;
    }

    private void setLocal(boolean local) {
	logger.info("Setting execution environment to local: " + local);
	this.local = local;
    }

    private String getRemoteAdress() {
	return this.remoteAddress;
    }

    private void setRemoteAdress(String address) {
	logger.info("Setting remote address: " + address);
	this.remoteAddress = address;
    }
}
