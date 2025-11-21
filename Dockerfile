# -------------------------
# 1) Build da aplicação
# -------------------------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copia o projeto para dentro da imagem de build
COPY . .

# Gera o artefato de produção do Quarkus
RUN mvn -B clean package -DskipTests

# -------------------------
# 2) Imagem de execução
# -------------------------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copia TODA a pasta quarkus-app gerada pelo build
COPY --from=build /app/target/quarkus-app/ ./quarkus-app/

# (Opcional, mas útil) copia o banco se você quiser que o container já tenha um
# COPY --from=build /app/banco.db ./banco.db

EXPOSE 8080

# Comando oficial para rodar um fast-jar do Quarkus
CMD ["java", "-jar", "quarkus-app/quarkus-run.jar"]
