FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copiamos todo el proyecto al contenedor
COPY . .

# Damos permisos de ejecución al wrapper
RUN chmod +x mvnw

# Compilamos el proyecto dentro del contenedor
RUN ./mvnw clean package -DskipTests

# Exponemos el puerto
EXPOSE 8080

# Ejecutamos el JAR generado
CMD ["java", "-jar", "target/priceservice-0.0.1-SNAPSHOT.jar"]
