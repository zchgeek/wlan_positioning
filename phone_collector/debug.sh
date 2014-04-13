ant debug
adb install -r bin/collector-debug.apk
adb shell am start wlan_positioning.collector/wlan_positioning.collector.Collector