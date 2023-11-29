move_toward_player(0, _, _, ENEMY_X, ENEMY_Y, ENEMY_X) :- !.
move_toward_player(RANDOMNESS, (PLAYER_X, PLAYER_Y), (ENEMY_X, ENEMY_Y), NEW_ENEMY_X) :-
    RANDOMNESS = 1 ->
    (
        ENEMY_X < PLAYER_X ->
            NEW_ENEMY_X is ENEMY_X + 40.0
        ;
            NEW_ENEMY_X is ENEMY_X - 40.0
    );
    RANDOMNESS = 2 ->
    (
        %Decido di stare fermo e non fare niente.
        ENEMY_X < PLAYER_X ->
            NEW_ENEMY_X is ENEMY_X
        ;
            NEW_ENEMY_X is ENEMY_X
    );
    RANDOMNESS = 3 ->
    (
       %Sto effettuando lo shooting del bullet quindi rimango fermo nella mia posizione.
       ENEMY_X < PLAYER_X ->
           NEW_ENEMY_X is ENEMY_X
       ;
           NEW_ENEMY_X is ENEMY_X
    ).








