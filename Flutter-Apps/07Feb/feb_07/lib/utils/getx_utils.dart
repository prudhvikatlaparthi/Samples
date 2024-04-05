import 'package:get/get.dart';

T putNew<T>(T init) {
  Get.delete<T>();
  return Get.put<T>(init);
}
