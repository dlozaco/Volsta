-- ROLES
INSERT INTO authorities(id, authority) VALUES
    (1, 'ADMIN'),
    (2, 'MANAGER');

-- Admin
-- password: Adminprueba1! y 2
INSERT INTO app_user(id, username, password, authority) VALUES
    (1, 'admin1', '$2b$10$BmaCz6sVPGjTcGDtni.M3u701mbsaAFxykvqlJuEXIz98f3VufENG', 1),
    (2, 'admin2', '$2b$10$eXxI8eN7dttXCGJNdzVOnuQR507.FOPYk420A4uuZjWC9IKwvq.RW', 1);

-- Manager
-- password: Managerprueba1! y 2
INSERT INTO app_user(id, username, password, authority) VALUES
    (3, 'manager1', '$2b$10$4IERjXlNeLxReEZXtVizqOHKPeMYLWe46QDliynJJmsJ8/uUxaCFu', 2),
    (4, 'manager2', '$2b$10$BbVzhUGEwndf3M8xW73zMeoan7G6S78HEHMtlU51qjZU/6z84Hx3i', 2);

INSERT INTO manager(id, name, surname, email, phone_number, user_id) VALUES
    (1, 'Manager1', 'Prueba1', 'manager@prueba.com', '111111111', 3),
    (2, 'Manager2', 'Prueba2', 'manager2@prueba.com', '222222222', 4);

-- Team
INSERT INTO team(id, name, foundation_date, owner_id) VALUES
    (1, 'QuokkaCV', '2025-10-01', 1),
    (2, 'Barcelona', '1938-11-29', 2);

-- Player
INSERT INTO player(id, name, surname, email, core_position, dorsal, active) VALUES
     (1, 'David Lozano', 'Lozano Acosta', 'david.lozano@quokka.es', 1, 10, true),
     (2, 'Carlos García', 'Martínez López', 'carlos.garcia@quokka.es', 1, 7, true),
     (3, 'Juan Rodríguez', 'Fernández Silva', 'juan.rodriguez@quokka.es', 3, 8, true),
     (4, 'Miguel Sánchez', 'Díaz Torres', 'miguel.sanchez@quokka.es', 0, 4, true),
     (5, 'Antonio', 'García Gómez', 'antonio.garcia@quokka.es', 4, 24, true),
     (6, 'Antonio López', 'Pérez Gómez', 'antonio.lopez@quokka.es', 2, 47, true),
     (7, 'Pedro González', 'Ruiz Castillo', 'pedro.gonzalez@barcelona.es', 1, 12, true),
     (8, 'Francisco Jiménez', 'Moreno Vega', 'francisco.jimenez@barcelona.es', 3,13, true),
     (9, 'José María', 'Campos Ramírez', 'jose.maria@barcelona.es', 0, 23, true),
     (10, 'Andrés Navarro', 'Romero Guerrero', 'andres.navarro@barcelona.es', 2, 41, true),
     (11, 'Roberto Cortés', 'Vargas Mendez', 'roberto.cortes@barcelona.es', 1, 14, true),
     (12, 'José', 'Jiménez Pérez', 'jose.jimenez@barcelona.es', 4, 1, true);

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
    (8, 'Colocación inteligente', 'Excelente distribuidor, visión de juego', 10),
    (9, 'MVP del partido', 'Máximo anotador, sostuvo al equipo en los momentos clave', 1),
    (10, 'Ataque decisivo', 'Lideró el ataque local y cerró el partido con fuerza', 8),
    (11, 'Actuación destacada', 'Decisivo en el tercer set, varios puntos clave en bloqueo', 3),
    (12, 'Servicio sólido', 'Su saque mantuvo la presión y aseguró la victoria', 6);

-- MATCH
INSERT INTO match(id, local_team_id, visitor_team_id, start_moment, end_moment, place, match_type) VALUES
    (1, 1, 2, '2025-02-15 19:00:00', '2025-02-15 20:30:00', 'Pabellón QuokkaCV', 0),
    (2, 2, 1, '2025-02-20 20:00:00', '2025-02-20 21:45:00', 'Estadio Barcelona', 0),
    (3, 1, 2, '2025-03-01 18:30:00', '2025-03-01 20:15:00', 'Pabellón QuokkaCV', 0),
    (4, 2, 1, '2025-03-10 19:30:00', '2025-03-10 21:00:00', 'Estadio Barcelona', 0);

INSERT INTO match_notes(match_id, notes_id) VALUES
    (1,1),
    (1,2),
    (1,9),
    (2,3),
    (2,4),
    (2,10),
    (3,5),
    (3,6),
    (3,11),
    (4,7),
    (4,8),
    (4,12);

-- MATCHSET
INSERT INTO match_set(id, set_number, local_team_score, visitor_team_score) VALUES
    (1, 1, 25, 23),
    (2, 2, 25, 21),
    (3, 3, 22, 25),
    (4, 1, 26, 24),
    (5, 2, 23, 25),
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
-- Los puntos de cada equipo en cada set suman exactamente el marcador del set.
INSERT INTO set_participation(id, player_id, match_set_id, points, faults, position_type) VALUES
    -- Partido 1: QuokkaCV 2 - Barcelona 1 (25-23, 25-21, 22-25)
    -- Set 1 (25-23)
    (1, 1, 1, 8, 2, 1),
    (2, 2, 1, 6, 1, 1),
    (3, 3, 1, 7, 2, 3),
    (4, 4, 1, 2, 0, 0),
    (5, 5, 1, 0, 1, 4),
    (6, 6, 1, 2, 1, 2),
    (7, 7, 1, 5, 1, 1),
    (8, 8, 1, 7, 2, 3),
    (9, 9, 1, 2, 0, 0),
    (10, 10, 1, 6, 2, 2),
    (11, 11, 1, 2, 1, 1),
    (12, 12, 1, 1, 0, 4),
    -- Set 2 (25-21)
    (13, 1, 2, 7, 1, 1),
    (14, 2, 2, 5, 2, 1),
    (15, 3, 2, 8, 2, 3),
    (16, 4, 2, 2, 0, 0),
    (17, 5, 2, 1, 1, 4),
    (18, 6, 2, 2, 1, 2),
    (19, 7, 2, 4, 1, 1),
    (20, 8, 2, 6, 2, 3),
    (21, 9, 2, 2, 0, 0),
    (22, 10, 2, 5, 1, 2),
    (23, 11, 2, 3, 1, 1),
    (24, 12, 2, 1, 0, 4),
    -- Set 3 (22-25)
    (25, 1, 3, 6, 2, 1),
    (26, 2, 3, 5, 1, 1),
    (27, 3, 3, 6, 2, 3),
    (28, 4, 3, 2, 1, 0),
    (29, 5, 3, 0, 0, 4),
    (30, 6, 3, 3, 1, 2),
    (31, 7, 3, 5, 1, 1),
    (32, 8, 3, 8, 2, 3),
    (33, 9, 3, 2, 0, 0),
    (34, 10, 3, 6, 2, 2),
    (35, 11, 3, 3, 1, 1),
    (36, 12, 3, 1, 0, 4),
    -- Partido 2: Barcelona 2 - QuokkaCV 1 (26-24, 23-25, 25-23)
    -- Set 4 (26-24)
    (37, 7, 4, 6, 1, 1),
    (38, 8, 4, 8, 2, 3),
    (39, 9, 4, 2, 0, 0),
    (40, 10, 4, 7, 2, 2),
    (41, 11, 4, 2, 1, 1),
    (42, 12, 4, 1, 0, 4),
    (43, 1, 4, 6, 2, 1),
    (44, 2, 4, 5, 1, 1),
    (45, 3, 4, 7, 2, 3),
    (46, 4, 4, 2, 0, 0),
    (47, 5, 4, 0, 1, 4),
    (48, 6, 4, 4, 1, 2),
    -- Set 5 (23-25)
    (49, 7, 5, 5, 2, 1),
    (50, 8, 5, 7, 2, 3),
    (51, 9, 5, 2, 0, 0),
    (52, 10, 5, 6, 2, 2),
    (53, 11, 5, 2, 1, 1),
    (54, 12, 5, 1, 0, 4),
    (55, 1, 5, 7, 1, 1),
    (56, 2, 5, 6, 2, 1),
    (57, 3, 5, 7, 2, 3),
    (58, 4, 5, 2, 0, 0),
    (59, 5, 5, 0, 1, 4),
    (60, 6, 5, 3, 1, 2),
    -- Set 6 (25-23)
    (61, 7, 6, 6, 1, 1),
    (62, 8, 6, 8, 2, 3),
    (63, 9, 6, 2, 0, 0),
    (64, 10, 6, 6, 2, 2),
    (65, 11, 6, 2, 1, 1),
    (66, 12, 6, 1, 0, 4),
    (67, 1, 6, 6, 2, 1),
    (68, 2, 6, 5, 1, 1),
    (69, 3, 6, 6, 2, 3),
    (70, 4, 6, 2, 1, 0),
    (71, 5, 6, 1, 1, 4),
    (72, 6, 6, 3, 1, 2),
    -- Partido 3: QuokkaCV 2 - Barcelona 1 (25-20, 24-26, 25-22)
    -- Set 7 (25-20)
    (73, 1, 7, 8, 2, 1),
    (74, 2, 7, 6, 1, 1),
    (75, 3, 7, 7, 2, 3),
    (76, 4, 7, 2, 0, 0),
    (77, 5, 7, 0, 1, 4),
    (78, 6, 7, 2, 1, 2),
    (79, 7, 7, 4, 2, 1),
    (80, 8, 7, 6, 1, 3),
    (81, 9, 7, 2, 0, 0),
    (82, 10, 7, 5, 2, 2),
    (83, 11, 7, 2, 1, 1),
    (84, 12, 7, 1, 0, 4),
    -- Set 8 (24-26)
    (85, 1, 8, 6, 1, 1),
    (86, 2, 8, 5, 2, 1),
    (87, 3, 8, 6, 2, 3),
    (88, 4, 8, 2, 0, 0),
    (89, 5, 8, 0, 1, 4),
    (90, 6, 8, 5, 1, 2),
    (91, 7, 8, 5, 1, 1),
    (92, 8, 8, 8, 2, 3),
    (93, 9, 8, 2, 0, 0),
    (94, 10, 8, 7, 2, 2),
    (95, 11, 8, 3, 1, 1),
    (96, 12, 8, 1, 0, 4),
    -- Set 9 (25-22)
    (97, 1, 9, 7, 2, 1),
    (98, 2, 9, 5, 1, 1),
    (99, 3, 9, 8, 2, 3),
    (100, 4, 9, 2, 0, 0),
    (101, 5, 9, 0, 1, 4),
    (102, 6, 9, 3, 1, 2),
    (103, 7, 9, 5, 1, 1),
    (104, 8, 9, 7, 2, 3),
    (105, 9, 9, 2, 0, 0),
    (106, 10, 9, 5, 2, 2),
    (107, 11, 9, 2, 1, 1),
    (108, 12, 9, 1, 0, 4),
    -- Partido 4: Barcelona 1 - QuokkaCV 2 (23-25, 25-27, 26-24)
    -- Set 10 (23-25)
    (109, 7, 10, 5, 1, 1),
    (110, 8, 10, 7, 2, 3),
    (111, 9, 10, 2, 0, 0),
    (112, 10, 10, 6, 2, 2),
    (113, 11, 10, 2, 1, 1),
    (114, 12, 10, 1, 0, 4),
    (115, 1, 10, 7, 2, 1),
    (116, 2, 10, 6, 1, 1),
    (117, 3, 10, 7, 2, 3),
    (118, 4, 10, 2, 0, 0),
    (119, 5, 10, 0, 1, 4),
    (120, 6, 10, 3, 1, 2),
    -- Set 11 (25-27)
    (121, 7, 11, 5, 2, 1),
    (122, 8, 11, 8, 2, 3),
    (123, 9, 11, 2, 0, 0),
    (124, 10, 11, 6, 2, 2),
    (125, 11, 11, 3, 1, 1),
    (126, 12, 11, 1, 0, 4),
    (127, 1, 11, 8, 2, 1),
    (128, 2, 11, 6, 1, 1),
    (129, 3, 11, 8, 2, 3),
    (130, 4, 11, 2, 0, 0),
    (131, 5, 11, 0, 1, 4),
    (132, 6, 11, 3, 1, 2),
    -- Set 12 (26-24)
    (133, 7, 12, 6, 1, 1),
    (134, 8, 12, 8, 2, 3),
    (135, 9, 12, 2, 0, 0),
    (136, 10, 12, 7, 2, 2),
    (137, 11, 12, 2, 1, 1),
    (138, 12, 12, 1, 0, 4),
    (139, 1, 12, 6, 2, 1),
    (140, 2, 12, 5, 1, 1),
    (141, 3, 12, 7, 2, 3),
    (142, 4, 12, 2, 0, 0),
    (143, 5, 12, 1, 1, 4),
    (144, 6, 12, 3, 1, 2);

