import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../base/responsive_view.dart';
import 'dm/home_d.dart';
import 'dm/home_m.dart';
import 'home_controller.dart';

class HomePage extends StatelessWidget {
  HomePage({super.key});
  final controller = Get.put(HomeController());
  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: Scaffold(
        body: ResponsiveView(mobileView: HomeM(), desktopView: HomeD()),
      ),
    );
  }
}
