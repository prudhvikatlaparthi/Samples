import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:pj/pages/home_page.dart';

import 'my_scroll_behaviour.dart';

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return GetMaterialApp(
        scrollBehavior: MyScrollBehavior(),
        debugShowCheckedModeBanner: false,
        title: 'pjl2',
        theme: ThemeData(
          colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
          useMaterial3: true,
        ),
        home: const HomePage(title: "Memories"));
  }
}
