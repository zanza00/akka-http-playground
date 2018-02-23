FROM hseeberger/scala-sbt
ADD . /app
RUN cd /app && sbt update
RUN cd /app && sbt assembly
CMD ["java","-jar", "/app/target/scala-2.12/drone-service-server.jar"]
EXPOSE 5000
WORKDIR /app