# Spring Boot CRUD Project

This project aims to develop a Spring Boot application that performs basic CRUD operations on three entities.

## Entities

The project includes the following entities:

- **Customer**: Represents a customer with a one-to-one relationship to an address and a many-to-one relationship to an order. Attributes: id, first name, last name.

- **Order**: Represents an order. Attributes: id, date, amount.

- **Address**: Represents an address. Attributes: id, street, city.

## Requirements

The following requirements should be fulfilled in the project:

- Implement CRUD operations for the entities.
- Write unit tests, end-to-end tests, integration tests, and lightweight integration tests using `@WebMvc`.
- Use PostgreSQL as the database, running in Docker.
- Configure Jenkins in Docker.

## Endpoints in Swagger

http://localhost:8080/swagger-ui/#/

## Technologies and Tools

The project will utilize the following technologies and tools:

- Spring Boot
- PostgreSQL
- Docker
- Jenkins
- Lombok

## Setup

To set up the project, follow these steps:

1. Ensure Docker is installed on your machine.
2. Create a Docker Compose file (`docker-compose.yml`) with the following services:
    - PostgreSQL container with the required configuration (username, password, database name).
    - Spring Boot application container.
    - Jenkins container.

3. Add the required dependencies in the `pom.xml` file:
    - PostgreSQL driver.
    - Lombok.

4. Configure the database connection in the `application.properties` (or `application.yml`) file with the appropriate URL, username, and password.

## Usage

To run the project, perform the following steps:

1. Start the Docker containers using Docker Compose.
2. Access the Spring Boot application endpoints to perform CRUD operations on the entities.
3. Use the provided tests to verify the functionality of the application.

## Testing

The project includes various types of tests:

- **Unit Tests**: These tests focus on testing individual components and methods.
- **End-to-End Tests**: These tests cover the entire application flow, simulating user interactions.
- **Integration Tests**: These tests ensure the integration between different components of the application.
- **Lightweight Integration Tests with `@WebMvc`**: These tests verify the integration with the web layer using the `@WebMvc` framework.

## Contributing

Contributions to this project are welcome. If you have any suggestions or improvements, please create a new issue or submit a pull request.

## License

No License needed.