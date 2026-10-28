const express = require('express');
const jwt = require('jsonwebtoken');
const cors = require('cors');
const { DatabaseSync } = require('node:sqlite');

const app = express();
app.use(express.json());
app.use(cors());

const SECRET_KEY = 'clave_secreta_udv_parcial2';
const PORT = 3000;

// Conexión a la base de datos local SQLite
const db = new DatabaseSync('notas.db');

// Habilitar soporte de llaves foráneas en SQLite
db.exec('PRAGMA foreign_keys = ON;');

// Tabla de usuarios
db.exec(`
  CREATE TABLE IF NOT EXISTS usuarios (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL
  )
`);

// Tabla de notas relacionada con el usuario
db.exec(`
  CREATE TABLE IF NOT EXISTS notas (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    titulo TEXT NOT NULL,
    contenido TEXT NOT NULL,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES usuarios(id) ON DELETE CASCADE
  )
`);

// Creación de usuario inicial para pruebas
const usuarioInicial = db.prepare('SELECT id FROM usuarios WHERE email = ?').get('admin@udv.edu.gt');
if (!usuarioInicial) {
  db.prepare('INSERT INTO usuarios (nombre, email, password) VALUES (?, ?, ?)').run(
    'Admin',
    'admin@udv.edu.gt',
    '123456'
  );
  console.log('Usuario registrado -> Email: admin@udv.edu.gt | Password: 123456');
}

// Middleware para verificar el token JWT en las cabeceras
function verificarToken(req, res, next) {
  const authHeader = req.headers['authorization'];
  
  if (!authHeader) {
    return res.status(401).json({ error: 'Token de autorización requerido' });
  }

  const token = authHeader.startsWith('Bearer ') ? authHeader.slice(7).trim() : authHeader.trim();

  if (!token) {
    return res.status(401).json({ error: 'Formato de token no válido' });
  }

  jwt.verify(token, SECRET_KEY, (err, decoded) => {
    if (err) {
      return res.status(403).json({ error: 'Token inválido o expirado' });
    }
    req.user = decoded;
    next();
  });
}

// Ruta de inicio de sesión y generación de token
app.post('/api/login', (req, res) => {
  try {
    const email = req.body.email ? String(req.body.email).trim() : '';
    const password = req.body.password ? String(req.body.password).trim() : '';

    if (!email || !password) {
      return res.status(400).json({ error: 'Todos los campos son obligatorios' });
    }

    const usuario = db.prepare(
      'SELECT id, nombre, email FROM usuarios WHERE email = ? AND password = ?'
    ).get(email, password);

    if (!usuario) {
      return res.status(401).json({ error: 'Credenciales incorrectas' });
    }

    const token = jwt.sign(
      { id: usuario.id, nombre: usuario.nombre, email: usuario.email },
      SECRET_KEY,
      { expiresIn: '24h' }
    );

    return res.status(200).json({
      token: token,
      nombre: usuario.nombre,
      email: usuario.email
    });
  } catch (error) {
    console.error('Error en login:', error);
    return res.status(500).json({ error: 'Error interno del servidor' });
  }
});

// Ruta para guardar una nota nueva asociada al usuario autenticado
app.post('/api/notas', verificarToken, (req, res) => {
  try {
    const titulo = req.body.titulo ? String(req.body.titulo).trim() : '';
    const contenido = req.body.contenido ? String(req.body.contenido).trim() : '';

    if (!titulo || !contenido) {
      return res.status(400).json({ error: 'El título y el contenido son obligatorios' });
    }

    const resultado = db.prepare(
      'INSERT INTO notas (user_id, titulo, contenido) VALUES (?, ?, ?)'
    ).run(req.user.id, titulo, contenido);

    const notaCreada = db.prepare(
      'SELECT id, user_id, titulo, contenido, fecha_creacion FROM notas WHERE id = ?'
    ).get(resultado.lastInsertRowid);

    return res.status(201).json(notaCreada);
  } catch (error) {
    console.error('Error al crear nota:', error);
    return res.status(500).json({ error: 'No se pudo guardar la nota' });
  }
});

// Ruta para consultar todas las notas del usuario autenticado
app.get('/api/notas', verificarToken, (req, res) => {
  try {
    const notas = db.prepare(
      'SELECT id, user_id, titulo, contenido, fecha_creacion FROM notas WHERE user_id = ? ORDER BY id DESC'
    ).all(req.user.id);

    return res.status(200).json(notas);
  } catch (error) {
    console.error('Error al obtener notas:', error);
    return res.status(500).json({ error: 'No se pudieron obtener las notas' });
  }
});

// Ruta para eliminar una nota por su ID asegurando que pertenezca al usuario autenticado
app.delete('/api/notas/:id', verificarToken, (req, res) => {
  try {
    const notaId = req.params.id;

    const nota = db.prepare('SELECT * FROM notas WHERE id = ? AND user_id = ?').get(notaId, req.user.id);

    if (!nota) {
      return res.status(404).json({ error: 'Nota no encontrada o no autorizada' });
    }

    db.prepare('DELETE FROM notas WHERE id = ?').run(notaId);

    return res.status(200).json({ mensaje: 'Nota eliminada correctamente' });
  } catch (error) {
    console.error('Error al eliminar nota:', error);
    return res.status(500).json({ error: 'No se pudo eliminar la nota' });
  }
});

// Arranque del servidor
app.listen(PORT, '0.0.0.0', () => {
  console.log(`Servidor activo en http://localhost:${PORT}`);
});