FROM amazoncorretto:17
MAINTAINER deniel
WORKDIR /app
COPY . /app

RUN yum update -y && \
    yum install -y dos2unix && \
    dos2unix gradlew && \
    yum remove -y dos2unix && \
    yum clean all

RUN chmod +x ./gradlew
RUN ./gradlew build -x test

ENTRYPOINT ["java", "-jar", "/app/build/libs/simple-register-login-0.0.1-SNAPSHOT.jar"]