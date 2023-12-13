
# Requisiti

## 1. Business 
* Creazione di un applicativo che permette di giocare a una copia di Metal Slug 3, implementando un sottoinsieme di funzionalità significative e una grafica simile all'originale.
* Implementare il suddetto applicativo in un tempo limite di due mesi.
## 2. Modello di dominio:
### Descrizione: 
Nel platformer originale il giocatore pilota un soldato attraverso diversi livelli e viene ostacolato da svariati nemici, ai quali può sparare col fine di ucciderli e proseguire. Il titolo presenta le classiche meccaniche platformer, quali, salto, corsa e arrampicata su piattaforme, ma è caratterizzato da meccaniche come sparare, lanciare bombe, accovacciarsi e ottenere power up che forniscono nuove armi o munizioni. Inoltre il gioco prevede la possibilità per il giocatore di pilotare potenti slug, che aiutano il protagonista a spazzare via i nemici per farsi largo fino al traguardo. Di seguito verranno riportati i requisiti emersi dalla fase di analisi divisi per categorie, che comprenderanno il sottoinsieme del dominio che si intende reimplementare.
### Scenario di gioco: 
Di seguito riportiamo una immagine rappresentativa del gioco originale.
![MetalSlug3_Dominio](./docs/img/MetalSlug_Scenario_Dominio.png)


## 3. Requisiti Funzionali:
### Utente:
* L'utente deve visualizzare la schermata iniziale dalla quale è possibile iniziare una partita o chiudere il gioco.
* L'utente deve poter visualizzare una nuova schermata dove può visualizzare la scena di gioco.
* L'utente deve essere in grado di controllare il personaggio principale muovendolo nello scenario di gioco utilizzando i tasti direzionali.
* L'utente deve poter visualizzare le munizioni a dispozione del personaggio principale.
* L'utente deve sparare utilizzando il tasto apposito.
* L'utente deve lanciare bombe con il tasto apposito.
* L'utente deve potersi accovacciare tramite tasto apposito.
* L'utente deve saltare con il tasto apposito.
* L'utente deve poter cambiare arma raccogliendo powerup nella scena di gioco.
* L'utente deve poter prendere munizioni raccogliendo powerup nella scena di gioco.
* L'utente deve muoversi nella scena di gioco utilizzando le meccaniche di un platformer.
* L'utente deve poter eliminare i nemici sparandogli.
* L'utente deve controllare uno slug quando ci entra in contatto.
* L'utente deve morire quando viene colpito da un nemico.
* L'utente deve vincere quando tutti i nemici sono stati eliminati.

## 4. Sistema
* Il sistema deve creare la scena di gioco, provvista di personaggio principale, piattaforme, power up per munizioni e armi, slug e nemici.
* Il sistema deve aggiornare la scena di gioco in base all'input dell'utente e allo scorrere del tempo.
* Il sistema deve aggiornare la schermata che visualizza la scena di gioco coerentemente con l'input dell'utente e lo scorrere del tempo.
* Il sistema deve gestire il comportamento dei nemici.

## 5. Non funzionali
* Grafica: il gioco deve ricordare visivamente l'originale.
* Usabilità: il gioco deve essere chiaro in risposta alle interazioni, fornendo elementi grafici che permettano all'utente di comprendere facilmente lo stato del gioco.
  
## 6. Di implementazione
### Utilizzo di:
* Scala 3.3.0
* ScalaTest 3.3.x
* JDK 17+
### Sviluppare un game engine:
* Proprietario senza usufruire di soluzioni già esistenti


* [Home](./index.md).

