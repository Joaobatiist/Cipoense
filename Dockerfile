# ESTÁGIO 1: BUILD
FROM gradle:jdk21 AS build
WORKDIR /home/gradle/src
COPY --chown=gradle:gradle . /home/gradle/src
RUN gradle build -x test

# ESTÁGIO 2: EXECUÇÃO (Runtime)
FROM eclipse-temurin:21-jre-alpine
EXPOSE 8080
RUN mkdir /app
WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/app.jar

# --- MODIFICAÇÕES AQUI ---
# 1. Instala o netcat, a ferramenta que o script de espera usa
RUN apk add --no-cache netcat-openbsd



# 4. Define o ENTRYPOINT para rodar o script antes da aplicação
CMD ["java", "-jar", "app.jar"]