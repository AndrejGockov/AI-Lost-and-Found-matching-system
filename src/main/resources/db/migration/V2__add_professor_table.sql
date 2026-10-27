-- Create the room table
CREATE TABLE room (
                      name VARCHAR(255) PRIMARY KEY,
                      capacity INT,
                      equipment_description TEXT,
                      location_description TEXT,
                      type VARCHAR(50)
);

-- Create the professor table
CREATE TABLE professor (
                           id VARCHAR(255) NOT NULL PRIMARY KEY REFERENCES auth_user(id) ON DELETE CASCADE,
                           title VARCHAR(255),
                           ordering_rank INT,
                           office_name VARCHAR(255) REFERENCES room(name) ON DELETE SET NULL,
                           active BOOLEAN,
                           email VARCHAR(255),
                           name VARCHAR(255)
);