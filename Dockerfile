FROM openjdk:17-jdk-slim

ENV ANDROID_HOME=/usr/local/android-sdk
ENV PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/cmdline-tools/bin:$ANDROID_HOME/platform-tools

RUN apt-get update && apt-get install -y wget unzip && rm -rf /var/lib/apt/lists/*
RUN mkdir -p $ANDROID_HOME/cmdline-tools \
    && cd $ANDROID_HOME/cmdline-tools \
    && wget https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip -O tools.zip \
    && mkdir latest \
    && unzip tools.zip -d latest \
    && rm tools.zip
RUN yes | sdkmanager --sdk_root=$ANDROID_HOME --licenses
RUN sdkmanager --sdk_root=$ANDROID_HOME "platform-tools" "build-tools;34.0.0" "platforms;android-34"

WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN sed -i 's/\r$//' gradlew
RUN ./gradlew assembleDebug --no-daemon
CMD ["ls", "-lh", "app/build/outputs/apk/debug/"]
