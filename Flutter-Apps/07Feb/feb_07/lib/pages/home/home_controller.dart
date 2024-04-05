import 'package:get/get.dart';

class HomeController extends GetxController {
  final Rx _count = 0.obs;

  void increment() {
    _count.value = _count.value + 1;
  }

  int getCount() => _count.value;
}
