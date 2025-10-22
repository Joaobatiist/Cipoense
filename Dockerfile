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

# --- CORREÇÕES OBRIGATÓRIAS ---

# 1. Instala netcat E dos2unix E O CLIENTE MYSQL
# O cliente mysql-client é necessário para o método de espera mais confiável.
RUN apk add --no-cache netcat-openbsd dos2unix mysql-client

# 2. Copia o script para o contêiner
COPY wait-for-db.sh /usr/local/bin/wait-for-db.sh

# 3. CORRIGE o formato de linha e torna o script executável
RUN dos2unix /usr/local/bin/wait-for-db.sh
RUN chmod +x /usr/local/bin/wait-for-db.sh

# 4. Define o ENTRYPOINT
ENTRYPOINT ["/usr/local/bin/wait-for-db.sh", "db:3306", "--", "java", "-jar", "app.jar"]