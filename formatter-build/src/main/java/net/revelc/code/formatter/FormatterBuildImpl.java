/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.revelc.code.formatter;

import dev.yracnet.formatter.FormatterBuild;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.WriterFactory;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.text.edits.MalformedTreeException;

import com.google.common.hash.Hashing;
import dev.yracnet.formatter.FormatterHelp;
import lombok.Getter;
import lombok.Setter;

import net.revelc.code.formatter.css.CssFormatter;
import net.revelc.code.formatter.html.HTMLFormatter;
import net.revelc.code.formatter.java.JavaFormatter;
import net.revelc.code.formatter.javascript.JavascriptFormatter;
import net.revelc.code.formatter.json.JsonFormatter;
import net.revelc.code.formatter.xml.XMLFormatter;
import dev.yracnet.formatter.FormatterLog;

/**
 * A Maven plugin mojo to format Java source code using the Eclipse code
 * formatter.
 *
 * Mojo parameters allow customizing formatting by specifying the config XML
 * file, line endings, compiler version, and source code locations. Reformatting
 * source files is avoided using an sha512 hash of the content, comparing to the
 * original hash to the hash after formatting and a cached hash.
 *
 * @author jecki
 * @author Matt Blanchette
 * @author marvin.froeder
 */
@Getter
@Setter
public class FormatterBuildImpl implements ConfigurationSource, FormatterBuild {

    private static final String FILE_S = " file(s)";

    private FormatterLog log;

    /**
     * Project's target directory as specified in the POM.
     */
    private File targetDirectory = new File("target");

    /**
     * Project's base directory.
     */
    private File basedir = new File("");

    /**
     * Location of the Java source files to format. Defaults to source main and
     * test directories if not set. Deprecated in version 0.3. Reintroduced in
     * 0.4.
     *
     * @since 0.4
     */
    private File[] directories;

    /**
     * List of fileset patterns for Java source locations to include in
     * formatting. Patterns are relative to the project source and test source
     * directories. When not specified, the default include is
     * <code>**&#47;*.java</code>
     *
     * @since 0.3
     */
    private String[] includes;

    /**
     * List of fileset patterns for Java source locations to exclude from
     * formatting. Patterns are relative to the project source and test source
     * directories. When not specified, there is no default exclude.
     *
     * @since 0.3
     */
    private String[] excludes;

    /**
     * Java compiler source version.
     */
    private String compilerSource = "1.8";

    /**
     * Java compiler compliance version.
     */
    private String compilerCompliance = "1.8";

    /**
     * Java compiler target version.
     */
    private String compilerTargetPlatform = "1.8";

    /**
     * The file encoding used to read and write source files. When not specified
     * and sourceEncoding also not set, default is platform file encoding.
     *
     * @since 0.3
     */
    private String encoding;

    /**
     * Sets the line-ending of files after formatting. Valid values are:
     * <ul>
     * <li><b>"AUTO"</b> - Use line endings of current system</li>
     * <li><b>"KEEP"</b> - Preserve line endings of files, default to AUTO if
     * ambiguous</li>
     * <li><b>"LF"</b> - Use Unix and Mac style line endings</li>
     * <li><b>"CRLF"</b> - Use DOS and Windows style line endings</li>
     * <li><b>"CR"</b> - Use early Mac style line endings</li>
     * </ul>
     *
     */
    private LineEnding lineEnding = LineEnding.AUTO;

    /**
     * File or classpath location of an Eclipse code formatter configuration xml
     * file to use in formatting.
     */
    private String configJavaFile = "formatter-config/eclipse/java.xml";

    /**
     * File or classpath location of an Eclipse code formatter configuration xml
     * file to use in formatting.
     */
    private String configJsFile = "formatter-config/eclipse/javascript.xml";

    /**
     * File or classpath location of a properties file to use in html
     * formatting.
     */
    private String configHtmlFile = "formatter-config/jsoup/html.properties";

    /**
     * File or classpath location of a properties file to use in xml formatting.
     */
    private String configXmlFile = "formatter-config/eclipse/xml.properties";

    /**
     * File or classpath location of a properties file to use in json
     * formatting.
     */
    private String configJsonFile = "formatter-config/jackson/json.properties";

    /**
     * File or classpath location of a properties file to use in css formatting.
     */
    private String configCssFile = "formatter-config/ph-css/css.properties";

    /**
     * Whether the java formatting is skipped.
     */
    private boolean skipJavaFormatting = false;

    /**
     * Whether the javascript formatting is skipped.
     */
    private boolean skipJsFormatting = false;

    /**
     * Whether the html formatting is skipped.
     */
    //@Parameter(defaultValue = "false", property = "formatter.html.skip")
    private boolean skipHtmlFormatting = false;

    /**
     * Whether the xml formatting is skipped.
     */
    private boolean skipXmlFormatting = false;

    /**
     * Whether the json formatting is skipped.
     */
    private boolean skipJsonFormatting = false;

    /**
     * Whether the css formatting is skipped.
     */
    private boolean skipCssFormatting = false;

    /**
     * Whether the formatting is skipped.
     *
     * @since 0.5
     */
    private boolean skipFormatting = false;

    /**
     * Use eclipse defaults when set to true for java and javascript.
     */
    private boolean useEclipseDefaults = false;

    private JavaFormatter javaFormatter = new JavaFormatter();

    private JavascriptFormatter jsFormatter = new JavascriptFormatter();

    private HTMLFormatter htmlFormatter = new HTMLFormatter();

    private XMLFormatter xmlFormatter = new XMLFormatter();

    private JsonFormatter jsonFormatter = new JsonFormatter();

    private CssFormatter cssFormatter = new CssFormatter();

    /**
     * Execute.
     *
     * @throws FormatterException the mojo execution exception
     * @see org.apache.maven.plugin.AbstractMojo#execute()
     */
    @Override
    public void execute() throws FormatterException {
        if (this.skipFormatting) {
            log.info("Formatting is skipped");
            return;
        }

        ResultCollector result = new ResultCollector();
        result.start();

        if (StringUtils.isEmpty(this.encoding)) {
            this.encoding = ReaderFactory.FILE_ENCODING;
            log.warn("File encoding has not been set, using platform encoding (" + this.encoding + ") to format source files, i.e. build is platform dependent!");
        } else {
            if (!Charset.isSupported(this.encoding)) {
                throw new FormatterException("Encoding '" + this.encoding + "' is not supported");
            }
            log.info("Using '" + this.encoding + "' encoding to format source files.");
        }

        List<File> files = new ArrayList<>();

        if (directories == null) {
            directories = new File[]{basedir};
        }

        for (File directory : this.directories) {
            if (directory.exists() && directory.isDirectory()) {
                files.addAll(FormatterHelp.addCollectionFiles(directory, includes, excludes));
            }
        }

        int numberOfFiles = files.size();

        log.info("Number of files to be formatted: " + numberOfFiles);

        if (numberOfFiles > 0) {
            createCodeFormatter();
            Properties hashCache = FormatterHelp.readFileHashCacheFile(targetDirectory);
            String basedirPath = FormatterHelp.getBasedirPath(basedir);
            for (File file : files) {
                if (file.exists()) {
                    if (file.canWrite()) {
                        formatFile(file, result, hashCache, basedirPath);
                    } else {
                        result.readOnlyCount();
                    }
                } else {
                    result.failCount();
                }
            }
            FormatterHelp.storeFileHashCache(hashCache, targetDirectory);
            result.stop();
            log.info("Successfully formatted:          " + result.getSuccessCount() + FILE_S);
            log.info("Fail to format:                  " + result.getFailCount() + FILE_S);
            log.info("Skipped:                         " + result.getSkippedCount() + FILE_S);
            log.info("Read only skipped:               " + result.getReadOnlyCount() + FILE_S);
            log.info("Approximate time taken:          " + result.getTimeClock() + "s");
        }
    }

    /**
     * Format file.
     *
     * @param file the file
     * @param rc the rc
     * @param hashCache the hash cache
     * @param basedirPath the basedir path
     */
    private void formatFile(File file, ResultCollector rc, Properties hashCache, String basedirPath)
            throws FormatterException {
        try {
            doFormatFile(file, rc, hashCache, basedirPath, false);
        } catch (IOException | MalformedTreeException | BadLocationException e) {
            rc.failCount();
            log.warn(e);
        }
    }

    /**
     * Format individual file.
     *
     * @param file the file
     * @param rc the rc
     * @param hashCache the hash cache
     * @param basedirPath the basedir path
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws BadLocationException the bad location exception
     */
    private void doFormatFile(File file, ResultCollector rc, Properties hashCache, String basedirPath, boolean dryRun)
            throws IOException, BadLocationException {
        log.debug("Processing file: " + file);
        String code = readFileAsString(file);
        String originalHash = sha512hash(code);

        String canonicalPath = file.getCanonicalPath();
        String path = canonicalPath.substring(basedirPath.length());
        String cachedHash = hashCache.getProperty(path);
        if (cachedHash != null && cachedHash.equals(originalHash)) {
            rc.skippedCount();
            log.debug("File is already formatted.");
            return;
        }

        Result result;
        if (file.getName().endsWith(".java") && javaFormatter.isInitialized()) {
            if (skipJavaFormatting) {
                log.info("Java formatting is skipped");
                result = Result.SKIPPED;
            } else {
                result = this.javaFormatter.formatFile(file, this.lineEnding, dryRun);
            }
        } else if (file.getName().endsWith(".js") && jsFormatter.isInitialized()) {
            if (skipJsFormatting) {
                log.info("Javascript formatting is skipped");
                result = Result.SKIPPED;
            } else {
                result = this.jsFormatter.formatFile(file, this.lineEnding, dryRun);
            }
        } else if (file.getName().endsWith(".html") && htmlFormatter.isInitialized()) {
            if (skipHtmlFormatting) {
                log.info("Html formatting is skipped");
                result = Result.SKIPPED;
            } else {
                result = this.htmlFormatter.formatFile(file, this.lineEnding, dryRun);
            }
        } else if (file.getName().endsWith(".xml") && xmlFormatter.isInitialized()) {
            if (skipXmlFormatting) {
                log.info("Xml formatting is skipped");
                result = Result.SKIPPED;
            } else {
                result = this.xmlFormatter.formatFile(file, this.lineEnding, dryRun);
            }
        } else if (file.getName().endsWith(".json") && jsonFormatter.isInitialized()) {
            if (skipJsonFormatting) {
                log.info("json formatting is skipped");
                result = Result.SKIPPED;
            } else {
                result = this.jsonFormatter.formatFile(file, this.lineEnding, dryRun);
            }
        } else if (file.getName().endsWith(".css") && cssFormatter.isInitialized()) {
            if (skipCssFormatting) {
                log.info("css formatting is skipped");
                result = Result.SKIPPED;
            } else {
                result = this.cssFormatter.formatFile(file, this.lineEnding, dryRun);
            }
        } else {
            result = Result.SKIPPED;
        }

        switch (result) {
            case SKIPPED:
                rc.skippedCount();
                return;
            case SUCCESS:
                rc.successCount();
                break;
            case FAIL:
                rc.failCount();
                return;
            default:
                break;
        }

        String formattedCode = readFileAsString(file);
        String formattedHash = sha512hash(formattedCode);
        hashCache.setProperty(path, formattedHash);

        if (originalHash.equals(formattedHash)) {
            rc.skippedCount();
            log.debug("Equal hash code. Not writing result to file.");
            return;
        }

        writeStringToFile(formattedCode, file);
    }

    /**
     * sha512hash.
     *
     * @param str the str
     * @return the string
     */
    private String sha512hash(String str) {
        return Hashing.sha512().hashBytes(str.getBytes(getEncodingAsCharset())).toString();
    }

    /**
     * Read the given file and return the content as a string.
     *
     * @param file the file
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private String readFileAsString(File file) throws java.io.IOException {
        StringBuilder fileData = new StringBuilder(1000);
        try ( BufferedReader reader = new BufferedReader(ReaderFactory.newReader(file, this.encoding))) {
            char[] buf = new char[1024];
            int numRead = 0;
            while ((numRead = reader.read(buf)) != -1) {
                String readData = String.valueOf(buf, 0, numRead);
                fileData.append(readData);
                buf = new char[1024];
            }
        }
        return fileData.toString();
    }

    /**
     * Write the given string to a file.
     *
     * @param str the str
     * @param file the file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void writeStringToFile(String str, File file) throws IOException {
        if (!file.exists() && file.isDirectory()) {
            return;
        }

        try ( BufferedWriter bw = new BufferedWriter(WriterFactory.newWriter(file, this.encoding))) {
            bw.write(str);
        }
    }

    /**
     * Create a {@link CodeFormatter} instance to be used by this mojo.
     *
     * @throws FormatterException the mojo execution exception
     */
    private void createCodeFormatter() throws FormatterException {
        Map<String, String> javaFormattingOptions = getFormattingOptions(this.configJavaFile);
        if (javaFormattingOptions != null) {
            this.javaFormatter.init(javaFormattingOptions, this);
        }
        Map<String, String> jsFormattingOptions = getFormattingOptions(this.configJsFile);
        if (jsFormattingOptions != null) {
            this.jsFormatter.init(jsFormattingOptions, this);
        }
        if (configHtmlFile != null) {
            this.htmlFormatter.init(FormatterHelp.getOptionsFromPropertiesFile(configHtmlFile, basedir), this);
        }
        if (configXmlFile != null) {
            Map<String, String> xmlFormattingOptions = FormatterHelp.getOptionsFromPropertiesFile(configXmlFile, basedir);
            xmlFormattingOptions.put("lineending", this.lineEnding.getChars());
            this.xmlFormatter.init(xmlFormattingOptions, this);
        }
        if (configJsonFile != null) {
            Map<String, String> jsonFormattingOptions = FormatterHelp.getOptionsFromPropertiesFile(configJsonFile, basedir);
            jsonFormattingOptions.put("lineending", this.lineEnding.getChars());
            this.jsonFormatter.init(jsonFormattingOptions, this);
        }
        if (configCssFile != null) {
            this.cssFormatter.init(FormatterHelp.getOptionsFromPropertiesFile(configCssFile, basedir), this);
        }
        // stop the process if not config files where found
        if (javaFormattingOptions == null && jsFormattingOptions == null && configHtmlFile == null
                && configXmlFile == null && configCssFile == null) {
            throw new FormatterException(
                    "You must provide a Java, Javascript, HTML, XML, JSON, or CSS configuration file.");
        }
    }

    /**
     * Return the options to be passed when creating {@link CodeFormatter}
     * instance.
     *
     * @return the formatting options or null if not config file found
     * @throws FormatterException the mojo execution exception
     */
    private Map<String, String> getFormattingOptions(String newConfigFile) throws FormatterException {
        if (this.useEclipseDefaults) {
            log.info("Using Ecipse Defaults");
            Map<String, String> options = new HashMap<>();
            options.put(JavaCore.COMPILER_SOURCE, this.compilerSource);
            options.put(JavaCore.COMPILER_COMPLIANCE, this.compilerCompliance);
            options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, this.compilerTargetPlatform);
            return options;
        }
        return FormatterHelp.getOptionsFromConfigFile(newConfigFile, basedir);
    }

    @Override
    public File getTargetDirectory() {
        return this.targetDirectory;
    }

    @Override
    public Charset getEncodingAsCharset() {
        return Charset.forName(this.encoding);
    }

    @Override
    public FormatterLog getLog() {
        return log;
    }

    @Override
    public void setBasedir(File basedir) {
        this.basedir = basedir;
        if (directories == null) {
            directories = new File[]{basedir};
        }
    }
}
