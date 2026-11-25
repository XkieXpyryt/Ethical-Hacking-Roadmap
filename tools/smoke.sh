#!/bin/bash
# Emulator smoke test: installs the debug APK, walks through key screens, captures screenshots and crash logs.
OUT=smoke; mkdir -p $OUT
APK=$(find apks -name 'app-debug.apk' | head -1)
PKG=com.bernportal.ehr.debug
adb install -r "$APK" > $OUT/install.txt 2>&1
adb shell settings put system screen_off_timeout 1800000
adb logcat -c
shot() { adb exec-out screencap -p > "$OUT/$1.png"; }
find_xy() {
  adb shell uiautomator dump /sdcard/ui.xml >/dev/null 2>&1; adb pull /sdcard/ui.xml /tmp/ui.xml >/dev/null 2>&1
  python3 - "$1" <<'PY'
import re, sys
t = sys.argv[1]; x = open('/tmp/ui.xml', encoding='utf-8').read()
for m in re.finditer(r'<node [^>]*>', x):
    n = m.group(0)
    tx = re.search(r' text="([^"]*)"', n); cd = re.search(r'content-desc="([^"]*)"', n)
    if (tx and t in tx.group(1)) or (cd and t in cd.group(1)):
        b = re.search(r'bounds="\[(\d+),(\d+)\]\[(\d+),(\d+)\]"', n)
        x1, y1, x2, y2 = map(int, b.groups()); print((x1 + x2) // 2, (y1 + y2) // 2); break
PY
}
tap() { xy=$(find_xy "$1"); if [ -n "$xy" ]; then adb shell input tap $xy; echo "tap '$1' at $xy" >> $OUT/steps.txt; else echo "NOT FOUND: '$1'" >> $OUT/steps.txt; fi; sleep ${2:-2}; }
swipe_up() { adb shell input swipe 540 1900 540 700 400; sleep 1.5; }

adb shell am start -n $PKG/com.bernportal.ehr.MainActivity; sleep 1.2
shot 01_intro_a; sleep 1.0; shot 02_intro_b; sleep 1.2; shot 03_intro_c; sleep 1.5; shot 04_intro_d; sleep 3
shot 05_onboarding_welcome
tap "Next"; shot 06_onboarding_level
tap "IT student"; tap "Next"; shot 07_onboarding_goals
tap "Next"; shot 08_onboarding_pace
tap "Next"; tap "Your name" 1; adb shell input text "Bernard"; adb shell input keyevent 111; sleep 1; shot 09_onboarding_plan
tap "Start learning" 4; shot 10_home
swipe_up; shot 11_home_scroll; swipe_up; shot 12_home_scroll2; swipe_up; shot 13_home_scroll3
tap "Learn" 3; shot 14_learn
tap "Computer Fundamentals" 3; shot 15_module
swipe_up; tap "What Computers Do" 3; shot 16_lesson_read
tap "Animated" 4; shot 17_lesson_animated
tap "Simulation" 3; shot 18_lesson_sim
tap "Explain this again" 2; shot 19_explain_sheet
adb shell input keyevent 4; sleep 1; adb shell input keyevent 4; sleep 1; adb shell input keyevent 4; sleep 1
tap "Labs" 3; shot 20_labs
tap "Linux Navigation Basics" 3; shot 21_lab_brief
swipe_up; swipe_up; tap "I accept the lab rules" 3
tap "type a command" 1; adb shell input text "pwd"; adb shell input keyevent 66; sleep 2
tap "type a command" 1; adb shell input text "ls%s-la"; adb shell input keyevent 66; sleep 2; adb shell input keyevent 111; sleep 1; shot 22_lab_terminal
adb shell input keyevent 4; sleep 1; adb shell input keyevent 4; sleep 1
tap "AI Tutor" 3; shot 23_tutor
tap "Progress" 3; shot 24_progress
tap "Open menu" 2; shot 25_drawer
tap "Practice Centre" 3; shot 26_practice
tap "Animated OSI model" 2; tap "Send a web request" 4; shot 27_osi
adb shell input keyevent 4; sleep 1; tap "CVSS v3.1 calculator" 2; shot 28_cvss
adb shell input keyevent 4; sleep 1; adb shell input keyevent 4; sleep 1
tap "Open menu" 2; swipe_up; tap "Certificates" 3; shot 29_certificates
tap "Open menu" 2; swipe_up; tap "Career Path" 3; shot 30_career
tap "Open menu" 2; swipe_up; tap "Settings" 3; shot 31_settings
tap "Search everything" 2; adb shell input text "nmap"; sleep 2; adb shell input keyevent 111; sleep 1; shot 32_search
adb shell input keyevent 4; sleep 1
tap "Open menu" 2; tap "Silverline Capstone" 3; shot 33_capstone
adb shell input keyevent 3; sleep 1
adb logcat -d > $OUT/logcat_full.txt
grep -E "FATAL EXCEPTION|AndroidRuntime|Process: com.bernportal" -A25 $OUT/logcat_full.txt > $OUT/crashes.txt || true
grep -i "com.bernportal" $OUT/logcat_full.txt | grep -iE " E |exception" | head -200 > $OUT/app_errors.txt || true
tail -c 300000 $OUT/logcat_full.txt > $OUT/logcat_tail.txt; rm $OUT/logcat_full.txt
echo "done" >> $OUT/steps.txt
