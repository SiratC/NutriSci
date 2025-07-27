CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS Profiles (
    id UUID DEFAULT uuid_generate_v4() NOT NULL,
    name VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    sex VARCHAR(100) NOT NULL,
    dob TIMESTAMP NOT NULL,
    height NUMERIC NOT NULL,
    weight NUMERIC NOT NULL,
    units VARCHAR(100) NOT NULL,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    modifiedAt TIMESTAMP,

    PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS MealLogs (
    id UUID DEFAULT uuid_generate_v4() NOT NULL,
    profileId UUID NOT NULL,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    modifiedAt TIMESTAMP,

    PRIMARY KEY (id),
    FOREIGN KEY (profileId) REFERENCES Profiles(id)
);

-- Canadian Nutrient File Database Schema

-- Support Tables (no foreign key dependencies)

CREATE TABLE IF NOT EXISTS NutrientName (
    nutrientNameId INTEGER PRIMARY KEY,
    nutrientName VARCHAR(200),
    unit VARCHAR(8)
);

CREATE TABLE IF NOT EXISTS FoodName (
    foodId INTEGER PRIMARY KEY,
    foodDescription TEXT,
    caloriesPer100g INTEGER
);

-- Principal Tables (with foreign key dependencies)

CREATE TABLE IF NOT EXISTS NutrientAmount (
    foodId INTEGER,
    nutrientNameId INTEGER,
    nutrientValue DECIMAL(12,5),
    PRIMARY KEY (foodId, nutrientNameId),
    FOREIGN KEY (foodId) REFERENCES FoodName(foodId),
    FOREIGN KEY (nutrientNameId) REFERENCES NutrientName(nutrientNameId)
);

-- junction table for the 1:M meal:foods relationship
CREATE TABLE IF NOT EXISTS MealLogFoods (
    logId UUID,
    foodId INTEGER,

    PRIMARY KEY (logId, foodId),
    FOREIGN KEY (foodId) REFERENCES FoodName(foodId),
    FOREIGN KEY (logId) REFERENCES MealLogs(id)
);
