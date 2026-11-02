-- V3: Carga inicial de dados para desenvolvimento e demonstração
-- Senha de todos os usuários de teste: senha123

-- Tutores
INSERT INTO cv_tutors (name, email, phone, active, created_at) VALUES
    ('Carlos Mendes',   'carlos.mendes@email.com',  '(11) 99001-1111', TRUE, CURRENT_TIMESTAMP),
    ('Ana Paula Silva', 'ana.silva@email.com',       '(11) 98002-2222', TRUE, CURRENT_TIMESTAMP);

-- Clínicas
INSERT INTO cv_clinics (name, address, phone, email, active, created_at) VALUES
    ('Clínica VetSaúde',  'Av. Paulista, 1000 - SP', '(11) 3001-0001', 'contato@vetsaude.com', TRUE, CURRENT_TIMESTAMP),
    ('Hospital Pet Vida', 'Rua Augusta, 500 - SP',   '(11) 3002-0002', 'contato@petvida.com',  TRUE, CURRENT_TIMESTAMP);

-- Pets
INSERT INTO cv_pets (name, species, breed, birth_date, weight_kg, active, created_at, tutor_id) VALUES
    ('Rex',     'DOG', 'Labrador',        '2020-03-15', 28.5, TRUE, CURRENT_TIMESTAMP, 1),
    ('Mia',     'CAT', 'Persa',           '2022-07-10',  4.2, TRUE, CURRENT_TIMESTAMP, 1),
    ('Bolinha', 'DOG', 'Bulldog Francês', '2021-01-20', 12.0, TRUE, CURRENT_TIMESTAMP, 2);

-- Vacinas
INSERT INTO cv_vaccines (pet_id, vaccine_name, applied_at, next_dose_at, batch, created_at) VALUES
    (1, 'V10',         '2025-01-10', '2026-01-10', 'LOTE-001', CURRENT_TIMESTAMP),
    (1, 'Antirrábica', '2025-01-10', '2026-01-10', 'LOTE-002', CURRENT_TIMESTAMP),
    (2, 'Quádrupla',   '2024-06-20', '2025-06-20', 'LOTE-010', CURRENT_TIMESTAMP);

-- Usuários de acesso (senha para todos: senha123)
-- Vet: acessa tudo. Tutores: cada um vinculado ao seu registro em cv_tutors.
INSERT INTO vf_users (name, email, password, role, tutor_id, enabled, created_at) VALUES
    ('Dra. Fernanda Alves', 'vet@vetflow.com',        '$2b$12$C6UZjwGVZxEB9gizbUhAUucjm1kxgatM2nvQP/rtuiQv3ANbhJd1u', 'VET',   NULL, TRUE, CURRENT_TIMESTAMP),
    ('Carlos Mendes',       'carlos.mendes@email.com','$2b$12$/Pcl7AHAuqG3Jnt5egnrueMLb9RsswAHviMCY6ipAEAscEgiv9EDW', 'TUTOR', 1,    TRUE, CURRENT_TIMESTAMP);
