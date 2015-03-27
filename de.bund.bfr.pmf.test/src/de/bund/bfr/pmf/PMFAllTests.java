package de.bund.bfr.pmf;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ PMFReaderTest.class, PMFWriterReaderTest.class,
		PMFWriterTest.class })
public class PMFAllTests {

}
