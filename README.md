## Inspection Reminder Service

This is a simple spring boot application that sends a reminder email to the user about the upcoming inspection date of
their vehicle.

The application uses the following technologies:

- Spring Boot
- Spring Data JPA
- Lombok
- PostgreSQL
- Spring Scheduler
- Testcontainers (for integration testing)
- -JUnit 5 and Mockito (for unit testing)
- Docker Compose to set up the database.

### Running the application

The application uses PostgreSQL as the database. You can run the database using Docker Compose.

```shell
  docker compose up -d
```

Then run the application using the following command:

```shell
./mvnw spring-boot:run
```

or simply run the InspectionReminderServiceApplication class in your IDE.

The application will start on port 8080.

### Running the tests

To run the tests, you can run the following command:

```shell
./mvnw test
```

or by the command :

```shell
mvn clean test
```

### API Documentation

The application has a single endpoint that allows user to subscribe to reminders
for the provided vehicle license plate number(s).

User can subscribe to reminders for multiple vehicles at once.

```shell
POST /api/subscribe
```

The request body should be in the following format:

```json
{
  "email": "test@example.com",
  "licensePlates": [
    "EB23467",
    "EB23468"
  ]
}
```

Successful response with status code 200:

```json
{
  "success": true,
  "message": "Api Call Successful",
  "data": {
    "id": "1b1a4953-b2aa-41be-bd3b-9dc4af2c1d10",
    "email": "test@example.com",
    "vehicles": [
      {
        "id": "4b5ae954-7f7a-44a9-95d0-d6976cf087f1",
        "licensePlate": "EB34659",
        "subscribed": true,
        "inspectionDeadline": "2026-02-27"
      },
      {
        "id": "17954bf7-34f9-442c-a3b8-f6c723a897ee",
        "licensePlate": "EB23467",
        "subscribed": true,
        "inspectionDeadline": "2026-01-14"
      }
    ]
  }
}
```

The API also has validations in place to ensure that the email and license plate number are valid.Following validations
are in place:

- Email should be present and sshould be a valid email address.
- At least one license plate number should be provided.
- License plate number should be in the format "EB12345".

Business rules:

- The user can subscribe to reminders for multiple vehicles.
- If the user is already subscribed to all provided vehicles, the application will respond with a message indicating
  that the user is already subscribed to all the vehicles.
- User cannot subscribe to any vehicle that is already subscribed to by another user, application will respond with a
  message indicating that the vehicle is already subscribed to by another user.

User can also unsubscribe from reminders for the provided vehicle license plate number(s).

```shell
POST /api/unsubscribe
```

The request body should be in the following format:

```json
{
  "email": "test@example.com",
  "licensePlates": [
    "EB23467",
    "EB23468"
  ]
}

```

```json
{
  "success": true,
  "message": "Api Call Successful",
  "data": {
    "id": "1b1a4953-b2aa-41be-bd3b-9dc4af2c1d10",
    "email": "test@example.com",
    "vehicles": [
      {
        "id": "4b5ae954-7f7a-44a9-95d0-d6976cf087f1",
        "licensePlate": "EB23467",
        "subscribed": false,
        "inspectionDeadline": "2026-02-27"
      },
      {
        "id": "17954bf7-34f9-442c-a3b8-f6c723a897ee",
        "licensePlate": "EB23468",
        "subscribed": false,
        "inspectionDeadline": "2026-01-14"
      }
    ]
  }
}
```

Business rules:

- The user can unsubscribe from reminders for multiple vehicles.
- If the user is not subscribed to any of the provided vehicles, the application will respond with a message indicating
  that the user is not subscribed to the vehicle.
  You cannot unsubscribe from a vehicle that you are not subscribed to.
- If the user is already unsubscribed from all provided vehicles, the application will respond with a message indicating
  that the user is already unsubscribed from all the vehicles.

### Database Schema

The application has two tables in the database:

- User
- Vehicle

User table has one-to-many relationship with Vehicle table.

## How scheduling works

### ReminderSchedulerService :

The application has a scheduler that runs every day at 9:00 AM.
The scheduler checks for the vehicles whose inspection date is 30 days away from the current date and logs a console
message(try to mimic sending an email) for each vehicle.
After console message is logged, the is_notification_sent flag is set to true for the vehicle in the database.

### InspectionDateSchedulerService :

There is also a scheduler that runs every day at 12:00 AM.
The scheduler checks for the vehicles whose inspection date is in the past. This scheduler will get the new inspection
date from the SVV API and update the inspection date in the database.
It will also set the is_notification_sent flag to false for the vehicle in the database.