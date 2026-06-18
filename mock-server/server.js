const http = require('http');

const PORT = 8081;
const DELAY_MS = 500;

const server = http.createServer((req, res) => {
    setTimeout(() => {
        res.writeHead(200, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ message: 'Success', delay: DELAY_MS }));
    }, DELAY_MS);
});

server.listen(PORT, () => {
    console.log(`Mock server listening on port ${PORT}`);
});
