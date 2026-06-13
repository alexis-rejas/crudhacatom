-- Crear tabla de expediciones
CREATE TABLE IF NOT EXISTS expediciones (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo_expedicion VARCHAR(10) UNIQUE NOT NULL,
    nombre_expedicion VARCHAR(255) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    sitio_arqueologico VARCHAR(255) NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Crear índices
CREATE INDEX idx_codigo ON expediciones(codigo_expedicion);
CREATE INDEX idx_fecha_inicio ON expediciones(fecha_inicio);

-- Datos de prueba
INSERT INTO expediciones (codigo_expedicion, nombre_expedicion, fecha_inicio, fecha_fin, sitio_arqueologico) 
VALUES 
('EXP001', 'Excavación Valle Grande 2023', '2023-01-15', '2023-03-30', 'Valle Grande, Perú'),
('EXP002', 'Investigación Machu Picchu', '2023-04-01', '2023-06-15', 'Machu Picchu, Perú'),
('EXP003', 'Survey Nazca Lines', '2023-07-01', '2023-08-30', 'Líneas de Nazca, Perú');
