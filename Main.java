import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// ==================== CREATIONAL PATTERN: FACTORY ====================
abstract class User {
    private final int id;
    private final String name;
    private final String email;

    protected User(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public abstract String getRole();
}

class Patient extends User {
    public Patient(int id, String name, String email) {
        super(id, name, email);
    }

    @Override
    public String getRole() { return "Patient"; }
}

class Doctor extends User {
    private final String specialization;

    public Doctor(int id, String name, String email, String specialization) {
        super(id, name, email);
        this.specialization = specialization;
    }

    public String getSpecialization() { return specialization; }

    @Override
    public String getRole() { return "Doctor"; }
}

class UserFactory {
    public static User createUser(
            String type, int id, String name, String email, String specialization) {
        if (type == null) {
            throw new IllegalArgumentException("User type cannot be null.");
        }
        switch (type.trim().toLowerCase()) {
            case "patient":
                return new Patient(id, name, email);
            case "doctor":
                return new Doctor(id, name, email, specialization);
            default:
                throw new IllegalArgumentException("Invalid user type: " + type);
        }
    }
}

// ==================== STRUCTURAL PATTERN: ADAPTER ====================
interface NotificationService {
    void sendNotification(String message);
}

// Simulated legacy/external email service; it prints to the console.
class EmailNotificationService {
    public void sendEmail(String message) {
        System.out.println("[Simulated email] " + message);
    }
}

class EmailAdapter implements NotificationService {
    private final EmailNotificationService emailService;

    public EmailAdapter(EmailNotificationService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void sendNotification(String message) {
        emailService.sendEmail(message);
    }
}

// ==================== BEHAVIORAL PATTERN: OBSERVER ====================
interface Observer {
    void update(String message);
}

class PatientNotification implements Observer {
    private final Patient patient;
    private final NotificationService notificationService;

    public PatientNotification(Patient patient, NotificationService notificationService) {
        this.patient = patient;
        this.notificationService = notificationService;
    }

    @Override
    public void update(String message) {
        notificationService.sendNotification(
            "To " + patient.getName() + " (" + patient.getEmail() + "): " + message
        );
    }
}

// ==================== APPOINTMENT ====================
class Appointment {
    private final int id;
    private final Patient patient;
    private final Doctor doctor;
    private final String date;
    private final String time;
    private String status = "Created";
    private final List<Observer> observers = new ArrayList<>();

    public Appointment(int id, Patient patient, Doctor doctor, String date, String time) {
        this.id = id;
        this.patient = patient;
        this.doctor = doctor;
        this.date = date;
        this.time = time;
    }

    public int getId() { return id; }
    public String getStatus() { return status; }

    public void addObserver(Observer observer) {
        if (observer != null) {
            observers.add(observer);
        }
    }

    private void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }

    public void book() {
        if ("Cancelled".equals(status)) {
            System.out.println("A cancelled appointment cannot be booked again.");
            return;
        }
        if ("Booked".equals(status)) {
            System.out.println("This appointment is already booked.");
            return;
        }
        status = "Booked";
        System.out.println("Appointment booked successfully.");
        notifyObservers("Appointment #" + id + " is booked with Dr. "
                + doctor.getName() + " on " + date + " at " + time + ".");
    }

    public void cancel() {
        if (!"Booked".equals(status)) {
            System.out.println("Only booked appointments can be cancelled.");
            return;
        }
        status = "Cancelled";
        System.out.println("Appointment cancelled successfully.");
        notifyObservers("Appointment #" + id + " has been cancelled.");
    }

    public void display() {
        System.out.println("----------------------------------------");
        System.out.println("Appointment ID : " + id);
        System.out.println("Patient        : " + patient.getName());
        System.out.println("Doctor         : Dr. " + doctor.getName());
        System.out.println("Specialization : " + doctor.getSpecialization());
        System.out.println("Date           : " + date);
        System.out.println("Time           : " + time);
        System.out.println("Status         : " + status);
    }
}

// ==================== MAIN CONSOLE APPLICATION ====================
public class Main {
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final List<Patient> PATIENTS = new ArrayList<>();
    private static final List<Doctor> DOCTORS = new ArrayList<>();
    private static final List<Appointment> APPOINTMENTS = new ArrayList<>();
    private static final NotificationService NOTIFICATIONS =
            new EmailAdapter(new EmailNotificationService());

    private static int nextUserId = 1;
    private static int nextAppointmentId = 1;

    public static void main(String[] args) {
        // Sample doctors make the program easy to test immediately.
        DOCTORS.add((Doctor) UserFactory.createUser(
                "doctor", nextUserId++, "Arun", "arun@hospital.com", "Cardiology"));
        DOCTORS.add((Doctor) UserFactory.createUser(
                "doctor", nextUserId++, "Priya", "priya@hospital.com", "Dermatology"));

        System.out.println("Smart Healthcare Appointment Management System");

        int choice;
        do {
            printMenu();
            choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1: registerPatient(); break;
                case 2: viewDoctors(); break;
                case 3: bookAppointment(); break;
                case 4: viewAppointments(); break;
                case 5: cancelAppointment(); break;
                case 6: System.out.println("Thank you for using the system."); break;
                default: System.out.println("Invalid choice. Enter a number from 1 to 6.");
            }
        } while (choice != 6);

        SCANNER.close();
    }

    private static void printMenu() {
        System.out.println("\n============== MAIN MENU ==============");
        System.out.println("1. Register Patient");
        System.out.println("2. View Doctors");
        System.out.println("3. Book Appointment");
        System.out.println("4. View Appointments");
        System.out.println("5. Cancel Appointment");
        System.out.println("6. Exit");
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = SCANNER.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private static String readRequired(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = SCANNER.nextLine().trim();
            if (!value.isEmpty()) return value;
            System.out.println("This field cannot be empty.");
        }
    }

    private static void registerPatient() {
        String name = readRequired("Patient name: ");
        String email = readRequired("Patient email: ");

        Patient patient = (Patient) UserFactory.createUser(
                "patient", nextUserId++, name, email, "");
        PATIENTS.add(patient);
        System.out.println("Patient registered. Patient ID: " + patient.getId());
    }

    private static void viewDoctors() {
        System.out.println("\n--------------- DOCTORS ---------------");
        for (Doctor doctor : DOCTORS) {
            System.out.println("ID: " + doctor.getId() + " | Dr. " + doctor.getName()
                    + " | " + doctor.getSpecialization());
        }
    }

    private static void bookAppointment() {
        if (PATIENTS.isEmpty()) {
            System.out.println("Please register a patient first (menu option 1).");
            return;
        }

        viewDoctors();
        int patientId = readInt("Enter patient ID: ");
        Patient patient = findPatient(patientId);
        if (patient == null) {
            System.out.println("Patient ID not found.");
            return;
        }

        int doctorId = readInt("Enter doctor ID: ");
        Doctor doctor = findDoctor(doctorId);
        if (doctor == null) {
            System.out.println("Doctor ID not found.");
            return;
        }

        String date = readRequired("Appointment date (DD-MM-YYYY): ");
        String time = readRequired("Appointment time (e.g., 10:30 AM): ");

        Appointment appointment = new Appointment(
                nextAppointmentId++, patient, doctor, date, time);
        appointment.addObserver(new PatientNotification(patient, NOTIFICATIONS));
        APPOINTMENTS.add(appointment);
        appointment.book();
    }

    private static Patient findPatient(int id) {
        for (Patient patient : PATIENTS) {
            if (patient.getId() == id) return patient;
        }
        return null;
    }

    private static Doctor findDoctor(int id) {
        for (Doctor doctor : DOCTORS) {
            if (doctor.getId() == id) return doctor;
        }
        return null;
    }

    private static void viewAppointments() {
        if (APPOINTMENTS.isEmpty()) {
            System.out.println("No appointments have been created yet.");
            return;
        }
        for (Appointment appointment : APPOINTMENTS) {
            appointment.display();
        }
    }

    private static void cancelAppointment() {
        if (APPOINTMENTS.isEmpty()) {
            System.out.println("There are no appointments to cancel.");
            return;
        }

        int id = readInt("Enter appointment ID to cancel: ");
        for (Appointment appointment : APPOINTMENTS) {
            if (appointment.getId() == id) {
                appointment.cancel();
                return;
            }
        }
        System.out.println("Appointment ID not found.");
    }
}
