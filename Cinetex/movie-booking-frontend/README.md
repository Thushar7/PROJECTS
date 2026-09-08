# MovieBookingFrontend

This project was generated using [Angular CLI](https://github.com/angular/angular-cli) version 20.3.2.

## Development server

To start a local development server, run:

```bash
ng serve
```

Once the server is running, open your browser and navigate to `http://localhost:4200/`. The application will automatically reload whenever you modify any of the source files.

## Code scaffolding

Angular CLI includes powerful code scaffolding tools. To generate a new component, run:

```bash
ng generate component component-name
```

For a complete list of available schematics (such as `components`, `directives`, or `pipes`), run:

```bash
ng generate --help
```

## Building

To build the project run:

```bash
ng build
```

This will compile your project and store the build artifacts in the `dist/` directory. By default, the production build optimizes your application for performance and speed.

## Running unit tests

To execute unit tests with the [Karma](https://karma-runner.github.io) test runner, use the following command:

```bash
ng test
```

## Running end-to-end tests

For end-to-end (e2e) testing, run:

```bash
ng e2e
```

Angular CLI does not come with an end-to-end testing framework by default. You can choose one that suits your needs.

## Additional Resources

For more information on using the Angular CLI, including detailed command references, visit the [Angular CLI Overview and Command Reference](https://angular.dev/tools/cli) page.

## Authentication Flow (JWT)

The application stores the authenticated user (including JWT) in `localStorage` under the key `auth_user_v1`.

Key behaviors:

- A lightweight interceptor automatically attaches the `Authorization: Bearer <token>` header for outgoing HTTP requests when a token exists.
- The route guard now checks token expiration client-side (via the `exp` claim). If the token is expired, it clears the session and redirects to `/login`, preserving the attempted URL as a return target.
- Expiration detection uses a small utility (`jwt.utils.ts`) that decodes the JWT payload without external dependencies.
- If a token lacks an `exp` claim, the guard treats it as non-expired (server remains the source of truth).
- After login, the app can navigate back to the originally requested protected route via the stored return URL.

Suggested backend improvements (optional):

- Provide refresh tokens to enable silent renewal before expiry.
- Return standardized error bodies for 401 to unify UX messaging.

To extend:

- Replace the simple interceptor with one that also handles 401 responses by clearing auth and redirecting.
- Implement a refresh flow by adding a refresh endpoint and scheduling renewal using the `exp` minus a buffer window.

## Seat Selection & Booking Flow

The application now supports selecting seats for a chosen movie showtime and creating a booking.

High-level UI steps:
1. Navigate to a movie's showtimes page (`/showtimes/:movieId`).
2. Click on a listed showtime. You are navigated to `/booking/:showtimeId`.
3. The booking page fetches the showtime, movie details, theatre details, then reserved seats: `GET http://localhost:8080/bookings/showtimes/{showtimeId}/reserved-seats`.
4. The seating grid (default 10 rows A–J × 12 seats) renders with:
	- Reserved seats (disabled, red tone)
	- Available seats (green tone)
	- Selected seats (blue tone)
5. User selects up to a configurable maximum (default 6 seats) and confirms.
6. A booking request is posted: `POST http://localhost:8080/bookings/create` with payload:
	```json
	{
	  "userId": 0,
	  "movieId": <movieId>,
	  "theatreId": <theatreId>,
	  "showtimeId": <showtimeId>,
	  "totalAmount": <seats * pricePerSeat>,
	  "paymentId": 0,
	  "bookingStatus": "CONFIRMED",
	  "seatCount": <n>,
	  "seatNumbers": ["A3","A4",...]
	}
	```
7. On success a confirmation panel on the booking page displays booking id, seats and status with options to book more or return home.

Race condition handling:
- If the backend returns `409` with an error message (seats just taken), the UI shows a conflict banner, refreshes reserved seats, and preserves the panel for a new selection.

Key files:
- `src/app/core/services/booking-api.service.ts` – Booking REST calls.
- `src/app/shared/components/seat-selection/` – SeatSelection component, SCSS & layout utilities.
- `src/app/features/booking/seat-booking-page.component.*` – Dedicated booking page.
- `src/app/features/showtimes/showtimes-page.component.*` – Showtimes listing (navigates to booking page).
- `seat-layout.util.ts` – Generates static layout; can be extended to accept theatre-specific configuration.

Configuration / assumptions:
- Theatre layout currently static (A–J, 12 seats). Extend `generateSeatLayout()` for dynamic layouts.
- `userId` is a placeholder (0) until backend provides numeric user id in auth/profile response.
- Pricing (`pricePerSeat`) is a simple input defaulting to 500 (currency units); adjust or fetch dynamically as needed.

Extending the feature:
- Integrate real user numeric ID when available.
- Accept per-theatre seat maps (add row/seat metadata to Theatre DTO or dedicated endpoint).
- Add seat category pricing (e.g., Premium, Standard) by extending Seat interface.
- Introduce real payment flow, updating `bookingStatus` after confirmation.
- Add live updates via WebSocket/SSE to reflect seats reserved by others in real-time.

Testing:
- `booking-api.service.spec.ts` covers reserved seats fetch + 409 conflict path.
- `seat-layout.util.spec.ts` covers layout generation and selection rules.

