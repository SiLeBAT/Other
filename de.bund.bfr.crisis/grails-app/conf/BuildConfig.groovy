import grails.util.Environment;

// PROXY: When using a proxy, please use grails add-proxy and set-proxy commands
// In case of failure, see proxy comment in grails.project.dependency.resolution

grails.servlet.version = "3.0" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.work.dir = "target/work"
grails.project.target.level = 1.7
grails.project.source.level = 1.7
grails.project.war.file = "target/${appName}.war" // -${appVersion}

grails.project.fork = [
	// configure settings for compilation JVM, note that if you alter the Groovy version forked compilation is required
	//  compile: [maxMemory: 256, minMemory: 64, debug: false, maxPerm: 256, daemon:true],

	// configure settings for the test-app JVM, uses the daemon by default
	test: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, daemon:true],
	// configure settings for the run-app JVM
	run: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
	// configure settings for the run-war JVM
	war: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
	// configure settings for the Console UI JVM
	console: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256]
]

grails.project.dependency.resolver = "maven" // or ivy
grails.project.dependency.resolution = {
	// PROXY: Hard-coding proxy, which is only necessary while setting up the project initially
	// It's best to setup the project externally
	// System.setProperty("http.proxyHost", 'webproxy.bfr.bund.de');
	// System.setProperty("http.proxyPort", '3128');

	// inherit Grails' default dependencies
	inherits("global") {
		// specify dependency exclusions here; for example, uncomment this to disable ehcache:
		// excludes 'ehcache'
	}
	log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
	checksums true // Whether to verify checksums on resolve
	legacyResolve false // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility

	repositories {
		inherits true // Whether to inherit repository definitions from plugins

		mavenCentral()
		grailsPlugins()
		grailsHome()
		mavenLocal()
		grailsCentral()
		// uncomment these (or add new ones) to enable remote dependency resolution from public Maven repositories
		//mavenRepo "http://repository.codehaus.org"
		//mavenRepo "http://download.java.net/maven/2/"
		//mavenRepo "http://repository.jboss.com/maven2/"
		
		//println "${new File('localRepo').toURI()}"
		//mavenRepo "${new File('localRepo').toURI()}"
		mavenRepo "http://repository.jboss.org/nexus/content/groups/public/"
	}

	dependencies {
		// specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes e.g.
		// runtime 'mysql:mysql-connector-java:5.1.29'
		// runtime 'org.postgresql:postgresql:9.3-1101-jdbc41'
		test "org.grails:grails-datastore-test-support:1.0.2-grails-2.4"

		compile 'com.google.gwt:gwt-codeserver:2.7.0'

		// these dependencies should actually be automatically be resolved when creating rpc services
		build 'com.google.gwt:gwt-servlet:2.7.0'

		compile 'org.hsqldb:hsqldb:2.3.2'
		compile 'com.h2database:h2:1.4.186'
		
//		compile 'org.apache.httpcomponents:httpclient:4.4', {
//			force = true
//		}
	}

	plugins {		
		// plugins for the build system only
		build ":tomcat:7.0.55"

		// plugins for the compile step
		compile ":scaffolding:2.1.2"
		compile ':cache:1.1.8'
		compile ":asset-pipeline:2.1.1"

		// plugins needed at runtime but not for compilation
		runtime ":hibernate4:4.3.6.1" // or ":hibernate:3.6.10.18"
		runtime ":database-migration:1.4.0"
		runtime ":jquery:1.11.1"

		//compile ":excel-import:1.0.0"
		compile ":resources:1.2.14"
		
//		compile ":smartgwt:0.2"
		// we have a patched version in our lib folder, which is automatically included
		compile ":extended-dependency-manager:0.5.5"
	
		compile ":gwt:1.0", { transitive=false }
		
		// Uncomment these to enable additional asset-pipeline capabilities
		//compile ":sass-asset-pipeline:1.9.0"
		//compile ":less-asset-pipeline:1.10.0"
		//compile ":coffee-asset-pipeline:1.8.0"
		//compile ":handlebars-asset-pipeline:1.3.0.3"
	}
}

gwt {
	version = "2.7.0"
	run.args = {
		jvmarg value: "-Xmx1024m"
	}
	//	use.provided.deps = true
	dependencies = [
		'org.gwtopenmaps.openlayers:gwt-openlayers-client:1.1-SNAPSHOT',
		'com.google.gwt:gwt-codeserver:2.7.0',
		'com.google.gwt:gwt-dev:2.7.0',
		'org.ow2.asm:asm:5.0.3',
		'org.ow2.asm:asm-commons:5.0.3',
		'org.ow2.asm:asm-util:5.0.3'
	]
}

// Remove jars before the war is bundled
grails.war.resources = { stagingDir ->
  delete(file:"${stagingDir}/WEB-INF/lib/gwt-dev-2.7.0.jar")
  delete(file:"${stagingDir}/WEB-INF/lib/validation-api-1.0.0.GA.jar")
  delete(file:"${stagingDir}/WEB-INF/lib/validation-api-1.0.0.GA-sources.jar")
}