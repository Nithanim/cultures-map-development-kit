CREATE TABLE author (
  id             INT          NOT NULL PRIMARY KEY,
  first_name     VARCHAR(50),
  last_name      VARCHAR(50)  NOT NULL
);

CREATE TABLE book (
  id             INT          NOT NULL PRIMARY KEY,
  title          VARCHAR(100) NOT NULL
);

CREATE TABLE author_book (
  author_id      INT          NOT NULL,
  book_id        INT          NOT NULL,

  PRIMARY KEY (author_id, book_id),
  CONSTRAINT fk_ab_author     FOREIGN KEY (author_id)  REFERENCES author (id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_ab_book       FOREIGN KEY (book_id)    REFERENCES book   (id)
);


CREATE TABLE codelens (
    type varchar NOT NULL,
    source_file varchar NOT NULL,
    --update_id INT NOT NULL,
    data other NOT NULL
);
