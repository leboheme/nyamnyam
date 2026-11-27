# Nyam Nyam — Prototipo

Fuente de verdad del **alcance inmediato y la validación**. Las reglas jugables pertenecen a [GAME_DESIGN.md](GAME_DESIGN.md); el contrato del código implementado, a [REMAKE_DIRECTION.md](REMAKE_DIRECTION.md). Actualizado el 27/11/2026 tras decidir la iteración de combate arcade.

## Objetivo actual — recuperar el combate arcade de Nyam

Antes de ampliar el área modular, simplificar la sala JSON de combate: ataque válido, captura inmediata, manipulación de burbuja, remate y drops. El juego debe dar protagonismo al movimiento y las plataformas. Esta iteración no amplía enemigos, personajes, economía, activas ni contenido de salas. Las reglas están en [P7.A–B](GAME_DESIGN.md#p7a--ciclo-básico-de-combate) y [P8](GAME_DESIGN.md#p8--personajes-y-uso-compartido-de-burbujas).

Orden de trabajo propuesto para incrementos comprobables:

1. Separar con claridad el proyectil de ataque y la burbuja física. Recuperar el arte legacy para el proyectil, mantener su conversión a burbuja vacía al fallar o topar con terreno, y eliminar la captura de enemigos por contacto pasivo con cualquier burbuja existente.
2. Capturar a un enemigo común con un solo impacto válido. Retirar resistencia/Power, desgaste previo, feedback exclusivo de desgaste y restauración de resistencia al escapar. Preservar la instancia del cautivo, remate, expiración con fuga ENFADADO y evaluación única de drops.
3. Retirar pickups y mecanismos de la build experimental: Power, Cadence, Salivation, Burbuja Gigante y Captura Prolongada. Actualizar consola y ayudas; dejar libres pero reservados los IDs 5–9.
4. Retirar por completo la reserva, coste, regeneración y HUD de salivación del flujo JSON. Mantener el ataque sostenido y Cadence base. Hacer que frutas y verduras siempre se coman al contacto, sin efecto, conservando drops, vuelo, aterrizaje, física, arte e identificadores; no alterar dientes ni salud.
5. Eliminar todo score del flujo nuevo: generación, estado, HUD, mensajes y comandos relacionados. Comprobar que ni remates ni recogidas conceden puntos o vidas extra. Conservar compatibilidad TMX mientras siga disponible para comparación; su score es legacy, no una regla del remake.
6. Verificar la regresión del combate y la navegación en Desktop y una sesión HTML/móvil representativa; actualizar el estado implementado y validado de la documentación con lo efectivamente comprobado.

La navegación modular, footprints, máscaras, conectores y minimapa quedan fuera de este cambio. Los enemigos siguen bloqueando la resolución del combate mientras estén libres o cautivos; abrir/cerrar puertas por combate pertenece a una iteración posterior.

### Estado implementado de esta iteración

El usuario validó exploratoriamente la sala JSON con burbujas, interacción, drops, dientes y una build de debug. Las cinco primeras tarjetas están implementadas: el ataque usa `BEAM`, distinto de las burbujas físicas `AIR_*`; el contacto con burbujas existentes no captura; un impacto captura de inmediato; ya no hay resistencia, Power, pickups de build, mutaciones, salivación ni score JSON. Los IDs debug 5–9 están reservados. La cadencia base se conserva y frutas/verduras se recogen sin efecto. El build completo pasó tras la cuarta tarjeta y `core:compileJava` pasó tras la quinta; falta la comprobación manual de esta iteración. La navegación modular de depuración con área generada sigue implementada. El detalle técnico vivo se registra en [REMAKE_DIRECTION.md](REMAKE_DIRECTION.md#runtime-json-implementado).

### Criterios de aceptación manual

- Un impacto válido de Nyam captura a un enemigo común; no quedan resistencia, Power ni feedback de desgaste.
- El proyectil usa el aspecto legacy y sigue siendo un concepto distinto de la burbuja física. Si falla por distancia o terreno, se convierte en burbuja vacía. Ninguna burbuja existente captura enemigos por simple contacto.
- Mantener ataque dispara según Cadence base, sin gastar recursos. No quedan reserva, regeneración ni HUD de salivación.
- Empuje lateral o desde abajo, presión, rebote al mantener salto, agrupaciones y cadenas bloqueadas por paredes siguen funcionando. Expirar no inicia cadenas.
- Expirar una captura libera al mismo enemigo ENFADADO, sin drops. Rematarlo resuelve una vez el cautivo y sortea los drops una sola vez.
- Frutas y verduras vuelan, aterrizan, se recogen y desaparecen siempre, sin curar, recargar, alterar stats ni dar puntos. Dientes, daño, invulnerabilidad y game over funcionan como antes.
- No hay score de ningún tipo en el flujo nuevo, incluidos remates y recogidas. Los IDs 5–9 no conceden las antiguas mejoras ni se reutilizan. Ayudas, spawn list y stats no anuncian efectos retirados.
- Salas y puertas modulares, máscaras de exclusión y minimapa siguen funcionando.
- Comprobar en Desktop y con una carga representativa en HTML/móvil. Registrar qué se probó realmente; la aplicación se verifica manualmente por el usuario.

### Valores y herramientas que se conservan para la prueba

Los tiempos de burbuja, física de contacto, vuelo de drops y salud siguen siendo provisionales en [PrototypeBalance.java](../core/src/com/aramirezochoa/nyamnyam/screen/game/run/PrototypeBalance.java). El perfil de drops permanece en [EnemyDropConfig.java](../core/src/com/aramirezochoa/nyamnyam/screen/game/run/EnemyDropConfig.java). No crear balance nuevo para sustituir Power o salivación.

En Desktop, stage 7 recarga la sala conservando la salud; restart la reinicia con la salud inicial; killall resuelve enemigos libres y cautivos; spawn 1–4 produce los dientes de debug. `stats` muestra solo Cadence base. Los IDs 5–9 quedan retirados y reservados. La consola no es una interfaz HTML/móvil. F11 permite comparar el flujo JSON con el TMX legacy mientras ambos existan.

## Experimento modular anterior — navegación sin combate

La prueba de navegación ya tiene plantillas, generación con semilla, conectores, transiciones y minimapa de depuración implementados; la validación manual completa sigue pendiente. Se pausa su ampliación hasta validar el combate arcade. Las reglas de diseño están en [P6.C](GAME_DESIGN.md#p6c--mapa-modular-y-salas-artesanales); el contrato implementado, en [REMAKE_DIRECTION.md](REMAKE_DIRECTION.md#autoría-modular--contrato-inicial-de-huellas-y-conectores).

La validación pendiente consiste en recorrer varias semillas con una rama y un callejón sin salida, cruzar N/S/E/O, regresar y comprobar puntos seguros de entrada. En esta prueba no hay enemigos ni bloqueo por combate. El mapa de depuración muestra huellas, conexiones y sala actual; no es el HUD final.

### Criterios de prueba de navegación

- Ningún módulo pertenece a dos salas; las huellas de varios módulos se tratan como una sala única. Una L puede coexistir con otra sala en su hueco.
- No se crean conexiones hacia huecos, entre módulos de la misma sala ni por simple vecindad sin conectores compatibles. Los conectores disponibles que no se activen quedan cerrados físicamente.
- Atravesar accesos laterales y verticales, regresar y recorrer una rama hasta su extremo conserva enlaces, orientación y posición de cada sala en el mapa. El movimiento básico de Nyam basta para llegar y pasar por los accesos.
- Los puntos de llegada no incrustan al personaje en material ni disparan un retorno inmediato. La cámara sigue la sala activa sin cambiar la escala visual de referencia.
- Una misma semilla reproduce el mismo mapa. Una semilla imposible con el catálogo actual produce un fallo o reintento acotado y visible, sin colgar el juego. Probar varias seeds y múltiples transiciones.

**Fuera de esta prueba:** enemigos, loot, cierre/apertura por combate, bosses, salas especiales, tiendas, secretos funcionales, economía, plataformas generadas tile a tile, arte final y nuevos personajes. La sesión larga y la comprobación HTML/móvil del combate siguen pendientes.

## Después de validar el combate

La primera integración usa el catálogo `world_01`, con diez adaptaciones de los originales 1–5 y áreas generadas que seleccionan parte del catálogo. La organización, compilación y comandos pertenecen al [contrato de catálogos](REMAKE_DIRECTION.md#catálogos-de-mundo-y-compilación). Validar primero las salas y sus accesos; después cerrar puertas durante el combate y abrirlas al resolverlo. Los drops ya persisten entre visitas; el combate parcial aún se reconstruye al regresar. La navegación con combate y esta selección de contenido requieren prueba manual. Las activas, otros personajes y el catálogo de mutaciones no forman parte de esta secuencia inmediata. La duración del tour se contrastará midiendo una sala y una primera área, según P6.
