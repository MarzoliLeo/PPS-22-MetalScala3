main(X, Y) :- X = 1, Y = 2.

%move_toward_player(0, (PLAYER_X, PLAYER_Y), (ENEMY_X, ENEMY_Y), NEW_ENEMY_X, NEW_ENEMY_Y) :- !.
move_toward_player(RANDOMNESS, (PLAYER_X, PLAYER_Y), (ENEMY_X, ENEMY_Y), NEW_ENEMY_X, NEW_ENEMY_Y) :-
    RANDOMNESS =< 0.5,
    NEW_ENEMY_X = 42.0,
    NEW_ENEMY_Y = 333.0.

move_toward_player(RANDOMNESS, (PLAYER_X, PLAYER_Y), (ENEMY_X, ENEMY_Y), NEW_ENEMY_X, NEW_ENEMY_Y) :-
    RANDOMNESS > 0.5,
    NEW_ENEMY_X = 78.0,
    NEW_ENEMY_Y = 5656.0.







