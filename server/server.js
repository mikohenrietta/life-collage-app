const Koa = require('koa');
const Router = require('@koa/router');
const bodyParser = require('koa-bodyparser');
const sqlite3 = require('sqlite3').verbose();
const WebSocket = require('ws');
const http = require('http');

const app = new Koa();
const router = new Router();
const server = http.createServer(app.callback());
const wss = new WebSocket.Server({ server });

app.use(bodyParser());

// --- 1. DATABASE SETUP (SQLite) ---
const db = new sqlite3.Database('./collage.db', (err) => {
    if (err) console.error(err.message);
    console.log('Connected to the SQLite database.');
});

db.run(`CREATE TABLE IF NOT EXISTS collages (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT,
    description TEXT,
    rating TEXT,
    date TEXT,
    imageUri TEXT
)`);

// --- 2. WEBSOCKET BROADCAST ---
const broadcast = (data) => {
    wss.clients.forEach((client) => {
        if (client.readyState === WebSocket.OPEN) {
            client.send(JSON.stringify(data));
        }
    });
};

// --- 3. REST API ENDPOINTS ---

// GET (Read all)
router.get('/collage', async (ctx) => {
    ctx.body = await new Promise((resolve, reject) => {
        db.all("SELECT * FROM collages", [], (err, rows) => {
            if (err) reject(err);
            resolve(rows);
        });
    });
    ctx.status = 200;
});

// POST (Create)
router.post('/collage', async (ctx) => {
    const item = ctx.request.body;
    console.log("Received POST request");
    const newId = await new Promise((resolve, reject) => {
        db.run(`INSERT INTO collages (title, description, rating, date, imageUri) VALUES (?, ?, ?, ?, ?)`,
            [item.title, item.description, item.rating, item.date, item.imageUri],
            function(err) {
                if (err) reject(err);
                resolve(this.lastID);
            }
        );
    });
    console.log("Data:", item);
    const createdItem = { ...item, id: newId };
    ctx.body = createdItem;
    ctx.status = 201;
    broadcast({ event: 'created', payload: createdItem });
});

// PUT (Update)
router.put('/collage/:id', async (ctx) => {
    const id = ctx.params.id;
    const item = ctx.request.body;
    console.log("Received PUT request");
    await new Promise((resolve, reject) => {
        db.run(`UPDATE collages SET title=?, description=?, rating=?, date=?, imageUri=? WHERE id=?`,
            [item.title, item.description, item.rating, item.date, item.imageUri, id],
            (err) => {
                if (err) reject(err);
                resolve();
            }
        );
    });
    console.log("Data:", item);
    const updatedItem = { ...item, id: parseInt(id) };
    ctx.body = updatedItem;
    ctx.status = 200;
    broadcast({ event: 'updated', payload: updatedItem });
});

// DELETE
router.delete('/collage/:id', async (ctx) => {
    const id = ctx.params.id;
    console.log("Received DELETE request for id:", id);
    await new Promise((resolve, reject) => {
        db.run(`DELETE FROM collages WHERE id=?`, [id], (err) => {
            if (err) reject(err);
            resolve();
        });
    });
    ctx.status = 204; // No Content
    broadcast({ event: 'deleted', payload: { id: parseInt(id) } });
});

app.use(router.routes()).use(router.allowedMethods());

server.listen(3000, () => {
    console.log('Server running on port 3000');
});