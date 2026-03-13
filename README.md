# Trucker Buddy

A native Android marketplace that connects shippers with truck owners. Shippers post trips with pickup/delivery details, truck owners bid on them, and deals are finalized through an in-app negotiation flow complete with a virtual coin economy.

Built with **Kotlin**, **Jetpack Compose**, and **Firebase**.

---

## Table of Contents

- [Features](#features)
- [Screenshots](#screenshots)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Database Schema](#database-schema)
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [Roadmap](#roadmap)

---

## Features

### Authentication
- Email/password sign-up and sign-in via Firebase Auth
- Email verification enforcement before posting trips or placing bids
- Password reset through email link

### Trip Management
- Create trips with pick-up date/time, origin & destination (Division/Zilla), truck type, and goods category
- Browse a live feed of available trips with location-based search
- 18 goods categories (Clothing, Electronics, Food, Construction Materials, etc.)

### Bidding System
- Place bids with a custom amount on any active trip
- Trip creators can view all received bids and inspect bidder profiles
- Bidder profiles display rating, completed trips, and contact information

### Deal Negotiation
- Send deal requests to preferred bidders (costs 5 coins)
- Bidders can accept or reject deal requests
- Cancel pending deal requests before a response

### In-App Coin Economy
- New users receive **10 coins** on sign-up
- Sending a deal request costs **5 coins**, preventing spam and encouraging thoughtful selection
- Coin balance displayed on the user profile

### Driver Rating
- Rate drivers after trip completion (star-based)
- Ratings aggregate on the driver's public profile

### Profile Management
- Upload and update profile pictures (stored in Firebase Storage)
- View personal stats: rating, completed trips, running trips, coin balance
- Sign out and account management

---


## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 1.8 |
| UI Framework | Jetpack Compose with Material 3 |
| Backend | Firebase (Auth, Firestore, Storage, Analytics) |
| Async | Kotlin Coroutines + Firebase KTX |
| Image Loading | Coil for Compose |
| Date/Time Pickers | Compose Material Dialogs |
| Typography | Google Fonts via `ui-text-google-fonts` |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 33 (Android 13) |

---

## Architecture

```
┌─────────────────────────────────────────────┐
│                   UI Layer                  │
│         Jetpack Compose Composables         │
│  (Feed, PostScreen, TripDetail, Profile..)  │
├─────────────────────────────────────────────┤
│              Activity Layer                 │
│   Each screen is a dedicated Activity that  │
│   hosts composables and handles navigation  │
├─────────────────────────────────────────────┤
│           Callback Interfaces               │
│  Typed interfaces decouple UI actions from  │
│  business logic (LoginCallBack, FeedCall-   │
│  Back, PostCallBack, etc.)                  │
├─────────────────────────────────────────────┤
│             Firebase Services               │
│  Auth · Firestore · Storage · Analytics     │
└─────────────────────────────────────────────┘
```

**Key design decisions:**

- **Activity-per-screen** with Jetpack Compose content, allowing deep-link support and straightforward lifecycle management
- **Callback interfaces** (`Functions.kt`) cleanly separate UI events from Activity-level logic
- **Data classes** (`TripBrief`, `BidderInfo`) model Firestore documents for type-safe access
- **Dynamic theming** with Material 3 and Android 12+ dynamic color support

---

## Database Schema

The app uses **Cloud Firestore** with the following collections:

### `Clients` (Users)
| Field | Type | Description |
|---|---|---|
| Name | `string` | Display name |
| Email | `string` | Account email |
| Phone | `string` | Contact number |
| Photo | `string` | Profile picture URL (Firebase Storage) |
| Coin | `number` | Virtual currency balance |
| Score | `number` | Cumulative rating score |
| Rating | `number` | Average star rating |
| Completed Trips | `number` | Total finished trips |
| Running Trips | `array<string>` | Active trip document IDs |
| My Bids | `array<string>` | Bid document IDs placed by user |
| Sent Deal Request | `array<string>` | Outgoing deal request IDs |
| Received Deal Request | `array<string>` | Incoming deal request IDs |

### `Trips`
| Field | Type | Description |
|---|---|---|
| Post Creator | `string` | UID of the shipper |
| Pick Up Date / Time | `string` | Scheduled pickup |
| Pick Up Division / Zilla | `string` | Origin location |
| Delivery Division / Zilla | `string` | Destination location |
| Needed Truck | `map` | Truck name, capacity, image |
| Type of Good | `string` | Cargo category |
| Running | `boolean` | Trip is still accepting bids |
| Assigned | `string` | UID of the assigned driver |
| Ongoing | `boolean` | Trip is in progress |
| Rated | `boolean` | Driver has been rated |
| Bids | `array<string>` | Bid document IDs |

### `Bids`
| Field | Type | Description |
|---|---|---|
| Trip Id | `string` | Associated trip |
| Bidder Id | `string` | UID of the bidding driver |
| Bid Amount | `string` | Proposed price |
| Deal Due / Deal Sent | `boolean` | Deal negotiation state |
| Driver Replied / Deal Accepted | `boolean` | Response status |

### `Trucks`
| Field | Type | Description |
|---|---|---|
| Photo | `string` | Truck image URL |
| Capacity | `number` | Load capacity |

### `Divisions`
| Field | Type | Description |
|---|---|---|
| Zilla | `array<string>` | Sub-regions under each division |

---

## Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17+
- A Firebase project with Auth, Firestore, and Storage enabled

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/<your-username>/Trucker_Buddy.git
   cd Trucker_Buddy
   ```

2. **Firebase configuration**
   - Create a Firebase project at [console.firebase.google.com](https://console.firebase.google.com)
   - Enable **Email/Password** authentication
   - Create a **Firestore** database
   - Enable **Firebase Storage**
   - Download `google-services.json` and place it in `app/`

3. **Seed Firestore data**
   - Add a `Divisions` collection with documents for each division, each containing a `Zilla` array field
   - Add a `Trucks` collection with documents containing `Photo` (URL) and `Capacity` (number) fields

4. **Build and run**
   ```bash
   ./gradlew installDebug
   ```
   Or open in Android Studio and click **Run**.

---

## Project Structure

```
app/src/main/java/com/abhishek/truckerbuddy/
│
├── MainActivity.kt                 # Login entry point
├── SignUpActivity.kt               # User registration
├── ForgetPassActivity.kt           # Password reset
├── ProfileActivity.kt              # User profile & settings
├── FeedActivity.kt                 # Trip feed browser
├── PostActivity.kt                 # Create a new trip
├── TruckScreenActivity.kt          # Truck type selector
├── MyRunningTripsActivity.kt       # Manage posted trips
├── TripDetailScreenActivity.kt     # View trip & place bid
├── ViewResponsesScreenActivity.kt  # View bids on a trip
├── BidderProfileScreenActivity.kt  # Bidder info & deal actions
│
├── Functions.kt                    # Callback interfaces
├── TripBrief.kt                    # Trip data model
├── BidderInfo.kt                   # Bidder data model
│
└── composables/
    ├── LoginScreen.kt              # Login UI
    ├── regScreen.kt                # Registration UI
    ├── ForgetPasswordScreen.kt     # Password reset UI
    ├── ProfileScreen.kt            # Profile UI
    ├── Feed.kt                     # Feed + TripCard + search
    ├── PostScreen.kt               # Trip creation form
    ├── TruckScreen.kt              # Truck grid + TruckCard
    ├── TripDetailScreen.kt         # Trip details + bid form
    ├── ViewResponsesScreen.kt      # Bid list + BidderCard
    ├── BidderProfileScreen.kt      # Bidder profile + deal UI
    ├── MyRunningTrips.kt           # Running trips list
    └── MyRunningTripsCard.kt       # Trip card with rate/done
```

---

## Roadmap

- [ ] Google Maps integration for pickup/delivery visualization
- [ ] Push notifications for bid updates and deal requests
- [ ] In-app messaging between shipper and driver
- [ ] Payment gateway integration
- [ ] Trip tracking with real-time location updates
- [ ] Migrate to single-Activity architecture with Compose Navigation

---

## License

This project is for educational and portfolio purposes.

---

<p align="center">
  Built by <strong>Abhishek</strong>
</p>
