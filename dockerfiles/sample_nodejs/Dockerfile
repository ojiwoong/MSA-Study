FROM node:14.17-alpine3.14

COPY ./package.json ./package.json
COPY ./index.js ./index.js

RUN npm install

CMD [ "npm", "start" ]