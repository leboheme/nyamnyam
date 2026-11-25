# Nyam Nyam — Prototipo

Fuente de verdad del **alcance inmediato, orden de implementación y criterios de prueba**. Las reglas pertenecen a [GAME_DESIGN.md](GAME_DESIGN.md); las ideas futuras, a [IDEAS.md](IDEAS.md). Las secciones de alcance describen lo autorizado; el estado implementado y validado se identifica expresamente más abajo (actualizado el 25/11/2026).

## Objetivo actual

La primera sala ya ha permitido explorar combate, recursos y una build pequeña. El siguiente experimento es **un área modular navegable sin enemigos**: comprobar colocación, conectores, transiciones y regreso antes de añadir desafíos o ampliar el catálogo.

## Prototipo 1 — alcance autorizado de diseño

Nyam, una sala artesanal, pocos enemigos y plataformas. Usar el flujo nuevo de rooms JSON, manteniendo el legacy disponible para comparar. Reutilizar assets y movimiento hacia focos existentes cuando sirvan a las reglas nuevas.

Implementar en incrementos jugables, revisando el código antes de concretar cada cambio:

1. **Combate y captura:** resistencia, captura, remate por contacto y fuga. Reglas en [P7.A](GAME_DESIGN.md#p7a--ciclo-básico-de-combate); propiedades compartidas en P4 y P8.
La interacción de burbujas se implementó tras captura/liberación y antes de resistencia/Power, mediante [su tarjeta específica](https://trello.com/c/OiN62Nqe).

2. **Salivación:** reserva, consumo, regeneración y representación mínima que permita probar el recurso. Reglas en [P7.B](GAME_DESIGN.md#p7b--barra-de-salivación-y-comida-común).
3. **Drops:** evaluación tras derrotar y frutas/verduras recogibles. Funciones en P7.B y [P9.A](GAME_DESIGN.md#p9a--comida-bebidas-y-economía).
4. **Supervivencia:** dientes normales y caries, daño y derrota. Reglas en [P7.C](GAME_DESIGN.md#p7c--supervivencia-cuatro-dientes).

Los valores necesarios para jugar serán provisionales y ajustables. No convierten en definitivas las probabilidades o duraciones pendientes del diseño.

### Criterios de prueba

- Un enemigo recibe varios impactos según su resistencia y el poder del ataque; reducirla no lo derrota por sí solo.
- Capturar permite rematar por contacto o dejar que expire; la fuga cumple la regla de reinicio definida en P7.A.
- La captura muestra al enemigo y permite distinguirlo de una burbuja vacía; no reutilizar el caramelo como significado de enemigo transformado.
- Rematar resuelve al enemigo y evalúa su drop una sola vez; no aparecen recompensas simplemente por capturar o escapar.
- Se puede agotar saliva, regenerarla y recargarla con ambas familias de comida, observando la diferencia definida en P7.B.
- El daño y la reposición permiten observar la diferencia entre diente normal y caries.
- Puede recorrerse y resolverse la sala con el movimiento base. Probar especialmente contacto lateral, contacto desde arriba, rebote y remate, sin decidir prioridades accidentalmente.
- Probar manualmente el combate en HTML/móvil con una carga representativa, además de desktop. Observar fluidez, respuesta táctil y acumulación de burbujas.

### Estado implementado y validado

El usuario ha validado manualmente los incrementos de combate, interacción de burbujas, recursos, drops y supervivencia en la sala JSON. Esto no equivale a una batería exhaustiva de dispositivos o sesiones largas. La gestión viva de tareas sigue en el board configurado en `docs/tasks/tasks.json`.

- **Combate:** resistencia/Power por impacto, feedback rojo breve, captura de la instancia Enemy, fuga con resistencia restaurada y enfado, remate único. Los proyectiles persistentes registran impactos para no repetir daño al mismo enemigo por solapamiento.
- **Burbujas:** entidad propia, vacía o con cautivo; empuje/arrastre limitado, presión e impacto, rebote con salto mantenido al caer, agrupación sin fusión y cadenas por contacto bloqueadas por paredes. Reglas exactas en P7.A.
- **Limpieza:** solo enemigos libres/cautivos bloquean resolver el combate. La sala sigue jugable, sin mensaje, WIN, progreso automático ni conversión de aire en verduras. La apertura por tipo de sala sigue pendiente.
- **Saliva:** pago único por ataque emitido, regeneración continua y texto provisional. No hay devoluciones por fallo ni recarga especial por limpiar.
- **Recompensas:** perfiles por enemigo con fallback común; fruta/verdura y posibilidad adicional de diente al rematar. Cuerpo visual separado, drops con parábola/giro/rebote exterior que se materializan al aterrizar y entonces se recogen por contacto. Comida y dientes no añaden score ni vidas extra al recogerse; el score de remate previo aún usa GameStatus.
- **Física de items JSON:** comida y pickups `FREE` caen continuamente y se separan entre sí; los ubicados en el JSON también caen desde su posición inicial. Al tocar al protagonista, un item aplicable se recoge; uno sin efecto permanece y se desplaza a velocidad limitada mientras el protagonista lo atraviesa. Enemigos ignoran estos items. `ANCHORED` y `BOMB` existen como capas lógicas, pero aún no hay contenido jugable JSON de pedestales/cofres o refrescos.
- **Supervivencia:** normales/caries por mitades, tres contenedores iniciales, prioridad de caries, daño con invulnerabilidad posterior y derrota con presentación antes de Game over. Sin inmunidad de entrada, consumo de vidas persistentes ni respawn JSON. Reglas exactas en P7.C.
- **HUD provisional:** cuadrados de salud arriba izquierda y saliva debajo, en el viewport de UI existente. Normal blanco, caries beige verdoso y contenedor vacío oscuro, todos con borde blanco. En JSON se ocultan los contadores legacy de nivel/vidas/score y el mensaje de esperar vidas; TMX conserva su presentación.

### Valores provisionales y balance

Editar [`PrototypeBalance.java`](../core/src/com/aramirezochoa/nyamnyam/screen/game/run/PrototypeBalance.java) y recompilar para ajustar recursos, combate, presión/contactos, vuelos y tiempos. Las tolerancias técnicas de colisión están comentadas aparte. No hay panel visual ni recarga de configuración en caliente.

`RunState` conserva modificadores tipados de Power, Cadence y Salivation como fracciones sumadas respecto a la base (`base × (1 + suma)`). Las bases siguen en sus fuentes existentes: `ATTACK_POWER`, `Constant.MAIN_AVATAR_FIRE_TIME_LAPSE` (Cadence se expresa como ataques/s) y `SALIVATION_REGENERATION_PER_SECOND`. La consulta Desktop `stats` muestra base, modificador y resultado. Los modificadores persisten al recargar sala/F11 y `restart` los limpia.

`spawn 5/6/7` crea mejoras provisionales de Power/Cadence/Salivation. Se aplican una vez al recoger, se acumulan sobre la base y no curan ni recargan la reserva actual. Son solo de debug, no forman parte de drops de enemigo. El usuario ha validado de forma exploratoria la primera build en Desktop; las pruebas largas y en HTML/móvil siguen pendientes.

Burbuja Gigante fue validada manualmente por el usuario: `spawn 8` la lanza. Es binaria en `RunState`: solo afecta a disparos de Nyam creados después de recogerla y a las burbujas que esos disparos generan, vacías o cautivas. Una segunda copia no aporta efecto y permanece en la sala. El factor provisional es `GIANT_BUBBLE_SCALE` en `PrototypeBalance` (actualmente 1,5: 32 → 48 px). Si el disparo no cabe al emitirlo, estalla en el punto de emisión; en pasillos más estrechos que su tamaño no puede avanzar. La mutación persiste con `stage 7`/F11 y se limpia con `restart`.

Captura Prolongada fue validada de forma exploratoria por el usuario: `spawn 9` la lanza. Una burbuja vacía puede capturar al tocar a un enemigo al que le queda como máximo un impacto con el Power actual. La edad de la burbuja continúa; el límite se fija al capturar y la mutación añade cinco segundos al límite base, sin alargar burbujas que sigan vacías. Una captura anterior conserva el límite elegido al iniciarse. La segunda copia no aporta efecto y permanece en la sala. También persiste con `stage 7`/F11 y se limpia con `restart`.

Primera build del prototipo 2 implementada: tres modificadores numéricos (Power, Cadence y Salivation) y dos mutaciones binarias (Gigante y Captura Prolongada), todos obtenibles solo por `spawn` en Desktop. Siguen fuera de esta build las otras cuatro stats principales del diseño, los demás efectos del banco P9.C, el catálogo de mutaciones, drops de mejoras, pools, tiendas, inventario, arte definitivo y la navegación roguelike. La sesión completa de juego y la comprobación HTML/móvil aún dependen de prueba manual; en HTML/móvil solo puede probarse el juego base porque no existe acceso a estos pickups debug.

[`EnemyDropConfig.java`](../core/src/com/aramirezochoa/nyamnyam/screen/game/run/EnemyDropConfig.java) concentra los perfiles por EnemyType. Actualmente la tabla de excepciones está vacía: todas las especies usan el perfil común. No se han implementado modificadores de dificultad o Luck.

| Parámetro | Valor actual de prueba |
| --- | --- |
| Resistencia enemiga / Power de ataque | 3 / 1: tres impactos para capturar |
| Pickup debug Power / Cadence / Salivation | +50 % / +50 % / +100 % sobre base; provisionales y sumables |
| Mutación debug Burbuja Gigante | ×1,5 tamaño de disparo y burbuja, una sola vez |
| Mutación debug Captura Prolongada | +5 s al límite de vida al capturar, una sola vez |
| Feedback de impacto | Rojo durante 0,12 s, sin interrumpir movimiento |
| Vida de burbuja vacía/cautiva base | 5 + 3 + 2 s de fases visuales; 10 s desde el nacimiento, también tras capturar |
| Saliva inicial/máxima, coste y regeneración | 6; 1 por ataque; 0,75/s incluso atacando |
| Recarga de fruta / verdura | +2 / +1, limitada a capacidad |
| Piezas de comida por remate | 25 % ninguna, 50 % una, 25 % dos |
| Familia de cada pieza | 50 % fruta / 50 % verdura; variantes equiprobables |
| Pickup de diente adicional | 20 % por remate, independiente de la comida |
| Tipo y tamaño del diente sorteado | 50 % normal/caries y 50 % medio/entero |
| Salud inicial / capacidad | 3 contenedores normales llenos / 8 espacios totales |
| Daño básico / protección posterior | 0,5 diente / 1 s de invulnerabilidad |
| Espera antes de Game over | 1,2 s, con animación DEAD sin respawn |
| Drops lanzados | Vuelo solo visual; comida y pickups físicos aparecen al aterrizar |
| Cuerpo derrotado | 1,2 s de vuelo, fade en los últimos 0,25 s |

Los ajustes de presión, velocidades, gravedad, restitución y rotación viven en el mismo archivo, con unidades y comentarios. No duplicarlos en otras fuentes de balance. La cadencia/movimiento heredados que aún se comparten con TMX siguen fuera de esta configuración.

### Cargar sala y reiniciar run

Salud y saliva viven en RunState, fuera de la geometría de sala y de la persistencia legacy. Cambiar o recargar sala conserva ambos recursos, incluidas las caries; estar a cero no permite revivir mediante un cambio de sala. El contenido de la sala sí vuelve a cargarse: todavía no hay navegación ni persistencia de salas visitadas.

Una nueva run o el comando `restart` restauran tres contenedores llenos, eliminan caries, llenan saliva y recargan la sala actual. `restart` no vuelve al nivel 1 ni reinicia las vidas persistentes legacy. Pausar congela gameplay, regeneración y espera de muerte.

### Consola de debug

La consola existente se abre con la tecla grave (`) y está habilitada solo en Desktop; no se ha ampliado su disponibilidad a HTML/móvil. `help` lista los comandos.

| Comando | Efecto |
| --- | --- |
| `stage 7` | Carga/recarga la sala de prueba conservando salud, caries y saliva |
| `restart` | Reinicia la sala actual con recursos iniciales de la run |
| `killall` | Resuelve enemigos libres y cautivos con sus cuerpos/drops; no cambia de sala ni reinicia recursos. Repetir devuelve cero |
| `spawn list` | Lista los IDs numéricos disponibles |
| `spawn 1` / `spawn 2` | Lanza un pickup normal entero / medio cerca de Nyam |
| `spawn 3` / `spawn 4` | Lanza una caries entera / media cerca de Nyam |
| `spawn 5` | Lanza mejora Power (+50 % de Power base) |
| `spawn 6` | Lanza mejora Cadence (+50 % de ataques/s base) |
| `spawn 7` | Lanza mejora Salivation (+100 % de regeneración base) |
| `spawn 8` | Lanza Burbuja Gigante (×1,5 en disparos y burbujas nuevos) |
| `spawn 9` | Lanza Captura Prolongada (+5 s al capturar, incluso una vacía ya existente) |
| `stats` | Muestra base, modificador y resultado de Power, Cadence y Salivation (JSON) |
| `clear` | Limpia la salida de consola |

`killall` y `spawn` requieren JSON. F11 alterna JSON/TMX cuando existe la room JSON; conserva los recursos de RunState al volver y recarga el contenido. Los IDs se declaran explícitamente en PickupType, no se derivan del orden del enum ni de los nombres; no reutilizar IDs retirados.

### Comprobación de regresión manual

- Capturar, dejar escapar y rematar; comprobar tres impactos base, restauración al escapar y resolución única en cadenas.
- Empujar lateralmente y desde abajo, apretar contra terreno y caer con/sin salto. Llegar a una pared sin presión del protagonista no revienta la burbuja.
- Gastar saliva, regenerar y recoger fruta/verdura; no superar capacidad ni cobrar por frame sostenido.
- Recibir medio diente de daño; rellenar normales y añadir medias/enteras caries. A capacidad o vida llena, los pickups sin efecto permanecen. El exceso parcial se descarta al recoger.
- Perder la última mitad: controles desactivados, animación de muerte y espera antes de Game over; sin respawn. `stage 7` no cura ni revive; `restart` sí restaura los recursos.
- Comprobar que cuerpos y drops atraviesan la geometría interior durante el vuelo, que los drops no se recogen ni bloquean antes de aterrizar y que al tocar suelo aparecen comida/pickups físicos recogibles. Probar pausa, reinicio, redimensionado y controles táctiles. Repetir la comparación TMX con F11 y una sesión HTML/móvil representativa.
- Física de items: dejar caer la manzana situada en el JSON y varios pickups de `spawn`; comprobar gravedad, apoyo en plataformas, separación entre `FREE` y que un pickup sin efecto se desplaza sin bloquear el paso del protagonista. Los enemigos no deben moverlos. No esperar pedestales/cofres ni refrescos jugables en esta sala.
- Stats: `restart` y ejecutar `stats` para anotar bases/modificadores. Probar `spawn 5`, recoger y observar +50 % Power (el enemigo básico pasa de tres a dos impactos); hacer `restart` antes de aislar `spawn 6` (+50 %, intervalo de 0,5 s) y `spawn 7` (+100 %, 1,5/s sin rellenar la reserva al recoger). Recoger una segunda copia de cada mejora y confirmar la suma. `stage 7`/F11 conservan la build; `restart` la borra. Confirmar que los pickups no alteran dientes/score y que `killall` no los suelta automáticamente.
- Mutaciones: tras `restart`, capturar sin mejora y observar expiración alrededor del segundo 10. Recoger `spawn 9` y capturar de nuevo: expiración alrededor del segundo 15 desde que nació la burbuja. Una vacía sigue expirando en 10 s. Debilitar un enemigo hasta dejarlo a un golpe, acercarle una burbuja vacía y comprobar captura por contacto sin reiniciar su edad. Probar una captura previa a recoger la mutación, una vacía previa que capture después, doble recogida, composición con `spawn 8` y persistencia con `stage 7`/F11; `restart` limpia ambas.

En estos incrementos se ejecutaron `git diff --check`, compilación Java 17 y build completo cuando cambiaron assets/i18n. No se ejecutó la aplicación ni se crearon/modificaron tests. La validación de juego fue manual por el usuario y no sustituye una sesión larga ni una prueba representativa HTML/móvil.

### Decisiones que siguen abiertas

Balance definitivo, escala de daño y perfiles por dificultad/contexto, arte/HUD definitivos y acceso según tipo de sala. Los criterios ya acordados de contacto, cadenas, pickups y derrota no siguen pendientes: se recogen en GAME_DESIGN.md. No se incorpora contenido futuro solo porque exista un punto de configuración.

### Fuera del alcance de los prototipos de combate y build

Bosses nuevos, tiendas, economía, progresión entre runs, catálogo completo de objetos, activas, engullido/escupitajo y nuevos ataques de Gluty/Tfly. La generación de un área y las transiciones entre salas se prueban por separado a continuación. No migrar automáticamente reglas legacy de score, vidas extra, mascotas o spawns temporizados.

## Siguiente experimento — área navegable sin combate

Usar el flujo Tiled → JSON como base de autoría. Preparar unas pocas plantillas sencillas, sin arte nuevo, que permitan probar huellas 1×1, 1×2, 2×1, 2×2 y L. Su tamaño físico en tiles es libre: la huella modular solo describe ocupación en el mapa. Definir y validar conectores disponibles de la plantilla antes de generar el área. Las reglas de ocupación, emparejamiento y dimensiones de acceso están en [P6.C](GAME_DESIGN.md#p6c--mapa-modular-y-salas-artesanales).

Generar con semilla un área conectada que incluya una rama y un callejón sin salida; no hardcodear un grafo único. Abrir permanentemente las conexiones elegidas: en esta prueba no hay enemigos, bloqueo por combate ni resolución de sala como condición para navegar. Al cruzar en N/S/E/O, cargar solo la sala de destino, aparecer en un punto seguro y poder volver por el mismo enlace. La sala visitada conserva su identidad y los cambios relevantes dentro de la run. Mostrar una vista de depuración del mapa modular con huellas, pertenencia a salas, conexiones y sala actual; no es el HUD final.

### Criterios de prueba de navegación

- Ningún módulo pertenece a dos salas; las huellas de varios módulos se tratan como una sala única. Una L puede coexistir con otra sala en su hueco.
- No se crean conexiones hacia huecos, entre módulos de la misma sala ni por simple vecindad sin conectores compatibles. Los conectores disponibles que no se activen quedan cerrados físicamente.
- Atravesar accesos laterales y verticales, regresar y recorrer una rama hasta su extremo conserva enlaces, orientación y posición de cada sala en el mapa. El movimiento básico de Nyam basta para llegar y pasar por los accesos.
- Los puntos de llegada no incrustan al personaje en material ni disparan un retorno inmediato. La cámara sigue la sala activa sin cambiar la escala visual de referencia.
- Una misma semilla reproduce el mismo mapa. Una semilla imposible con el catálogo actual produce un fallo o reintento acotado y visible, sin colgar el juego. Probar varias seeds y múltiples transiciones.

**Fuera de esta prueba:** enemigos, loot, cierre/apertura por combate, bosses, salas especiales, tiendas, secretos funcionales, economía, plataformas generadas tile a tile, arte final y nuevos personajes. Conservar intacto el prototipo JSON de combate y el flujo TMX legacy para comparar. La sesión larga y la comprobación HTML/móvil del combate siguen pendientes.

## Secuencia posterior acordada

No amplía por sí sola el trabajo de navegación:

- **Prototipo 2:** primera build de tres stats y dos mutaciones implementada y probada de forma exploratoria en Desktop. El resto del banco de efectos y del catálogo permanece solo en P9.C–D del maestro, sin implementación ni orden autorizado por esta sección.
- **Prototipo 3:** activas recargables, colmillo e implante y recompensas. Sus reglas están en P7.C y P9.E.
- Incorporar Gluty y Tfly sobre el sistema compartido después de validar la base; no se fija aquí su orden exacto.

La generación y la duración del tour se contrastarán midiendo primero una sala y una primera área, según P6.
