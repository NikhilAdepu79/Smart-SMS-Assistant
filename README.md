README.md Content
Markdown
# Smart SMS Assistant 🤖📱

A sophisticated, automated SMS response system built with Kotlin and Android SDK. This assistant manages missed calls and incoming messages with intelligent, context-aware English greetings and automated logic.

## 🚀 Key Features
* **Dynamic User Identity:** Configure your name directly through the app UI to personalize all outgoing AI responses.
* **Contact-Aware Intelligence:** Automatically retrieves caller names from the Android Contacts database to provide a natural, human-like greeting.
* **Conversational English Logic:** Built-in triggers for common English phrases and questions (e.g., "Where are you?", "Call me").
* **Custom Keyword Engine:** Utilizes a local JSON mapping system (powered by Gson) to trigger unique replies for user-defined keywords like "urgent" or "exam".
* **Missed Call Automation:** Monitors phone states to detect missed calls (25s threshold) and delivers a helpful context-aware SMS.
* **Privacy-First Design:** All processing, contact lookups, and response generation occur 100% on-device with no external data logging.

## 🛠️ Tech Stack
* **Language:** Kotlin 100%
* **Core APIs:** Android BroadcastReceivers, TelephonyManager, and Contacts Provider.
* **Persistence:** SharedPreferences for user settings and JSON-based rule storage.
* **Libraries:** Google Gson for robust data serialization.

## 📖 Conversation Examples
| Incoming Text | AI Automated Response |
| :--- | :--- |
| "Where are you?" | "[Name] is out at the moment but will be back soon. I'll let them know you asked!" |
| "Hey, call me!" | "Sure! I've noted your request. [Name] will call you back as soon as they are free." |
| "Is it urgent?" | "Noted. I will try to alert [Name] immediately regarding your urgent message." |
| "Who is this?" | "I am [Name]'s automated assistant. I'm helping manage messages while they are busy." |

## 📥 Installation & Setup
Follow these steps to run the project on your local machine:

1. **Clone the Repository:**
   ```bash
   git clone [https://github.com/NikhilAdepu79/Smart-SMS-Assistant.git](https://github.com/NikhilAdepu79/Smart-SMS-Assistant.git)
Open in Android Studio:

Launch Android Studio (Ladybug or newer recommended).

Select File > Open and navigate to the cloned folder.

Sync Gradle:

Wait for the IDE to finish the Gradle Sync process.

Note: This project uses Gradle 8.7 and Gson 2.10.1.

Grant Permissions:

Run the app on a physical device or emulator.

Upon launch, the app will request permissions for SMS, Phone State, and Contacts. These are required for the AI logic to function.

Configure Identity:

Enter your name in the input field and click Save Name.

Toggle the AI Mode switch to ON.

📝 **Future Extensions (v2.0)**
Keyword Manager UI: A dedicated screen to add, edit, or delete custom keyword-reply pairs dynamically.

Activity Logging: Integration with a Room Database to provide a searchable history of all handled conversations.

Hybrid AI Core: Integration with the Gemini API for advanced natural language processing when online, while maintaining local logic for offline use.

Calendar Awareness: Automatically adjusting responses based on real-time Google Calendar availability.

Developed by **Adepu Nikhil**
M.Sc. in Artificial Intelligence & Data Science


***

4. Click **Commit changes**.

Now your project looks like a complete, professional software package!
