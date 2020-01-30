# formatter
Format Source Code JAVA, CSS, JS, XML, HTML with Eclipse Platform


## Formatter Help is an API as utility based in https://code.revelc.net/formatter-maven-plugin

## What is the difference?

  1. formatter-maven-plugin is an wrap of eclipse jdt core for maven-plugin and required that used in a pom.xml 
  2. formatter-help not depend of maven-plugin, this could use as a simple LIB and wrap the eclipse format jdt

You can use this api in your application in runtime without include the maven-plugin dependency

Declare dependency:

     <dependency>
         <groupId>com.github.yracnet.formatter</groupId>
         <artifactId>formatter-help</artifactId>
         <version>0.2.0</version>
     </dependency>

In your code:

    public static void main(String[] args) throws FormatterException, IOException {
      File basedir = new File("/work/project-x");
      File dir1 = new File(basedir, "/src/main");
      File dir2 = new File(basedir, "/src/test");
      
      FormatterBuild build = FormatterBuild.create();

      //basedir is a root directory when exist a source code as JAVA, JS, HTML, CSS
      build.setBasedir(basedir); 

      // this is necesary for declare all directory that format
      // remember that dir1, dir2 are subdirectory of basedir
      build.setDirectories(dir1, dir2); 

      //This execute the format code
      build.execute();  
    }


The FormatterBuild Class has many method for configure the execution, please see the project https://code.revelc.net/formatter-maven-plugin

