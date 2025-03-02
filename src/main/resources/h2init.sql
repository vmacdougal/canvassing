CREATE SCHEMA IF NOT EXISTS demo;
SET SCHEMA demo;
CREATE TABLE IF NOT EXISTS household (
    id IDENTITY PRIMARY KEY,
    address VARCHAR UNIQUE NOT NULL,
    latitude FLOAT NOT NULL,
    longitude FLOAT NOT NULL,
    status INT NOT NULL,
    check(status >= 0 AND status < 5),
    check(latitude <= 90.0 AND latitude >= -90.0),
    check(longitude <= 180.0 AND longitude >= -180.0)
);

CREATE TABLE IF NOT EXISTS question (
    id IDENTITY PRIMARY KEY,
    question VARCHAR UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS answer (
     id IDENTITY PRIMARY KEY,
     question_id INT NOT NULL,
     answer VARCHAR NOT NULL,
     FOREIGN KEY (question_id) REFERENCES question(id) ON DELETE CASCADE,
    CONSTRAINT question_answer UNIQUE(question_id, answer)
);

CREATE TABLE IF NOT EXISTS response (
    id IDENTITY PRIMARY KEY,
    household_id INT NOT NULL,
    question_id INT NOT NULL,
    answer_id INT NOT NULL,
    --wipe out a household's responses if the household is removed
    FOREIGN KEY (household_id) REFERENCES household(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES question(id) ON DELETE CASCADE,
    FOREIGN KEY (answer_id) REFERENCES answer(id) ON DELETE CASCADE,
    CONSTRAINT household_response UNIQUE (household_id, question_id, answer_id)
);