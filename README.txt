
  R E A D M E
  ===========

  Building the webapp
  -------------------

  0) Servlet Container as for instance Apache Tomcat/Catalina (see build.properties)
  1) Copy build.properties to local.build.properties
  2) Modify local.build.properties to your needs (re-configure the location of your Tomcat)
  3) Build the webapp by running './build.sh install'
  4) Startup your servlet container (e.g. Tomcat)
  5) Debug with 'tail -f TOMCAT/webapps/meguni/WEB-INF/logs/log4j.log'
     (Whereas one can re-configure log4j at lib/log4j.properties (and rebuild) or directly at TOMCAT/webapps/meguni/WEB-INF/classes/log4j.properties
