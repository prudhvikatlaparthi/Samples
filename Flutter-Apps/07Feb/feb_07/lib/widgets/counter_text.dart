import 'package:feb_07/pages/home/home_controller.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class CounterText extends StatelessWidget {
  CounterText({
    super.key,
  });

  final HomeController homeController = Get.find<HomeController>();

  @override
  Widget build(BuildContext context) {
    return Obx(() => Text('Count ${homeController.getCount()}'));
  }
}
