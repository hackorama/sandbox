FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD Main.class Main.class
ADD RangeSet.class RangeSet.class
ADD Range.class Range.class
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS Main" ]
