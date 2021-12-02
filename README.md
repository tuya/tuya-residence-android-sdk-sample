# Tuya Smart Residence App SDK Sample for Android

 [English](README.md) | [中文版](README-zh.md)

---

This sample demonstrates the use of Tuya Smart Residence App SDK to build a smart residence app. The SDK is divided into several functional modules to give you a clear insight into the implementation of different features, including the user registration process, site management by different users, app access management, and password access management.


## Prerequisites

- Android Studio  Arctic Fox | 2020.3.1 Patch 3 Build #AI-203.7717.56.2031.7784292, built on October 1, 2021
- Gradle 7.0 and later

## Use the sample

1. Tuya Smart Residence App SDK is integrated based on Gradle. Required resources must be installed.

2. Clone or download this sample.

3. This sample requires you to get a pair of keys and a security image from [Tuya Developer Platform](https://developer.tuya.com/), and register a developer account on this platform if you do not have one. Then, perform the following steps:

   1. Log in to the [Tuya IoT Development Platform](https://iot.tuya.com/). In the left-side navigation pane, choose **App** > **SDK Development**.

   2. Click **Create** to create an app.

   3. Fill in the required information, such as the app name and package name.

   4. You can find the AppKey, AppSecret, and security image on the **Get Key** tab.

4. Open the sample project `AndroidManiFest.xml`.

5. Fill in the AppKey and SecretKey in the **AndroidManiFest.xml** file.

    ```
            <meta-data
                android:name="TUYA_SMART_APPKEY"
                android:value="" />
            <meta-data
                android:name="TUYA_SMART_SECRET"
                android:value="" />
    ```

6. Download the security image, rename it `t_s.bmp`, and then drag it to the `assets` directory.

    **Note**: The package name, AppKey, AppSecret, and security image must be the same as those used for your app on the [Tuya IoT Development Platform](https://iot.tuya.com). Otherwise, the sample cannot be run as expected.

7. Configure the running environment of Jetpack Compose to develop the sample. For more information, see Android's documentation.

8. Run gradle sync.

## References
For more information about Tuya Smart Residence App SDK, see [Smart Residence App SDK](https://developer.tuya.com/en/docs/app-development).
