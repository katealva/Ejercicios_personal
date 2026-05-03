Hackathon #1: Oreo Insight Factory 🍪📈
Descripción General
¿A quién no le gusta meter una Oreo 🍪 en un vaso con leche 🥛?

La fábrica de Oreo está por lanzar un piloto con UTEC para transformar sus datos de ventas en insights accionables. Tu misión es construir un backend sencillo y sólido que permita registrar ventas y, a partir de esos datos, generar resúmenes automáticos en lenguaje natural usando GitHub Models (vía GitHub Marketplace).

El énfasis no está en pantallas ni frontends, sino en la calidad del contrato de API, autenticación básica, persistencia, pruebas mínimas y un endpoint de insights que consulte un LLM. La validación se hará ejecutando un Postman Flow end-to-end sobre tu backend. 🥛🤖

Duración y Formato
Tiempo: 2 horas
Equipos: Grupos de 4 o 5 estudiantes
Recursos: Uso de IA permitido, documentación y material de Internet
Contexto del Negocio
Oreo quiere dejar de "mojar la galleta a ciegas" y empezar a entender qué pasa en cada sucursal: qué SKU lidera, cuándo hay picos de demanda y cómo evoluciona el ticket promedio. Para ello, busca un backend que reciba ventas, consolide métricas y pida a un LLM un resumen corto y claro que cualquier analista pueda leer en segundos. 🍪🥛

Tu servicio será el motor de insights: seguro (JWT), consistente (JPA) y probado (testing mínimo). Si el Postman Flow "se la come" completa —login, seed de ventas, consultas y /summary—, ¡estás listo para producción… o al menos para un vaso grande de leche! 🚀

💡 ¿Por Qué Este Hackathon Es Tu Mejor Carta de Presentación?
Este proyecto no es solo un ejercicio académico - es tu portafolio estrella. 🌟

Imagina estar en una entrevista y poder decir: "Desarrollé un sistema que integra autenticación JWT, procesamiento asíncrono, integración con LLMs, y envío automatizado de reportes. Todo en 2 horas, trabajando en equipo bajo presión."

Lo que demuestras con este proyecto:

✅ Manejo de arquitecturas modernas (async, eventos, microservicios)
✅ Integración con IA/LLMs (la skill más demandada del 2025)
✅ Seguridad y autenticación empresarial
✅ Trabajo con APIs externas y servicios de terceros
✅ Colaboración efectiva bajo presión
Este es el tipo de proyecto que los reclutadores buscan en GitHub. Es real, es complejo, y resuelve un problema de negocio tangible. 🎯

🚀 Estrategia para el Éxito: Divide y Vencerás
¡Ustedes pueden con esto! El secreto no está en que todos hagan todo, sino en la comunicación y división inteligente del trabajo.

Distribución Sugerida (5 personas):
El Arquitecto 🏗️: Setup inicial, estructura del proyecto, configuración de Spring Boot
El Guardian 🔐: JWT, Spring Security, roles y permisos
El Persistente 💾: JPA, entidades, repositorios, queries
El Comunicador 📡: Integración con GitHub Models y servicio de email
El Validador ✅: Postman Collection, testing, documentación
Pro tip: Los primeros 20 minutos son CRUCIALES. Úsenlos para:

Definir interfaces claras entre componentes
Acordar DTOs y contratos
Crear branches en Git para cada uno
Establecer un punto de integración a los 60 minutos
Recuerden: La comunicación constante es clave. Un equipo que se comunica bien puede lograr más que 5 genios trabajando aislados. 💪

Requerimientos Técnicos
Tecnologías Obligatorias
Java 21+
Spring Boot 3.x
Spring Security con JWT
Spring Data JPA
H2 o PostgreSQL (a elección)
Cliente HTTP o SDK para GitHub Models API
JavaMail o Spring Boot Mail para envío de correos
@Async y @EventListener para procesamiento asíncrono
Variables de Entorno Requeridas
GITHUB_TOKEN=<tu_token_de_GitHub>
GITHUB_MODELS_URL=<endpoint_REST_de_GitHub_Models>
MODEL_ID=<id_del_modelo_del_Marketplace>
JWT_SECRET=<clave_para_firmar_JWT>
# Para envío de correos:
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=<tu_email@gmail.com>
MAIL_PASSWORD=<app_password>
# Si usas PostgreSQL:
DB_URL=<jdbc_url>
DB_USER=<usuario_db>
DB_PASS=<password_db>
Roles y Seguridad
Implementar JWT para autenticación y los siguientes roles con sus respectivos permisos:

ROLE_CENTRAL: Oficina central de Oreo - Acceso completo a todas las ventas de todas las sucursales, reportes globales y gestión de usuarios
ROLE_BRANCH: Usuario de sucursal - Solo puede ver y crear ventas de su propia sucursal asignada
Cada usuario con ROLE_BRANCH debe tener una sucursal asignada al momento del registro.

Funcionalidades Requeridas
1. Autenticación JWT
   Método	Endpoint	Descripción	Roles Permitidos	Request Body	Response
   POST	/auth/register	Crear nuevo usuario	Público	{"username": "oreo.admin", "email": "admin@oreo.com", "password": "Oreo1234", "role": "CENTRAL"} o {"username": "miraflores.user", "email": "mira@oreo.com", "password": "Oreo1234", "role": "BRANCH", "branch": "Miraflores"}	201: {"id": "u_01J...", "username": "oreo.admin", "email": "admin@oreo.com", "role": "CENTRAL", "branch": null, "createdAt": "2025-09-12T18:10:00Z"}
   POST	/auth/login	Autenticar y obtener JWT	Público	{"username": "oreo.admin", "password": "Oreo1234"}	200: {"token": "eyJhbGci...", "expiresIn": 3600, "role": "CENTRAL", "branch": null}
   Reglas de validación:

Username: 3-30 caracteres, alfanumérico + _ y .
Email: formato válido
Password: mínimo 8 caracteres
Role: debe ser uno de ["CENTRAL", "BRANCH"]
Branch: obligatorio si role es "BRANCH", null si es "CENTRAL"
2. Gestión de Ventas
   Método	Endpoint	Descripción	Roles Permitidos	Request Body	Response
   POST	/sales	Crear nueva venta	CENTRAL (cualquier branch), BRANCH (solo su branch)	Ver ejemplo abajo	201: Venta creada
   GET	/sales/{id}	Obtener detalle de venta	CENTRAL (todas), BRANCH (solo de su branch)	-	200: Detalle completo
   GET	/sales	Listar ventas con filtros	CENTRAL (todas), BRANCH (solo su branch)	Query params: from, to, branch, page, size	200: Lista paginada
   PUT	/sales/{id}	Actualizar venta	CENTRAL (todas), BRANCH (solo de su branch)	Ver ejemplo abajo	200: Venta actualizada
   DELETE	/sales/{id}	Eliminar venta	CENTRAL	-	204: No Content
   Ejemplo de creación de venta:

{
"sku": "OREO_CLASSIC_12",
"units": 25,
"price": 1.99,
"branch": "Miraflores",
"soldAt": "2025-09-12T16:30:00Z"
}
Nota: Los usuarios BRANCH solo pueden crear ventas para su sucursal asignada. Si intentan crear para otra sucursal, devolver 403.

Response esperado (201):

{
"id": "s_01K...",
"sku": "OREO_CLASSIC_12",
"units": 25,
"price": 1.99,
"branch": "Miraflores",
"soldAt": "2025-09-12T16:30:00Z",
"createdBy": "miraflores.user"
}
3. Resumen Semanal ASÍNCRONO con LLM y Email
   Método	Endpoint	Descripción	Roles Permitidos
   POST	/sales/summary/weekly	Solicitar generación asíncrona de resumen y envío por email	CENTRAL (cualquier branch), BRANCH (solo su branch)
   Request para /sales/summary/weekly:

{
"from": "2025-09-01",
"to": "2025-09-07",
"branch": "Miraflores",
"emailTo": "gerente@oreo.com"
}
Si no se envía from y to, calcular automáticamente la última semana. Usuarios BRANCH solo pueden generar resúmenes de su propia sucursal. El campo emailTo es obligatorio.

Response INMEDIATA (202 Accepted):

{
"requestId": "req_01K...",
"status": "PROCESSING",
"message": "Su solicitud de reporte está siendo procesada. Recibirá el resumen en gerente@oreo.com en unos momentos.",
"estimatedTime": "30-60 segundos",
"requestedAt": "2025-09-12T18:15:00Z"
}
📧 Implementación Asíncrona Requerida
Este es el corazón del ejercicio: Implementar procesamiento ASÍNCRONO usando las herramientas de Spring que hemos estudiado.

Flujo requerido:

Controller recibe la petición y retorna inmediatamente 202 Accepted
Evento ReportRequestedEvent se publica con ApplicationEventPublisher
Listener con @EventListener y @Async procesa en background:
Calcula agregados de ventas
Consulta GitHub Models API
Genera el resumen
Envía el email
Email llega al destinatario con el resumen
Ejemplo de implementación:

// En el Service
@Async
@EventListener
public void handleReportRequest(ReportRequestedEvent event) {
// 1. Calcular agregados
// 2. Llamar a GitHub Models
// 3. Enviar email
// 4. Opcionalmente, actualizar status en BD
}
4. Gestión de Usuarios (Solo CENTRAL)
   Método	Endpoint	Descripción	Roles Permitidos
   GET	/users	Listar todos los usuarios	CENTRAL
   GET	/users/{id}	Ver detalle de usuario	CENTRAL
   DELETE	/users/{id}	Eliminar usuario	CENTRAL
5. Requerimiento de Testing Unitario
   OBLIGATORIO: Implementar tests unitarios para el servicio de cálculo de agregados de ventas (SalesAggregationService o similar).

Tests Requeridos
Debes implementar mínimo 5 test cases que cubran:

Test de agregados con datos válidos: Verificar que se calculen correctamente totalUnits, totalRevenue, topSku y topBranch con un dataset conocido
Test con lista vacía: Verificar comportamiento cuando no hay ventas en el rango de fechas
Test de filtrado por sucursal: Verificar que solo considere ventas de la sucursal especificada (para usuarios BRANCH)
Test de filtrado por fechas: Verificar que solo considere ventas dentro del rango de fechas especificado
Test de cálculo de SKU top: Verificar que identifique correctamente el SKU más vendido cuando hay empates
Ejemplo de Test Esperado
@ExtendWith(MockitoExtension.class)
class SalesAggregationServiceTest {

    @Mock
    private SalesRepository salesRepository;

    @InjectMocks
    private SalesAggregationService salesAggregationService;

    @Test
    void shouldCalculateCorrectAggregatesWithValidData() {
        // Given
        List<Sale> mockSales = List.of(
            createSale("OREO_CLASSIC", 10, 1.99, "Miraflores"),
            createSale("OREO_DOUBLE", 5, 2.49, "San Isidro"),
            createSale("OREO_CLASSIC", 15, 1.99, "Miraflores")
        );
        when(salesRepository.findByDateRange(any(), any())).thenReturn(mockSales);

        // When
        SalesAggregates result = salesAggregationService.calculateAggregates(
            LocalDate.now().minusDays(7), LocalDate.now(), null
        );

        // Then
        assertThat(result.getTotalUnits()).isEqualTo(30);
        assertThat(result.getTotalRevenue()).isEqualTo(42.43);
        assertThat(result.getTopSku()).isEqualTo("OREO_CLASSIC");
        assertThat(result.getTopBranch()).isEqualTo("Miraflores");
    }

    // ... más tests
}
🎯 RETO EXTRA: Carta Mágica de Puntos Bonus 🪄
¡Para los valientes que quieran puntos extra! 🏆

El Desafío Premium
Ya estás enviando resúmenes por email de manera asíncrona... ¿pero qué tal si los gerentes quieren algo más profesional? 📊📄

El reto: En lugar de enviar un email con texto plano, envía un email HTML profesional con:

El resumen formateado elegantemente
Gráficos embebidos (bar charts, pie charts)
PDF adjunto con el reporte completo
Endpoints Bonus
Método	Endpoint	Descripción	Roles Permitidos
POST	/sales/summary/weekly/premium	Solicitar reporte premium asíncrono	CENTRAL, BRANCH
Request:

{
"from": "2025-09-01",
"to": "2025-09-07",
"branch": "Miraflores",
"emailTo": "gerente@oreo.com",
"format": "PREMIUM",
"includeCharts": true,
"attachPdf": true
}
Response inmediata (202 Accepted):

{
"requestId": "req_premium_01K...",
"status": "PROCESSING",
"message": "Su reporte premium está siendo generado. Incluirá gráficos y PDF adjunto.",
"estimatedTime": "60-90 segundos",
"features": ["HTML_FORMAT", "CHARTS", "PDF_ATTACHMENT"]
}
Pistas para el Email Premium 🕵️‍♂️
Pista #1: Para gráficos en emails, genera URLs de imágenes con servicios como QuickChart.io e insértalas como <img src="...">
Pista #2: El LLM puede generar configuraciones de Chart.js que luego conviertes a URLs de QuickChart
Pista #3: Para el PDF, considera iText o Apache PDFBox en Java
Pista #4: Spring Boot Mail soporta HTML y attachments nativamente
Pista #5: Todo esto también debe ser asíncrono - ¡más razón para usar eventos!
Ejemplo de Email HTML (simplificado)
<!DOCTYPE html>
<html>
<head>
    <style>
        .header { background: #6B46C1; color: white; padding: 20px; }
        .metric { display: inline-block; margin: 10px; padding: 15px; background: #f0f0f0; }
    </style>
</head>
<body>
    <div class="header">
        <h1>🍪 Reporte Semanal Oreo</h1>
    </div>
    <div class="content">
        <p>Esta semana vendimos <strong>1,250 unidades</strong>...</p>
        <div class="metric">
            <h3>Total Revenue</h3>
            <p>$4,800.50</p>
        </div>
        <img src="https://quickchart.io/chart?c={type:'bar',data:{...}}" />
    </div>
</body>
</html>
Criterios de Evaluación del Reto
+3 puntos: Email HTML con formato profesional
+5 puntos: Incluir al menos un gráfico embebido en el email
+10 puntos: Email HTML + múltiples gráficos + PDF adjunto con formato profesional
Nota: Este reto es OPCIONAL y los puntos obtenidos se sumarán a su Hackathon 0. Los equipos que lo intenten y fallen no serán penalizados. ¡Es puro upside! 🚀

Integración con GitHub Models
Documentación y Setup
Para usar GitHub Models en tu proyecto, necesitarás:

Documentación oficial: GitHub Models REST API
Token de acceso: Crear un Personal Access Token con permisos de model en tu cuenta de GitHub
Modelo recomendado para esta hackaton: OpenAI gpt-5-mini
Proceso Requerido
Calcular agregados de las ventas:

totalUnits
totalRevenue
topSku (el más vendido por unidades)
topBranch (sucursal con más ventas)
Construir prompt para el LLM:

{
"model": "${MODEL_ID}",
"messages": [
{"role": "system", "content": "Eres un analista que escribe resúmenes breves y claros para emails corporativos."},
{"role": "user", "content": "Con estos datos: totalUnits=1250, totalRevenue=4800.50, topSku=OREO_DOUBLE, topBranch=Miraflores. Devuelve un resumen ≤120 palabras para enviar por email."}
],
"max_tokens": 200
}
Validaciones del resumen:

Máximo 120 palabras
Debe mencionar al menos uno: unidades totales, SKU top, sucursal top, o total recaudado
En español, claro y sin alucinaciones
Enviar por email (de manera asíncrona):

Subject: "Reporte Semanal Oreo - [fecha_desde] a [fecha_hasta]"
Body: El summaryText generado + los aggregates principales
Manejo de Errores
Formato estándar para todos los errores:

{
"error": "BAD_REQUEST",
"message": "Detalle claro del problema",
"timestamp": "2025-09-12T18:10:00Z",
"path": "/sales"
}
Códigos HTTP esperados:

201: Recurso creado
202: Accepted (para procesamiento asíncrono)
200: OK
204: Sin contenido (cuando no hay ventas en el rango)
400: Validación fallida
401: No autenticado
403: Sin permisos (intentando acceder a datos de otra sucursal)
404: Recurso no encontrado
409: Conflicto (username/email ya existe)
503: Servicio no disponible (LLM caído o servicio de email no disponible)
Validación con Postman Flow
La colección ejecutará esta secuencia:

Register CENTRAL → Assert 201, guardar userId
Login CENTRAL → Assert 200, guardar {{centralToken}}
Register BRANCH (Miraflores) → Assert 201
Login BRANCH → Assert 200, guardar {{branchToken}}
Crear 5 ventas (con CENTRAL) → Assert 201 cada una (diferentes sucursales)
Listar todas las ventas (con CENTRAL) → Assert 200, lista con todas
Listar ventas (con BRANCH) → Assert 200, solo ventas de Miraflores
Solicitar resumen asíncrono (con BRANCH) → Assert 202, requestId presente
Intentar crear venta otra sucursal (con BRANCH) → Assert 403 Forbidden
Eliminar venta (con CENTRAL) → Assert 204
Datos de Prueba (Seeds)
[
{"sku": "OREO_CLASSIC_12", "units": 25, "price": 1.99, "branch": "Miraflores", "soldAt": "2025-09-01T10:30:00Z"},
{"sku": "OREO_DOUBLE", "units": 40, "price": 2.49, "branch": "Miraflores", "soldAt": "2025-09-02T15:10:00Z"},
{"sku": "OREO_THINS", "units": 32, "price": 2.19, "branch": "San Isidro", "soldAt": "2025-09-03T11:05:00Z"},
{"sku": "OREO_DOUBLE", "units": 55, "price": 2.49, "branch": "San Isidro", "soldAt": "2025-09-04T18:50:00Z"},
{"sku": "OREO_CLASSIC_12", "units": 20, "price": 1.99, "branch": "Miraflores", "soldAt": "2025-09-05T09:40:00Z"}
]
Entregables
Código fuente completo en un repositorio público de GitHub
Postman Collection (archivo .json) en el root del repositorio
README.md con:
Información del equipo: Nombres completos y códigos UTEC de todos los integrantes
Instrucciones para ejecutar el proyecto
Instrucciones para correr el Postman workflow
Explicación de la implementación asíncrona
(Si intentaste el reto) Documentación del endpoint premium
Variables de entorno: Entregar por Canvas en formato texto las variables necesarias para ejecutar el proyecto
Criterios de Evaluación
Sistema de Evaluación Todo o Nada:

✅ 20 puntos: Si completan todas las funcionalidades principales:
Autenticación JWT con roles
CRUD de ventas con permisos por sucursal
Resumen asíncrono con email
Testing unitario del servicio de agregados (mínimo 5 tests)
Postman Collection funcional
❌ 0 puntos: Si no completan alguna de las funcionalidades principales
El proyecto debe funcionar completamente end-to-end para obtener los puntos. No hay evaluación parcial.

Observaciones Adicionales
CRÍTICO: El procesamiento del resumen DEBE ser asíncrono usando @Async y eventos
Habilita async en tu aplicación con @EnableAsync
El prompt al LLM debe ser corto y explícito con los números agregados
Si usas H2, activa la consola en modo dev para debugging
NUNCA subas tokens o secretos al repositorio (especialmente passwords de email)
El resumen debe reflejar los datos reales (no inventar información)
Maneja las fallas del LLM y del servicio de email con 503 y mensaje claro
Los usuarios BRANCH solo ven/modifican datos de su sucursal asignada
Para testing local de emails, considera usar MailDev o Mailtrap
Recuerden: La comunicación del equipo es más importante que el código individual
¡Que la galleta esté de tu lado! 🍪✨

Ustedes pueden con esto. Confíen en sus habilidades, comuníquense, y dividan el trabajo inteligentemente. Este proyecto puede ser la estrella de su portafolio. ¡A por ello! 💪🚀

Con mucho cariño desde California,

Gabriel Romero ❤️
