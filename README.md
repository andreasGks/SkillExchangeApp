# 🤝 SkillExchange App

Μια Android εφαρμογή που επιτρέπει σε χρήστες να ανταλλάσσουν δεξιότητες **χωρίς τη διαμεσολάβηση χρημάτων**.  
Ο κάθε χρήστης μπορεί να ανεβάσει τις δεξιότητές του, να δηλώσει ποιες δεξιότητες αναζητά, να δει το feed με τα posts άλλων χρηστών, να αναζητήσει συγκεκριμένες δεξιότητες και να επικοινωνήσει με άλλα μέλη.

---

## 🖼 Screenshots
*(Πρόσθεσε εικόνες εδώ μόλις έχεις έτοιμο UI – login screen, feed, profile, settings)*

---

## 🛠 Tech Stack
- **Language:** Kotlin  
- **Architecture:** MVVM  
- **Asynchronous:** Coroutines  
- **Database & Auth:** Firebase Firestore & Firebase Authentication  
- **IDE:** Android Studio  
- **UI Components:** Fragments, RecyclerViews, BottomNavigation  
- **APIs:** Google API (για αναζήτηση / posts metadata)  

---

## ✨ Features
- 🔑 **Authentication:** Login & Registration με Firebase Auth  
- 🏠 **Home Screen:** Εμφάνιση posts άλλων χρηστών  
- 📰 **Feed Screen:** Συνεχής ενημέρωση με RecyclerView  
- 🔍 **Search:** Αναζήτηση posts / δεξιοτήτων  
- 👤 **Profile Screen:** Προβολή & επεξεργασία προφίλ  
- 🛠 **Settings Screen:** Βασικές ρυθμίσεις λογαριασμού  
- 📸 **Upload Photo:** Ανεβάζει προφίλ φωτογραφία (σε Firestore ως Base64)  
- ➕ **Post Creation:** Δημιουργία posts για τις δεξιότητες που προσφέρεις/ζητάς  
- 🗑 **Editable Lists:** Προβολή & επεξεργασία λίστας skills (offered/wanted)  
- 🔄 **Real-time Updates:** Τα δεδομένα συγχρονίζονται σε πραγματικό χρόνο μέσω Firestore  

---

## 🏗 App Structure
- **Fragments:** Για καλύτερη διαχείριση UI (FeedFragment, ProfileFragment, SearchFragment, SettingsFragment)  
- **ViewModels:** Για διαχωρισμό UI και business logic (MVVM pattern)  
- **Repositories:** Χειρίζονται τα calls στο Firebase  
- **RecyclerViews & Adapters:** Για δυναμική εμφάνιση posts & skills  

---

## ▶️ How to Run
1. Κάνε clone το repo:
   ```bash
   git clone https://github.com/username/SkillExchange.git
