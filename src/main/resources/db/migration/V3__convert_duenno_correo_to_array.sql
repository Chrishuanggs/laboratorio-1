DROP INDEX idx_duenno_correo;

ALTER TABLE duenno
    ALTER COLUMN correo TYPE VARCHAR(255)[]
    USING CASE WHEN correo IS NULL THEN NULL ELSE ARRAY[correo] END;

CREATE INDEX idx_duenno_correo ON duenno USING GIN (correo);
