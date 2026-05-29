# 📋 Backlog de Volsta

Este documento aglutina y estructura las Historias de Usuario (User Stories) para la plataforma de gestión de equipos, partidos y estadísticas deportivas.

---

## 👥 1. Roles del Sistema (Actores)
Para entender el contexto de cada historia, se definen los siguientes roles con sus respectivos permisos:
* **Usuario (Visitante / General):** Cualquier persona que accede a la plataforma de forma pública o busca registrarse.
* **Mánager:** Responsable de la gestión táctica y administrativa de un equipo específico (añadir jugadores, programar amistosos, registrar estadísticas).
* **Jugador:** Miembro de un equipo con acceso de lectura para consultar el estado de su plantilla y los próximos rivales.
* **Administrador de Liga:** Rol global encargado de la organización, supervisión y calendario de las competiciones oficiales.

---

## 📖 2. Catálogo de Historias de Usuario (Product Backlog)

### 👤 Módulo 1: Gestión de Usuarios (USER)
Módulo encargado de la autenticación, seguridad y persistencia de las cuentas dentro del sistema.

#### [US-USER-01] Registro en la plataforma https://github.com/dlozaco/Volsta/issues/11
* **Descripción:** Como usuario, quiero registrarme en la plataforma para tener mi propia cuenta y acceder al sistema.
* **Prioridad:** Alta (Must Have)
* **Criterios de Aceptación:**
	* **Necesito** un formulario para registrarme con mi nombre de usuario y contraseña. Si el username es repetido, me da error de que ya existe.

#### [US-USER-02] Inicio de sesión https://github.com/dlozaco/Volsta/issues/12
* **Descripción:** Como usuario, quiero iniciar sesión para entrar de forma segura a mi perfil.
* **Prioridad:** Alta (Must Have)
* **Criterios de Aceptación:**
	* **Necesito** que si pongo mal mi nombre o contraseña me salga un aviso genérico

#### [US-USER-03] Edición de perfil https://github.com/dlozaco/Volsta/issues/13
* **Descripción:** Como usuario, quiero editar mi perfil para mantener mis datos personales actualizados.
* **Prioridad:** Media (Should Have)
* **Criterios de Aceptación:**
    * **Dado que** he iniciado sesión, **cuando** modifico mis datos (foto de perfil, nombre, teléfono), **entonces** el sistema valida los formatos y guarda los cambios mostrando una notificación de éxito.

#### [US-USER-04] Cierre de sesión https://github.com/dlozaco/Volsta/issues/14
* **Descripción:** Como usuario, quiero poder cerrar sesión para proteger la privacidad de mis datos cuando termine de usar la aplicación.
* **Prioridad:** Alta (Must Have)
* **Criterios de Aceptación:**
    * **Dado que** estoy dentro de la aplicación, **cuando** hago clic en "Cerrar Sesión", **entonces** el sistema invalida el token de sesión actual, me saca del sistema y me impide volver atrás usando el historial del navegador.

---

### 🛡️ Módulo 2: Gestión de Equipos (TEAM)
Módulo orientado a la creación, administración e inspección de los equipos deportivos de la plataforma.

#### [US-TEAM-01] Creación de equipo
* **Descripción:** Como mánager, quiero crear un nuevo equipo para poder inscribirlo en competiciones y gestionar a mis jugadores.
* **Prioridad:** Alta (Must Have)
* **Criterios de Aceptación:**
    * **Dado que** mi usuario tiene permisos de Mánager y no poseo un equipo activo, **cuando** completo los datos requeridos, **entonces** el sistema genera el equipo y me asigna automáticamente como el propietario/administrador de este.

#### [US-TEAM-02] Visualización de equipo propio
* **Descripción:** Como mánager, quiero ver los detalles de mi equipo para llevar un seguimiento de su estado y estadísticas.
* **Prioridad:** Alta (Must Have)

#### [US-TEAM-03] Edición de perfil de equipo
* **Descripción:** Como mánager, quiero editar el perfil de mi equipo (nombre, logo) para mantener nuestra identidad visual e información al día.
* **Prioridad:** Media (Should Have)

#### [US-TEAM-04] Exploración de equipos rivales (Mánager)
* **Descripción:** Como mánager, quiero ver los detalles de otro equipo para analizar contra qué rivales me puedo enfrentar.
* **Prioridad:** Media (Should Have)

#### [US-TEAM-05] Baja de equipo
* **Descripción:** Como mánager, quiero dar de baja a mi equipo para retirarlo del sistema si ya no vamos a competir.
* **Prioridad:** Baja (Could Have)

#### [US-TEAM-06] Consulta de equipo propio (Jugador)
* **Descripción:** Como jugador, quiero ver la información del equipo en el que estoy para conocer el estado de mi plantilla.
* **Prioridad:** Alta (Must Have)

#### [US-TEAM-07] Exploración de equipos rivales (Jugador)
* **Descripción:** Como jugador, quiero explorar los otros equipos para saber contra quiénes nos enfrentamos.
* **Prioridad:** Media (Should Have)

---

### 🏐 Módulo 3: Gestión de Jugadores (PLAYER)
Gestión del alta, modificación y bajas de los atletas que componen cada plantilla deportiva.

#### [US-PLAY-01] Alta de jugadores
* **Descripción:** Como mánager, quiero añadir nuevos jugadores para completar mi plantilla de cara a la temporada.
* **Prioridad:** Alta (Must Have)
* **Criterios de Aceptación:**
    * **Dado que** estoy en el panel de mi equipo, **cuando** introduzco los datos de un nuevo jugador, **entonces** el sistema lo vincula a mi plantilla siempre y cuando el dorsal no esté repetido en el mismo equipo.

#### [US-PLAY-02] Edición de jugador
* **Descripción:** Como mánager, quiero editar el perfil de mis jugadores para corregir o actualizar sus datos si es necesario.
* **Prioridad:** Media (Should Have)

#### [US-PLAY-03] Baja de jugador (Soft Delete)
* **Descripción:** Como mánager, quiero dar de baja (soft delete) a jugadores para liberar espacio en la plantilla sin perder su histórico de puntos.
* **Prioridad:** Alta (Must Have)
* **Criterios de Aceptación:**
    * **Dado que** un jugador ya no forma parte del equipo activo, **cuando** el mánager lo da de baja, **entonces** el sistema realiza un borrado lógico (*soft delete*); el jugador desaparece de la lista actual de la plantilla, pero su histórico estadístico se mantiene intacto para no alterar las métricas globales pasadas.

---

### 🏟️ Módulo 4: Gestión de Partidos (MATCH)
Organización de encuentros deportivos, divididos entre el calendario oficial de liga y los encuentros amistosos.

#### [US-MATC-01] Creación de partido oficial
* **Descripción:** Como administrador de liga, quiero crear nuevos partidos de liga para organizar el calendario oficial de la competición.
* **Prioridad:** Alta (Must Have)

#### [US-MATC-02] Reprogramación de partido oficial
* **Descripción:** Como administrador de liga, quiero editar partidos de liga no jugados para reprogramarlos si hay problemas de horario o clima.
* **Prioridad:** Alta (Must Have)

#### [US-MATC-03] Creación de partido amistoso
* **Descripción:** Como mánager, quiero crear nuevos partidos amistosos para preparar a mi equipo fuera de la competición oficial.
* **Prioridad:** Media (Should Have)

#### [US-MATC-04] Reprogramación de amistoso
* **Descripción:** Como mánager, quiero editar partidos amistosos no jugados para reajustarlos ante imprevistos o cambios de planes.
* **Prioridad:** Media (Should Have)

#### [US-MATC-05] Creación de notas de partido
* **Descripción:** Como mánager, quiero crear notas de mis jugadores de un partido para tener observaciones tácticas o de rendimiento.
* **Prioridad:** Media (Should Have)

#### [US-MATC-06] Eliminación de notas de partido
* **Descripción:** Como mánager, quiero borrar notas de mis jugadores de un partido para quitarlas si me he equivocado o es erróneo.
* **Prioridad:** Baja (Could Have)

---

### 📊 Módulo 5: Resultados y Estadísticas (STATS)
Visualización y control analítico del desempeño de los equipos y jugadores por sets.

#### [US-STAT-01] Consulta de sets de partido
* **Descripción:** Como usuario, quiero ver cómo ha quedado cada set de un partido para analizar la evolución y lo reñido que estuvo el encuentro.
* **Prioridad:** Alta (Must Have)

#### [US-STAT-02] Consulta de estadísticas individuales
* **Descripción:** Como mánager, quiero ver la participación estadística de los jugadores en un partido para evaluar su rendimiento individual (titularidades, puntos anotados, posición).
* **Prioridad:** Alta (Must Have)
* **Criterios de Aceptación:**
    * **Dado que** un partido ha concluido y se han registrado los datos, **cuando** accedo al apartado estadístico, **entonces** el sistema me muestra una tabla detallada con los sets jugados, puntos anotados, fallos y efectividad por cada jugador que participó.

---

### 📅 Módulo 6: Panel de Control (DASHBOARD)
Módulo centralizado para la visualización cronológica y rápida de eventos futuros.

#### [US-DASH-01] Calendario interactivo de mánager
* **Descripción:** Como mánager, quiero tener un calendario interactivo con los próximos partidos para llevar un control visual del tiempo restante antes de cada encuentro.
* **Prioridad:** Media (Should Have)
* **Criterios de Aceptación:**
    * **Dado que** estoy en el Dashboard como Mánager, **cuando** cargo la sección principal, **entonces** visualizo un componente de calendario que resalta los días con partidos agendados y muestra una cuenta atrás (*countdown*) con los días/horas restantes para el partido más cercano.