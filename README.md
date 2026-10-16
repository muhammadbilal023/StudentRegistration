# Student Registration App (Mobile Apps II - Assignment 1)

Native Android app written in **Kotlin**. Stack: Fragments + Navigation Component, Room database,
ViewModel + LiveData, Material 3, View Binding.

## How to run
1. Install **Android Studio** (Koala 2024.1 or newer; it bundles JDK 17).
2. Unzip the project. In Android Studio choose **File > Open** and select the `StudentRegistration` folder.
3. Wait for the Gradle sync to finish (first sync downloads Gradle 8.7 + libraries, so you need internet).
   If Android Studio asks to install an SDK platform, accept (compileSdk 34 / Android 14).
4. Create an emulator (**Device Manager > Create Device**, any Pixel, API 24 or higher) or plug in a phone with USB debugging.
5. Press the green **Run** button. The app opens on the Home page.
6. Run unit tests (optional): right-click `app/src/test/.../ValidatorTest.kt` > **Run**.

## Suggested demo / test script (use for your screenshots)
| Step | Action | Expected result |
|------|--------|-----------------|
| 1 | Home page | Two buttons + "Registered students: 0" |
| 2 | Add Student, leave fields empty, Register | Errors on name, course and year |
| 3 | Year = 5 (also try 0, -1) | "Year must be a whole number from 1 to 4", nothing saved |
| 4 | Name = Ali Khan, Course = Software Engineering, Year = 2, Register | "Student registered successfully! Student ID: 1" |
| 5 | Press "Search this student" | Search page opens with ID 1 pre-filled, shows name + course |
| 6 | Back to Home | Counter shows 1 and updated by itself (LiveData) |
| 7 | Find Student, type 999, Search | "No student found with ID 999" |
| 8 | Find Student, leave empty, Search | "Please enter a valid numeric student ID" |
| 9 | Close and reopen the app, search ID 1 | Student still found (data persisted in Room) |

## Project structure
```
app/src/main/java/com/example/studentregistration/
  MainActivity.kt              single activity hosting the NavHost
  data/   Student.kt (entity) | StudentDao.kt | AppDatabase.kt | StudentRepository.kt
  viewmodel/StudentViewModel.kt  logic + LiveData state (register / search)
  ui/     HomeFragment | RegistrationFragment | SearchFragment
  util/   Validator.kt           year (1-4) / name / course rules
app/src/main/res/
  navigation/nav_graph.xml     the 3 pages and the actions between them
  layout/                      activity_main + one layout per page
  values/                      strings.xml, themes.xml
app/src/test/.../ValidatorTest.kt  unit tests for the validation rules
```

## How the project meets each requirement
| Requirement (marks) | Where / how it is met |
|---|---|
| **Homepage** that navigates to the other pages (4) | `HomeFragment` + `fragment_home.xml`: "Add Student" and "Find Student" buttons call `findNavController().navigate(...)` using actions declared in `nav_graph.xml`. Home is the start destination. |
| **Registration page** with ID (auto), Name, Course, Year stored in **Room** (4) | `RegistrationFragment` + `fragment_registration.xml` (Name, Course, Year inputs). `Student` entity has `@PrimaryKey(autoGenerate = true) studentId`, so the ID is auto-generated and shown after saving. `StudentViewModel.register()` -> `StudentRepository.addStudent()` -> `StudentDao.insert()` writes to the Room database `student_registration.db`. |
| **Student Search page** by ID: show name + course if found, error if not (4) | `SearchFragment` + `fragment_search.xml`. `StudentDao.findById()` returns `Student?`. Found -> card with ID, name, course (and year). Not found (`null`) -> red message "No student found with ID X". Non-numeric/empty input -> "Please enter a valid numeric student ID". |
| **Data validation: year must be 1-4** (3) | Three layers: (1) `Validator.isValidYear()` is called by `StudentViewModel.register()` before anything is saved and the form shows "Year must be a whole number from 1 to 4"; (2) `StudentRepository.addStudent()` uses `require(...)` so invalid data can never reach Room even if a screen forgets to validate; (3) `ValidatorTest` unit-tests 1-4 (valid) and 0, 5, -2, null (invalid). Name and course are also required. |
| **Show ALL design considerations and references, incl. AI code** | See `REPORT.md` (Section 1 design decisions, references list, AI-use declaration section). |
| **Section 2 - LiveData and navigating with data** (3 + 3) | Written answers in `REPORT.md`. The LiveData answer is demonstrated in the app by the live student counter on Home; navigating with data is demonstrated by the Registration -> Search `studentId` Bundle argument. |
