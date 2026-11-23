# Nyam Nyam — Prototipo

Fuente de verdad del **alcance inmediato, orden de implementación y criterios de prueba**. Las reglas pertenecen a [GAME_DESIGN.md](GAME_DESIGN.md); las ideas futuras, a [IDEAS.md](IDEAS.md). Este documento no afirma que lo descrito esté implementado.

## Objetivo actual

Validar el ritmo del combate y la gestión de recursos en una sola sala, antes de generar áreas o ampliar el catálogo.

## Prototipo 1 — alcance autorizado de diseño

Nyam, una sala artesanal, pocos enemigos y plataformas. Usar el flujo nuevo de rooms JSON, manteniendo el legacy disponible para comparar. Reutilizar assets y movimiento hacia focos existentes cuando sirvan a las reglas nuevas.

Implementar en incrementos jugables, revisando el código antes de concretar cada cambio:

1. **Combate y captura:** resistencia, captura, remate por contacto y fuga. Reglas en [P7.A](GAME_DESIGN.md#p7a--ciclo-básico-de-combate); propiedades compartidas en P4 y P8.
La interacción de burbujas de P7.A se valida como incremento tras captura/liberación y antes de incorporar resistencia/Power. La secuencia concreta y sus dependencias están en [la tarjeta de interacción](https://trello.com/c/OiN62Nqe); no ampliar la tarea de captura que ya está en curso.

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

### Decisiones necesarias durante esta iteración

- Prioridades exactas entre apoyo/rebote y remate sobre burbujas, y tratamiento de las vacías.
- Valores iniciales de resistencia, poder, tiempos, reserva, regeneración y drops.
- Orden de consumo, reposición y ocupación de espacios de dientes; instante de derrota al perder la última pieza. Son pendientes del maestro, no reglas implícitas heredadas de Isaac o del legacy.
- Comportamiento al limpiar esta sala de prueba, sin implementar todavía navegación de un tour.

### Fuera del alcance actual

Generación de mapas, puertas entre salas, mundos y transiciones, bosses nuevos, tiendas, economía, progresión entre runs, catálogo completo de objetos, activas, engullido/escupitajo y nuevos ataques de Gluty/Tfly. No migrar automáticamente reglas legacy de score, vidas extra, mascotas o spawns temporizados.

## Secuencia posterior acordada

No amplía el trabajo inmediato:

- **Prototipo 2:** sistema genérico de stats y unas pocas mutaciones. El banco de efectos y el catálogo se mantienen solo en P9.C–D del maestro; no requieren una implementación distinta por fila.
- **Prototipo 3:** activas recargables, colmillo e implante y recompensas. Sus reglas están en P7.C y P9.E.
- Incorporar Gluty y Tfly sobre el sistema compartido después de validar la base; no se fija aquí su orden exacto.

La generación y la duración del tour se contrastarán midiendo primero una sala y una primera área, según P6.
