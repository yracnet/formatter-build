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
package dev.yracnet.formatter;

/**
 * @author yracnet
 */
public interface FormatterConfig {

    /**
     * Get File Configuration
     *
     * @return
     */
    String getFileConfig();

    /**
     * Set File Configuration
     *
     * @param fileConfig
     */
    void setFileConfig(String fileConfig);

    /**
     * Get Skip Format Code
     *
     * @return
     */
    boolean isSkip();

    /**
     * Set Skip Format Code
     *
     * @param skip
     */
    void setSkip(boolean skip);

    /**
     * Get Extension File Support
     *
     * @return
     */
    String[] getExtensions();

    /**
     * Set Extension File Support
     *
     * @param extension
     */
    void setExtensions(String... extension);

}
