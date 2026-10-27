SMART HEALTHCARE APPOINTMENT MANAGEMENT SYSTEM
Java console prototype for a Design Patterns course project

FILES
- Main.java: Complete Java source code (all classes are included in this file).

REQUIREMENTS
- JDK 8 or later
- VS Code with the Extension Pack for Java (optional, but useful)

RUN IN TERMINAL
  javac Main.java
  java Main

FEATURES
- Register patients
- View sample doctors
- Book appointments
- View appointment details
- Cancel booked appointments
- Simulated notification messages in the console

DESIGN PATTERNS
- Factory: UserFactory creates Patient and Doctor objects.
- Adapter: EmailAdapter adapts NotificationService to EmailNotificationService.
- Observer: Appointment notifies PatientNotification observers when booking/cancellation changes status.

SCOPE NOTE
This is a console prototype. Records are kept in memory and disappear when the program exits.
The email service is simulated; no real email is sent. Doctor availability is not checked against a schedule,
and no database or login system is included.
