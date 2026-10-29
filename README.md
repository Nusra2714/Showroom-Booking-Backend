# Pixcom Showroom Booking — Java + MySQL backend

This is a learning/demo backend for the Angular showroom booking UI. It provides a REST API and stores bookings in MySQL.

## Requirements
- JDK 17 or newer
- Maven 3.9+ (or use an IDE with Maven support)
- MySQL 8.x

## 1. Create the database
Run in MySQL Workbench or the MySQL CLI:

```sql
CREATE DATABASE pixcom_showroom;
```

## 2. Configure database credentials
Edit `src/main/resources/application.properties`, or set environment variables:
- `DB_URL=jdbc:mysql://localhost:3306/pixcom_showroom`
- `DB_USERNAME=your_mysql_username`
- `DB_PASSWORD=your_mysql_password`

Do not commit real passwords to GitHub. The `change-me` default must be replaced locally.

## 3. Run the API
From this directory:

```bash
mvn spring-boot:run
```

The API starts at `http://localhost:8080`.

## Endpoints
- `GET /api/rooms` — room status for rooms 1–4, including current booking if busy.
- `GET /api/bookings` — all current bookings.
- `POST /api/bookings` — create a booking.
- `DELETE /api/rooms/{roomNumber}/booking` — release a room (removes its current booking).

Example POST body:

```json
{
  "roomNumber": 1,
  "customerName": "Test Customer",
  "mobile": "9876543210",
  "email": "test@example.com",
  "model": "Cadillac Escalade",
  "date": "2026-10-30",
  "time": "14:30"
}
```

A successful create returns HTTP 201. An occupied room returns HTTP 409 Conflict. A database unique constraint also protects against two simultaneous booking requests for the same room.

## Test without the Angular UI
Open `http://localhost:8080/api/rooms` in a browser, or use Postman/curl. To create a booking, send a JSON POST to `/api/bookings`. Then call `GET /api/rooms` again. Release with `DELETE /api/rooms/1/booking`.

## Important limitations
- This is a learning prototype, not production-ready. There is no login/role-based authorization, audit trail, or protection against unauthorized room release.
- The CORS configuration allows localhost and Netlify subdomains for development. Before production, restrict it to the exact deployed frontend origin.
- A room is held until its booking is explicitly released; selected date/time are stored as booking details and do not auto-release the room.
- `spring.jpa.hibernate.ddl-auto=update` is convenient for local learning. Production systems should use controlled schema migrations such as Flyway.
