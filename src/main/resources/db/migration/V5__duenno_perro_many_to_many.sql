CREATE TABLE duenno_perro (
    duenno_id BIGINT NOT NULL,
    perro_id  BIGINT NOT NULL,
    CONSTRAINT pk_duenno_perro PRIMARY KEY (duenno_id, perro_id),
    CONSTRAINT fk_duenno_perro_duenno FOREIGN KEY (duenno_id) REFERENCES duenno (id) ON DELETE CASCADE,
    CONSTRAINT fk_duenno_perro_perro FOREIGN KEY (perro_id) REFERENCES perro (id) ON DELETE CASCADE
);

-- La PK ya indexa por duenno_id; este indice cubre las busquedas por perro
CREATE INDEX idx_duenno_perro_perro_id ON duenno_perro (perro_id);

-- Conservar las relaciones existentes antes de eliminar la columna
INSERT INTO duenno_perro (duenno_id, perro_id)
SELECT duenno_id, id
FROM perro;

DROP INDEX idx_perro_duenno_id;

ALTER TABLE perro
    DROP CONSTRAINT fk_perro_duenno,
    DROP COLUMN duenno_id;
