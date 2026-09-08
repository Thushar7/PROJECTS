# Notification Service

Processes and stores notification events. Flyway manages the schema.

## Schema & Migrations
- Location: `src/main/resources/db/migration`
- `V1__init_notification_schema.sql` defines `notifications` table, indexes, and CHECK constraints matching enums:
  - NotificationEvent: BOOKING_CONFIRMED, BOOKING_CANCELLED, PAYMENT_SUCCESS, MOVIE_ADDED
  - NotificationType: USER
  - NotificationStatus: NEW, RECEIVED, PROCESSED, FAILED

## Runtime Configuration
- Hibernate validation only (`ddl-auto=validate`).
- Flyway: enabled, baseline-on-migrate, clean-disabled.

## Tests
- Test profile (H2) disables Flyway for speed; schema generated via Hibernate.
- Add an integration profile if you need to validate migrations on H2 or a containerized MySQL.

## Adding a Migration
1. Create `V2__<change>.sql`.
2. Forward-only migration; never alter existing version scripts.
3. Run:
```
./mvnw clean package
./mvnw spring-boot:run
```

## Next Steps (Optional)
- Add repeatable migrations for static messages.
- Add integration tests verifying constraint alignment with enums.
# Theatre Service

Flyway-enabled service managing theatres and showtimes.

## Database Migrations
- Location: `src/main/resources/db/migration`
- Baseline migration: `V1__init_theatre_schema.sql` (tables: theatres, showtimes + indexes)
- Hibernate is set to `validate`; only Flyway changes schema.
- Safe guards: `spring.flyway.clean-disabled=true`, `baseline-on-migrate=true`.

## Adding a Migration
1. Create `V2__<change>.sql` in `db/migration`.
2. Use forward-only DDL. Do not edit older scripts.
3. Run:
```
./mvnw clean package
./mvnw spring-boot:run
```

## Testing
- Test profile (`application-test.properties`) uses H2 + `ddl-auto=create-drop` and `spring.flyway.enabled=false` for fast unit tests.
- To exercise Flyway in tests, create an `application-it.properties` and enable Flyway there.

## Future Ideas
- Add repeatable migration for reference data (e.g., default theatres).
- Introduce integration tests asserting Flyway history.

