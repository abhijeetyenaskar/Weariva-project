FROM eclipse-temurin:21.0.8_9-jdk-jammy AS builder

WORKDIR /opt/app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

#  linux specific execuable permission
RUN chmod +x mvnw

# this line help to convert CRLF -> LF
RUN sed -i 's/\r$//' mvnw

RUN ./mvnw dependency:go-offline

COPY src/ src/

RUN ./mvnw clean package -DskipTests


FROM eclipse-temurin:21.0.8_9-jre-jammy

WORKDIR /opt/app

COPY --from=builder /opt/app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]