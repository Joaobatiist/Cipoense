# ESTÁGIO 1: BUILD
# Usa uma imagem com JDK e Gradle para construir a aplicação
FROM gradle:jdk21 AS build
WORKDIR /home/gradle/src

# Copia todo o código-fonte
COPY --chown=gradle:gradle . /home/gradle/src

# Executa o build do Gradle. A flag -x test pula os testes.
RUN gradle build -x test

# ESTÁGIO 2: EXECUÇÃO (Runtime)
# Usa uma imagem minimalista (apenas JRE) para rodar a aplicação
FROM eclipse-temurin:21-jre-alpine

# Expõe a porta que sua aplicação Spring Boot usa (padrão é 8080)
EXPOSE 8080

# Cria um diretório de trabalho
RUN mkdir /app
WORKDIR /app

# Copia o arquivo JAR de build do ESTÁGIO 1
# O caminho é build/libs/*.jar, que é o padrão do Gradle para o JAR do Spring Boot
COPY --from=build /home/gradle/src/build/libs/*.jar /app/app.jar

# Comando para rodar a aplicação Spring Boot
ENTRYPOINT ["java", "-XX:InitialRAMPercentage=75.0", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]