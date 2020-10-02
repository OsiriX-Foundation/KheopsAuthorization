FROM gradle:6.5.1-jdk11 AS build
WORKDIR /home/gradle/authorization
COPY --chown=gradle:gradle build.gradle /home/gradle/authorization/build.gradle
COPY --chown=gradle:gradle src /home/gradle/authorization/src
RUN gradle war --no-daemon --info

FROM tomcat:9.0.35-jdk11
ARG VCS_REF
LABEL org.label-schema.vcs-ref=$VCS_REF \
      org.label-schema.vcs-url="https://github.com/OsiriX-Foundation/KheopsAuthorization"

ENV SECRET_FILE_PATH=/run/secrets \
    REPLACE_FILE_PATH=/usr/local/tomcat/conf/context.xml

COPY --from=build /home/gradle/authorization/build/libs/authorization.war /usr/local/tomcat/webapps/authorization.war
COPY setenv.sh $CATALINA_HOME/bin/setenv.sh
COPY kheops-entrypoint.sh /kheops-entrypoint.sh
COPY context.xml /usr/local/tomcat/conf/context.xml

#FILEBEAT
COPY --from=osirixfoundation/kheops-beat:latest /install/deb/filebeat-amd64.deb .
RUN dpkg -i filebeat-amd64.deb && \
 rm filebeat-amd64.deb && \
 rm /etc/filebeat/filebeat.reference.yml && \
 rm /etc/filebeat/modules.d/*

COPY filebeat/filebeat.yml /etc/filebeat/filebeat.yml
RUN chmod go-w /etc/filebeat/filebeat.yml

#METRICBEAT
COPY --from=osirixfoundation/kheops-beat:latest /install/deb/metricbeat-amd64.deb .
RUN dpkg -i metricbeat-amd64.deb && \
 rm metricbeat-amd64.deb && \
 rm /etc/metricbeat/metricbeat.reference.yml && \
 rm /etc/metricbeat/modules.d/*

COPY metricbeat/metricbeat.yml /etc/metricbeat/metricbeat.yml
COPY metricbeat/http.yml /etc/metricbeat/modules.d/http.yml
RUN chmod go-w /etc/metricbeat/metricbeat.yml
RUN chmod go-w /etc/metricbeat/modules.d/http.yml


#RUN mkdir -pv $CATALINA_HOME/log4j2/lib
#RUN mkdir -pv $CATALINA_HOME/log4j2/conf

# Download api and core libraries $CATALINA_HOME/log4j2/lib has to be added to the CLASSPATH
#RUN curl -o $CATALINA_HOME/log4j2/lib/log4j-api-2.13.3.jar https://repo1.maven.org/maven2/org/apache/logging/log4j/log4j-api/2.13.3/log4j-api-2.13.3.jar
#RUN curl -o $CATALINA_HOME/log4j2/lib/log4j-core-2.13.3.jar https://repo1.maven.org/maven2/org/apache/logging/log4j/log4j-core/2.13.3/log4j-core-2.13.3.jar
# Download appserver library. This one replaces the old log4j "extras" one
#RUN curl -o $CATALINA_HOME/log4j2/lib/log4j-appserver-2.13.3.jar https://repo1.maven.org/maven2/org/apache/logging/log4j/log4j-appserver/2.13.3/log4j-appserver-2.13.3.jar
# Log4j JDK Logging Adapter. Allow apps to use java.util.logging via log4j2
#RUN curl -o $CATALINA_HOME/log4j2/lib/log4j-jul-2.13.3.jar https://repo1.maven.org/maven2/org/apache/logging/log4j/log4j-jul/2.13.3/log4j-jul-2.13.3.jar

# Copy the log4j2-tomcat.xml in $CATALINA_HOME/log4j2/conf
#COPY log4j2-tomcat.xml $CATALINA_HOME/log4j2/conf/log4j2-tomcat.xml
#COPY server.xml $CATALINA_HOME/conf/server.xml

RUN rm $CATALINA_HOME/conf/logging.properties

CMD ["catalina.sh", "run"]
ENTRYPOINT ["/kheops-entrypoint.sh"]
