CREATE SCHEMA IF NOT EXISTS demo;

CREATE TYPE status AS ENUM ('UNCANVASSED',
    'NOT_HOME',
    'CANVASSED',
    'REFUSED',
    'INACCESSIBLE');

CREATE TABLE IF NOT EXISTS household (
    id SERIAL PRIMARY KEY,
    address VARCHAR UNIQUE NOT NULL,
    latitude FLOAT NOT NULL,
    longitude FLOAT NOT NULL,
    household_status status NOT NULL,
    check(latitude <= 90.0 AND latitude >= -90.0),
    check(longitude <= 180.0 AND longitude >= -180.0)
);

CREATE TABLE IF NOT EXISTS question (
    id SERIAL PRIMARY KEY,
    question VARCHAR UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS answer (
     id SERIAL PRIMARY KEY,
     question_id INT NOT NULL,
     answer VARCHAR NOT NULL,
     FOREIGN KEY (question_id) REFERENCES question(id) ON DELETE CASCADE,
    CONSTRAINT question_answer UNIQUE(question_id, answer)
);

CREATE TABLE IF NOT EXISTS response (
    id SERIAL PRIMARY KEY,
    household_id INT NOT NULL,
    question_id INT NOT NULL,
    answer_id INT NOT NULL,
    --wipe out a household's responses if the household is removed
    FOREIGN KEY (household_id) REFERENCES household(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES question(id) ON DELETE CASCADE,
    FOREIGN KEY (answer_id) REFERENCES answer(id) ON DELETE CASCADE,
    CONSTRAINT household_response UNIQUE (household_id, question_id, answer_id)
);