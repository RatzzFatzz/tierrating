FROM node:latest
WORKDIR /app

# Copy everything at once
COPY . .

# Install dependencies
RUN npm install

# Build the app
RUN npm run build

# Set up user
RUN groupadd -g 1234 tierrating && \
    useradd -m -u 1234 -g tierrating tierrating
RUN chown -R tierrating:tierrating /app
USER tierrating

# Create a wrapper script to handle runtime environment variables
RUN echo '#!/bin/sh\n\
exec npm start' > /app/start.sh && \
chmod +x /app/start.sh

EXPOSE 3000
CMD ["/app/start.sh"]