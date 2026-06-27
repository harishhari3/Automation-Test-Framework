const http = require('http');
const fs = require('fs');
const path = require('path');

const PORT = 8080;
const WEBAPP_DIR = path.join(__dirname, 'webapp');
const DB_FILE = path.join(__dirname, 'db.json');

// Load database into memory
let db = { users: [], products: [] };
try {
    const data = fs.readFileSync(DB_FILE, 'utf8');
    db = JSON.parse(data);
    console.log('Database loaded successfully.');
} catch (e) {
    console.error('Failed to load database:', e.message);
}

const MIME_TYPES = {
    '.html': 'text/html',
    '.css': 'text/css',
    '.js': 'text/javascript',
    '.json': 'application/json',
    '.png': 'image/png',
    '.jpg': 'image/jpeg',
    '.svg': 'image/svg+xml'
};

const server = http.createServer((req, res) => {
    const urlParts = req.url.split('?');
    const urlPath = urlParts[0];

    // API: GET /api/products
    if (req.method === 'GET' && urlPath === '/api/products') {
        res.writeHead(200, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify(db.products));
        return;
    }

    // API: POST /api/login
    if (req.method === 'POST' && urlPath === '/api/login') {
        let body = '';
        req.on('data', chunk => { body += chunk.toString(); });
        req.on('end', () => {
            try {
                const credentials = JSON.parse(body);
                const user = db.users.find(u => u.username === credentials.username && u.password === credentials.password);
                
                if (user) {
                    if (user.status === 'locked') {
                        res.writeHead(400, { 'Content-Type': 'application/json' });
                        res.end(JSON.stringify({ error: user.errorMessage }));
                    } else {
                        const sendResponse = () => {
                            res.writeHead(200, { 'Content-Type': 'application/json' });
                            res.end(JSON.stringify({ success: true, user: { username: user.username, glitch: user.glitch || false } }));
                        };
                        
                        if (user.delayMs) {
                            setTimeout(sendResponse, user.delayMs);
                        } else {
                            sendResponse();
                        }
                    }
                } else {
                    res.writeHead(400, { 'Content-Type': 'application/json' });
                    res.end(JSON.stringify({ error: "Epic Fail: Username and password do not match any entity in this universe." }));
                }
            } catch (err) {
                res.writeHead(400, { 'Content-Type': 'application/json' });
                res.end(JSON.stringify({ error: "Invalid JSON format" }));
            }
        });
        return;
    }

    // Static files server
    let filePath = path.join(WEBAPP_DIR, urlPath === '/' ? 'index.html' : urlPath);
    if (!filePath.startsWith(WEBAPP_DIR)) {
        res.writeHead(403, { 'Content-Type': 'text/plain' });
        res.end('Forbidden');
        return;
    }

    const extname = path.extname(filePath);
    let contentType = MIME_TYPES[extname] || 'application/octet-stream';

    fs.readFile(filePath, (err, content) => {
        if (err) {
            if (err.code === 'ENOENT') {
                res.writeHead(404, { 'Content-Type': 'text/html' });
                res.end('<h1>404 Not Found</h1>', 'utf-8');
            } else {
                res.writeHead(500);
                res.end(`Server Error: ${err.code}`);
            }
        } else {
            res.writeHead(200, { 'Content-Type': contentType });
            res.end(content, 'utf-8');
        }
    });
});

server.listen(PORT, () => {
    console.log(`Server running at http://localhost:${PORT}/`);
});
