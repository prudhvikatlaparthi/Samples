import 'package:firebase_core/firebase_core.dart';
import 'package:flutter/material.dart';

import 'my_app.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp(
      options: const FirebaseOptions(
          apiKey: "AIzaSyDWz0uX8gyHOsJI3KeWv3GI5xIPsqYIig0",
          appId: "1:565686084418:web:281bd45a57d2cfb2589edb",
          messagingSenderId: "565686084418",
          projectId: "wepj-7b184"));
  runApp(const MyApp());
}
