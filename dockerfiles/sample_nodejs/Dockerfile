FROM node:14.17-alpine3.14

RUN mkdir /myapp

WORKDIR /myapp

COPY ./package.json ./package.json
COPY ./index.js ./index.js

RUN npm install
RUN ["apk", "--no-cache", "add", "curl"]

CMD [ "npm", "start" ]