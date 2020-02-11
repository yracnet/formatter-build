/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.yracnet.formatter.test;

import dev.yracnet.formatter.FormatterBuild;
import java.io.File;
import java.io.IOException;
import dev.yracnet.formatter.FormatterException;

/**
 *
 * @author wyujra
 */
public class MainRun {

    public static void main(String[] args) throws FormatterException, IOException {
        FormatterBuild build = FormatterBuild.create();
        File basedir = new File("../demo");
        System.out.println("--->" + basedir.getCanonicalPath());
        build.setIncludes("**/*.xml", "**/*.xhtml");
        //build.setExcludes("**/target/*");
        build.setBasedir(basedir);
        build.getXmlFormatterConfig().setExtensions(".xml", ".xhtml");
        build.getXmlFormatterConfig().setSkip(false);
        //build.setDirectories(basedir);
        //build.setDirectories(new File(new File(""), "../demo"));
        build.execute();
    }

}
