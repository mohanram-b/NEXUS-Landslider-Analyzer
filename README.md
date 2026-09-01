# NEXUS: Autonomous Smart City Incident Management Platform

**NEXUS** is an AI-powered municipal incident triage and emergency coordination application built natively for Android using Jetpack Compose, Material Design 3, and modern Android architecture principles.

NEXUS functions as an autonomous municipal command center that ingests citizen reports and real-time IoT sensor telemetry, correlates multi-channel data streams, executes automated multi-agent AI risk assessments, and coordinates rapid response dispatching.

---

## 🌟 Key Features

### 1. Multi-Agent AI Intelligence Pipeline
- **Triage & Classification Agent**: Categorizes incidents (Flooding, Structural Damage, Power Outage, Traffic Disruption, Hazard) and computes severity scores (1–100).
- **Deduplication & Correlation Agent**: Computes geospatial and contextual similarity to prevent duplicate dispatches.
- **Impact & Escalation Predictor**: Estimates population risk, economic disruption score, and response urgency.
- **Department Routing Engine**: Dispatches incidents to appropriate municipal agencies (Emergency Services, Public Works, Traffic Management, Environmental Protection, Power & Grid Authority).
- **SOP Generator**: Generates actionable, step-by-step containment protocols and safety instructions for field responders.
- **Citizen Communication Synthesizer**: Formulates real-time public advisories and emergency broadcast notices.

### 2. Real-Time IoT Sensor Grid & Telemetry
- Monitors municipal sensor nodes (water level monitors, acoustic vibration sensors, air quality meters, grid voltage sensors).
- Detects threshold anomalies and automatically triggers emergency incident records when critical limits are breached.
- Includes a 1-click **Surge Simulation** to test autonomous flood and infrastructure failover response.

### 3. Incident Management & Triage Workflow
- **Live Command Dashboard**: Aggregated operational KPIs, active priority incident feed, live sensor grid status, and recent activity audit logs.
- **Incident Repository**: Search, filter, and sort municipal events across categories, severities, and lifecycle statuses.
- **Multi-Step Report Intake**: 3-step reporting wizard with real-time AI validation and department assignment.
- **Detailed Incident View**: Live status toggles, dispatcher tactical notes, timestamped audit log timeline, and AI analysis breakdowns.

### 4. Design & User Experience
- **Frosted Glass Dark Aesthetics**: Deep obsidian canvas (`#09090B`), translucent glass containers, specular vertical gradient borders, and electric blue status accents.
- **Side Drawer Navigation**: Spring-animated drawer with route switching between Dashboard, Incidents, Settings, IoT Sensors, and Operational Analytics.
- **Dual Navigation Structure**: Side navigation drawer combined with a quick-access bottom navigation bar.

---

## 🏗️ Architecture & Technology Stack

| Layer | Technologies |
| :--- | :--- |
| **UI & Layout** | Jetpack Compose, Material Design 3 (M3), Compose Navigation, Compose Animation |
| **Architecture** | MVVM (Model-View-ViewModel), Unidirectional Data Flow, Repository Pattern |
| **Concurrency** | Kotlin Coroutines, `StateFlow`, `SharedFlow` |
| **Local Persistence** | Android Room Database (SQLite), TypeConverters |
| **Icons & Design** | Material Symbols / Material Icons Extended |
| **Build System** | Gradle (Kotlin DSL - `.gradle.kts`), Android Gradle Plugin (AGP), KSP |

---

## 🗄️ Database Schema Overview

The local Room database (`NexusDatabase`) manages the following core entities:

- **`IncidentEntity`**: Stores municipal incident reports (`id`, `title`, `description`, `category`, `status`, `severity`, `severityScore`, `impactScore`, `latitude`, `longitude`, `address`, `reporterName`, `departmentName`, `createdAt`, `updatedAt`, `isSensorTriggered`).
- **`SensorEntity`**: Stores IoT monitoring stations (`id`, `name`, `type`, `zone`, `currentValue`, `unit`, `warningThreshold`, `criticalThreshold`, `status`, `lastPingAt`).
- **`SensorReadingEntity`**: Historical time-series telemetry data.
- **`AIAnalysisEntity`**: Multi-agent AI triage results, confidence scores, SOP steps, and citizen alerts.
- **`ActivityEntity`**: Audit trail and chronological dispatch log entries.
- **`UserEntity` & `DepartmentEntity`**: Operator profiles, roles (Dispatcher, Field Commander, Administrator), and municipal service directories.

---

## 📂 Project Structure

```
app/src/main/java/com/example/
├── MainActivity.kt                      # App entry point
├── data/
│   └── local/
│       ├── NexusDatabase.kt             # Room Database configuration
│       ├── Converters.kt                # Enum & Timestamp Room converters
│       ├── dao/                         # Data Access Objects (IncidentDao, SensorDao, etc.)
│       └── entities/                    # Room Database Entities
├── domain/
│   ├── model/                          # Domain Enums & Models
│   └── repository/                     # NexusRepository implementation & Seed Data
└── ui/
    ├── components/                     # Reusable UI widgets (IncidentCard, SensorCard, Drawer, etc.)
    ├── navigation/                     # Navigation Host & Routes
    ├── screens/                        # Compose Screen implementations
    │   ├── DashboardScreen.kt
    │   ├── IncidentsScreen.kt
    │   ├── IncidentDetailScreen.kt
    │   ├── ReportIncidentScreen.kt
    │   ├── SensorsScreen.kt
    │   ├── AnalyticsScreen.kt
    │   └── SettingsScreen.kt
    ├── theme/                          # Color palettes, Typography, Frosted Glass definitions
    └── viewmodel/                      # NexusViewModel state management
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug (or newer)
- Android SDK 35 / Min SDK 26
- Java 17+

### Running the App
1. Open the project in Android Studio or Google AI Studio.
2. Allow Gradle to sync dependencies.
3. Select an emulator or connected physical Android device (API 26+).
4. Run the `:app` configuration (`Shift + F10`).

---

## 📄 License
This project is licensed under the Apache 2.0 License.
