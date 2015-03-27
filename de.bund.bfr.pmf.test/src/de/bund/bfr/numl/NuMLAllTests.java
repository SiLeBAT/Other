package de.bund.bfr.numl;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ DescriptionTest.class, NuMLReaderTest.class,
		NuMLWriterReaderTest.class, NuMLWriterTest.class })
public class NuMLAllTests {

}
