@echo off
setlocal enabledelayedexpansion

echo ========================================
echo    HelloApp APK Builder
echo ========================================

set ANDROID_HOME=C:\Program Files (x86)\Android\android-sdk
set BUILD_TOOLS=%ANDROID_HOME%\build-tools\35.0.0
set PLATFORM=%ANDROID_HOME%\platforms\android-35
set PROJECT_DIR=%CD%
set OUTPUT_DIR=%PROJECT_DIR%\build\outputs
set INTERMEDIATES_DIR=%PROJECT_DIR%\build\intermediates

:: Clean
if exist "%PROJECT_DIR%\build" rmdir /s /q "%PROJECT_DIR%\build"
mkdir "%OUTPUT_DIR%\apk"
mkdir "%INTERMEDIATES_DIR%\res"
mkdir "%INTERMEDIATES_DIR%\classes"

set AAPT2=%BUILD_TOOLS%\aapt2.exe
set D8=%BUILD_TOOLS%\d8.bat
set APKSIGNER=%BUILD_TOOLS%\apksigner.bat
set ZIPALIGN=%BUILD_TOOLS%\zipalign.exe
set ANDROID_JAR=%PLATFORM%\android.jar

echo [1/6] Compiling resources (aapt2)...
%AAPT2% compile -o "%INTERMEDIATES_DIR%\res\resources.zip" --dir "%PROJECT_DIR%\app\src\main\res" 2>&1
if !ERRORLEVEL! neq 0 (
    echo ERROR: Resource compilation failed
    exit /b 1
)
echo OK

echo [2/6] Linking resources...
%AAPT2% link -o "%INTERMEDIATES_DIR%\app-debug.ap_" ^
    -I "%ANDROID_JAR%" ^
    --manifest "%PROJECT_DIR%\app\src\main\AndroidManifest.xml" ^
    -R "%INTERMEDIATES_DIR%\res\resources.zip" ^
    --auto-add-overlay 2>&1
if !ERRORLEVEL! neq 0 (
    echo ERROR: Resource linking failed
    exit /b 1
)
echo OK

echo [3/6] Compiling Java source...
dir /s /b "%PROJECT_DIR%\app\src\main\java\*.java" > "%INTERMEDIATES_DIR%\sources.txt"
"%JAVA_HOME%\bin\javac" -d "%INTERMEDIATES_DIR%\classes" ^
    -bootclasspath "%ANDROID_JAR%" ^
    -source 1.8 -target 1.8 ^
    @("%INTERMEDIATES_DIR%\sources.txt") 2>&1
if !ERRORLEVEL! neq 0 (
    echo ERROR: Java compilation failed
    exit /b 1
)
echo OK

echo [4/6] Converting to DEX...
dir /s /b "%INTERMEDIATES_DIR%\classes\*.class" > "%INTERMEDIATES_DIR%\classes.txt"
call %D8% --lib "%ANDROID_JAR%" ^
    --output "%INTERMEDIATES_DIR%\classes.dex" ^
    @("%INTERMEDIATES_DIR%\classes.txt") 2>&1
if !ERRORLEVEL! neq 0 (
    echo ERROR: DEX conversion failed
    exit /b 1
)
echo OK

echo [5/6] Packaging APK...
:: Create APK by combining resources and DEX
cd "%INTERMEDIATES_DIR%"
copy /Y "app-debug.ap_" "%OUTPUT_DIR%\apk\HelloApp-unsigned.apk" >nul
"%BUILD_TOOLS%\aapt2.exe" add "%OUTPUT_DIR%\apk\HelloApp-unsigned.apk" "classes.dex" 2>&1
cd "%PROJECT_DIR%"
if !ERRORLEVEL! neq 0 (
    echo ERROR: APK packaging failed
    exit /b 1
)
echo OK

echo [6/6] Signing APK...
:: Check for debug keystore
set KEYSTORE=%USERPROFILE%\.android\debug.keystore
if not exist "%KEYSTORE%" (
    echo Creating debug keystore...
    "%JAVA_HOME%\bin\keytool" -genkey -v -keystore "%KEYSTORE%" ^
        -alias androiddebugkey -storepass android -keypass android ^
        -keyalg RSA -keysize 2048 -validity 10000 ^
        -dname "CN=Android Debug,O=Android,C=US" 2>&1
)

call %APKSIGNER% sign --ks "%KEYSTORE%" ^
    --ks-pass pass:android ^
    --key-pass pass:android ^
    --ks-key-alias androiddebugkey ^
    "%OUTPUT_DIR%\apk\HelloApp-unsigned.apk" 2>&1
if !ERRORLEVEL! neq 0 (
    echo ERROR: Signing failed
    exit /b 1
)

:: Zipalign
"%ZIPALIGN%" -v -f 4 "%OUTPUT_DIR%\apk\HelloApp-unsigned.apk" "%OUTPUT_DIR%\apk\HelloApp.apk" 2>&1
if !ERRORLEVEL! neq 0 (
    echo ERROR: Zipalign failed
    exit /b 1
)

:: Clean up unsigned
if exist "%OUTPUT_DIR%\apk\HelloApp-unsigned.apk" del "%OUTPUT_DIR%\apk\HelloApp-unsigned.apk"

echo ========================================
echo    BUILD SUCCESS!
echo    APK: %OUTPUT_DIR%\apk\HelloApp.apk
echo ========================================

endlocal
