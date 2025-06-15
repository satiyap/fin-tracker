# Fin-Tracker Development Guide

## Build Commands
- Build: `mvn clean install`
- Run: `mvn spring-boot:run`
- Run tests: `mvn test`
- Run single test: `mvn test -Dtest=TestClassName#testMethodName`
- Run specific test class: `mvn test -Dtest=TestClassName`

## Java Version Compatibility
- Using Java 21
- For testing with Java 21, add `-Dnet.bytebuddy.experimental=true` to JVM arguments
- Required dependency versions for Java 21:
  - Byte Buddy 1.14.12+
  - Mockito 5.10.0+

## Code Style Guidelines
- Use Lombok annotations (`@Data`, `@Builder`, etc.) for reducing boilerplate
- Entity classes should use `@Entity`, `@Table`, and JPA annotations
- Follow AAA pattern in tests (Arrange, Act, Assert)
- Use builder pattern for object creation
- Use BigDecimal for monetary values
- Use proper exception handling with @ExceptionHandler and custom exceptions
- Follow Spring naming conventions for controllers, services, repositories
- Services should validate inputs and throw appropriate exceptions
- API errors should use standardized ApiError response format
- Use StandardizedTitleCasing for class names, camelCase for methods/variables

## Architecture
- Controller → Service → Repository layering
- DTO objects for API requests/responses
- Domain entities for persistence
- Mappers for DTO ↔ Entity conversion