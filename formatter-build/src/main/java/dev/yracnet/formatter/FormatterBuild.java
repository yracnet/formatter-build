/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.yracnet.formatter;

import java.io.File;
import java.nio.charset.Charset;
import net.revelc.code.formatter.FormatterBuildImpl;
import net.revelc.code.formatter.LineEnding;

/**
 *
 * @author wyujra
 */
public interface FormatterBuild {

    /**
     * Create the default instance for FormatterBuild
     *
     * @return
     */
    public static FormatterBuild create() {
        FormatterBuild build = new FormatterBuildImpl();
        build.setLog(new FormatterLogImpl());
        return build;
    }

    /**
     * Execute the format code
     *
     * @throws FormatterException
     */
    public void execute() throws FormatterException;

    public FormatterLog getLog();

    /**
     * Set the Logger Implement
     *
     * @param logger
     */
    public void setLog(FormatterLog logger);

    public File getTargetDirectory();

    /**
     * Set the Target Directory where generated the resume and cache properties
     *
     * @param targetDirectory
     */
    public void setTargetDirectory(File targetDirectory);

    public File getBasedir();

    /**
     * Set the base directory
     *
     * @param basedir
     */
    public void setBasedir(File basedir);

    public File[] getDirectories();

    /**
     * Set sub directory of {basedir} for format
     *
     * @param directories
     */
    public void setDirectories(File... directories);

    public String[] getIncludes();

    /**
     * Set the criterial include format files
     *
     * @param includes
     */
    public void setIncludes(String... includes);

    public String[] getExcludes();

    /**
     * Set the criterial exclude format files
     *
     * @param excludes
     */
    public void setExcludes(String... excludes);

    public String getCompilerSource();

    /**
     * Set the Version Java Source
     *
     * @param compilerSource
     */
    public void setCompilerSource(String compilerSource);

    public String getCompilerCompliance();

    /**
     * Set the Version Java Compile
     *
     * @param compilerCompliance
     */
    public void setCompilerCompliance(String compilerCompliance);

    public String getCompilerTargetPlatform();

    /**
     * Set the Version Target Platform
     *
     * @param compilerTargetPlatform
     */
    public void setCompilerTargetPlatform(String compilerTargetPlatform);

    public Charset getEncodingAsCharset();

    public String getEncoding();

    /**
     * Set Encoding Files
     *
     * @param encoding
     */
    public void setEncoding(String encoding);

    public LineEnding getLineEnding();

    /**
     * Set the Line End character for WIN, LNX o MAC
     *
     * @param lineEnding
     */
    public void setLineEnding(LineEnding lineEnding);

    public FormatterConfig getJavaFormatterConfig();

    public FormatterConfig getCssFormatterConfig();

    public FormatterConfig getJsFormatterConfig();

    public FormatterConfig getJsonFormatterConfig();

    public FormatterConfig getHtmlFormatterConfig();

    public FormatterConfig getXmlFormatterConfig();

    public boolean isUseEclipseDefaults();

    /**
     * Set TRUE for use the default format config of Eclipse
     *
     * @param useEclipseDefaults
     */
    public void setUseEclipseDefaults(boolean useEclipseDefaults);

}
