# Nyam Nyam — Guía de arte y escala visual

Fuente de verdad de la **escala visual y las convenciones de producción de arte**. El diseño jugable pertenece a [GAME_DESIGN.md](GAME_DESIGN.md), la arquitectura a [REMAKE_DIRECTION.md](REMAKE_DIRECTION.md) y el alcance de implementación a [PROTOTYPE.md](PROTOTYPE.md). Esta guía no modifica colisiones, cámara ni tareas en curso.

## Base de producción provisional

**Personajes normales: canvas nativo de 36×36 px**, con cabeza dominante y cuerpo sugerido. Mantener el encuadre actual como referencia de las pruebas de arte. Esta base procede de las comparativas visuales aportadas por el autor; se revisará si las animaciones o nuevas pruebas de gameplay lo requieren.

El canvas incluye transparencia: no implica una silueta de 36 px de ancho ni una hitbox de ese tamaño. Diseñar primero para legibilidad, personalidad y espacio disponible en escena.

## Referencia técnica actual

Medidas del runtime al redactar esta guía, no tamaños nuevos que haya que implementar. Las fuentes ejecutables son `Constant.java`, `GameplayCamera`, el viewport HUD de `GameScreen` y el render de `Entity`; verificar el código antes de integrar nuevos assets.

| Elemento | Referencia actual |
| --- | --- |
| Pantalla vertical | 360×640 |
| Mundo visible | FitViewport de 320×400 unidades: 20×25 tiles |
| Tile | 16×16 |
| Proporción del mundo | 4:5; escala uniforme, espacio sobrante negro |
| HUD y controles | ExtendViewport independiente, base 360×640 |
| Personaje/enemigo normal | Dimensión lógica 32×32 |
| PHANT | Dimensión lógica 28×28 |
| Comida | Dimensión lógica 32×32 |
| Bosses actuales | Dimensión lógica 96×96; boss Nyam 128×128 |

En una ventana de 360×640, el mundo ocupa aproximadamente 360×450 px, con 95 px libres arriba y abajo. La escala es ×1,125: un tile ocupa 18 px y un frame de 36×36 se mostraría a 40,5×40,5 px. Estas medidas de pantalla no son la resolución a la que dibujar el sprite.

Actualmente los frames se dibujan al tamaño de su textura, sin normalizarlos a la dimensión lógica. Exportar el doble de resolución aumenta su presencia en escena. La lógica puede desacoplarse del arte, pero **pasar a canvas 36×36 no autoriza aumentar la hitbox 32×32**. Comprobar alineación al suelo, centrado y márgenes transparentes al integrar; el render actual centra horizontalmente respecto a la dimensión lógica y parte de la posición inferior.

Las salas mayores pueden ampliar el escenario con cámara móvil sin aumentar el tamaño de los personajes. Los módulos 1×1 del mapa de P6 son unidades lógicas de navegación, sin equivalencia en tiles ni píxeles; no sustituyen este encuadre de prueba. Los huecos de acceso de 3 tiles de alto o 2 de ancho son geometría de nivel, no tamaños de sprite. Los menús legacy no son referencia artística definitiva.

## Qué mostraron las comparativas

El personaje anterior dedica casi toda su silueta a la cara: funciona como una cabeza con patas. Un bípedo con torso, brazos y rasgos propios necesita repartir ese mismo espacio.

Según las pruebas aportadas, se compararon versiones aisladas de 96×96, 48×48 y 32×32, y tamaños en escena actual, 36×36, 40×40 y 48×48:

- **96×96:** conserva detalle y personalidad; útil como referencia, retrato o promo, sin usarlo directamente a ese tamaño en gameplay normal.
- **48×48:** buena lectura, pero más presencia y menos aire en la sala. 40×40 también aumenta esa presencia respecto a 36×36.
- **32×32:** viable, con menos margen para una anatomía bípeda y rasgos adicionales.
- **36×36:** equilibrio elegido para producción. La muestra tenía una silueta aproximada de 28×34 px dentro del canvas, aun sin ajuste manual nativo. No es una caja obligatoria para todos los sprites.

Son observaciones visuales, no una medición final de todas las animaciones ni del rendimiento.

## Lenguaje de los glotones

Criaturas caricaturescas y cómicas, viajeras del tour gastronómico. Pensarlas como **cabezones glotones bípedos con cuerpo sugerido**, no como humanoides pequeños llenos de detalle.

- Cabeza dominante: referencia aproximada del 65–75 % de la altura percibida, flexible según la silueta.
- Cuerpo y barriga simples, brazos y piernas cortos, pies mínimos.
- Cara, silueta y postura por delante de detalles internos: ojos grandes, boca clara y dientes simplificados.
- Baba, lengua, babero u otros detalles solo cuando aporten lectura. Uno o dos píxeles bien colocados pueden bastar.

### Base compartida y diferencias

Los glotones deben sentirse de una misma especie base. Compartir proporción cabeza/cuerpo, altura general, escala de ojos, extremidades, línea de suelo, outline, familia de expresiones y sombreado. La coherencia anatómica de Isaac es una referencia, no una plantilla que reproducir.

Cada personaje se apoya en **uno o dos rasgos distintivos**: color, orejas, alas, lengua, colmillos, antenas, cuernos, cola, manchas o accesorio. Las variaciones funcionales deben seguir siendo reconocibles sin romper la base común.

Un objeto característico puede diferenciar a un glotón: pañuelo, caramelo, cucharita, corona o bollo son ejemplos, no asignaciones cerradas. No imponer el mismo objeto a todos ni recargar el canvas con accesorios.

## Producción y animación

- Dibujar nativamente a 36×36 para los personajes normales. El arte grande puede servir de concepto; reducirlo no sustituye el trabajo de simplificación y ajuste de píxeles.
- Validar silueta y contraste sobre fondos de gameplay, en movimiento y con HUD; no solo sobre un fondo neutro o con zoom del editor.
- Mantener anclaje al suelo, posición de ojos y centro visual consistentes en las poses neutras y entre personajes. Los desplazamientos expresivos de una animación deben ser intencionales, sin vibración accidental entre frames.
- Base de animaciones propuesta: `idle`, `run`, `jump`, `fall`, `attack`, `hurt`. Es una lista artística; no exige nuevos nombres de estado o regiones de atlas. Verificar el contrato existente antes de exportar.
- Revisar especialmente rasgos de ataque y deformaciones: la lengua, alas o efectos pueden necesitar frames/elementos adicionales; no ampliar por ello todos los frames sin evaluar el caso.

| Familia de arte | Orientación de producción |
| --- | --- |
| Glotones normales | Canvas nativo 36×36 |
| Enemigos pequeños o simples | 36×36 o menos según rol; no obliga a cambiar todos los enemigos actuales |
| Especiales / minibosses | Considerar 48×48 o 64×64 si el diseño lo requiere |
| Bosses nuevos | Tamaño por caso: 64×64, 96×96 o mayor; no redefine sus dimensiones lógicas actuales |
| Retratos, selección, UI y promo | Piezas independientes de mayor resolución, por ejemplo 64×64 o 96×96 |

El tamaño contenido ayuda a limitar superficie de frames, memoria de texturas y trabajo de dibujo. El coste real también depende de número de frames, atlas y efectos: **36×36 no es por sí solo una garantía de rendimiento HTML/móvil**.

## Validación pendiente

Probar el primer personaje nativo con animaciones dentro del encuadre actual, sobre fondos representativos y en móvil. Revisar lectura de cara y rasgo dominante, diferenciación entre personajes, alineación con suelo/colisión y espacio visual durante el combate. Reajustar esta guía si esas pruebas lo piden; no aumentar cámara o hitbox automáticamente para acomodar el arte.
