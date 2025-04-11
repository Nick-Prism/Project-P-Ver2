#!/bin/bash
# Build the Docker image
docker build -t android-builder .

# Run the build inside the container
docker run -v $(pwd):/app android-builder gradle clean assembleDebug

# Check if build succeeded
if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
    echo "Build successful! APK generated at: app/build/outputs/apk/debug/app-debug.apk"
else
    echo "Build failed. Check the output for errors."
fi