# Nyam Nyam — Game Design

> Fuente de verdad del diseño del remake. Reconciliado el 23/11/2026 con el boceto P1–P9, revisión técnica, y las aclaraciones posteriores de esta sesión. No describe necesariamente lo ya implementado.

## Cómo leer este documento

- **Acordado:** dirección adoptada, aunque falten valores o implementación. El texto normativo y las tablas de reglas pertenecen a esta categoría salvo indicación contraria.
- **Hipótesis / propuesta / candidato:** opción para validar; no se convierte en requisito por aparecer aquí.
- **Pendiente:** decisión todavía abierta. No debe resolverse silenciosamente al implementar.

El alcance inmediato, su secuencia y los criterios de prueba viven únicamente en [PROTOTYPE.md](PROTOTYPE.md). Las expansiones sin compromiso viven en [IDEAS.md](IDEAS.md). La dirección técnica está en [REMAKE_DIRECTION.md](REMAKE_DIRECTION.md); el comportamiento anterior, en [LEGACY_GAME_DESIGN.md](LEGACY_GAME_DESIGN.md).

La escala visual, anatomía compartida y convenciones de producción de assets se definen únicamente en [ART_GUIDE.md](ART_GUIDE.md).

## Visión general

Un glotón se apunta a un tour gastronómico por mundos imposibles. Explora, captura y remata criaturas, recoge comida para recargar la salivación y obtiene platos que transforman su build. Objetivo de ritmo: tours estándar de unos 15–20 minutos, ampliables mediante rutas extra.

## P1 · Premisa global

«Un glotón se apunta a un misterioso tour gastronómico por mundos imposibles, dispuesto a probarlo todo sin imaginar los disparatados peligros que le esperan».

Se elige un único personaje por run; la mini-intro del folleto funciona con cualquiera.

La motivación es absurda y sencilla: probar comida extraordinaria, no salvar el mundo.

Agencia, autenticidad del tour y desenlaces quedan abiertos para historias futuras.

## P2 · Mundos y transiciones

Mundos temáticos de libertad caricaturesca: calles arcade, cavernas, bosques, mundos tecnológicos, etc.

Guía turístico recurrente: criatura con gorro/banderilla/paraguas de la agencia. Espera profesionalmente a sus clientes.

Entre mundos, el guía engulle al personaje; el siguiente destino puede comenzar con un aterrizaje cómicamente desagradable.

Entre áreas del mismo mundo, puede abrir puertas, persianas, ascensores u otros pasos; no se fija un tipo único.

Posibilidades futuras relacionadas: [IDEAS.md](IDEAS.md).

Mecánicas de navegación específicas por mundo son posibles; el wrap-around vertical de Bubble Bobble queda como opción temática, no regla universal.

## P3 · Bosses y manjares

Dirección preferida: safari culinario. Los bosses pueden ser criaturas extraordinarias o guardianes de un manjar (por ejemplo, una dragona que protege un huevo).

El boss entrega una recompensa gastronómica que afecta a la build. No hay crafting.

Los chefs funcionan mejor como locales/encuentros especiales, no como requisito para todos los bosses.

Los nombres, criaturas y manjares absurdos se decidirán por mundo.

| Pool | Identidad |
| --- | --- |
| Shop | Comida comercial, snacks, productos corrientes |
| Boss | Manjares exóticos, legendarios o custodiados |
| Chef | Platos de autor y alta cocina experimental |
| Contrabando | Comida sospechosa y mercancía arriesgada |

Las pools solo definen procedencia. No exigen sistemas de objetos separados; un objeto puede pertenecer a más de una.

## P4 · Combate arcade

### P4.A · Núcleo común

El combate conserva una base arcade de plataformas, captura y remate. Las reglas y transiciones se definen únicamente en [P7.A](#p7a--ciclo-básico-de-combate); el uso común de burbujas y las diferencias por personaje, en [P8](#p8--personajes-y-uso-compartido-de-burbujas).

Engullir y escupir no son capacidades universales. La posible expansión está en [IDEAS.md](IDEAS.md).

## P5 · Tiendas y eventos

Primero, salas independientes que ofrecen objetos. Las situaciones cómicas permiten expandir narrativamente más adelante sin aumentar el alcance inicial.

| Escenario | Función inicial / posibilidades |
| --- | --- |
| Tienda | Comida rápida, vending o food truck acorde al mundo; opción común |
| Restaurante — chef | Platos especiales de la pool Chef; detalles de acceso y descuentos pendientes. |
| Restaurante — contenedores | Sobras peligrosas: posible coste de vida y objetos potentes; excluye entrar al chef |
| Vendedor clandestino | Gabardina, ítems de diversas pools; coste en salud o mucho dinero |
| Repartidor interdimensional | Pedido desde teléfono/cabina; entrega idealmente en la sala del teléfono |

Posibilidades futuras relacionadas: [IDEAS.md](IDEAS.md).

## P6 · Tours, áreas y generación

### P6.A · Estructura y ritmo

Hipótesis NO comprometida: 3 mundos

Run = tour; mundo = destino temático; área = grupo de salas generadas; sala = unidad de exploración/combate/evento.

Objetivo aspiracional de tour estándar: 15–20 min, aún no contrastado con contenido ni ritmo real.

Builds OP pueden ser rapidísimas; una build mediocre puede ser difícil, pero no debería forzar runs normales de ~40 min por falta de poder.

Recompensas y exploración no deben depender exclusivamente de matar bosses.

Antes de fijar cantidad de mundos/áreas, medir el tiempo real de una sala y una primera área. Aún faltan mundos, enemigos rediseñados, bosses, tiendas y generación; el ritmo no se puede estimar de forma fiable solo con la estructura teórica.

### P6.B · Progresión

Dentro de la run: platos, caramelos, stats, activas y mutaciones. Los drops abundantes de fruta/verdura recargan salivación. Un objeto puede corregir debilidades de la build.

Entre runs: candidatos a desbloqueo son personajes, objetos, destinos, eventos y rutas. No se ha decidido progresión permanente de stats.

Final principal y desenlaces alternativos se trabajarán al desarrollar historias/lore.

### P6.C · Mapa: candidato para prototipar

Generador con camino hacia boss, ramas opcionales y eventuales reconexiones; sin cantidad de salas fijada.

Salas artesanales de plataformas 2D, dimensiones variadas y formas L/O. Cámara de referencia 1×2 celdas, siguiendo al protagonista en salas mayores.

Cuadrícula mínima y puertas configurables: los accesos activos de cada sala deben poder alcanzarse con el moveset base de todos los personajes.

Modelo A favorecido para probar: puertas laterales a distintas alturas. Salas verticales pueden conectar plantas sin puertas de techo/suelo. Modelo B con conexiones verticales explícitas permanece como alternativa.

La experiencia de regresar del boss, el minimapa y la taxonomía de conectores se resolverán con prototipos, no sobre el papel.

## P7 · Combate, recursos y supervivencia

### P7.A · Ciclo básico de combate

Burbujas → reducir resistencia → ATURDIDO/captura → remate por contacto → DERROTADO → evaluación del drop.

Vocabulario de diseño compartido: NORMAL, ENFADADO, ATURDIDO y DERROTADO. Timeout es un evento, no un estado. Son estados conceptuales; no obligan a una arquitectura exclusiva: ENFADADO puede coexistir con otros comportamientos del enemigo.

Transiciones de referencia: NORMAL → ENFADADO; NORMAL/ENFADADO → ATURDIDO; ATURDIDO → ENFADADO al escapar por timeout; ATURDIDO → DERROTADO por remate válido.

Se mantiene la utilidad de las burbujas como apoyo en plataformas; las burbujas ambientales dan acceso a todos los personajes.

Prototipo inicial: cuando una captura expira, el enemigo recupera su resistencia inicial y pasa a ENFADADO. Esta es una regla de prueba, no un comportamiento universal definitivo. En P10 se podrán definir reacciones diferentes por especie, incluyendo enemigos que se debilitan, se fortalecen o huyen.

El enemigo aturdido queda capturado dentro de una burbuja, sin necesitar un estado conceptual adicional. Las burbujas, incluso al reducir resistencia, NO derrotan por sí solas. La derrota exige un remate válido.

Reglas de interacción acordadas y probadas en el prototipo:

- Una burbuja puede estar vacía o contener un enemigo; ambas comparten movimiento, contacto y estallido. Las capturas no se representan como caramelos.
- El contacto lateral o desde abajo empuja la burbuja y su grupo. La presión sostenida puede romperla; apretarla contra terreno acelera el estallido. Un impacto suficientemente fuerte puede romperla directamente.
- Al caer sobre una burbuja, mantener salto permite rebotar conservándola; sin salto se revienta.
- Las burbujas se amontonan manteniendo entidades y capturas independientes, sin fusionarse.
- Un estallido se propaga entre burbujas en contacto, con una tolerancia técnica mínima; las paredes bloquean la conexión. La propagación no depende de qué haya provocado el estallido. Actualmente el protagonista es quien inicia los estallidos.
- Tocar una pared por sí solo frena la burbuja, sin romperla. Disparar sin espacio contra una pared puede reventarla por compresión contra el propio protagonista.
- Expirar es distinto de estallar: elimina la burbuja, libera al cautivo si lo tiene y no inicia cadenas ni remata otras capturas.

La sala queda con el combate resuelto cuando no quedan enemigos libres ni cautivos. La comida, los pickups y las burbujas vacías pendientes no lo bloquean. Resolver combate no decide por sí mismo la apertura de salidas: dependerá de las reglas del tipo de sala. En la sala de prueba se sigue jugando, sin mensaje, WIN, avance automático ni premio por convertir aire en verduras.

### P7.B · Barra de salivación y comida común

Generar burbujas consume salivación. Existe una regeneración natural basal; Nyam destaca por su regeneración elevada.

Las frutas y verduras aparecen como drops abundantes (referencia: 0–2 por enemigo) y se recogen por contacto para recargar la barra.

Las frutas recargan MÁS que las verduras. Las variantes visuales dentro de cada familia tienen el mismo efecto; cantidades pendientes de balance.

En el prototipo, Nyam paga una sola vez al emitir realmente cada ataque; no paga por frame de botón sostenido ni vuelve a pagar al capturar. Disparar al vacío o sin espacio también consume. Cadence y disponibilidad de saliva deben cumplirse a la vez; mantener ataque permite volver a disparar al regenerar lo suficiente. La regeneración es continua, también mientras ataca, limitada a la capacidad y detenida al pausar. Limpiar la sala no rellena la reserva.

La salud y la salivación pertenecen a la run y se conservan al cambiar de sala; un reinicio explícito restaura sus valores iniciales. Los valores de prueba actuales se recogen en [PROTOTYPE.md](PROTOTYPE.md#valores-provisionales-y-balance).

La capacidad máxima de salivación y el coste por burbuja son atributos secundarios modificables; la regeneración es stat principal.

Las burbujas falladas no devuelven salivación al recogerse. Regeneración basal + frutas/verduras sustituyen aquella idea. Las burbujas vacías pueden permanecer como elemento de plataformas y futuras mutaciones.

Diferencia entre personajes a validar: Gluty puede usar la lengua sin salivación y solo paga cuando crea una burbuja; Nyam necesita salivación para disparar. No compensar anticipadamente sin probar distancia, cadencia y juego real.

### P7.C · Supervivencia: cuatro dientes

Concepto: cuando el protagonista se queda sin dientes, no puede continuar comiendo en el tour. Los cuatro tipos previstos se describen abajo; el prototipo empieza con **tres contenedores normales llenos**, no cuatro. Límite provisional: ocho espacios en total.

Para normales y caries se acuerdan medias piezas: salud y pickups pueden representar medio diente o uno entero. El daño básico inicial es medio diente; sus cantidades y futura escala se ajustarán por configuración y contexto de partida.

| Diente | Efecto acordado |
| --- | --- |
| Normal | Vida recuperable. Al perderlo queda el contenedor/base vacío, que puede rellenarse. |
| Caries | Protección temporal, divisible en mitades. La parte consumida desaparece; no deja contenedor. |
| Colmillo | Protege de un golpe; si el daño fue melee, responde con un mordisco automático. Luego desaparece. Potencia pendiente. |
| Implante | Diente en formación: al cambiar de mundo o al conseguir otro implante pasa a diente normal. |

La grasa protectora y el aumento de tamaño corporal como mecánica quedan descartados.

Reglas acordadas para normales y caries:

- El daño consume primero las caries; después, la vida normal. Los contenedores normales se conservan incluso vacíos.
- El pickup normal rellena vida en contenedores existentes; no crea contenedores nuevos.
- La caries añade protección en espacios adicionales disponibles, sin sustituir contenedores normales vacíos. Dos medias caries comparten un cuadrado; al agotarse desaparece su espacio.
- El límite cuenta contenedores normales, estén llenos o vacíos, y los espacios ocupados por caries. No se supera la capacidad; un pickup que no aporta nada permanece en la sala. Si solo cabe parte de una pieza entera, se aplica esa parte y se consume el pickup.
- La run termina al no quedar ninguna mitad de vida normal ni caries, aunque queden contenedores vacíos. Se desactivan los controles y se deja una espera de presentación con la animación de muerte antes de mostrar Game over; no hay respawn ni consumo de vidas persistentes legacy.
- No hay invencibilidad ni bloqueo inicial al entrar en la sala JSON. Tras recibir daño sí existe una ventana de invulnerabilidad, de duración provisional.

Los dientes pueden aparecer al rematar enemigos, nunca por capturar o escapar. Las probabilidades y cantidades iniciales son de prueba. Colmillo e implante son contenido acordado para incorporar después de validar la base, no ideas descartables. No se han definido medias piezas para esos otros tipos.

Representación provisional: cuadrados con borde blanco, interior oscuro para contenedor vacío, blanco para vida normal y beige verdoso para caries; relleno por mitades. Se usa en pickups y en el HUD arriba a la izquierda. El arte definitivo sigue pendiente. La secuencia y los valores de implementación viven en [PROTOTYPE.md](PROTOTYPE.md).

### P7.D · Estadísticas y atributos

| Stats principales (7) | Ámbito |
| --- | --- |
| Speed | Movimiento |
| Cadence | Frecuencia de ataque |
| Power | Reducción de resistencia enemiga |
| Shot Speed | Velocidad de la burbuja |
| Luck | Probabilidades |
| Salivation | Regeneración natural de salivación |
| Bubble Duration | Tiempo de vida de la burbuja, con mínimo jugable |

Capacidades secundarias: vida máxima/espacios de salud, salivación máxima y coste por burbuja.

Size y Range dejan de ser stats universales. Tamaño de personaje/burbujas, longitud de la lengua, empuje y trayectorias especiales pueden cambiar mediante efectos concretos.

Bubble Duration no obliga a que el temporizador del enemigo aturdido sea idéntico al de una burbuja vacía; el ATURDIDO mantiene su propia lógica.

No permitir que mejoras/penalizaciones de salto hagan imposible acceder a salidas obligatorias con el movimiento base.

## P8 · Personajes y uso compartido de burbujas

Todos los personajes actuales pueden generar burbujas y beneficiarse de sus mutaciones. Los ataques y ventajas innatas se diferencian; las salas obligatorias siguen siendo transitables con el movimiento base.

| Personaje | Ataque y generación | Rasgo propio |
| --- | --- | --- |
| Nyam | Crea burbujas directamente desde su posición. | Regeneración de salivación elevada; puede sostener el ataque con descansos cortos. |
| Gluty | Lengüetazo disponible siempre; si golpea al aire genera burbuja desde la punta, consumiendo saliva solo en ese caso. | Si alcanza un enemigo aturdido puede atraerlo hacia sí. |
| Tfly | Aleteo corto disponible siempre; después puede generar burbuja. | Empuje en el aleteo y en sus burbujas (este último reutilizable como mutación); doble salto. Diseño físico pendiente. |

Burbujas vacías y con enemigo usan una entidad de burbuja común, con propiedades generales y mutaciones compartidas. Durante la captura, la burbuja posee la instancia del enemigo y suspende su actualización y colisiones independientes; escapar devuelve esa misma instancia al mundo y rematar la resuelve una sola vez. La frontera técnica se documenta en [REMAKE_DIRECTION.md](REMAKE_DIRECTION.md#runtime-json-implementado).

Tfly no necesita empujar burbujas ya existentes con el aleteo; no añadir interacciones extra sin justificación.

Posibilidades futuras relacionadas: [IDEAS.md](IDEAS.md).

## P9 · Objetos, pools y kit mínimo de prototipo

### P9.A · Comida, bebidas y economía

| Familia | Función / estado |
| --- | --- |
| Frutas | Drops abundantes; recarga superior de salivación. Variantes visuales equivalentes. |
| Verduras | Drops abundantes; recarga inferior de salivación. Variantes visuales equivalentes. |
| Caramelos | Subidones temporales conocidos: ventaja intensa + efecto secundario cómico. Cada variedad tiene temporizador independiente; se solapan. Envoltorio sorpresa elige uno de los efectos conocidos. |
| Refrescos agitados | Consumibles relativamente escasos; se colocan a los pies y explotan tras aviso. Herramienta ofensiva y para abrir caminos. |
| Platos preparados | Pasivas de estadísticas y mutaciones; identidad gastronómica se asignará después de definir los efectos. |
| Cócteles | Objetos de una pool especial de coctelería, no clase mecánica exclusiva. |
| Tickets | Moneda universal del tour, provisional. |
| Aires | Elementos ambientales; los efectos especiales (tornado, pinchos, etc.) podrán reutilizarse como mutaciones o activas. |

Presentación de recompensas acordada para el prototipo: al rematar, el cuerpo del enemigo sale en parábola, gira y puede rebotar en los límites laterales de la sala, atravesando su geometría interior. Es un efecto visual breve que se desvanece, sin IA, colisiones de combate ni espera para resolver al enemigo.

Los drops sorteados salen simultáneamente, cada uno con su propia parábola y giro, más exagerados que el cuerpo. Rebotan en los límites exteriores, atraviesan la geometría interior al subir y aterrizan sobre la primera superficie cruzada al caer. Son recogibles durante el vuelo tras una breve demora para que lleguen a verse. Si no hay recompensa, solo sale el cuerpo: no se transforma más tarde en comida.

Los perfiles de drops pueden variar por tipo de enemigo, con un perfil común de respaldo. El efecto de la familia recogida es independiente del enemigo de procedencia. Dificultad, Luck y otros criterios futuros no tienen reglas implementadas todavía; disponer de perfiles no autoriza a inventarlas. Los pickups usan IDs numéricos explícitos y estables, independientes de sus nombres provisionales.

Refrescos: propuesta de explosión circular de 1,5 tiles, bloqueada por obstáculos; rompe tiles destructibles y accesos/cofres específicos. Una explosión no atraviesa los tiles que acaba de destruir.

Hay obstáculos rompibles señalizados y paredes secretas aparentemente normales. No se contemplan reacciones en cadena por defecto.

Se retiran los boosts de oruga/huevo como mascotas asociadas a Tfly/Gluty; sus sprites podrán reutilizarse.

### P9.B · Tipos de objeto, pools y calidad

Tipos: pasivas permanentes, pasivas temporales y activas recargables, preparadas manualmente.

Calidad/tier numérico ascendente, inicialmente 0–2 (el kit básico usa sobre todo 0–1); escalable sin nombres de categoría rígidos.

Las pools determinan procedencia (tesoro, tienda, boss, chef, contrabando y coctelería); cada pool tiene su propia distribución de calidades. Un objeto puede pertenecer a varias pools.

Según calidad y reglas configurables puede volver a aparecer un objeto al agotarse una pool. Repetición y acumulación son reglas distintas.

Bonificaciones numéricas por defecto acumulativas linealmente respecto al valor base; objetos específicos podrían modificar el propio valor base. Sin porcentajes fijados.

La comida rápida es un posible grupo temático, no una pool o una calidad: pizzas, burgers, carnes, perritos, sushi de supermercado, kebab, dürum, etc.

Posibilidades futuras relacionadas: [IDEAS.md](IDEAS.md).

### P9.C · Kit estadístico v0.2 — 21 efectos

Banco experimental de efectos SIN nombres de comidas ni porcentajes definitivos. Siete puros, nueve dobles y cinco con contrapartida. Las calidades son tentativas.

| # | Modificación | Grupo | Calidad |
| --- | --- | --- | --- |
| 01 | +Speed | Puro | 0 |
| 02 | +Cadence | Puro | 0 |
| 03 | +Power | Puro | 0 |
| 04 | +Shot Speed | Puro | 0 |
| 05 | +Luck | Puro | 0 |
| 06 | +Salivation | Puro | 0 |
| 07 | +Bubble Duration | Puro | 0 |
| 08 | +Speed, +Cadence | Doble | 1 |
| 09 | +Speed, +Power | Doble | 1 |
| 10 | +Cadence, +Shot Speed | Doble | 0 |
| 11 | +Cadence, +Salivation | Doble | 1 |
| 12 | +Power, +Shot Speed | Doble | 1 |
| 13 | +Power, +Bubble Duration | Doble | 1 |
| 14 | +Shot Speed, +Bubble Duration | Doble | 0 |
| 15 | +Luck, +Salivation | Doble | 0 |
| 16 | +Luck, +Bubble Duration | Doble | 0 |
| 17 | ++Speed, −Power | Contrapartida | 1 |
| 18 | ++Cadence, −Power | Contrapartida | 1 |
| 19 | ++Power, −Cadence | Contrapartida | 1 |
| 20 | ++Shot Speed, −Bubble Duration | Contrapartida | 1 |
| 21 | ++Salivation, −salivación máxima | Contrapartida | 1 |

Efectos redundantes y mejoras positivas sin desventajas son aceptables. Las mutaciones, no las mejoras básicas, darán la mayor personalidad al catálogo.

### P9.D · Kit mínimo de mutaciones

| Dominio | Mutación | Efecto propuesto |
| --- | --- | --- |
| Burbuja | Gigante | Aumenta tamaño de burbujas. |
| Burbuja | Rebotadora | Rebota contra paredes. |
| Burbuja | Impulsora | Empuje/knockback al contacto; Tfly lo posee de serie. |
| Burbuja (iteración 2) | Doble | Genera dos burbujas por disparo; consumo por ataque a probar. |
| Captura | Prolongada | Aumenta la duración del aturdimiento. |
| Captura | Rápida | Las burbujas con enemigo capturado se desplazan más deprisa. |
| Remate | Explosivo | El remate produce una explosión pequeña. |
| Remate | Nutritivo | Recupera salivación ÚNICAMENTE con remate por contacto cuerpo a cuerpo. |
| Salivación | Regeneración mejorada | Aumenta la regeneración natural sin condiciones. |
| Salivación (candidata) | Sobresalivación | Comida recogida con barra llena permite superar temporalmente capacidad máxima. |

Posibilidades futuras relacionadas: [IDEAS.md](IDEAS.md).

### P9.E · Primer catálogo de activas

Activa equipada: pulsación para preparar el efecto sobre la PRÓXIMA burbuja generada. Pulsar de nuevo cancela la preparación sin gastar carga. La carga se consume al crear la burbuja. La burbuja conserva el efecto mientras exista, incluso si captura un enemigo. El efecto se desencadena SOLO cuando el jugador la rompe con la misma acción de remate, esté vacía o con captura. La desaparición por timeout NO activa el efecto.

| Activa | Efecto al estallar |
| --- | --- |
| Rayo | Descarga que atraviesa enemigos, pero se detiene contra paredes u obstáculos sólidos. |
| Tornado | Tornado que avanza afectando a enemigos hasta encontrar obstáculo. |
| Charco | Charco de baba que ralentiza a los enemigos que lo pisan durante un intervalo. |

Las activas no tienen que limitarse a modificar burbujas en el catálogo definitivo; estas tres sí lo hacen para facilitar pruebas comunes.

Recarga propuesta previamente: avance por tiempo y recarga completa al limpiar sala. La interacción definitiva con salivación se validará en el prototipo; no se fusionan automáticamente ambas barras.

## Próximos temas de diseño

**Pendiente:** balance definitivo de valores, duraciones, probabilidades y distribución de dientes; escala de daño y drops por dificultad/contexto; HUD y arte definitivos. El prototipo ya fija una regla de derrota, medias piezas y una representación temporal, sin convertir sus números en balance final.

**P10 pendiente:** diseñar enemigos y bosses, incluidas las reacciones por especie al escapar. También quedan pendientes generación, navegación y duración efectiva de las runs.
