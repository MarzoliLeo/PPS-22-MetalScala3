# Design Architetturale
## Architettura complessiva
Dopo un’attenta analisi dei requisiti, si è proceduto con la fase di design architetturale. La seguente immagine definisce il core dell’architettura generale implementata.

![Architettura di Sistema.](../img/ArchitetturaPPS.png)

Breve spiegazione dell'immagine:
* **View**: ha l’obbiettivo di fornire all’utente una schermata intuitiva per il controllo del gioco.
* **ECS**: è un aggregato di sistemi, entità e componenti che esprimono e contengono in maniera astratta il modello di gioco.
* **Engine**: rappresenta il cuore del gioco; incapsula il flusso di controllo principale e tramite ECS calcola continuamente il nuovo stato.

## Pattern architetturali utilizzati
###  ECS
Trattandosi dello sviluppo di un videogioco, per il quale è necessario creare un motore di gioco, è stato scelto di utilizzare il pattern architetturale **Entity Component System** (ECS). Questo pattern è largamente utilizzato nello sviluppo di videogiochi per motivi di performance e per la sua estensibilità e manutenibilità.
Si compone di tre parti principali:
* **Entità**: rappresentano gli elementi in gioco, ad esempio player, enemy, slug. Ad ogni entità vengono associati uno o più componenti.
* **Componenti**: sono delle proprietà che vengono possedute da una o più entità e ne rappresentano lo stato. Esempi di possibili componenti sono posizione o velocità.
* **Sistemi**: definiscono dei comportamenti necessari a gestire dei sottoinsiemi della logica di gioco. Gestiscono le interazioni tra le entità agendo sui componenti associati ad esse.



[Home](../index.md).
