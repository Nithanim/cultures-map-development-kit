CREATE TABLE codelens (
    type varchar NOT NULL,
    source_file varchar NOT NULL,
    data other NOT NULL
);

CREATE TABLE diagnostic (
    type varchar NOT NULL,
    source_file varchar NOT NULL,
    data other NOT NULL
);

CREATE TABLE intelligence (
    source_file varchar NOT NULL,
    line_number integer NOT NULL,
    line_type varchar NOT NULL, -- command/category/...
    category_type varchar NOT NULL,
    category_number integer NOT NULL, -- if multiple categories with the same name exist (e.g. MissionData)
    data other NOT NULL,

    PRIMARY KEY (source_file, line_number, line_type)
)
