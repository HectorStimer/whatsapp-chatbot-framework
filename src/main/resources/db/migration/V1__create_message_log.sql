CREATE TABLE message_log (
                             id          BIGSERIAL PRIMARY KEY,
                             group_id    VARCHAR(255) NOT NULL,
                             user_name   VARCHAR(255),
                             message     TEXT         NOT NULL,
                             response    TEXT,
                             created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_message_log_group_id ON message_log (group_id);
CREATE INDEX idx_message_log_created_at ON message_log (created_at);
