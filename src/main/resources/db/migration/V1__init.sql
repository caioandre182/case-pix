BEGIN;

CREATE TABLE titular (
  id UUID PRIMARY KEY,
  tipo_pessoa VARCHAR(2) NOT NULL,
  cpf VARCHAR(11),
  cnpj VARCHAR(14),
  nome VARCHAR(30) NOT NULL,
  sobrenome VARCHAR(45) NULL,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  CONSTRAINT uk_titular_cpf  UNIQUE (cpf),
  CONSTRAINT uk_titular_cnpj UNIQUE (cnpj)
);

CREATE TABLE conta_bancaria (
  id UUID PRIMARY KEY,
  titular_id UUID NOT NULL REFERENCES titular (id),
  tipo_conta VARCHAR(10) NOT NULL,
  numero_agencia VARCHAR(4) NOT NULL,
  numero_conta VARCHAR(8) NOT NULL,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW(),
  CONSTRAINT uk_conta_unica_por_titular
        UNIQUE (titular_id, tipo_conta, numero_agencia, numero_conta)
);

CREATE INDEX idx_conta_ag_cc_tipo
  ON conta_bancaria (numero_agencia, numero_conta, tipo_conta);

CREATE TABLE pix_chave (
  id UUID PRIMARY KEY,
  conta_id UUID NOT NULL REFERENCES conta_bancaria (id),
  tipo_chave VARCHAR(9) NOT NULL,
  valor_chave VARCHAR(77) NOT NULL,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW(),
  deleted_at TIMESTAMPTZ NULL,
  CONSTRAINT uk_pix_valor UNIQUE (valor_chave)
);

CREATE INDEX idx_pix_conta ON pix_chave (conta_id);
CREATE INDEX idx_pix_tipo ON pix_chave (tipo_chave);
CREATE INDEX idx_pix_created_at ON pix_chave (created_at);
CREATE INDEX idx_pix_dt_inativacao ON pix_chave (deleted_at);

COMMIT;
