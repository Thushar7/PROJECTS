# Booking Service

Handles bookings and reserved seats. Flyway manages schema evolution.

## Migrations
- Location: `src/main/resources/db/migration`
- `V1__init_booking_schema.sql` creates `bookings` & `reserved_seats` with constraints and indexes.
- Enum constraint enforced for `booking_status` (PENDING, CONFIRMED, CANCELLED, FAILED).

## Config Highlights
- Runtime: `hibernate.ddl-auto=validate`, Flyway enabled, clean disabled.
- Test profile: H2 in-memory, Flyway disabled, schema auto-created/dropped.

## Adding a Migration
1. Add `V<next>__description.sql`.
2. Apply forward-only change; never rewrite older versions.
3. Build & run service:
```
./mvnw clean package
./mvnw spring-boot:run
```

## Tips
- Keep enum constraints in sync with Java enums.
- Consider a repeatable migration (`R__seed_data.sql`) for stable seed/reference data.

