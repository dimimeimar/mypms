package org.pms.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.Firestore;
import java.io.IOException;
import java.io.InputStream;

public class FirebaseConfig {

    private static FirebaseConfig instance;
    private Firestore firestore;
    private boolean initialized = false;

    private FirebaseConfig() {}

    public static synchronized FirebaseConfig getInstance() {
        if (instance == null) {
            instance = new FirebaseConfig();
        }
        return instance;
    }

    public Firestore getFirestore() {
        if (!initialized) {
            throw new IllegalStateException("Firebase δεν έχει αρχικοποιηθεί!");
        }
        return firestore;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void initializeWithStream(InputStream serviceAccountStream) throws IOException {
        if (initialized) {
            System.out.println("Firebase ήδη αρχικοποιημένο");
            return;
        }

        try {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
                    .build();

            FirebaseApp.initializeApp(options);
            this.firestore = FirestoreClient.getFirestore();
            this.initialized = true;

            System.out.println("✓ Firebase αρχικοποιήθηκε επιτυχώς");

        } catch (IOException e) {
            System.err.println("Σφάλμα αρχικοποίησης Firebase: " + e.getMessage());
            throw e;
        }
    }


}