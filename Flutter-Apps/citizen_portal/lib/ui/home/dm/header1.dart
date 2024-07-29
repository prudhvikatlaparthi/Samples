import 'package:citizen_portal/ui/home/home_controller.dart';
import 'package:citizen_portal/utils/colors.dart';
import 'package:citizen_portal/utils/screen_utils.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class Header1 extends StatelessWidget {
  Header1({
    super.key,
  });

  final HomeController controller = Get.find<HomeController>();

  @override
  Widget build(BuildContext context) {
    final imgWidth = isDesktop() ? 11.0 : 22.0;
    final imgHeight = isDesktop() ? 8.0 : 10.0;
    final separatorHeight = isDesktop() ? 8.0 : 10.0;
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        Material(
          borderRadius: BorderRadius.circular(8),
          elevation: 8,
          child: Container(
            padding: const EdgeInsets.all(3),
            width: wp(imgWidth),
            height: hp(imgHeight),
            decoration: BoxDecoration(
                border: Border.all(color: const Color(0xFF999999)),
                borderRadius: BorderRadius.circular(8)),
            child: Image.asset(
              controller.img1,
              fit: BoxFit.contain,
            ),
          ),
        ),
        Container(
          width: 2,
          height: hp(separatorHeight),
          margin: const EdgeInsets.symmetric(horizontal: 10),
          decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(5),
            color: orangeColor,
          ),
        ),
        Material(
          borderRadius: BorderRadius.circular(8),
          elevation: 8,
          child: Container(
            padding: const EdgeInsets.all(3),
            width: wp(imgWidth),
            height: hp(imgHeight),
            decoration: BoxDecoration(
                border: Border.all(color: const Color(0xFF999999)),
                borderRadius: BorderRadius.circular(8)),
            child: Image.asset(
              controller.img2,
              fit: BoxFit.contain,
            ),
          ),
        ),
      ],
    );
  }
}
