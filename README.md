# formatter
Format Source Code JAVA, CSS, JS, XML, HTML with Eclipse Platform


Formatter Help is an API as utility based in https://code.revelc.net/formatter-maven-plugin

What is the difference?

1.- formatter-maven-plugin is an wrap of eclipse jdt core for maven-plugin and required that used in a pom.xml 

2.- formatter-help not depend of maven-plugin, this could use as a simple LIB and wrap the eclipse format jdt

You can use this api in your application in runtime without include the maven-plugin dependency

This is simple:

FormatterBuild build = FormatterBuild.create();
build.setBasedir(basedir); //basedir is a root directory when exist a source code as JAVA, JS, HTML, CSS
build.setDirectories(directory1, directory2); // this is necesary for declare all directory that format, remember that directory1, directory2 is a subdirectory of basedir
build.execute();  //This execute the format code


The FormatterBuild Class has many method for configure the execution like a https://code.revelc.net/formatter-maven-plugin

