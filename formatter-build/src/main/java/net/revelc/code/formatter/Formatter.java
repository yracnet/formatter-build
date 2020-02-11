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

import dev.yracnet.formatter.FormatterConfig;
import java.io.File;
import java.util.Map;
import net.revelc.code.formatter.ConfigurationSource;
import net.revelc.code.formatter.LineEnding;
import net.revelc.code.formatter.Result;

/**
 * @author marvin.froeder
 */
public interface Formatter extends FormatterConfig{

    /**
     * Initialize the {@link org.eclipse.jdt.core.formatter.CodeFormatter}
     * instance to be used by this component.
     * @param options
     * @param cfg
     */
    abstract void init(Map<String, String> options, ConfigurationSource cfg);

    /**
     * Format individual file.
     * @param file
     * @param ending
     * @param dryRun
     * @return 
     */
    abstract Result formatFile(File file, LineEnding ending, boolean dryRun);

    /**
     * return true if this formatter have been initialized
     * @return 
     */
    boolean isInitialized();

}
