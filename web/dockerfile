FROM node:latest

EXPOSE 3000

RUN groupadd -g 1234 tierrating && \
    useradd -m -u 1234 -g tierrating tierrating

WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN chown -R tierrating:tierrating /app

USER tierrating

CMD npm run dev