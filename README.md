# Proyecto neoris-devops-java

### Dependencia para exponer HealthCheck mediante un endpoint
~~~
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-smallrye-health</artifactId>
</dependency>
~~~
Endpoint a consumir ´/q/health´
### Elaboración de Dockerfile
-  Uso de imagen base alpine para que imagen final sea de tamaño pequeño
-  Creación de usuario y grupo para que la imagen no se ejecute con usuario **root**
-  Exposición de puerto **8080** para que pueda ser consumido por servicios externos
-  Healthcheck para monitore del ejecutable

### Elaboración de Workflow
- Se genera un workflow completamente para la compilacion, escaneo y despliegue de imagen hacia Kubernetes
- Workflow **build.yml** esta dividido en 4 jobs que son:
    - **buids:**  Destinado para la generación del compilado y el escaneo de codigo estatico junto con pruebas de covertura mediante SonarCloud | Se generar artefactos del compilado y el reporte de JaCoCo
    - **unit_tests:** Destinado para ejecución de pruebas unitarias del codigo
    - **docker_image:** Destinado a la elaboración de la imágen Docker y carga al registry público Docker Hub | Escaneo de vulnerabilidades a nivel de imagen mediante Trivy
    - **deploy_to_eks:** Destinado para la ejecución de manifiestos de Kubernetes (EKS).
- Workflow **terraform.yml** esta dividido en 2 jobs, para ello fue necesario validar la descripción del commit, es decir, dependiendo de que infra quiere ejecutar, debe tener las palabras eks o vpc o destroy para una ejecución correcta. Por ejemplo: 
**fix(ci): eks modify -> Realizara cambios sobre la infra de eks**
**fix(ci): eks destroy -> eliminara la infra de eks**
**fix(ci): vpc modify sunets -> Realizara cambios sobre la infra de vpc**
Los jobs son los siguientes
    -   **terraform_plan:** Destinado para la ejecución de fmt, validate y plan
    -   **terraform_apply:** Destinado para la ejecución de apply

#### Scan Vulnerabilities

![ScanVulnerabilities](./img/ScanVulnerabilities.png)

### Terraform
#### Módulos VPC y EKS
- Se elabora módulo VPC el cual incluye:
    - **VPC:** Creación de VPC especificando CIDR Block
    - **Subnets:** Creacion de Subredes Publicas y Privadas | desde las Publicas, se crea un NAT Gateway con salida a Internet
    - **Internet_Gateway:** Creación de IG para salida a Internet
    - **Nat Gateway:** Creación de Nat Gateway por cada subred publica para traduccion de IPs privadas a publicas
    - **Route Tables:** Creacion de route tables para definir rutas hacia internet y rutas de manera interna
    - **Route Table Association:** Creación de route table association para enlazar las tablas de enrutamiento creadas previamente
- Se elabora módulo EKS que permite el despligue de toda la infraestructra necesaria para un cluster operativo, este es creado en las subredes publicas para tener acceso directo hacia el cluster
#### Ejecución de Modulos Terraform
- Se crea carpetas por cada modulo a usar para legibilidad y buenas practicas:
    - **Carpeta VPC**
    - **Carpeta EKS**
#### Arquitectura

![Arquitectura](./img/Arquitectura.png)

#### Kubernetes (EKS)
- Se crea 7 manifiestos para el despliegue del microservicio en Kubernetes los cuales son detallados a continuación:
    - **namespace.yml:** Creación del namespace en donde se van a agrupar los recursos siguientes.
    - **secrets.yml:** Creación de secretos cuyos valores sensibles estarán codificados en base64
    - **configmap.yml:** Creación de valores no sensibles
    - **deployment.yml:** Manifiesto que despliega el microservicio, tiene implementado readinessprobe y livenessprobe. El limite de recursos tambien es incorporado para que HPA pueda funcionar.
    - **hpa.yml:** Manifiesto para autoescalado de microservicios dependiendo de umbrales basados en el consumo de CPU
    - **service.yml** Manifiesto para la exposicion desde el POD hacia el nodo worker
    - **ingress.yml** Manifiesto para la comunicacion entre el balanceador de aplicacion de AWS y el service creado anteriormente, esto nos permite automaticamente definir extrictamnete los endpoints que debeder ser consumidos en nuestro aplicativo y que microservicio va a recibir la peticion.

##### Importante: Para el correcto funcionamientoe integracion con un ALB AWS, es necesario crear AWS Ingress Controller quien es el encargado de crear balanceadores automaticamente mediante las anotations definidas en el archivo ingress.yml
####

## Uso de Endpoints

## URL: http://ad0b6fac0c94745b5b3d8275f646b28d-641fe9733abb04cf.elb.us-east-1.amazonaws.com

1. Se necesita obtener el JWT que sera una cabezera necesaria para consumir el endpoint /DevOps, para ello necesitamos un usuario definido ya en la DB, para este caso usamos user **admin** y password: **admin**:

curl --location 'http://ad0b6fac0c94745b5b3d8275f646b28d-641fe9733abb04cf.elb.us-east-1.amazonaws.com:80/jwt/generate' \
--header 'X-Parse-REST-API-Key: 2f5ae96c-b558-4c7b-a590-a501ae1c3f6c' \
--header 'Authorization: Basic YWRtaW46YWRtaW4='

Con el que obtendremos un jwt:

eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ1cG4iOiJhZG1pbiIsImdyb3VwcyI6WyJXRUIiXSwiZXhwIjoxNzIzNzAyMjIyLCJpYXQiOjE3MjM3MDIyMTIsImp0aSI6Ijc2M2IwMjJlLTkyMWEtNGM3MC05ZTEyLWY1ZDk4MThlNzU1ZSIsImlzcyI6IlRlc3RCUCJ9.J4tlJPD74Ar8mxN-2yZclY4hqy8myQFclh9qHSIsln0mzOjct0hYukORS2Hfqg7EA-hiK6YsyPm4RUZ8V6QbqkcJeH3X9XXo3zo26eR_7lJfDqGEsvJ6uEE5rmvJXhReezPnD8JtbIeQWRPlvbPc20aiJeIKqjBIbsAmvhS0INOPQdTMBNcfErmLBdHec61WHPhs1TwXxujmktS4PWAD_Bu2o9GbOyDq1FxSE87N9N4HyAdsU_X0jSSUL1vca-Uw2w9m1oDXJdwD-Ly02HJQIpColAJuwoLQHE6dF8cDutPClk6R9lWsS6sXM0Xs1_3srL3k0C3ERXc6CzxLljPGdA

2. Consumimos el endpoint /DevOps con el JWT obtenido anteriormente:
3. 
curl --location 'http://ad0b6fac0c94745b5b3d8275f646b28d-641fe9733abb04cf.elb.us-east-1.amazonaws.com:80/DevOps' \
--header 'X-Parse-REST-API-Key: 2f5ae96c-b558-4c7b-a590-a501ae1c3f6c' \
--header 'X-JWT-KWY: eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ1cG4iOiJhZG1pbiIsImdyb3VwcyI6WyJXRUIiXSwiZXhwIjoxNzIzNzAyMjIyLCJpYXQiOjE3MjM3MDIyMTIsImp0aSI6Ijc2M2IwMjJlLTkyMWEtNGM3MC05ZTEyLWY1ZDk4MThlNzU1ZSIsImlzcyI6IlRlc3RCUCJ9.J4tlJPD74Ar8mxN-2yZclY4hqy8myQFclh9qHSIsln0mzOjct0hYukORS2Hfqg7EA-hiK6YsyPm4RUZ8V6QbqkcJeH3X9XXo3zo26eR_7lJfDqGEsvJ6uEE5rmvJXhReezPnD8JtbIeQWRPlvbPc20aiJeIKqjBIbsAmvhS0INOPQdTMBNcfErmLBdHec61WHPhs1TwXxujmktS4PWAD_Bu2o9GbOyDq1FxSE87N9N4HyAdsU_X0jSSUL1vca-Uw2w9m1oDXJdwD-Ly02HJQIpColAJuwoLQHE6dF8cDutPClk6R9lWsS6sXM0Xs1_3srL3k0C3ERXc6CzxLljPGdA' \
--header 'Content-Type: application/json' \
--data '{
    "message": "This is a test",
    "to": "Juan Perez",
    "from": "Rita Asturia",
    "timeToLifeSec": "45"
}'

Y obtenemos la siguiente respuesta:

{
    "message": "Hello Juan Perez your message will be send"
}