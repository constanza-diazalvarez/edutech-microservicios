INSERT INTO roles (id, rol) VALUES (1, 'ADMIN') ON CONFLICT (id) DO NOTHING;
INSERT INTO roles (id, rol) VALUES (2, 'GERENTE_CURSOS') ON CONFLICT (id) DO NOTHING;
INSERT INTO roles (id, rol) VALUES (3, 'INSTRUCTOR') ON CONFLICT (id) DO NOTHING;
INSERT INTO roles (id, rol) VALUES (4, 'ESTUDIANTE') ON CONFLICT (id) DO NOTHING;
INSERT INTO roles (id, rol) VALUES (5, 'SOPORTE') ON CONFLICT (id) DO NOTHING;

INSERT INTO usuarios (id, nombre, correo, password, rol_id) VALUES (1, 'admin', 'admin@correo.com', 'pass123', 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO usuarios (id, nombre, correo, password, rol_id) VALUES (2, 'pupis', 'pupis@gmail.pu', 'pass456', 2) ON CONFLICT (id) DO NOTHING;
