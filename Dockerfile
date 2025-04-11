FROM ubuntu:22.04

# Install basic dependencies
RUN apt-get update && \
    apt-get install -y wget unzip openjdk-17-jdk gradle

# Install Android SDK
RUN wget https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip -O /tmp/cmdline-tools.zip && \
    mkdir -p /usr/local/android-sdk/cmdline-tools && \
    unzip /tmp/cmdline-tools.zip -d /usr/local/android-sdk/cmdline-tools && \
    mv /usr/local/android-sdk/cmdline-tools/cmdline-tools /usr/local/android-sdk/cmdline-tools/latest && \
    rm /tmp/cmdline-tools.zip

# Set environment variables
ENV ANDROID_HOME=/usr/local/android-sdk
ENV PATH="${PATH}:${ANDROID_HOME}/cmdline-tools/latest/bin:${ANDROID_HOME}/platform-tools"

# Accept licenses and install required components
RUN yes | sdkmanager --licenses && \
    sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"

WORKDIR /app
COPY . .