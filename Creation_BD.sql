-- USERS
CREATE TABLE IF NOT EXISTS users (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  username text NOT NULL,
  email text NOT NULL,
  password text NOT NULL,
  date_created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- CATEGORIES
CREATE TABLE IF NOT EXISTS categories (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  name text NOT NULL,
  type VARCHAR NOT NULL
);

-- GOALS
CREATE TABLE IF NOT EXISTS goals (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id uuid NOT NULL REFERENCES users(id),
  title text NOT NULL,
  target_amount numeric NOT NULL,
  current_amount numeric NOT NULL,
  deadline timestamp
);

-- TRANSACTIONS
CREATE TABLE IF NOT EXISTS transactions (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id uuid NOT NULL REFERENCES users(id),
  category_id uuid REFERENCES categories(id),
  amount numeric NOT NULL,
  date timestamp NOT NULL,
  frequency VARCHAR,
  description text,
  transaction_type VARCHAR NOT NULL,
  is_template boolean NOT NULL DEFAULT false
);