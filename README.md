##Feed reader app

###How to build:

Execute command `mvn clean package` in the root directory.
Make sure you get application.properties and pom.xml files updated before doing that (later on issues might occur trying to connect to DB or deploy to Tomcat)

###How to run:

1. Download Apache Tomcat and unpackage it into a tomcat folder;
2. Copy our WAR file from target/spring-boot-tomcat.war to the tomcat/webapps/ folder;
3. From a terminal navigate to tomcat/bin folder and execute catalina.bat run (on Windows) or catalina.sh run (on Unix-based systems);
4. Go to `http://localhost:8080/feed-reader`

Spoiler alert! Due to frontend files being quite heavy, nice UI won't be loaded :( However, one can also run application locally.

####Links to code repositories:
1. Backend: https://github.com/povilaspetkevicius/rss-feed-reader
2. Frontend: https://github.com/povilaspetkevicius/rss-reader