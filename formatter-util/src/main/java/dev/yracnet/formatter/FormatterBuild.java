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

    public static FormatterBuild create() {
        FormatterBuild  build = new FormatterBuildImpl();
        build.setLog(new FormatterLogImpl());
        return build;
    }

    public void execute() throws FormatterException;

    public FormatterLog getLog();

    public void setLog(FormatterLog logger);

    public File getTargetDirectory();

    public void setTargetDirectory(File targetDirectory);

    public File getBasedir();

    public void setBasedir(File basedir);

    public File[] getDirectories();

    public void setDirectories(File... directories);

    public String[] getIncludes();

    public void setIncludes(String... includes);

    public String[] getExcludes();

    public void setExcludes(String... excludes);

    public String getCompilerSource();

    public void setCompilerSource(String compilerSource);

    public String getCompilerCompliance();

    public void setCompilerCompliance(String compilerCompliance);

    public String getCompilerTargetPlatform();

    public void setCompilerTargetPlatform(String compilerTargetPlatform);

    public Charset getEncodingAsCharset();
    
    public String getEncoding();

    public void setEncoding(String encoding);

    public LineEnding getLineEnding();

    public void setLineEnding(LineEnding lineEnding);

    public String getConfigJavaFile();

    public void setConfigJavaFile(String configJavaFile);

    public String getConfigJsFile();

    public void setConfigJsFile(String configJsFile);

    public String getConfigHtmlFile();

    public void setConfigHtmlFile(String configHtmlFile);

    public String getConfigXmlFile();

    public void setConfigXmlFile(String configXmlFile);

    public String getConfigJsonFile();

    public void setConfigJsonFile(String configJsonFile);

    public String getConfigCssFile();

    public void setConfigCssFile(String configCssFile);

    public boolean isSkipJavaFormatting();

    public void setSkipJavaFormatting(boolean skipJavaFormatting);

    public boolean isSkipJsFormatting();

    public void setSkipJsFormatting(boolean skipJsFormatting);

    public boolean isSkipHtmlFormatting();

    public void setSkipHtmlFormatting(boolean skipHtmlFormatting);

    public boolean isSkipXmlFormatting();

    public void setSkipXmlFormatting(boolean skipXmlFormatting);

    public boolean isSkipJsonFormatting();

    public void setSkipJsonFormatting(boolean skipJsonFormatting);

    public boolean isSkipCssFormatting();

    public void setSkipCssFormatting(boolean skipCssFormatting);

    public boolean isSkipFormatting();

    public void setSkipFormatting(boolean skipFormatting);

    public boolean isUseEclipseDefaults();

    public void setUseEclipseDefaults(boolean useEclipseDefaults);

}
