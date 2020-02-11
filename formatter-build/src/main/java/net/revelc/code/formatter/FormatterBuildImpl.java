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

import dev.yracnet.formatter.FormatterException;
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

    private JavascriptFormatter jsFormatter = new JavascriptFormatter( );

    private HTMLFormatter htmlFormatter = new HTMLFormatter();

    private XMLFormatter xmlFormatter = new XMLFormatter();

    private JsonFormatter jsonFormatter = new JsonFormatter();

    private CssFormatter cssFormatter = new CssFormatter();
    
    public FormatterBuildImpl(){
        javaFormatter.setFileConfig("formatter-config/eclipse/java.xml");
        jsFormatter.setFileConfig("formatter-config/eclipse/javascript.xml");
        htmlFormatter.setFileConfig("formatter-config/jsoup/html.properties");
        xmlFormatter.setFileConfig("formatter-config/eclipse/xml.properties");
        jsonFormatter.setFileConfig("formatter-config/jackson/json.properties");
        cssFormatter.setFileConfig("formatter-config/ph-css/css.properties");
    }

    /**
     * Execute.
     *
     * @throws FormatterException the mojo execution exception
     * @see org.apache.maven.plugin.AbstractMojo#execute()
     */
    @Override
    public void execute() throws FormatterException {
        if (skipFormatting) {
            log.info("Formatting is skipped");
            return;
        }

        ResultCollector result = new ResultCollector();
        result.start();

        if (StringUtils.isEmpty(encoding)) {
            encoding = ReaderFactory.FILE_ENCODING;
            log.warn("File encoding has not been set, using platform encoding (" + encoding + ") to format source files, i.e. build is platform dependent!");
        } else {
            if (!Charset.isSupported(encoding)) {
                throw new FormatterException("Encoding '" + encoding + "' is not supported");
            }
            log.info("Using '" + encoding + "' encoding to format source files.");
        }

        List<File> files = new ArrayList<>();

        if (directories == null) {
            directories = new File[]{basedir};
        }

        for (File directory : directories) {
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
        log.info("Processing file: " + file);
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
        if (javaFormatter.isSupport(file.getName()) && javaFormatter.isInitialized()) {
            if (javaFormatter.isSkip()) {
                log.info("Java formatting is skipped");
                result = Result.SKIPPED;
            } else {
                result = javaFormatter.formatFile(file, lineEnding, dryRun);
            }
        } else if (jsFormatter.isSupport(file.getName())  && jsFormatter.isInitialized()) {
            if (jsFormatter.isSkip()) {
                log.info("Javascript formatting is skipped");
                result = Result.SKIPPED;
            } else {
                result = jsFormatter.formatFile(file, lineEnding, dryRun);
            }
        } else if (htmlFormatter.isSupport(file.getName()) && htmlFormatter.isInitialized()) {
            if (htmlFormatter.isSkip()) {
                log.info("Html formatting is skipped");
                result = Result.SKIPPED;
            } else {
                result = htmlFormatter.formatFile(file, lineEnding, dryRun);
            }
        } else if ( xmlFormatter.isSupport(file.getName())  && xmlFormatter.isInitialized()) {
            if (xmlFormatter.isSkip()) {
                log.info("Xml formatting is skipped");
                result = Result.SKIPPED;
            } else {
                result = xmlFormatter.formatFile(file, lineEnding, dryRun);
            }
        } else if ( jsonFormatter.isSupport(file.getName())  && jsonFormatter.isInitialized()) {
            if (jsonFormatter.isSkip()) {
                log.info("json formatting is skipped");
                result = Result.SKIPPED;
            } else {
                result = jsonFormatter.formatFile(file, lineEnding, dryRun);
            }
        } else if (cssFormatter.isSupport(file.getName()) && cssFormatter.isInitialized()) {
            if (cssFormatter.isSkip()) {
                log.info("css formatting is skipped");
                result = Result.SKIPPED;
            } else {
                result = cssFormatter.formatFile(file, lineEnding, dryRun);
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
        try ( BufferedReader reader = new BufferedReader(ReaderFactory.newReader(file, encoding))) {
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

        try ( BufferedWriter bw = new BufferedWriter(WriterFactory.newWriter(file, encoding))) {
            bw.write(str);
        }
    }

    /**
     * Create a {@link CodeFormatter} instance to be used by this mojo.
     *
     * @throws FormatterException the mojo execution exception
     */
    private void createCodeFormatter() throws FormatterException {
        Map<String, String> javaFormattingOptions = getFormattingOptions(javaFormatter.getFileConfig());
        if (javaFormattingOptions != null) {
            javaFormatter.init(javaFormattingOptions, this);
        }
        Map<String, String> jsFormattingOptions = getFormattingOptions(jsFormatter.getFileConfig());
        if (jsFormattingOptions != null) {
            jsFormatter.init(jsFormattingOptions, this);
        }
        if (htmlFormatter.getFileConfig() != null) {
            htmlFormatter.init(FormatterHelp.getOptionsFromPropertiesFile(htmlFormatter.getFileConfig(), basedir), this);
        }
        if (xmlFormatter.getFileConfig() != null) {
            Map<String, String> xmlFormattingOptions = FormatterHelp.getOptionsFromPropertiesFile(xmlFormatter.getFileConfig(), basedir);
            xmlFormattingOptions.put("lineending", lineEnding.getChars());
            xmlFormatter.init(xmlFormattingOptions, this);
        }
        if (jsonFormatter.getFileConfig() != null) {
            Map<String, String> jsonFormattingOptions = FormatterHelp.getOptionsFromPropertiesFile(jsonFormatter.getFileConfig(), basedir);
            jsonFormattingOptions.put("lineending", lineEnding.getChars());
            jsonFormatter.init(jsonFormattingOptions, this);
        }
        if (cssFormatter.getFileConfig() != null) {
            cssFormatter.init(FormatterHelp.getOptionsFromPropertiesFile(cssFormatter.getFileConfig(), basedir), this);
        }
        // stop the process if not config files where found
        if (javaFormattingOptions == null && jsFormattingOptions == null && htmlFormatter.getFileConfig() == null
                && xmlFormatter.getFileConfig() == null && cssFormatter.getFileConfig() == null) {
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
        if (useEclipseDefaults) {
            log.info("Using Ecipse Defaults");
            Map<String, String> options = new HashMap<>();
            options.put(JavaCore.COMPILER_SOURCE, compilerSource);
            options.put(JavaCore.COMPILER_COMPLIANCE, compilerCompliance);
            options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, compilerTargetPlatform);
            return options;
        }
        return FormatterHelp.getOptionsFromConfigFile(newConfigFile, basedir);
    }

    @Override
    public File getTargetDirectory() {
        return targetDirectory;
    }

    @Override
    public Charset getEncodingAsCharset() {
        return Charset.forName(encoding);
    }

    @Override
    public FormatterLog getLog() {
        return log;
    }

    @Override
    public void setBasedir(File basedir) {
        basedir = basedir;
        if (directories == null) {
            directories = new File[]{basedir};
        }
    }
}
