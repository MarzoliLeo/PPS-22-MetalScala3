# Backlogs
Ciascuno sprint aveva cadenza settimanale. Il venerdì di ciascuna settimana si passava ad una nuova fase di sprint.
Legenda dei nomi: 
* LM = Leo Marzoli
* LM2 = Lorenzo Massone
* GR = Giacomo Romagnoli

# Sprint 1: Implementazione delle Funzionalità di Base

| Product Item | Attività                                          | Assegnato a | Ore Stim. | Ore Effett. | D1 | D2 | D3 | D4 | D5 | D6 |
|:-------------|:---------------------------------------------------|:------------|:----------|:------------|:--|:--|:--|:--|:--|:--|
| PI-001       | Implementazione di Pause e Resume del GameLoop     | LM          | 8         | 8           | | | | | |   |
| PI-002       | Definizione di stati dell'engine (start/pause/stop)| LM          | 6         | 6           | | | | | |   |
| PI-003       | Creazione di una entità giocatore                  | GR          | 10        | 10          | | | | | |   |
| PI-004       | Creazione di una componente posizione             | GR          | 6         | 6           | | | | | |   |
| PI-005       | Creazione di una finestra di base con JavaFX       | LM2         | 12        | 12          | | | | | |   |
| PI-006       | Creazione di un entity manager per l'entità giocatore| LM2       | 8         | 8           | | | | | |   |
| PI-007       | Creazione di un sistema che simuli i tick dell'engine (tempo) tramite GameLoop| LM | 10| 10         | | | | | |   |
| PI-008       | Collegare il movimento al rendering del giocatore nella finestra| LM2 | 8         | 8         | | | | | |   |
| PI-009       | Creazione di un system manager per il sistema di movimento| LM | 5| 5         | | | | | |   |


# Sprint 2: Miglioramenti e Ottimizzazion

| Product Item | Attività                                      | Assegnato a | Ore Stim. | Ore Effett. | D1 | D2 | D3 | D4 | D5 | D6 |
|:-------------|:----------------------------------------------|:------------|:----------|:------------|:--|:--|:--|:--|:--|:--|
| PI-010       | Refactoring di ECS                             | GR          | 12        |             | Lu| Ma| Me| Gi| Ve|   |
| PI-011       | Aggiustare i Test per ECS e aggiungerne       | LM2         | 14        |             |    |    |    |    |    |    |
| PI-012       | Creazione di un SystemGravity                  | LM          | 10        |             |    |    |    |    |    |    |
| PI-013       | Aggiustare Test Engine e aggiungerne          | LM          | 8         |             |    |    |    |    |    |    |
| PI-014       | Rendering del Player Entity nella finestra di View.| LM2       | 12        |             |    |    |    |    |    |    |
| PI-015       | Component Gravity                              | LM          | 6         |             |    |    |    |    |    |    |
| PI-016       | Verificare che l'engine decrementi la y per arrivare al fondo della finestra.| LM | 6|    |    |    |    |    |    |
| PI-017       | Collegare il systemGravity alla box (player) della View per verificare che non esca dal bordo della finestra in altezza.| LM| 8|    |    |    |    |    |    |

# Sprint 3: Aggiunta di feature di gioco non banali.

| Product Item | Attività                                                                        | Assegnato a | Ore Stim. | Ore Effett. | D1 | D2 | D3 | D4 | D5 | D6 |
|:-------------|:--------------------------------------------------------------------------------|:------------|:----------|:------------|:--|:--|:--|:--|:--|:--|
| PI-018       | Verificare che se due entità dispongono di una Collision Component succede qualcosa (come una stampa a video)| LM2 | 10 | | | | | | | |
| PI-019       | Creazione di un Movement System per il bullet che sia lineare, dove una volta "spawnato" deve andare dritto per una direzione| GR | 14 | | | | | | | |
| PI-020       | Creazione di un Bullet Entity                                                 | GR      | 8         |             |    |    |    |    |    |    |
| PI-021       | Creazione di un Collision System                                               | LM2     | 12        |             |    |    |    |    |    |    |
| PI-022       | Component di Collision                                                        | LM2     | 8         |             |    |    |    |    |    |    |
| PI-023       | Creazione di una component Sprite                                             | LM          | 10        |             |    |    |    |    |    |    |
| PI-024       | Aggiungere fluidità ai movimenti del player                                   | LM          | 8         |             |    |    |    |    |    |    |
| PI-025       | Gestire animazioni degli Sprite                                               | LM          | 10        |             |    |    |    |    |    |    |

# Sprint 4: Nuove Funzionalità e Miglioramenti

| Product Item | Attività                                                                        | Assegnato a | Ore Stim. | Ore Effett. | D1 | D2 | D3 | D4 | D5 | D6 |
|:-------------|:--------------------------------------------------------------------------------|:------------|:----------|:------------|:--|:--|:--|:--|:--|:--|
| PI-018       | Creato una rotazione degli sprite sulla base della direzione                     | LM           | 10        |             |    |    |    |    |    |    |
| PI-019       | Introdurre una Componente di Velocity che rappresenta la velocità di una entità   | LM2, GR          | 14        |             |    |    |    |    |    |    |
| PI-020       | Refactor del metodo getComponent                                              | GR           | 8         |             |    |    |    |    |    |    |
| PI-021       | Migliorare il movimento e il salto attraverso forze come gravità e attrito     | LM2          | 12        |             |    |    |    |    |    |    |
| PI-022       | Introduzione di un Command Pattern per l'input                               | LM           | 10        |             |    |    |    |    |    |    |
| PI-023       | Aggiornare il gioco tenendo conto dell'elapsed time, per rendere l'aggiornamento fluido | LM2, GR | 8  |             |    |    |    |    |    |    |
| PI-024       | Cambiare la logica di render                                                 | LM           | 10        |             |    |    |    |    |    |    |
| PI-025       | Refactor del command pattern                                                  | GR           | 8         |             |    |    |    |    |    |    |
| PI-026       | Impostare la logica di gioco separando ECS-View                              | LM, GR, LM2           | 14        |             |    |    |    |    |    |    |
| PI-027       | Refactor Sprite component e Sprite System                                    | LM, GR           | 12        |             |    |    |    |    |    |    |
| PI-028       | Verificare che se due entità che dispongono di una Collision Component non si possono "attraversare" nel gioco (a livello grafico)| LM2 | 10 |             |    |    |    |    |    |    |

# Sprint 5: Espansione delle Funzionalità di Gioco

| Product Item | Attività                                                                        | Assegnato a | Ore Stim. | Ore Effett. | D1 | D2 | D3 | D4 | D5 | D6 |
|:-------------|:--------------------------------------------------------------------------------|:------------|:----------|:------------|:--|:--|:--|:--|:--|:--|
| PI-029       | Creare un componente Weapon, possibilmente il più generalizzato possibile in modo da creare in futuro diverse armi | LM2 | 12 |             |    |    |    |    |    |    |
| PI-030       | Collegamento della logica in TuProlog al codice Scala3                          | LM         | 14 |             |    |    |    |    |    |    |
| PI-031       | Attaccare una collision al projectile                                           | LM2     | 10 |             |    |    |    |    |    |    |
| PI-032       | Verificare che una volta che il projectile collida con un enemy (o col bordo finestra se sei cieco e lo manchi mentre spari) poi venga distrutto ed eliminato come entità | Lorenzo | 14 |             |    |    |    |    |    |    |
| PI-033       | Sviluppo di una logica AI per il nemico in TuProlog                             | LM         | 16 |             |    |    |    |    |    |    |
| PI-034       | Implementare una generalizzazione del concetto di CollisionHandler              | LM2     | 12 |             |    |    |    |    |    |    |

# Sprint 6: Refactoring e Miglioramenti

| Product Item | Attività                                                                        | Assegnato a | Ore Stim. | Ore Effett. | D1 | D2 | D3 | D4 | D5 | D6 |
|:-------------|:--------------------------------------------------------------------------------|:------------|:----------|:------------|:--|:--|:--|:--|:--|:--|
| PI-035       | Se collido con un'arma devo salvare in qualche modo nel player il tipo di arma che ho preso e cambiare lo sprite dei proiettili in corrispondenza | LM2 | 12 | | | | | | | |
| PI-036       | Separare il positionUpdateSystem                                                | LM, GR, LM2       | 14 | | | | | | | |
| PI-037       | Separare l'AI del nemico                                                        | LM, GR, LM2       | 16 | | | | | | | |
| PI-038       | Separare l'Input Movement System                                                | LM, GR, LM2       | 10 | | | | | | | |
| PI-039       | Separare il Bullet Movement System                                              | LM, GR, LM2       | 12 | | | | | | | |
| PI-040       | Separare il Gravity System                                                      | LM, GR, LM2       | 10 | | | | | | | |
| PI-041       | La gravità deve essere soggettiva, ovvero si applica se hai il componente        | LM, GR, LM2       | 8  | | | | | | | |
| PI-042       | Estratto la logica degli sprite in un system                                    | LM          | 10 | | | | | | | |
| PI-043       | Scaricati le immagini degli sprite e creato animazioni                          | LM          | 12 | | | | | | | |
| PI-044       | Far funzionare la logica di AI dei nemici per più nemici nella scena di gioco, non solo per uno | LM | 14 | | | | | | | |
| PI-045       | Fatto in modo che se un proiettile colpisce un nemico questo venga eliminato    | LM          | 10 | | | | | | | |
| PI-046       | Se un proiettile nemico spara ad un nemico non deve eliminarlo                  | LM          | 12 | | | | | | | |
| PI-047       | Sistemare la direction del projectile in modo che segua l'enemy e non il player | LM          | 10 | | | | | | | |
| PI-048       | Sistemare la grafica in modo che il proiettile ruoti opportunamente             | LM          | 8  | | | | | | | |
| PI-049       | Fixare il fatto che se un enemy sbatte contro un box deve collidere      | LM          | 10 | | | | | | | |
| PI-050       | Ridurre la collision box quando si effettua il comando B da tastiera             | GR          | 8  | | | | | | | |
| PI-051       | Mettere le collisioni nel cluch                                                 | LM, GR       | 12 | | | |
| PI-052       | Implementare una nuova entità di Slug                                          | LM          | 10 | | | | | | | |
| PI-053       | Creare una nuova entità "AmmoBox", a cui attaccare una collisione, quando si collide guadagni munizioni per l'arma corrente | LM2 | 14 | | | | | | | |
| PI-054       | Creare un livello da game designer                                              | GR          | 12 | | | | | | | |

# Sprint 7: Ottimizzazioni finali.

| Product Item | Attività                                                                        | Assegnato a | Ore Stim. | Ore Effett. | D1 | D2 | D3 | D4 | D5 | D6 |
|:-------------|:--------------------------------------------------------------------------------|:------------|:----------|:------------|:--|:--|:--|:--|:--|:--|
| PI-055       | Posizione randomica dei nemici e delle armi e delle munizioni                   | LM          | 10 | | | | | | | |
| PI-056       | Sistemare lo spawn dei proiettili in modo che sia quanto più vicino possibile alle entità che lo sparano | LM2 | 14 | | | | | | | |
| PI-057       | Quando un player o un nemico sta sopra un oggetto, la gravità continua ad aumentare e ad un certo punto bypassa le collisioni se stai fermo in volo | LM | 16 | | | | | | | |
| PI-058       | Aggiungere il comando B (lettera che volete voi), sostituire lo sprite dei proiettili a bombe (10) e aggiungi la gravità alle bombe | LM, GR, LM2 | 18 | | | | | | | |
| PI-059       | Quando il player muore il gioco si ferma                                       | GR, LM       | 10 | | | | | | | |

# Product Backlog Riassuntivo:

| Id | Item                                               | Stima in ore | Ore effettive | Sprint1      | Sprint2      | Sprint3      | Sprint4      | Sprint5      | Sprint6      | Sprint7      |
|----|----------------------------------------------------|--------------|---------------|--------------|--------------|--------------|--------------|--------------|--------------|--------------|
| 1  | Implementazione di Funzionalità di Base           | -            | -             | Completato   |              |              |              |              |              |              |
| 2  | Miglioramenti e Ottimizzazioni                    | -            | -             |              | Completato   |              |              |              |              |              |
| 3  | Aggiunta di Feature di Gioco non Banali            | -            | -             |              |              | Completato   |              |              |              |              |
| 4  | Nuove Funzionalità e Miglioramenti                | -            | -             |              |              |              | Completato   |              |              |              |
| 5  | Espansione delle Funzionalità di Gioco             | -            | -             |              |              |              |              | Completato   |              |              |
| 6  | Refactoring e Miglioramenti                       | -            | -             |              |              |              |              |              | Completato   |              |
| 7  | Ottimizzazioni Finali                              | -            | -             |              |              |              |              |              |              | Completato   |

