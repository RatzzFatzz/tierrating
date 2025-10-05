# Stage 1: Build
FROM node:latest AS builder
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Stage 2: Serve
FROM node:alpine
WORKDIR /app
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/public ./public
COPY --from=builder /app/package.json ./package.json

# Set up user
RUN addgroup -S tierrating && \
    adduser -S tierrating -G tierrating
RUN chown -R tierrating:tierrating /app
USER tierrating

RUN npm install next

EXPOSE 3000

CMD ["npm", "start"]