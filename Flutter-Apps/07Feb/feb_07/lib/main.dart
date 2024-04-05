import 'package:feb_07/pages/home/home_page.dart';
import 'package:flutter/cupertino.dart';
import 'package:get/get.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return GetCupertinoApp(
      title: 'Getx',
      theme: const CupertinoThemeData(brightness: Brightness.light),
      home: HomePage(),
    );
  }
}
