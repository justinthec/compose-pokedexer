#!/bin/bash
set -e

# Define variables
ANDROID_HOME="$HOME/android-sdk"
CMDLINE_TOOLS_URL="https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip"
CMDLINE_TOOLS_DIR="$ANDROID_HOME/cmdline-tools"
LATEST_DIR="$CMDLINE_TOOLS_DIR/latest"

# Create directories
mkdir -p "$ANDROID_HOME"
mkdir -p "$CMDLINE_TOOLS_DIR"

# Download and unzip command line tools if not already present
if [ ! -d "$LATEST_DIR" ]; then
    echo "Downloading Command Line Tools..."
    wget -q "$CMDLINE_TOOLS_URL" -O cmdline-tools.zip
    unzip -q cmdline-tools.zip -d "$CMDLINE_TOOLS_DIR"
    mv "$CMDLINE_TOOLS_DIR/cmdline-tools" "$LATEST_DIR"
    rm cmdline-tools.zip
fi

# Set environment variables for this script
export ANDROID_HOME="$ANDROID_HOME"
export PATH="$LATEST_DIR/bin:$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator:$PATH"

# Accept licenses
yes | sdkmanager --licenses

# Install packages
# Note: Using build-tools 35.0.0 as a safe fallback if 36 isn't explicitly listed yet,
# but installing platforms;android-36 as requested by build.gradle.kts
echo "Installing Android SDK packages..."
sdkmanager "platform-tools" \
           "platforms;android-36" \
           "build-tools;35.0.0" \
           "system-images;android-36;google_apis;x86_64" \
           "emulator"

echo "Android SDK setup complete."
