
move_toward_player(0, _, _, ENEMY_X, ENEMY_Y, ENEMY_X) :- !.
move_toward_player(RANDOMNESS, (PLAYER_X, PLAYER_Y), (ENEMY_X, ENEMY_Y), NEW_ENEMY_X) :-
    RANDOMNESS =< 0.5 ->
    (
        ENEMY_X < PLAYER_X ->
            NEW_ENEMY_X is ENEMY_X + 2.0
        ;
            NEW_ENEMY_X is ENEMY_X - 2.0
    );
    (
        ENEMY_X < PLAYER_X ->
            NEW_ENEMY_X is ENEMY_X + 10.0
        ;
            NEW_ENEMY_X is ENEMY_X - 10.0
    ).








