import 'package:get/get.dart';

const kThresholdValue = 600;

bool isDesktop() {
  return Get.width >= 600;
}

double wp(double percent) {
  return (Get.width * percent) / 100;
}

double hp(double percent) {
  return (Get.width * percent) / 100;
}
