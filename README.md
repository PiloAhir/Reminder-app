# મારો રિમાઇન્ડર — Offline Android App

## વિશેષતાઓ
- કોઈ એકાઉન્ટ કે ઇન્ટરનેટની જરૂર નથી.
- Android manifest માં `INTERNET` permission નથી.
- SQLite માં સ્થાનિક રિમાઇન્ડર સંગ્રહ.
- તારીખ/સમય આધારિત સૂચનાઓ.
- લાઇટ/ડાર્ક થીમ.
- JSON Export/Import બેકઅપ.
- GitHub Actions APK build.

## GitHub પર APK બનાવો
1. ZIP extract કરીને બધી ફાઇલો GitHub repository ના root માં upload કરો.
2. `.github/workflows/android-build.yml` પાથ જાળવો.
3. Actions → Build Maro Reminder APK → Run workflow.
4. સફળ build પછી `Maro-Reminder-debug-apk` artifact ડાઉનલોડ કરો.

## Permissions
`INTERNET` permission ઉમેરેલી નથી. Notifications અને reboot પછી reminders ફરી schedule કરવા માટે notification તથા boot permissions છે.

## નોંધ
Android ની battery optimization અને manufacturer restrictions ને કારણે ચોક્કસ સમયે સૂચના મોડું આવી શકે છે. એપને notifications મંજૂર કરો. આ આવૃત્તિ exact-alarm special access માગતી નથી; `setAndAllowWhileIdle` વાપરે છે.
