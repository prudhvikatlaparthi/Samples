import 'package:shared_preferences/shared_preferences.dart';

setIsAdminPref(bool isAdmin) async {
  final prefs = await _getPref();
  prefs.setBool("IsAdmin", isAdmin);
}

Future<bool> getIsAdminPref() async {
  return (await _getPref()).getBool("IsAdmin") ?? false;
}

clearPref() async {
  final prefs = await _getPref();
  prefs.clear();
}

Future<SharedPreferences> _getPref() async {
  return SharedPreferences.getInstance();
}
