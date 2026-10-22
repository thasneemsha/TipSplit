# How to Run TipSplit

This guide is for anyone (like a professor or a classmate) who wants to download this project and run it on their own computer. You do **not** need to install the Kotlin command-line compiler to run this Android app. Android Studio handles everything.

---

## Prerequisites (What you need)

Before you begin, make sure you have:
1. A Windows, Mac, or Linux computer (Windows is assumed for this guide).
2. At least 8GB of RAM (16GB is recommended for the emulator).
3. At least 10GB of free disk space.
4. A stable internet connection (for the first time you open the project).

---

## Step 1: Install Android Studio

Android Studio is the official IDE (Integrated Development Environment) for Android development. It contains everything needed to build and run this project, including its own bundled Kotlin compiler and Android SDK.

1. Go to the [Android Studio download page](https://developer.android.com/studio).
2. Download the installer for your operating system.
   * **Windows:** `android-studio-[version]-windows.exe`
3. Run the installer and click **Next** through the wizard. (Leave all default options checked).
4. Once installed, launch Android Studio.
5. When asked, choose **"Do not import settings"**.
6. The Setup Wizard will appear. Choose **"Standard"** installation and click **Next**.
7. **Accept** all the license agreements.
8. Click **Finish** and wait for Android Studio to download the Android SDK and necessary tools. This may take 5–15 minutes.
9. **Important:** If Windows asks for permission to allow `adb.exe` through the firewall, click **Allow access**.

---

## Step 2: Download this Project

1. On the GitHub page for this repository, click the green **"<> Code"** button.
2. Select **"Download ZIP"**.
3. Extract the downloaded ZIP file to a folder on your computer (e.g., `C:\Users\YourName\AndroidStudioProjects\TipSplit`).
   * *Note: Avoid paths with spaces (like `C:\Program Files\`) as it can cause build issues.*

---

## Step 3: Open the Project in Android Studio

1. Open Android Studio.
2. On the Welcome screen, click **"Open"**.
3. Navigate to the `TipSplit` folder you just extracted.
4. Select the folder and click **OK**.
5. Android Studio will now run a **"Gradle Sync"**. Look at the bottom of the screen for a progress bar. 
   * The first sync can take **3–10 minutes** because it needs to download the required libraries.
   * If a banner pops up at the top saying *"Gradle files have changed"*, click **Sync Now** (or the elephant icon with the blue arrow).

---

## Step 4: Set Up an Emulator (Virtual Phone)

To run the app, you need an Android device. If you don't have a physical Android phone plugged into your computer, you can use the built-in Emulator.

1. In the top toolbar, look for the dropdown next to the green Play button (▶️). It might say **"No Devices"**.
2. Click the dropdown and select **"Device Manager"**.
3. In the window that opens, click the **"+"** icon and select **"Create Virtual Device"**.
4. Choose a phone model (e.g., **Pixel 7**) and click **Next**.
5. Select a system image to download. The latest stable version (e.g., **API 34** or **API 35**) is recommended. Click the download icon next to it.
6. Accept the license and wait for the download to finish. Click **Next**.
7. Name your emulator (or leave the default) and click **Finish**.

---

## Step 5: Run the App

1. Make sure your emulator is selected in the top toolbar dropdown.
2. Click the **green Play button (▶️)** at the top of Android Studio.
3. Android Studio will:
   * Build the project.
   * Start the emulator.
   * Install the app.
   * Launch the app automatically.

*Note: The first time you launch the emulator, it might take a minute to boot up. If you get a "device offline" error, open the Device Manager, click the three dots next to your emulator, and select **"Cold Boot Now"**.*

---

## Step 6: Verify the App Works

Once the app is open on the emulator, test these features:

1. **Bill Input:** Type `100` into the bill field.
2. **Tip Selection:** Tap the `20%` chip. The total should instantly update to `$120.00`.
3. **Split Count:** Tap the `+` button twice. The per-person amount should update to `$40.00`, and the LazyColumn at the bottom should show 3 rows.
4. **Rounding Feature:** Change the bill to `128`. Set the tip to `15%`. Split between `2` people. Click **"Round Down"**. A red warning should appear saying **"$1.20 short of total"**.
5. **Edge Cases:** Try typing letters like `abc` into the bill field. The app will ignore them. Try tapping the `-` button until it reaches 1 person. The button should disable itself.

---

## Troubleshooting

* **"Gradle sync failed"**: Check your internet connection. Then click **Try Again** in the sync error banner, or go to **File > Sync Project with Gradle Files**.
* **"SDK location not found"**: Go to **File > Project Structure > SDK Location** and make sure a valid Android SDK path is selected.
* **"Device offline" on the emulator**: Open the **Device Manager**, click the three dots next to the emulator, and select **Cold Boot Now**.
* **Build takes forever**: The first build is always the slowest because it downloads dependencies. Subsequent builds will be much faster.

---
**Enjoy the app!**
