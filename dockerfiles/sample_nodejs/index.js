const express = require('express');

const app = express();

app.get('/', (req, res) => {
    res.send('Welcome to the NODE page.');
});

app.listen(18000, () => {
    console.log('Listening on port 18000');
});