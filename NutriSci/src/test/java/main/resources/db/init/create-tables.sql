CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS Profiles (
    id UUID DEFAULT uuid_generate_v4() NOT NULL,
    name VARCHAR(100) UNIQUE NOT NULL,
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
    -- TODO: should prob reference an ingredient log table or something for the 1:M relationship
    items VARCHAR(100) NOT NULL,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

    PRIMARY KEY (id)
)

-- Canadian Nutrient File Database Schema

-- Support Tables (no foreign key dependencies)

CREATE TABLE IF NOT EXISTS FoodGroup (
    foodGroupId INTEGER PRIMARY KEY,
    foodGroupCode INTEGER,
    foodGroupName VARCHAR(200),
    foodGroupNameF VARCHAR(200)
);

CREATE TABLE IF NOT EXISTS FoodSource (
    foodSourceId INTEGER PRIMARY KEY,
    foodSourceCode INTEGER,
    foodSourceDescription VARCHAR(200),
    foodSourceDescriptionF VARCHAR(200)
);

CREATE TABLE IF NOT EXISTS NutrientName (
    nutrientNameId INTEGER PRIMARY KEY,
    nutrientCode INTEGER,
    nutrientSymbol VARCHAR(10),
    unit VARCHAR(8),
    nutrientName VARCHAR(200),
    nutrientNameF VARCHAR(200),
    tagname VARCHAR(20),
    nutrientDecimals INTEGER
);

CREATE TABLE IF NOT EXISTS NutrientSource (
    nutrientSourceId INTEGER PRIMARY KEY,
    nutrientSourceCode INTEGER,
    nutrientSourceDescription VARCHAR(200),
    nutrientSourceDescriptionF VARCHAR(200)
);

CREATE TABLE IF NOT EXISTS MeasureName (
    measureId INTEGER PRIMARY KEY,
    measureName VARCHAR(200),
    measureNameF VARCHAR(200)
);

CREATE TABLE IF NOT EXISTS RefuseName (
    refuseId INTEGER PRIMARY KEY,
    refuseName VARCHAR(200),
    refuseNameF VARCHAR(200)
);

CREATE TABLE IF NOT EXISTS YieldName (
    yieldId INTEGER PRIMARY KEY,
    yieldName VARCHAR(200),
    yieldNameF VARCHAR(200)
);

-- Principal Tables (with foreign key dependencies)

CREATE TABLE IF NOT EXISTS FoodName (
    foodId INTEGER PRIMARY KEY,
    foodCode INTEGER,
    foodGroupId INTEGER,
    foodSourceId INTEGER,
    foodDescription VARCHAR(255),
    foodDescriptionF VARCHAR(255),
    countryCode INTEGER,
    foodDateOfEntry DATE,
    foodDateOfPublication DATE,
    scientificName VARCHAR(100),
    FOREIGN KEY (foodGroupId) REFERENCES FoodGroup(foodGroupId),
    FOREIGN KEY (foodSourceId) REFERENCES FoodSource(foodSourceId)
);

CREATE TABLE IF NOT EXISTS NutrientAmount (
    foodId INTEGER,
    nutrientNameId INTEGER,
    nutrientValue DECIMAL(12,5),
    standardError DECIMAL(8,4),
    numberOfObservations INTEGER,
    nutrientSourceId INTEGER,
    nutrientDateOfEntry DATE,
    PRIMARY KEY (foodId, nutrientNameId),
    FOREIGN KEY (foodId) REFERENCES FoodName(foodId),
    FOREIGN KEY (nutrientNameId) REFERENCES NutrientName(nutrientNameId),
    FOREIGN KEY (nutrientSourceId) REFERENCES NutrientSource(nutrientSourceId)
);

CREATE TABLE IF NOT EXISTS ConversionFactor (
    foodId INTEGER,
    measureId INTEGER,
    conversionFactorValue DECIMAL(10,5),
    convFactorDateOfEntry DATE,
    PRIMARY KEY (foodId, measureId),
    FOREIGN KEY (foodId) REFERENCES FoodName(foodId),
    FOREIGN KEY (measureId) REFERENCES MeasureName(measureId)
);

CREATE TABLE IF NOT EXISTS RefuseAmount (
    foodId INTEGER,
    refuseId INTEGER,
    refuseAmount DECIMAL(9,5),
    refuseDateOfEntry DATE,
    PRIMARY KEY (foodId, refuseId),
    FOREIGN KEY (foodId) REFERENCES FoodName(foodId),
    FOREIGN KEY (refuseId) REFERENCES RefuseName(refuseId)
);

CREATE TABLE IF NOT EXISTS YieldAmount (
    foodId INTEGER,
    yieldId INTEGER,
    yieldAmount DECIMAL(9,5),
    yieldDateOfEntry DATE,
    PRIMARY KEY (foodId, yieldId),
    FOREIGN KEY (foodId) REFERENCES FoodName(foodId),
    FOREIGN KEY (yieldId) REFERENCES YieldName(yieldId)
);