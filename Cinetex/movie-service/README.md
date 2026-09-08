# Movie Service

This module manages movie metadata (movies, genres, languages) for the CineTix platform.

## Flyway Migrations
Flyway is enabled to manage the relational schema. Key points:

- Migration scripts live under `src/main/resources/db/migration`.
- Current baseline migration: `V1__init_schema.sql` creates core tables and seeds minimal reference data.
- Hibernate is set to `spring.jpa.hibernate.ddl-auto=validate` so it will only validate the schema, never create / update.
- `spring.flyway.clean-disabled=true` prevents accidental destructive `clean` operations in all environments.
- `spring.flyway.baseline-on-migrate=true` allows adopting an existing database (will create a baseline marker if no Flyway history exists).

### Adding a new migration
1. Create a new file with the pattern `V<version>__<description>.sql` in `db/migration` (e.g. `V2__add_movie_rating.sql`).
2. Increment `<version>` sequentially (no gaps required, but conventional).
3. Use **idempotent** DDL carefully; Flyway runs each script exactly once—avoid `IF NOT EXISTS` for objects you expect to change (forces drift detection). Reserve it only for safe initialization like lookup tables.
4. Run tests: `mvnw test` inside the module.

### Rollbacks
Flyway Community (core) does not support automatic undo migrations. Create a forward fix as a new migration if needed.

## Local Development

Prerequisites:
- MySQL running locally with a user that matches `application.properties` (default `root/root123`) OR update the credentials via environment variables.
- Java 17

Run tests:
```
./mvnw test
```

Run the service:
```
./mvnw spring-boot:run
```

## Configuration Overrides
You can override datasource or Flyway settings via environment variables, for example:
```
set SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/moviedb
set SPRING_DATASOURCE_USERNAME=dev
set SPRING_DATASOURCE_PASSWORD=secret
```

## Future Improvements
- Add separate migration locations for test-specific data seeds.
- Introduce integration tests that verify baseline + repeatable migrations (if added later).
- Consider switching poster storage to an object store and keep only URL + metadata in DB.

