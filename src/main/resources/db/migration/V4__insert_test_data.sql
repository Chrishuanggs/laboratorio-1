-- 100 Duennos
-- cedula: '1' + 8-digit sequence (100000001..100000100)
-- correo: every 10th duenno has none, every 3rd has two, the rest have one
WITH datos AS (
    SELECT i,
           (ARRAY['Ana', 'Carlos', 'María', 'José', 'Laura', 'Diego', 'Sofía', 'Andrés', 'Valeria', 'Luis',
                  'Camila', 'Jorge', 'Daniela', 'Pablo', 'Gabriela', 'Ricardo', 'Fernanda', 'Esteban', 'Natalia', 'Óscar'])[1 + (i - 1) % 20] AS nombre,
           (ARRAY['Rodríguez', 'Jiménez', 'Fernández', 'Castro', 'Méndez', 'Herrera', 'Araya', 'Salazar', 'Vargas', 'Mora',
                  'Solís', 'Quesada', 'Rojas', 'Chaves', 'Brenes', 'Alvarado', 'Calderón', 'Sánchez', 'Ramírez', 'Núñez',
                  'Villalobos', 'Campos', 'Arias'])[1 + (i - 1) % 23] AS apellido1,
           (ARRAY['Vargas', 'Mora', 'Solís', 'Quesada', 'Rojas', 'Chaves', 'Brenes', 'Alvarado', 'Arias', 'Campos',
                  'Ulate', 'Monge', 'Segura'])[1 + (i * 7) % 13] AS apellido2
    FROM generate_series(1, 100) AS i
)
INSERT INTO duenno (cedula, nombre, correo)
SELECT '1' || lpad(i::text, 8, '0'),
       nombre || ' ' || apellido1 || ' ' || apellido2,
       CASE
           WHEN i % 10 = 0 THEN NULL
           WHEN i % 3 = 0 THEN ARRAY[
               translate(lower(nombre || '.' || apellido1), 'áéíóúñ', 'aeioun') || i || '@example.com',
               translate(lower(left(nombre, 1) || apellido2), 'áéíóúñ', 'aeioun') || i || '@trabajo.example.com']::VARCHAR(255)[]
           ELSE ARRAY[
               translate(lower(nombre || '.' || apellido1), 'áéíóúñ', 'aeioun') || i || '@example.com']::VARCHAR(255)[]
       END
FROM datos
ORDER BY i;

-- 100 Perros
-- Perro i belongs to duenno #(1 + (i - 1) % 60), resolved by cedula so the FK does not
-- depend on generated ids: duennos 1-40 get two perros, 41-60 get one, 61-100 get none.
INSERT INTO perro (nombre, raza, duenno_id)
SELECT (ARRAY['Max', 'Luna', 'Rocky', 'Canela', 'Toby', 'Nala', 'Bruno', 'Kira', 'Simba', 'Lola',
              'Thor', 'Chispa', 'Coco', 'Bella', 'Zeus', 'Mía', 'Oreo', 'Duque', 'Princesa', 'Firulais',
              'Manchas', 'Pelusa', 'Rex', 'Lucky', 'Maya'])[1 + (i - 1) % 25],
       (ARRAY['Labrador Retriever', 'Golden Retriever', 'Bulldog Francés', 'Zaguate', 'Beagle', 'Pastor Alemán',
              'Boxer', 'Husky Siberiano', 'Chihuahua', 'Poodle', 'Rottweiler', 'Schnauzer', 'Dálmata',
              'Shih Tzu', 'Border Collie', 'Pug', 'Dachshund', 'Doberman', 'Cocker Spaniel'])[1 + (i * 3) % 19],
       d.id
FROM generate_series(1, 100) AS i
JOIN duenno d ON d.cedula = '1' || lpad((1 + (i - 1) % 60)::text, 8, '0')
ORDER BY i;
