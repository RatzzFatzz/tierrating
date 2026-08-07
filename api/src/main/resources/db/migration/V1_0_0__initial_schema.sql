-- user
CREATE TABLE users
(
    id         BIGINT                      NOT NULL,
    username   VARCHAR(255)                NOT NULL,
    email      VARCHAR(255)                NOT NULL,
    password   VARCHAR(255)                NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    bio        VARCHAR(255),
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT uc_users_username UNIQUE (username);

-- SyncJob
CREATE TABLE sync_jobs
(
    id           BIGINT                      NOT NULL,
    user_id      BIGINT                      NOT NULL,
    media_source VARCHAR(255)                NOT NULL,
    media_type   VARCHAR(255)                NOT NULL,
    status       VARCHAR(255)                NOT NULL,
    error        VARCHAR(255),
    started_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    completed_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_sync_jobs PRIMARY KEY (id)
);

ALTER TABLE sync_jobs
    ADD CONSTRAINT FK_SYNC_JOBS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX idx_syncjob_user_id ON sync_jobs (user_id);

CREATE UNIQUE INDEX idx_unique_active_sync_job
    ON sync_jobs (user_id, media_source, media_type)
    WHERE status IN ('PENDING', 'IN_PROGRESS');

-- Tierlist
CREATE TABLE tierlists
(
    id         BIGINT       NOT NULL,
    user_id    BIGINT       NOT NULL,
    service    VARCHAR(255) NOT NULL,
    type       VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_tierlists PRIMARY KEY (id)
);

CREATE TABLE tiers
(
    tierlist_id    BIGINT           NOT NULL,
    name           VARCHAR(255)     NOT NULL,
    color          VARCHAR(255),
    score          DOUBLE PRECISION NOT NULL,
    adjusted_score DOUBLE PRECISION NOT NULL
);

ALTER TABLE tiers
    ADD CONSTRAINT uc_tiers_tierlist_id_score UNIQUE (tierlist_id, score);

ALTER TABLE tierlists
    ADD CONSTRAINT uc_tierlists_user_id_service_type UNIQUE (user_id, service, type);

ALTER TABLE tierlists
    ADD CONSTRAINT FK_TIERLISTS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX idx_tierlist_user_id ON tierlists (user_id);

ALTER TABLE tiers
    ADD CONSTRAINT fk_tiers_on_tierlist FOREIGN KEY (tierlist_id) REFERENCES tierlists (id);

-- TMDB cover cache
CREATE TABLE tmdb_cover_cache
(
    id        BIGINT NOT NULL,
    season    BIGINT,
    cover_url VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_tmdbcovercache PRIMARY KEY (id)
);

ALTER TABLE tmdb_cover_cache
    ADD CONSTRAINT uc_tmdb_cover_cache_id_season UNIQUE (id, season);

-- media type settings
CREATE TABLE media_source_connections
(
    id                  BIGINT       NOT NULL,
    user_id             BIGINT       NOT NULL,
    source              VARCHAR(255) NOT NULL,
    third_party_user_id VARCHAR(255) NOT NULL,
    access_token        VARCHAR(2047),
    refresh_token       VARCHAR(2047),
    expires_on          TIMESTAMP WITHOUT TIME ZONE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_media_source_connections PRIMARY KEY (id)
);

ALTER TABLE media_source_connections
    ADD CONSTRAINT uc_media_source_connections_third_party_user_id_source UNIQUE (third_party_user_id, source);

ALTER TABLE media_source_connections
    ADD CONSTRAINT fk_media_source_connections_on_user FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX idx_media_source_connections_user_id ON media_source_connections (user_id);

CREATE TABLE hidden_states
(
    media_type_settings_id BIGINT NOT NULL,
    hidden_state           VARCHAR(255)
);

CREATE TABLE media_type_settings
(
    id            BIGINT   NOT NULL,
    connection_id BIGINT,
    type          SMALLINT NOT NULL,
    login_pull    BOOLEAN  NOT NULL,
    auto_push     BOOLEAN  NOT NULL,
    show_public   BOOLEAN  NOT NULL,
    hidden        BOOLEAN  NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_media_type_settings PRIMARY KEY (id)
);

ALTER TABLE media_type_settings
    ADD CONSTRAINT FK_MEDIA_TYPE_SETTINGS_ON_CONNECTION FOREIGN KEY (connection_id) REFERENCES media_source_connections (id);

CREATE INDEX idx_media_type_settings_connection_id ON media_type_settings (connection_id);

ALTER TABLE hidden_states
    ADD CONSTRAINT fk_hidden_states_on_media_type_settings FOREIGN KEY (media_type_settings_id) REFERENCES media_type_settings (id);

-- media entries

CREATE TABLE anilist_media_entries
(
    id           BIGINT                      NOT NULL,
    type         VARCHAR(255),
    title        VARCHAR(255),
    cover_url    VARCHAR(1023),
    cover_path   VARCHAR(1023),
    created_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    title_romaji VARCHAR(255),
    CONSTRAINT pk_anilist_media_entries PRIMARY KEY (id)
);

CREATE TABLE steam_media_entries
(
    id         BIGINT                      NOT NULL,
    type       VARCHAR(255),
    title      VARCHAR(255),
    cover_url  VARCHAR(1023),
    cover_path VARCHAR(1023),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_steam_media_entries PRIMARY KEY (id)
);

CREATE TABLE trakt_media_entries
(
    id         BIGINT                      NOT NULL,
    type       VARCHAR(255),
    title      VARCHAR(255),
    cover_url  VARCHAR(1023),
    cover_path VARCHAR(1023),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    season     INTEGER,
    CONSTRAINT pk_trakt_media_entries PRIMARY KEY (id)
);

-- media entry states
CREATE TABLE user_media_entry_states
(
    id       BIGINT       NOT NULL,
    user_id  BIGINT       NOT NULL,
    source   VARCHAR(255) NOT NULL,
    entry_id BIGINT       NOT NULL,
    score    FLOAT        NOT NULL,
    state    VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_user_media_entry_states PRIMARY KEY (id)
);

ALTER TABLE user_media_entry_states
    ADD CONSTRAINT uc_user_media_entry_states_user_id_source_entry_id UNIQUE (user_id, source, entry_id);

ALTER TABLE user_media_entry_states
    ADD CONSTRAINT FK_USER_MEDIA_ENTRY_STATES_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX idx_user_media_entry_state_user_id ON user_media_entry_states (user_id);