package com.mvwsolutions;

import java.io.File;
import java.io.FileInputStream;

import org.junit.Test;

import com.mvwsolutions.android.daogen.FileSourceInterface;
import com.mvwsolutions.android.daogen.SourceFileGenerator;
import com.mvwsolutions.classwriter.ClassWriter;

public class SourceFileGeneratorTest {

	@Test
	public void test()  {
		try {
		File base = new File("/tmp");
		ClassWriter cw = new ClassWriter();
		cw
				.readClass(new FileInputStream(
						"./target/test-classes/com/mvwsolutions/TestBean.class"));
		new SourceFileGenerator(new FileSourceInterface(base)).generate(cw);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
