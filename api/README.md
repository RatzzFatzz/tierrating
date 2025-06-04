# TierRating
![Release Build Status](https://github.com/RatzzFatzz/tierrating/workflows/release-build/badge.svg)

Go to [tierrating-ui](https://github.com/RatzzFatzz/tierrating-ui) for deployment and more information.


## Development

This repository contains the backend service for TierRating, a Spring Boot application that handles fetching and synchronizing ratings with third-party providers.

### Prerequisites

- Java JDK 21 or higher
- Maven 3.6+

### Local Development Setup

1. Clone the repository
```bash
git clone https://github.com/RatzzFatzz/tierrating.git
cd tierrating
```

2. Build the application
```bash
mvn clean package install
```

3. Run the application
```bash
mvn spring-boot:run
```

The API will be available at http://localhost:8080

### Project Structure

This is a standard Spring Boot application:

- `/src/main/java` - Java source code
- `/src/main/resources` - Configuration files and static resources
- `/src/test` - Test classes

### Building for Production

```bash
mvn clean package
```

This will generate a JAR file in the `/target` directory.

### Docker Build (Optional)

If you want to build the Docker image locally:

```bash
docker build -t tierrating:latest .
```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request