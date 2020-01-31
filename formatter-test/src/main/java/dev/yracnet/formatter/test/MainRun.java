/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.yracnet.formatter.test;

import dev.yracnet.formatter.FormatterBuild;
import java.io.File;
import java.io.IOException;
import net.revelc.code.formatter.FormatterException;

/**
 *
 * @author wyujra
 */
public class MainRun {

    public static void main(String[] args) throws FormatterException, IOException {
        FormatterBuild build = FormatterBuild.create();
        File basedir = new File("../demo");
        System.out.println("--->" + basedir.getCanonicalPath());
        build.setBasedir(basedir);
        //build.setDirectories(basedir);
        //build.setDirectories(new File(new File(""), "../demo"));
        build.execute();
    }

}
