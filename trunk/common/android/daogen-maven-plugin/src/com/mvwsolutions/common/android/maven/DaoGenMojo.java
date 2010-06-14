package com.mvwsolutions.common.android.maven;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import com.mvwsolutions.android.daogen.FileSourceInterface;
import com.mvwsolutions.android.daogen.SourceFileGenerator;
import com.mvwsolutions.android.daogen.SourceInterface.SIException;
import com.mvwsolutions.classwriter.ClassWriter;
import com.mvwsolutions.classwriter.CodeCheckException;
import com.mvwsolutions.common.Utils;

/**
 * 
 * @author SMineyev
 * @goal run
 */
public class DaoGenMojo extends AbstractMojo {

	/** @parameter default-value="${project}" */
	private MavenProject mavenProject;

//	/** @parameter default-value="gen" */
//	private String genOutput;

	public void execute() throws MojoExecutionException {

		getLog().info(
				"Project output: "
						+ mavenProject.getBuild().getOutputDirectory());
		
		final File genOutputDir = new File(mavenProject.getBuild().getSourceDirectory());
		
		getLog().info("Generated files will be placed into: " + genOutputDir.getAbsolutePath());

		Utils.traverseFileTree(new File(mavenProject.getBuild()
				.getOutputDirectory()), new Utils.FileVisitor() {

			@Override
			public void visit(File file) {
				if (!file.isDirectory() && file.getName().endsWith(".class")) {
					try {
						processClassFile(file, genOutputDir);
					} catch (Exception e) {
						getLog().error(
								"Unable to process class file: "
										+ file.getAbsolutePath(), e);
					}
				}

			}

		});

	}

	private void processClassFile(File classFile, File outputDir)
			throws FileNotFoundException, IOException, CodeCheckException,
			SIException {
getLog().info(outputDir.getAbsolutePath() +" , " + classFile.getAbsolutePath());		
		ClassWriter cw = new ClassWriter();
		cw.readClass(new FileInputStream(classFile));
		boolean generated = new SourceFileGenerator(new FileSourceInterface(
				outputDir)).generate(cw);
		if (generated) {
			getLog().info(
					"DAO class (" + outputDir.getAbsolutePath() + ") was generated for "
							+ classFile.getAbsolutePath());
		}
	}
}
