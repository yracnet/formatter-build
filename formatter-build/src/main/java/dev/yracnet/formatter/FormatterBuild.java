/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.yracnet.formatter;

import java.io.File;
import java.nio.charset.Charset;
import net.revelc.code.formatter.FormatterBuildImpl;
import net.revelc.code.formatter.FormatterException;
import net.revelc.code.formatter.LineEnding;

/**
 *
 * @author wyujra
 */
public interface FormatterBuild {
/**
 * Create the default instance for FormatterBuild
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
     * Set the Target Directory where generated the resumen and cache properties
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

    public String getConfigJavaFile();

    /**
     * Set the File XML format eclipse Java Files
     *
     * @param configJavaFile
     */
    public void setConfigJavaFile(String configJavaFile);

    public String getConfigJsFile();

    /**
     * Set the File XML format eclipse JS Files
     *
     * @param configJsFile
     */
    public void setConfigJsFile(String configJsFile);

    public String getConfigHtmlFile();

    /**
     * Set the File Properties format eclipse HTML Files
     *
     * @param configHtmlFile
     */
    public void setConfigHtmlFile(String configHtmlFile);

    public String getConfigXmlFile();

    /**
     * Set the File Properties format eclipse XML Files
     *
     * @param configXmlFile
     */
    public void setConfigXmlFile(String configXmlFile);

    public String getConfigJsonFile();

    /**
     * Set the File Properties format eclipse JSON Files
     *
     * @param configJsonFile
     */
    public void setConfigJsonFile(String configJsonFile);

    public String getConfigCssFile();

    /**
     * Set the File Properties format eclipse CSS Files
     *
     * @param configCssFile
     */
    public void setConfigCssFile(String configCssFile);

    public boolean isSkipJavaFormatting();

    /**
     * Set Skip format Java files
     *
     * @param skipJavaFormatting
     */
    public void setSkipJavaFormatting(boolean skipJavaFormatting);

    public boolean isSkipJsFormatting();

    /**
     * Set Skip format JS files
     *
     * @param skipJsFormatting
     */
    public void setSkipJsFormatting(boolean skipJsFormatting);

    public boolean isSkipHtmlFormatting();

    /**
     * Set Skip format HTML files
     *
     * @param skipHtmlFormatting
     */
    public void setSkipHtmlFormatting(boolean skipHtmlFormatting);

    public boolean isSkipXmlFormatting();

    /**
     * Set Skip format XML files
     *
     * @param skipXmlFormatting
     */
    public void setSkipXmlFormatting(boolean skipXmlFormatting);

    public boolean isSkipJsonFormatting();

    /**
     * Set Skip format JSON files
     *
     * @param skipJsonFormatting
     */
    public void setSkipJsonFormatting(boolean skipJsonFormatting);

    public boolean isSkipCssFormatting();

    /**
     * Set Skip format CSS files
     *
     * @param skipCssFormatting
     */
    public void setSkipCssFormatting(boolean skipCssFormatting);

    public boolean isSkipFormatting();

    /**
     * Set Skip format all files
     *
     * @param skipFormatting
     */
    public void setSkipFormatting(boolean skipFormatting);

    public boolean isUseEclipseDefaults();

    /**
     * Set TRUE for use the default format config of Eclipse
     *
     * @param useEclipseDefaults
     */
    public void setUseEclipseDefaults(boolean useEclipseDefaults);

}
