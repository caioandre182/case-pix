CREATE TYPE tipo_pessoa_enum AS ENUM ('PF', 'PJ');
CREATE TYPE tipo_conta_enum AS ENUM ('CORRENTE', 'POUPANCA');
CREATE TYPE tipo_chave_enum AS ENUM ('CELULAR', 'CPF', 'CNPJ', 'EMAIL', 'ALEATORIA');

CREATE TABLE titular (
    id UUID PRIMARY KEY,
    tipo_pessoa tipo_pessoa_enum NOT NULL,
    documento TEXT,
    nome TEXT,
    sobrenome TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE conta_bancaria (
    id UUID PRIMARY KEY,
    titular_id UUID NOT NULL REFERENCES titular(id),
    tipo_conta tipo_conta_enum NOT NULL,
    numero_agencia TEXT NOT NULL,
    numero_conta TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_conta_unica_por_titular UNIQUE (titular_id, tipo_conta, numero_agencia, numero_conta)
);

CREATE INDEX idx_conta_titular ON conta_bancaria(titular_id);

CREATE TABLE pix_key (
    id_registro UUID PRIMARY KEY,
    titular_id UUID NOT NULL REFERENCES titular(id),
    conta_id UUID NOT NULL REFERENCES conta_bancaria(id),
    tipo_chave tipo_chave_enum NOT NULL,
    valor_chave TEXT NOT NULL,
    nome_correntista TEXT NOT NULL,
    sobrenome_correntista TEXT NOT NULL,
    ativa BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uk_pix_valor_chave UNIQUE (valor_chave),

    CONSTRAINT ck_pix_valor_por_tipo CHECK (
        CASE tipo_chave
            WHEN 'CELULAR'   THEN valor_chave ~ '^\+[1-9][0-9]{1,14}$'
            WHEN 'CPF'       THEN valor_chave ~ '^[0-9]{11}$'
            WHEN 'CNPJ'      THEN valor_chave ~ '^[0-9]{14}$'
            WHEN 'EMAIL'     THEN valor_chave ~ '^[A-Za-z0-9._%+\-]+@[A-Za-z0-9.\-]+\.[A-Za-z]{2,}$'
            WHEN 'ALEATORIA' THEN valor_chave ~ '^[0-9a-fA-F\-]{36}$'
            ELSE FALSE
        END
    )
);

CREATE INDEX idx_pix_titular ON pix_key(titular_id);
CREATE INDEX idx_pix_conta   ON pix_key(conta_id);
CREATE INDEX idx_pix_tipo    ON pix_key(tipo_chave);

CREATE TABLE app_user (
    id UUID PRIMARY KEY,
    nome_usuario TEXT NOT NULL UNIQUE,
    hash_senha TEXT NOT NULL,
    perfis TEXT NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    tentativas_falhas INT NOT NULL DEFAULT 0,
    bloqueado_ate TIMESTAMPTZ NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);


