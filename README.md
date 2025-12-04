# MyRuns – Android Fitness Tracking Application

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)](https://kotlinlang.org)
[![Google Maps](https://img.shields.io/badge/Maps-Google%20Maps%20API-red.svg)](https://developers.google.com/maps)
[![License](https://img.shields.io/badge/License-Academic-yellow.svg)]()

A comprehensive Android fitness tracking application that captures and displays physical activities using GPS tracking, manual entry, and machine learning-based automatic activity recognition.

📥 Download APK: [**MyRuns APK**](https://1drv.ms/u/c/931d74c7905f6350/IQCOFYicNJH_QItd6ZWhgI6XAaHU56tAEio1UHPHTz9AHCo?e=xZ8mHe)

---

## 📱 Features

### Core Functionality

#### Multiple Input Modes
- **Manual Entry**: Input workout details manually with custom dialogs
- **GPS Tracking**: Real-time location tracking with live map visualization
- **Automatic Recognition**: ML-based activity detection using accelerometer data

#### User Profile Management
- Customizable profile with photo, name, email, phone, gender, and major
- Camera integration or gallery selection for profile pictures
- Persistent storage using SharedPreferences

#### Exercise History
- Comprehensive workout history with chronological listing
- Distance, duration, speed, calories, and location tracking
- Detailed view for manual entries
- Interactive map visualization for GPS-tracked activities
- Delete functionality for managing entries

#### Settings & Preferences
- Unit preferences (Metric/Imperial) with automatic conversion
- Privacy settings
- User comments and customization options

---

## 🏗️ Architecture

### Design Patterns
- **MVVM (Model-View-ViewModel)** architecture for separation of concerns
- **Repository Pattern** for data abstraction layer
- **LiveData** for reactive UI updates
- **Room Database** for local data persistence

### Project Structure

```
MyRuns/
├── Database
│   ├── Converters (Type converters for Room Database)
│   ├── ExerciseEntry (Room entity)
│   ├── WorkoutDatabase (Singleton app database)
│   ├── WorkoutDatabaseDao (Database operations)
│   ├── WorkoutRepository (Data access abstraction)
│   └── WorkoutViewModel (Database view model)
├── Dialogs
│   ├── InputDialogFragment (Dialog for user inputs)
│   └── OptionDialogFragment (Dialog for selecting from multiple options)
├── DisplayEntry
│   └── DisplayEntryActivity (Display exercise info from database)
├── Main/
│   ├── History/
│   │   ├── HistoryAdapter (Custom adapter for exercise list)
│   │   └── HistoryFragment (Exercise history display)
│   ├── SettingsFragment (App preferences)
│   └── StartFragment (Workout input selection)
├── ManualInput
│   └── ManualInputActivity (Record manual exercise entry)
├── MapDisplay
│   ├── MapDisplayActivity (Record and display GPS/Automatic exercise entry)
│   ├── MapDisplayViewModel (UI/Service management)
│   └── TrackingService (Background location and activity tracking)
├── UserProfile
│   ├── UserProfileActivity (User profile UI)
│   └── UserProfileViewModel (UI state management)
├── Weka/
│   ├── Classifier/
│   │   └── WekaWrapper.java (Weka classifier wrapper)
│   └── FFT/
│       └── FFT.java (Fast Fourier Transform implementation)
├── MainActivity (TabLayout with 3 fragments)
├── MyFragmentStateAdapter (ViewPager2 adapter for fragments)
├── ViewModelFactory (ViewModel factory for dependency injection)
└── WorkoutFormatter (Utility class to format exercise stats and conversion)
```

---

## 🛠️ Technical Implementation

### Technologies & Libraries

| Component | Technology |
|-----------|-----------|
| Language | Kotlin/Java | 
| Database | Room Persistence Library |
| Maps | Google Maps Android API |
| Machine Learning | Weka Classifier |
| Architecture | MVVM with LiveData & ViewModel |
| UI Components | TabLayout, ListView, DialogFragment |
| Storage | SharedPreferences, Room Database |

### Key Features Implementation

#### 1. GPS Tracking
- Real-time location updates using bound services and message handlers
- Polyline visualization showing complete route
- Start and end markers on map
- Live statistics: average speed, current speed, distance and calories
- Persistent foreground notification during tracking
- Background service for continuous tracking

#### 2. Activity Recognition
- Accelerometer-based feature extraction using FFT (Fast Fourier Transform)
- 64-sample sliding window for real-time classification
- Weka classifier integration for activity prediction
- Supported activities: Walking, Running, Standing
- Majority voting for final activity determination

#### 3. Data Persistence
- **SharedPreferences**: User profile and app settings
- **Room Database**: Exercise entries with full CRUD operations
- **BLOB Storage**: Efficient GPS coordinate storage
- **Thread-safe operations**: Repository pattern with coroutines

#### 4. Map Display
- Dual mode: History display and live tracking
- FrameLayout with overlaid status information
- Dynamic polyline updates
- Camera positioning and zoom control
- Custom markers for start/end points

---

## 📊 Data Structure

### ExerciseEntry Model

```kotlin
@Entity(tableName = "workout_data")
data class ExerciseEntry (

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,                                          // Unique ID to each entry

    var inputType: Int = -1,                                    // Manual, GPS or automatic
    var activityType: Int = -1,                                 // Running, cycling etc.
    var dateTime: Calendar = Calendar.getInstance(),            // Date and Time ("9:45:12 Oct 18 2025")
    var duration: Double = 0.0,                                 // Exercise duration in seconds
    var distance: Double = 0.0,                                 // Distance traveled in kms or miles
    var avgSpeed: Double = 0.0,                                 // Average speed
    var calorie: Double = 0.0,                                  // Calories burnt
    var heartRate: Double = 0.0,                                // Heart rate in bpm
    var comment: String = "",                                   // Comments
    var locationList: ArrayList<LatLng> = ArrayList()           // Location: latitudes and longitudes
```

### Manual Entry Fields

| Field | Input Type | Storage Format |
|-------|-----------|---------------|
| Activity Type | Spinner | Integer |
| Date & Time | DatePickerDialog + TimePickerDialog | Calendar → Long |
| Duration | InputDialog | Minutes (Double) |
| Distance | InputDialog | Kilometers/miles (Double) |
| Calories | InputDialog | Double |
| Heart Rate | InputDialog | BPM (Double) |
| Comment | InputDialog | String |

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Arctic Fox (2020.3.1) or later
- Android SDK 24+ (Android 7.0 Nougat)
- Google Maps API key
- Physical Android device with GPS (recommended for full functionality)
- Minimum 2GB RAM for Android Studio

### Installation

#### 1. Clone the Repository
```bash
git clone https://github.com/AyushArora10hg/MyRuns---CMPT362.git
```

#### 2. Configure Google Maps API
- Obtain a Google Maps API key from [Google Cloud Console](https://console.cloud.google.com)
- Enable "Maps SDK for Android" in your Google Cloud project
- Add your API key to `local.properties`:
```properties
MAPS_API_KEY = your_api_key_here
```
- Or add directly to `AndroidManifest.xml`:
```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="YOUR_API_KEY_HERE" />
```

#### 3. Sync and Build
```bash
./gradlew clean
./gradlew assembleDebug
```
Or use Android Studio:
- File → Sync Project with Gradle Files
- Build → Make Project

#### 4. Run on Device
```bash
./gradlew installDebug
```
Or use Android Studio's Run button (Shift + F10)

### Required Permissions

The app requires the following permissions (handled at runtime):

```xml
<!-- Location permissions -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

<!-- Camera and storage -->
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

<!-- Activity recognition -->
<uses-permission android:name="android.permission.ACTIVITY_RECOGNITION" />

<!-- Notification -->
<uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>
    
```

---

## 📖 User Guide

### Setting Up Your Profile

1. Launch the app and navigate to **Settings** tab
2. Tap **User Profile**
3. Tap **Change** button to add profile picture:
   - Select "Open camera" to use camera
   - Select "Select from gallery" to choose existing photo
4. Fill in your details: Name, Email, Phone, Gender, Class, Major
5. Tap **Save** to store your profile
6. Tap **Cancel** to discard changes

### Manual Entry Mode

1. Navigate to **Start** tab
2. Select **"Manual Entry"** from Input Type spinner
3. Choose your **Activity Type** (Running, Cycling, etc.)
4. Tap **Start** button
5. Input workout details using dialogs:
   - Date & Time
   - Duration (minutes)
   - Distance (miles or kilometers)
   - Calories burned
   - Heart Rate
   - Optional comments
6. Tap **Save** to store the entry

### GPS Tracking Mode

1. Navigate to **Start** tab
2. Select **"GPS"** from Input Type spinner
3. Choose your **Activity Type**
4. Tap **Start** to begin tracking
5. View real-time map with:
   - Current location (red marker)
   - Route polyline (black line)
   - Start position (green marker)
   - Live statistics overlay
6. Tap **Save** to store workout or **Cancel** to discard
7. Notification remains active during tracking

### Automatic Recognition Mode

1. Navigate to **Start** tab
2. Select **"Automatic"** from Input Type spinner
3. Tap **Start** button
4. System automatically detects activity type using:
   - Accelerometer data
   - Machine learning classifier (Trained using Weka)
5. Most frequent activity becomes final classification
6. Tap **Save** to store workout or **Cancel** to discard
7. Notification remains active during tracking

### Viewing History

1. Navigate to **History** tab
2. Browse chronological list of workouts
3. Each entry shows:
   - Activity type and date (first line)
   - Distance and duration (second line)
4. Tap any entry to view details:
   - Manual entries → Detailed statistics view
   - GPS entries → Interactive map with route
5. Use **DELETE** button to remove entries

### Configuring Settings

1. Navigate to **Settings** tab
2. Configure preferences:
   - **User Profile**: Edit personal information
   - **Unit Preference**: Choose Metric (km) or Imperial (miles)
   - **Privacy**: Toggle privacy settings
   - **Comments**: Add app-wide comments
3. All settings save automatically

---

## 🧪 Activity Recognition Training

### Collecting Training Data

1. **Setup Data Collector App**
   - Import `myrunsdatacollector.zip` into Android Studio
   - Build and install on your phone
   - Grant necessary permissions

2. **Collect Activity Samples**
   - Select activity type (Walking, Running, Standing)
   - Tap "Start Recording"
   - Perform the activity naturally for 1-2 minutes
   - Tap "Stop Recording"
   - Repeat for each activity type (minimum 10 samples each)

3. **Export Training Data**
   - Tap "Export" in the data collector app
   - Save `features.arff` file to your device
   - Transfer file to computer

### Training the Classifier

1. **Open Weka**
   - Download from [Weka Website](https://www.cs.waikato.ac.nz/ml/weka/)
   - Launch Weka Explorer

2. **Load Training Data**
   - Click "Open File" and select `features.arff`
   - Verify all attributes and class labels

3. **Train Classifier**
   - Navigate to "Classify" tab
   - Choose classifier: `trees.J48` or `functions.SMO`
   - Click "Start" to train
   - Evaluate accuracy (aim for >85%)

### Feature Extraction Details

The app extracts features from accelerometer data using:
- **64 samples** per classification window
- **Fast Fourier Transform (FFT)** for frequency domain features
- **Maximum magnitude** as additional feature
- **Real-time processing** in background thread

```kotlin
val features = DoubleArray(FEATURE_VECTOR_SIZE) //65

        var max = Double.MIN_VALUE
        for (value in accBlock) {
            if (max < value) {
                max = value
            }
        }

        val fft = FFT(ACCELEROMETER_BLOCK_CAPACITY) //64
        fft.fft(accBlock, im)

        for (i in 0 until ACCELEROMETER_BLOCK_CAPACITY) {
            val magnitude = sqrt(accBlock[i] * accBlock[i] + im[i] * im[i])
            features[i] = magnitude
            im[i] = 0.0
        }
        features[ACCELEROMETER_BLOCK_CAPACITY] = max
```

---

## 🔧 Development Guidelines

### Code Style & Standards

- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names
- Keep functions small and focused (Single Responsibility Principle)
- Document complex algorithms with comments

### Error Handling Best Practices

**Normal Operation Expectations:**
- App must work under typical user conditions
- Handle permission grants gracefully
- Manage network connectivity changes
- Support device orientation changes

**Acceptable Failures:**
- User denies required permissions
- No GPS signal in underground locations
- Device lacks required sensors

### UI/UX Considerations

1. **Orientation Handling**
   - Maintain tab selection across rotation
   - Preserve form input during configuration changes
   - Use ViewModel to retain UI state

2. **Performance**
   - Database operations on background threads
   - Smooth scrolling in history list
   - Efficient map rendering
   - Minimal battery drain during tracking

3. **User Feedback**
   - Loading indicators for async operations
   - Toast messages for confirmations
   - Clear error messages
   - Progress updates during tracking
     
---

## 🔄 Project Development Timeline

### User Profile (Week 1-2)
- ✅ Profile Activity with form inputs
- ✅ Camera integration for profile picture
- ✅ SharedPreferences for data persistence
- ✅ Save/Cancel functionality

### User Interface (Week 3-4)
- ✅ MainActivity with TabLayout
- ✅ Three fragments: Start, History, Settings
- ✅ Gallery selection for profile image
- ✅ Navigation between activities
- ✅ Orientation handling

### Database Implementation (Week 7-8)
- ✅ Room Database setup
- ✅ ExerciseEntry entity and DAO
- ✅ Repository pattern
- ✅ Manual entry creation
- ✅ History list display
- ✅ Entry deletion

### Google Maps Integration (Week 9-10)
- ✅ Google Maps API setup
- ✅ TrackingService implementation
- ✅ Real-time GPS tracking
- ✅ Polyline visualization
- ✅ Location data persistence
- ✅ Map-based history display

### Activity Recognition (Week 11)
- ✅ Accelerometer data collection
- ✅ FFT feature extraction
- ✅ Weka classifier integration
- ✅ Real-time activity inference
- ✅ Automatic mode implementation

---

## 📄 License

This project is developed as part of an academic course assignment for educational purposes.

---

## 📊 Project Statistics

- **Total Activities**: 4 (MapDisplay, ManualInput, DisplayEntry, UserProfile) + 1 (Main)
- **Fragments**: 5 (Start, History, Settings, InputDialog, OptionDialog)
- **Database Tables**: 1 (ExerciseEntry)
- **Service Components**: 1 (TrackingService)
- **Lines of Code**: ~5000+ (estimated)
- **Development Time**: 9 weeks

---

**Built with ❤️ for Android Development Course**

*Last Updated: December 2025*

