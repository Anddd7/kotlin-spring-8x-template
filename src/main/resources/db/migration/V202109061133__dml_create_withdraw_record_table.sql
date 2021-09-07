CREATE TABLE withdraw_record
(
    id                  BIGSERIAL NOT NULL
        CONSTRAINT withdraw_record_pk PRIMARY KEY,
    merchant_account_id BIGINT    NOT NULL,
    amount              INT       NOT NULL,
    currency            VARCHAR   NOT NULL,
    channel             VARCHAR   NOT NULL,
    status              VARCHAR   NOT NULL
);
