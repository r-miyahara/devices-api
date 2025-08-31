CREATE TABLE IF NOT EXISTS idempotency_keys (
  ikey VARCHAR(200) PRIMARY KEY,
  resource_id UUID NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  expires_at TIMESTAMPTZ NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_idempotency_keys_ikey ON idempotency_keys (ikey);
CREATE INDEX IF NOT EXISTS ix_idempotency_keys_expires_at ON idempotency_keys (expires_at);
