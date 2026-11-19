CREATE TABLE users (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email           TEXT NOT NULL UNIQUE,
    name            TEXT NOT NULL,
    password_hash   TEXT NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE ingredients (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name            TEXT NOT NULL,
    kcal_per100g    NUMERIC(7,2) NOT NULL CHECK (kcal_per100g >= 0),
    protein_per100g NUMERIC(6,2) NOT NULL CHECK (protein_per100g >= 0),
    carbs_per100g   NUMERIC(6,2) NOT NULL CHECK (carbs_per100g >= 0),
    fat_per100g     NUMERIC(6,2) NOT NULL CHECK (fat_per100g >= 0),
    is_favorite     BOOLEAN NOT NULL DEFAULT FALSE,
    barcode         TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (user_id, barcode)
);

CREATE TABLE meals (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name            TEXT NOT NULL,
    is_favorite     BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE meal_items (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    meal_id         BIGINT NOT NULL REFERENCES meals(id) ON DELETE CASCADE,
    ingredient_id   BIGINT NOT NULL REFERENCES ingredients(id) ON DELETE RESTRICT,
    grams           NUMERIC(7,2) NOT NULL CHECK (grams > 0)
);

CREATE TABLE log_entries (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    logged_on       DATE NOT NULL DEFAULT CURRENT_DATE,
    meal_slot       TEXT NOT NULL CHECK (meal_slot IN ('breakfast, lunch, dinner, snack')),
    name            TEXT NOT NULL,
    grams           NUMERIC(7,2) NOT NULL CHECK (grams > 0),
    kcal            NUMERIC(7,2) NOT NULL,
    protein         NUMERIC(6,2) NOT NULL DEFAULT 0,
    carbs           NUMERIC(6,2) NOT NULL DEFAULT 0,
    fat             NUMERIC(6,2) NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE weight_entries (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    measured_on     DATE NOT NULL DEFAULT CURRENT_DATE,
    weight_kg       NUMERIC(5,2) NOT NULL CHECK (weight_kg > 0),
    UNIQUE (user_id, measured_on)
);

CREATE INDEX idx_ingredients_user_id_name ON ingredients (user_id, name);
CREATE INDEX idx_meals_user_id ON meals (user_id);
CREATE INDEX idx_meal_items_meal_id ON meal_items (meal_id);
CREATE INDEX idx_meal_items_ingredient_id ON meal_items (ingredient_id);
CREATE INDEX idx_log_entries_user_id_logged_on ON log_entries (user_id, logged_on);
