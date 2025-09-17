# Library Microservices

Este proyecto implementa un sistema de **microservicios para una biblioteca** usando Spring Boot, Spring Cloud, Eureka Server y un API Gateway. Cada servicio se comunica a través de Eureka y los datos se almacenan en bases de datos PostgreSQL separadas.

## Servicios incluidos


| Servicio                      | Puerto | Descripción                                    |
| ----------------------------- | ------ | ----------------------------------------------- |
| **Eureka Server**             | 8761   | Registro de servicios (Service Discovery)       |
| **API Gateway**               | 8080   | Puerta de entrada para todos los microservicios |
| **User Service**              | 8081   | Gestión de usuarios                            |
| **Book Service**              | 8082   | Gestión de libros                              |
| **Reservation Service**       | 8083   | Gestión de reservas de libros                  |
| **PostgreSQL User DB**        | 5433   | Base de datos para usuarios                     |
| **PostgreSQL Book DB**        | 5434   | Base de datos para libros                       |
| **PostgreSQL Reservation DB** | 5435   | Base de datos para reservas                     |

---

## Estructura de carpetas

<pre class="overflow-visible!" data-start="1235" data-end="1561"><div class="contain-inline-size rounded-2xl relative bg-token-sidebar-surface-primary"><div class="sticky top-9"><div class="absolute end-0 bottom-0 flex h-9 items-center pe-2"><div class="bg-token-bg-elevated-secondary text-token-text-secondary flex items-center gap-4 rounded-sm px-2 font-sans text-xs"></div></div></div><div class="overflow-y-auto p-4" dir="ltr"><code class="whitespace-pre!"><span><span>project-root/
│
├── eureka-server/           </span><span># Eureka Server</span><span>
├── api-gateway/             </span><span># API Gateway</span><span>
├── user-service/            </span><span># Servicio de usuarios</span><span>
├── book-service/            </span><span># Servicio de libros</span><span>
├── reservation-service/     </span><span># Servicio de reservas</span><span>
└── docker-compose.yml       </span><span># Configuración de contenedores</span><span>
</span></span></code></div></div></pre>

## Requisitos

* Docker >= 20.10
* Docker Compose >= 1.29
* Java 17+ (para compilar los servicios si no usas imágenes preconstruidas)

## Levantar el proyecto

1. Clonar el repositorio:

<pre class="overflow-visible!" data-start="1773" data-end="1827"><div class="contain-inline-size rounded-2xl relative bg-token-sidebar-surface-primary"><div class="sticky top-9"><div class="absolute end-0 bottom-0 flex h-9 items-center pe-2"><div class="bg-token-bg-elevated-secondary text-token-text-secondary flex items-center gap-4 rounded-sm px-2 font-sans text-xs"></div></div></div><div class="overflow-y-auto p-4" dir="ltr"><code class="whitespace-pre! language-bash"><span><span>git </span><span>clone</span><span> <tu-repositorio>
</span><span>cd</span><span> project-root
</span></span></code></div></div></pre>

2. Levantar todos los contenedores:

<pre class="overflow-visible!" data-start="1866" data-end="1903"><div class="contain-inline-size rounded-2xl relative bg-token-sidebar-surface-primary"><div class="sticky top-9"><div class="absolute end-0 bottom-0 flex h-9 items-center pe-2"><div class="bg-token-bg-elevated-secondary text-token-text-secondary flex items-center gap-4 rounded-sm px-2 font-sans text-xs"></div></div></div><div class="overflow-y-auto p-4" dir="ltr"><code class="whitespace-pre! language-bash"><span><span>docker-compose up --build
</span></span></code></div></div></pre>

3. Acceder a los servicios:

* **Eureka Server:**[http://localhost:8761](http://localhost:8761)
* **API Gateway:**[http://localhost:8080](http://localhost:8080)

Los microservicios se registrarán automáticamente en Eureka y estarán disponibles a través del API Gateway.

## Integración con Eureka y API Gateway

* Todos los microservicios están configurados para **registrarse en Eureka** (`EUREKA_CLIENT_SERVICEURL_DEFAULTZONE`) automáticamente.
* El **API Gateway** enruta las solicitudes hacia los microservicios según la configuración de rutas y balanceo de carga.

## Bases de datos

Cada microservicio tiene su propia base de datos PostgreSQL:


| Servicio    | DB Name        | Usuario  | Contraseña |
| ----------- | -------------- | -------- | ----------- |
| User        | usersdb        | postgres | postgres    |
| Book        | booksdb        | postgres | postgres    |
| Reservation | reservationsdb | postgres | postgres    |

Los volúmenes de Docker garantizan persistencia de datos:

* `postgres-user-data`
* `postgres-book-data`
* `postgres-reservation-data`

## Endpoints principales

Ejemplo de rutas a través del **API Gateway**:

* **Usuarios:**`http://localhost:8080/users/**`
* **Libros:**`http://localhost:8080/books/**`
* **Reservas:**`http://localhost:8080/reservations/**`

## Notas

* El proyecto incluye **Resilience4j** para tolerancia a fallos con fallback en caso de que algún servicio esté caído.
* Todos los servicios están preparados para ser escalables mediante Docker y Kubernetes si se desea.
