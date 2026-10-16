# Mobile Apps II - Assignment 1 Report

**Student name / ID:** ______________________   **Date:** ____________

---
## Section 1 - Design considerations

### 1.1 Architecture
The app follows the recommended Android layered architecture (MVVM):

| Layer | Classes | Responsibility |
|---|---|---|
| UI (View) | `HomeFragment`, `RegistrationFragment`, `SearchFragment`, XML layouts | Draw screens, forward clicks, observe LiveData. No business logic. |
| ViewModel | `StudentViewModel` | Validates input, calls the repository inside `viewModelScope` coroutines, exposes results as `LiveData`. Survives screen rotation. |
| Repository | `StudentRepository` | Single access point for data; re-checks the year rule. |
| Data (Model) | `Student`, `StudentDao`, `AppDatabase` (Room) | Persistent storage in SQLite. |

**Why:** separating the layers keeps the Fragments small, makes the validation unit-testable (`Validator` has no Android dependencies) and means data survives configuration changes.

### 1.2 Navigation
A **single Activity** (`MainActivity`) hosts a `NavHostFragment`; the three pages are Fragments connected in `nav_graph.xml`. This gives a consistent action bar title, an automatic Up/Back button and a correct back stack. Registration -> Search uses `popUpTo="homeFragment"` so Back returns Home instead of to a stale form.

### 1.3 Database design
Table `students(studentId INTEGER PK AUTOINCREMENT, name TEXT, course TEXT, year INTEGER)`.
The ID is auto-generated (allowed by the brief) which removes duplicate-ID errors and saves the user typing. Database calls are `suspend` functions, so Room runs them off the main thread and the UI never freezes.

### 1.4 Validation (year 1-4)
- The year field is deliberately a free numeric field (not a dropdown) so the validation is real and demonstrable.
- `Validator.isValidYear(year)` accepts only whole numbers 1..4; text, empty, 0, 5 and negatives are rejected with an inline error under the field.
- Defence in depth: the ViewModel validates for user feedback, and the Repository `require()`s the rule again before inserting.
- Name and course must not be blank. Search input must be a number.

### 1.5 User-experience decisions
Material 3 components with inline error messages (`TextInputLayout.error`), `inputType="number"` to open the numeric keyboard, a live counter on Home, an IME "Search" key on the search box, and a "Search this student" shortcut after registering.

### 1.6 Testing performed
Manual script in `README.md` (9 steps incl. invalid year, unknown ID, persistence after restart) and JUnit tests in `ValidatorTest.kt`. **Add your own screenshots here.**

### 1.7 References
- Android Developers. *Save data in a local database using Room.* https://developer.android.com/training/data-storage/room
- Android Developers. *LiveData overview.* https://developer.android.com/topic/libraries/architecture/livedata
- Android Developers. *ViewModel overview.* https://developer.android.com/topic/libraries/architecture/viewmodel
- Android Developers. *Navigation component - Get started / Pass data between destinations.* https://developer.android.com/guide/navigation
- Android Developers. *View binding.* https://developer.android.com/topic/libraries/view-binding
- Android Developers. *Guide to app architecture.* https://developer.android.com/topic/architecture
- Material Design 3. *Text fields.* https://m3.material.io/components/text-fields

### 1.8 AI-assisted code declaration  (REQUIRED by the assignment brief)
The brief allows AI for **at most 3 uses, 1-2 lines of functionality each**, with commented code, a distinct section in this report, a reference, and a screenshot of the interaction.
Fill in **honestly** for any AI help you actually used:

| # | Prompt you gave the AI | Code (1-2 lines) and file/line | What it does | Screenshot |
|---|---|---|---|---|
| 1 | | | | |
| 2 | | | | |
| 3 | | | | |

Reference format: *Anthropic (2026) Claude [Large language model]. Available at: https://claude.ai (Accessed: 15 October 2026).*

---
## Section 2

### Q1. Distinct advantages of LiveData (3 marks)
`LiveData` is an observable, **lifecycle-aware** data holder from Android Jetpack.

1. **Lifecycle awareness:** it only notifies observers that are in an active state (STARTED/RESUMED), so the app never tries to update a screen that is stopped or destroyed - no crashes.
2. **No memory leaks:** observers bound to a `LifecycleOwner` are removed automatically on `DESTROYED`; no manual unregister code.
3. **Always up to date:** when a screen returns from the background or is recreated after rotation, it immediately receives the latest value.
4. **Survives configuration changes** when stored in a `ViewModel` - no re-querying the database after rotating the phone.
5. **Automatic UI refresh from Room:** a DAO method returning `LiveData` re-emits whenever the table changes, so the UI does not need a manual "refresh".
6. **Separation of concerns:** the ViewModel exposes read-only `LiveData`, the UI only observes; data flow is one direction and easy to test.

**Hypothetical example - university seat availability board.** A course-enrolment app shows "Seats left in SE101" on the screen of every student who is browsing. If a student enrols in another tab, the number should drop immediately.
```kotlin
@Query("SELECT COUNT(*) FROM enrolments WHERE courseId = :id")
fun enrolledCount(id: Int): LiveData<Int>          // DAO

viewModel.enrolledCount.observe(viewLifecycleOwner) { n ->
    seatsLeft.text = "Seats left: ${CAPACITY - n}"  // runs on every change
}
```
When another student enrols, Room updates the table, `LiveData` emits the new count and the label changes with no extra code. If the user switches to another app, updates pause (saves battery); on return the label instantly shows the latest value; if the phone is rotated, no new query is needed. Without LiveData the programmer would need callbacks, manual refresh, and manual cleanup to avoid leaks.
*In this project:* the "Registered students: N" label on the Home page uses exactly this mechanism (`StudentDao.observeCount()`).

### Q2. Two means of navigating with data (3 marks)

**Option A - Pass arguments with the Navigation component (Bundle / Safe Args).**
Data is attached to the navigation action: `navigate(R.id.action..., bundleOf("studentId" to id))`, and the destination reads it from `arguments`.
Advantages:
- Simple, explicit and decoupled: the receiving page does not need to know which page sent the data.
- Data is stored in the back stack entry, so it **survives process death and rotation**.
- With Safe Args plugin it becomes **type-safe** (compile-time checks) and supports default values; works with deep links.
- Best for small values such as IDs, strings and flags.
Limitations: Bundle has a size limit (~1 MB) and the data is one-way (only sent forward).
*Used in this app:* Registration passes the new `studentId` to the Search page.

**Option B - Share data through a shared ViewModel (`activityViewModels()` or a navigation-graph-scoped ViewModel).**
Both screens obtain the same ViewModel instance and read/write the same LiveData.
Advantages:
- Can share **large or complex objects** (lists, objects that are not Parcelable) without size limits.
- Data is **observable and two-way**: the second page can change the data and the first page sees it instantly when the user goes back (e.g. an edit screen).
- No serialisation required; survives configuration changes.
Limitations: creates coupling between the screens, and the data is lost if the process is killed unless saved with `SavedStateHandle`.

**Recommendation:** pass a small identifier by arguments (Option A) and let the destination load the full record from Room; use a shared ViewModel (Option B) when screens must work on the same live data together.
