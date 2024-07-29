import 'package:citizen_portal/utils/colors.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:google_fonts/google_fonts.dart';

import 'ui/home/home_page.dart';
import 'utils/my_translations.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return GetMaterialApp(
      defaultTransition: Transition.native,
      translations: StrRes(),
      locale: const Locale('pt'),
      debugShowCheckedModeBanner: false,
      theme: _buildTheme(),
      home: HomePage(),
    );
  }
}

_buildTheme() {
  final baseTheme = ThemeData(
      colorScheme: ColorScheme.fromSeed(seedColor: blueColor),
      tooltipTheme: const TooltipThemeData(
        decoration: BoxDecoration(
          color: Colors.transparent,
        ),
      ),
      useMaterial3: true);
  return baseTheme.copyWith(
    textTheme: GoogleFonts.interTextTheme(baseTheme.textTheme
        .apply(bodyColor: Colors.black, displayColor: Colors.black)),
  );
}
