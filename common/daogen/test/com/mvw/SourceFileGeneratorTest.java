package com.mvw;

import java.io.File;
import java.io.FileInputStream;

import org.junit.Test;

import com.antlersoft.android.dbgen.FileSourceInterface;
import com.antlersoft.android.dbgen.SourceFileGenerator;
import com.antlersoft.classwriter.ClassWriter;

public class SourceFileGeneratorTest {

	@Test
	public void test() throws Exception {
		File base = new File("C:/tmp/3/MultipleChoiceDialogExample/src");
		ClassWriter cw = new ClassWriter();
		cw
				.readClass(new FileInputStream(
						"C:/tmp/3/MultipleChoiceDialogExample/bin/android/androidVNC/IConnectionBean.class"));
		new SourceFileGenerator(new FileSourceInterface(base)).generate(cw);
	}
}
