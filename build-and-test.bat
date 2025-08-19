@echo off
echo 🚀 Building LineCut App...
gradlew assembleDebug

if %ERRORLEVEL% == 0 (
    echo ✅ Build successful!
    echo 📱 APK location: app\build\outputs\apk\debug\app-debug.apk
    echo.
    echo 🔧 To install on device/emulator:
    echo adb install app\build\outputs\apk\debug\app-debug.apk
) else (
    echo ❌ Build failed!
    echo Check the errors above.
)

pause
