# 🛍️ PriceService

Microservicio REST desarrollado en Spring Boot que permite consultar el precio aplicable a un producto de una cadena en una fecha determinada. Utiliza una base de datos H2 en memoria y responde según las reglas de prioridad y rango de fechas definidas en el enunciado técnico.

---

## 🚀 Tecnologías

- Java 17
- Spring Boot 3.5.x
- Spring Data JPA
- H2 Database
- Maven Wrapper (compilación dentro del contenedor)
- Docker & Docker Compose
- Postman (para pruebas funcionales)
- JaCoCo (para cobertura de tests)

---

## 📦 Instalación y ejecución

### 🔧 Requisitos

- Docker y Docker Compose instalados
- No se requiere Maven ni JDK en el sistema local

### 🐳 Ejecutar con Docker Compose

1. Clona el repositorio:

```bash
git clone https://github.com/tu-usuario/priceservice.git
cd priceservice
```

2. Ejecuta el servicio:

```bash
docker-compose up --build
```

3. Accede al endpoint:
```code
http://localhost:8080/api/prices
```

### Ejemplo:
```http
GET /api/prices?date=2020-06-14T16:00:00&productId=35455&brandId=1
```

### Respuesta:
```json
{
  "productId": 35455,
  "brandId": 1,
  "priceList": 2,
  "startDate": "2020-06-14T15:00:00",
  "endDate": "2020-06-14T18:30:00",
  "price": 25.45,
  "currency": "EUR"
}
```
