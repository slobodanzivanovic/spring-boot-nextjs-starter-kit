CREATE EXTENSION IF NOT EXISTS pgcrypto; -- JUST TO MAKE SURE

CREATE TABLE IF NOT EXISTS public.users (
    id uuid PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    profile_image_url VARCHAR(255),
    phone_number VARCHAR(255),
    birth_date DATE,
    gender VARCHAR(255), -- CHANGE THIS??
    verification_code VARCHAR(255),
    verification_code_expires_at TIMESTAMP(6) WITHOUT TIME ZONE,
    password_reset_token VARCHAR(255),
    password_reset_expires_at TIMESTAMP(6) WITHOUT TIME ZONE,
    enabled BOOLEAN NOT NULL DEFAULT FALSE,
    account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,
    failed_login_attempts INTEGER DEFAULT 0,
    account_locked_until TIMESTAMP(6) WITHOUT TIME ZONE,
    password_changed_at TIMESTAMP(6) WITHOUT TIME ZONE,
    last_login_at TIMESTAMP(6) WITHOUT TIME ZONE,
    last_login_ip VARCHAR(255),
    created_at TIMESTAMP(6) WITHOUT TIME ZONE,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP(6) WITHOUT TIME ZONE,
    deleted_by VARCHAR(255),
    version BIGINT
);

CREATE TABLE IF NOT EXISTS public.roles (
    id uuid PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    status BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP(6) WITHOUT TIME ZONE,
    deleted_by VARCHAR(255),
    version BIGINT
);

CREATE TABLE IF NOT EXISTS public.user_roles (
    user_id uuid NOT NULL,
    role_id uuid NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES public.users (id),
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES public.roles (id)
);

INSERT INTO
    public.roles (id, name, status, created_at, updated_at, version)
VALUES
(
    gen_random_uuid(),
    'ROLE_USER',
    TRUE,
    NOW(),
    NOW(),
    0
),
(
    gen_random_uuid(),
    'ROLE_ADMIN',
    TRUE,
    NOW(),
    NOW(),
    0
)
ON CONFLICT (name) DO NOTHING;

CREATE TABLE IF NOT EXISTS public.uploaded_files (
    id uuid PRIMARY KEY,
    url VARCHAR(255) NOT NULL,
    size BIGINT,
    original_file_name VARCHAR(255),
    extension VARCHAR(50),
    uploaded_at TIMESTAMP(6) WITHOUT TIME ZONE,
    user_id uuid,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP(6) WITHOUT TIME ZONE,
    deleted_by VARCHAR(255),
    version BIGINT,
    CONSTRAINT fk_uploaded_files_user FOREIGN KEY (user_id) REFERENCES public.users (id)
);

CREATE INDEX IF NOT EXISTS idx_users_enabled ON public.users (enabled);
CREATE INDEX IF NOT EXISTS idx_users_account_non_locked ON public.users (account_non_locked);
CREATE INDEX IF NOT EXISTS idx_users_deleted ON public.users (deleted);
CREATE INDEX IF NOT EXISTS idx_roles_status ON public.roles (status);
CREATE INDEX IF NOT EXISTS idx_roles_deleted ON public.roles (deleted);
CREATE INDEX IF NOT EXISTS idx_uploaded_files_user_id ON public.uploaded_files (user_id);
CREATE INDEX IF NOT EXISTS idx_uploaded_files_deleted ON public.uploaded_files (deleted);
