BEGIN;

INSERT INTO titular (id, tipo_pessoa, cpf, nome, sobrenome, created_at)
VALUES ('11111111-1111-1111-1111-111111111111', 'PF', '12345678901', 'Joao', 'Silva', NOW());

INSERT INTO titular (id, tipo_pessoa, cnpj, nome, created_at)
VALUES ('22222222-2222-2222-2222-222222222222', 'PJ', '12345678000199', 'ACME LTDA', NOW());

INSERT INTO conta_bancaria (id, titular_id, tipo_conta, numero_agencia, numero_conta, created_at, updated_at)
VALUES ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111',
        'CORRENTE', '0001', '12345678', NOW(), NOW());

INSERT INTO conta_bancaria (id, titular_id, tipo_conta, numero_agencia, numero_conta, created_at, updated_at)
VALUES ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '22222222-2222-2222-2222-222222222222',
        'POUPANCA', '0002', '87654321', NOW(), NOW());

COMMIT;
