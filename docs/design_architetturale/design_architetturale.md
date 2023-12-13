# Design Architetturale
## Architettura complessiva
Dopo un’attenta analisi dei requisiti, si è proceduto con la fase di design architetturale la seguente immagine definisce il core dell’architettura generale implementata.

![Architettura di Sistema.](../img/ArchitetturaPPS.png)

Breve spiegazione dell'immagine:
* **View**: ha l’obbiettivo di fornire all’utente una schermata intuitiva per il controllo del gioco.
* **ECS**: è un aggregato di sistemi, entità e componenti che esprimono e contengono in maniera astratta il modello di gioco.
* **Engine**: rappresenta il cuore del gioco; incapsula il flusso di controllo principale e tramite ECS calcola continuamente il nuovo ambiente.

## descrizione di pattern architetturali usati
## scelte tecnologiche cruciali ai fini architetturali -- corredato da pochi ma efficaci diagrammi


* [Home](../index.md).
