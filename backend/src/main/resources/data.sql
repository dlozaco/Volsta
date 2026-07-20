-- ROLES
INSERT INTO authorities(id, authority) VALUES
    (1, 'ADMIN'),
    (2, 'MANAGER');

-- Admin
INSERT INTO app_user(id, username, password, authority) VALUES
    (1, 'admin1', 'adminprueba1!', 1),
    (2, 'admin2', 'adminprueba2!', 1);

-- Manager
INSERT INTO app_user(id, username, password, authority) VALUES
    (3, 'manager1', 'manager1!', 2),
    (4, 'manager2', 'manager2!', 2);

INSERT INTO manager(id, name, surname, email, phone_number, user_id) VALUES
    (1, 'Manager1', 'Prueba1', 'manager@prueba.com', '111111111', 3),
    (2, 'Manager2', 'Prueba2', 'manager2@prueba.com', '222222222', 4);

-- Team
INSERT INTO team(id, name, foundation_date) VALUES
    (1, 'QuokkaCV', '2025-10-01'),
    (2, 'Barcelona', '1938-11-29');

INSERT INTO manager_teams(manager_id, teams_id) VALUES
    (1,1),
    (2,2);

-- Player
INSERT INTO player(id, name, surname, email, core_position, dorsal) VALUES
     (1, 'David Lozano', 'Lozano Acosta', 'david.lozano@quokka.es', 1, 10),
     (2, 'Carlos García', 'Martínez López', 'carlos.garcia@quokka.es', 1, 7),
     (3, 'Juan Rodríguez', 'Fernández Silva', 'juan.rodriguez@quokka.es', 3, 8),
     (4, 'Miguel Sánchez', 'Díaz Torres', 'miguel.sanchez@quokka.es', 0, 4),
     (5, 'Antonio', 'García Gómez', 'antonio.garcia@quokka.es', 4, 24),
     (6, 'Antonio López', 'Pérez Gómez', 'antonio.lopez@quokka.es', 2, 47),
     (7, 'Pedro González', 'Ruiz Castillo', 'pedro.gonzalez@barcelona.es', 1, 12),
     (8, 'Francisco Jiménez', 'Moreno Vega', 'francisco.jimenez@barcelona.es', 3,13),
     (9, 'José María', 'Campos Ramírez', 'jose.maria@barcelona.es', 0, 23),
     (10, 'Andrés Navarro', 'Romero Guerrero', 'andres.navarro@barcelona.es', 2, 41),
     (11, 'Roberto Cortés', 'Vargas Mendez', 'roberto.cortes@barcelona.es', 1, 14),
     (12, 'José', 'Jiménez Pérez', 'jose.jimenez@barcelona.es', 4, 1);

INSERT INTO team_players(players_id, team_id) VALUES
    (1,1),
    (2,1),
    (3,1),
    (4,1),
    (5,1),
    (6,1),
    (7,2),
    (8,2),
    (9,2),
    (10,2),
    (11,2),
    (12,2);

-- NOTES
INSERT INTO notes(id, subject, description, player_id) VALUES
    (1, 'Mejora en defensa', 'Necesita trabajar más la posición defensiva y anticipación', 1),
    (2, 'Buen saque', 'Tiene un saque muy consistente, debe seguir practicando', 2),
    (3, 'Velocidad de red', 'Muy rápido en la red, buena verticalidad', 3),
    (4, 'Control de balón', 'Debe mejorar el control en situaciones de presión', 4),
    (5, 'Liderazgo', 'Excelente capitán, motiva al equipo', 6),
    (6, 'Recepción variable', 'Tiene dificultades con recepciones complicadas', 7),
    (7, 'Remate potente', 'Muy buen remate, necesita más precisión', 8),
    (8, 'Colocación inteligente', 'Excelente distribuidor, visión de juego', 10);

-- MATCH
INSERT INTO match(id, local_team_id, visitor_team_id, start_moment, end_moment, place, match_type) VALUES
    (1, 1, 2, '2025-02-15 19:00:00', '2025-02-15 20:30:00', 'Pabellón QuokkaCV', 0),
    (2, 2, 1, '2025-02-20 20:00:00', '2025-02-20 21:45:00', 'Estadio Barcelona', 0),
    (3, 1, 2, '2025-03-01 18:30:00', '2025-03-01 20:15:00', 'Pabellón QuokkaCV', 0),
    (4, 2, 1, '2025-03-10 19:30:00', '2025-03-10 21:00:00', 'Estadio Barcelona', 0);

INSERT INTO match_notes(match_id, notes_id) VALUES
    (1,1),
    (1,2),
    (2,3),
    (2,4),
    (3,5),
    (3,6),
    (4,7),
    (4,8);

-- MATCHSET
INSERT INTO match_set(id, set_number, local_team_score, visitor_team_score) VALUES
    (1, 1, 25, 23),
    (2, 2, 25, 21),
    (3, 3, 22, 25),
    (4, 1, 26, 24),
    (5, 2, 25, 25),
    (6, 3, 25, 23),
    (7, 1, 25, 20),
    (8, 2, 24, 26),
    (9, 3, 25, 22),
    (10, 1, 23, 25),
    (11, 2, 25, 27),
    (12, 3, 26, 24);

INSERT INTO match_sets(match_id, sets_id) VALUES
    (1,1),
    (1,2),
    (1,3),
    (2,4),
    (2,5),
    (2,6),
    (3,7),
    (3,8),
    (3,9),
    (4,10),
    (4,11),
    (4,12);

-- SETPARTICIPATION
INSERT INTO set_participation(id, player_id, match_set_id, points, position_type) VALUES
    (1, 1, 1, 8, 1),
    (2, 2, 1, 6, 1),
    (3, 3, 1, 7, 3),
    (4, 6, 1, 4, 2),
    (5, 1, 2, 10, 1),
    (6, 2, 2, 5, 1),
    (7, 4, 2, 6, 0),
    (8, 3, 2, 4, 3),
    (9, 1, 3, 9, 1),
    (10, 6, 3, 5, 2),
    (11, 7, 4, 11, 1),
    (12, 8, 4, 7, 3),
    (13, 10, 4, 6, 2),
    (14, 11, 4, 3, 1),
    (15, 1, 7, 12, 1),
    (16, 2, 7, 8, 1),
    (17, 3, 7, 5, 3),
    (18, 7, 10, 9, 1),
    (19, 8, 10, 6, 3),
    (20, 10, 10, 7, 2);

