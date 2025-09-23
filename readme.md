# MyPMS - Customer & Shipment Management System

## Περιγραφή

Το MyPMS είναι ένα ολοκληρωμένο σύστημα διαχείρισης πελατών και αποστολών που αναπτύχθηκε σε Java με Swing GUI και MySQL database. Η εφαρμογή προσφέρει πλήρη λειτουργικότητα για τη διαχείριση επιχειρηματικών δεδομένων με έμφαση στην ευκολία χρήσης και την αξιοπιστία.

## Κύρια Χαρακτηριστικά

### 🏢 Διαχείριση Πελατών
- **Πλήρη διαχείριση στοιχείων πελατών**: ΑΦΜ, επωνυμία εταιρίας, νόμιμος εκπρόσωπος
- **Διευθύνσεις και στοιχεία επικοινωνίας**: Τηλέφωνα, email, ταχυδρομική διεύθυνση
- **Υπεύθυνοι επικοινωνίας**: Ξεχωριστοί υπεύθυνοι για διαφορετικές κατηγορίες (Συμβάσης, Επικοινωνίας, Πληρωμών, Λογιστηριού, Αντικαταβολών)
- **Προηγμένη αναζήτηση**: Φιλτράρισμα και εύρεση πελατών
- **Validation**: Αυτόματος έλεγχος ΑΦΜ, email, τηλεφώνων

### 📦 Διαχείριση Αποστολών
- **Πλήρη διαχείριση αποστολών**: Courier, αριθμός αποστολής, στοιχεία παραλήπτη
- **Status Management**: Διαχείριση κατάστασης αποστολών (Courier Status & MyPMS Status)
- **ACS Integration**: Αυτοματισμός tracking και ημερομηνιών μέσω ACS API
- **Customer Care**: Σύστημα σχολίων και παρακολούθησης για κάθε αποστολή
- **Import/Export**: Μαζική εισαγωγή/εξαγωγή δεδομένων μέσω Excel
- **Προηγμένα φίλτρα**: Αναζήτηση με πολλαπλά κριτήρια

### 🔄 Εξαγωγή & Εισαγωγή Δεδομένων
- **Excel Support**: Πλήρης υποστήριξη για .xlsx αρχεία
- **Template-based Import**: Χρήση των εξαγόμενων Excel ως templates για εισαγωγή
- **Data Validation**: Έλεγχος και καθαρισμός δεδομένων κατά την εισαγωγή
- **Error Handling**: Ολοκληρωμένη διαχείριση σφαλμάτων με user-friendly μηνύματα

## Αρχιτεκτονική

### Τεχνολογικό Stack
- **Frontend**: Java Swing
- **Backend**: Java 11+
- **Database**: MySQL 8.0+
- **Build Tool**: Maven/Gradle
- **External Libraries**: Apache POI (Excel), MySQL Connector

### Design Patterns
- **MVC Architecture**: Διαχωρισμός UI, Business Logic, και Data Access
- **DAO Pattern**: Encapsulation των database operations
- **Observer Pattern**: Event handling για UI updates
- **Factory Pattern**: Δημιουργία UI components
- **Utility Pattern**: Reusable functionality

### Layer Architecture

```
┌─────────────────────┐
│   Presentation      │  ← UI Layer (Swing Components)
│   (UI)              │
├─────────────────────┤
│   Business Logic    │  ← Service Layer (Business Rules)
│   (Service)         │
├─────────────────────┤
│   Data Access       │  ← DAO Layer (Database Operations)  
│   (DAO)             │
├─────────────────────┤
│   Database          │  ← MySQL Database
│   (MySQL)           │
└─────────────────────┘
```

## Κύριες Λειτουργικότητες

### Dashboard
- Κεντρική σελίδα εκκίνησης
- Navigation προς όλες τις λειτουργίες
- Σύνδεση με database και error handling

### Customer Management
- CRUD operations για πελάτες
- Form validation με real-time feedback
- Tabbed interface για οργάνωση δεδομένων
- Bulk operations για μαζικές ενέργειες

### Shipment Management  
- Comprehensive shipment tracking
- Status management με color-coded indicators
- Integration με external APIs (ACS)
- Customer care comment system
- Advanced filtering και reporting

### Utilities & Tools
- Automated data validation
- String manipulation και cleaning
- Date formatting και parsing
- File operations (Excel import/export)
- Error logging και monitoring

## Business Logic

### Customer Domain
```
Pelatis (Customer)
├── Basic Info (ΑΦΜ, Επωνυμία, Κατηγορία)
├── Etairia (Company Details)
│   ├── Address Information
│   └── Contact Details
└── Ypeuthynoi (Contact Persons)
    ├── Συμβάσης
    ├── Επικοινωνίας
    ├── Πληρωμών
    ├── Λογιστηριού
    └── Αντικαταβολών
```

### Shipment Domain
```
Apostoli (Shipment)
├── Basic Info (Courier, Αριθμός, Ημερομηνίες)
├── Recipient Details (Παραλήπτης, Διεύθυνση)
├── Status Management
│   ├── Courier Status
│   └── MyPMS Status
└── Customer Care
    ├── Comments
    └── History Tracking
```

### Data Flow
1. **Input Validation**: Όλα τα δεδομένα ελέγχονται κατά την εισαγωγή
2. **Business Rules**: Εφαρμογή business logic (π.χ. status transitions)
3. **Database Operations**: CRUD μέσω DAO pattern
4. **UI Updates**: Automatic refresh και notifications
5. **External Integrations**: API calls για tracking updates

## Πλεονεκτήματα

### Maintainability
- **Clean Code Architecture**: Σαφής διαχωρισμός responsibilities
- **Constants Management**: Centralized configuration
- **Error Handling**: Comprehensive exception management
- **Logging**: Detailed logging για debugging

### Scalability  
- **Modular Design**: Εύκολη προσθήκη νέων features
- **Database Abstraction**: DAO pattern για database independence
- **Service Layer**: Business logic decoupled από UI
- **Utility Classes**: Reusable components

### User Experience
- **Intuitive Interface**: User-friendly Swing components
- **Real-time Validation**: Immediate feedback
- **Keyboard Shortcuts**: Power-user functionality
- **Error Messages**: Clear, actionable error messages σε ελληνικά

### Performance
- **Lazy Loading**: Data loaded on demand
- **Connection Pooling**: Efficient database connections
- **Caching**: Strategic caching για frequent operations
- **Bulk Operations**: Efficient mass data processing

## Επεκτασιμότητα

### Προγραμματισμένες Βελτιώσεις
- **REST API**: Web service layer για external integrations
- **Reporting Module**: Advanced reporting και analytics
- **Mobile App**: Companion mobile application
- **Cloud Integration**: Cloud storage και backup
- **Multi-tenant**: Support για πολλαπλές επιχειρήσεις

### Τεχνικές Βελτιώσεις
- **Spring Framework**: Migration to Spring για better dependency injection
- **JPA/Hibernate**: ORM για database operations
- **JavaFX**: Modern UI framework
- **Microservices**: Service decomposition για scalability