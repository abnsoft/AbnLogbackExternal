
Logback, outside of war, external using logback, logback outside war.

It extends of ServletContextListener. 
Using this listener allows you to use in web application Logback config file in any place of filesystem. You should not place now logback.xml either only inside of a web app or classpath. 

The main goal of this library is changing the logging on the fly without restarting and changing any files in web applicat

ion. It is based on: 

Servlets 2.5, 
spring-web 4.1.5.RELEASE, 
logback-classic 


You have to define 2 things : 

1. In JVM arguments (jvm or Tomcat vm arguments) define some arguments. I.e. 
-DmyConfigDir="d:/my/config/logback/" . 
Where "d:/my/config/logback/" is folder where you will put your config file. 

2. In web.xml file create new context-param : 
 <context-param>
     <param-name>LogbackConfigFilename</param-name>
     <param-value>file:${myConfigDir}/logback.xml</param-value>
 </context-param>
 

 
