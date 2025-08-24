CREATE TABLE IF NOT EXISTS devices (
  id UUID PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  brand VARCHAR(255) NOT NULL,
  state VARCHAR(20) NOT NULL,
  creation_time TIMESTAMPTZ NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_devices_brand ON devices (brand);
CREATE INDEX IF NOT EXISTS idx_devices_state ON devices (state);

-- Opcional: reforça domínio no banco
ALTER TABLE devices
  ADD CONSTRAINT chk_devices_state
  CHECK (state IN ('AVAILABLE','IN_USE','INACTIVE'));
