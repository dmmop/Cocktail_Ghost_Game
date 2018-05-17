# Cocktail Ghost Game [![Generic badge](https://img.shields.io/badge/Spark-2.2.0,%202.3.0-green.svg)](https://shields.io/) [![Generic badge](https://img.shields.io/badge/Scala-2.11.8-green.svg)](https://shields.io/)

Queremos crear un juego llamado CocktailGhost basado en el juego de Ghost. En este juego
dos jugadores se turnan para construir, de izquierda a derecha, el nombre de un determinado
cocktail.
* Cada jugador agrega una letra por turno. 
* El objetivo es completar el nombre cuanto antes. Si se agrega una letra que completa el nombre de un cocktail se gana.
* El usuario juega a CocktailGhost contra la computadora. 
* La computadora debe jugar de manera óptima dada la lista de cocktails adjunta.
* Debe permitir que el humano juegue primero. 
* Si la computadora cree que va a ganar, debe jugar al azar entre todos sus movimientos ganadores. 
* Si la computadora piensa que perderá, debería jugar para extender el juego el mayor tiempo posible.