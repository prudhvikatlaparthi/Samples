import 'package:citizen_portal/ui/home/home_controller.dart';
import 'package:citizen_portal/utils/colors.dart';
import 'package:citizen_portal/utils/my_translations.dart';
import 'package:citizen_portal/utils/screen_utils.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class MenuView extends StatelessWidget {
  MenuView({
    super.key,
  });

  final HomeController controller = Get.find<HomeController>();

  @override
  Widget build(BuildContext context) {
    return Obx(
      () => AnimatedSize(
        duration: const Duration(milliseconds: 500),
        curve: Curves.fastOutSlowIn,
        child: Container(
          width: double.infinity,
          height: hp(controller.openMenu.value ? 25 : 12),
          padding: const EdgeInsets.all(6),
          decoration: BoxDecoration(
              color: grayColor, borderRadius: BorderRadius.circular(5)),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              InkWell(
                onTap: () {
                  controller.openMenu.toggle();
                },
                child: Material(
                  color: grayColor,
                  borderRadius: BorderRadius.circular(4),
                  elevation: 5,
                  child: const Padding(
                    padding: EdgeInsets.all(2),
                    child: Icon(
                      Icons.menu,
                      size: 32,
                      color: grayColor2,
                    ),
                  ),
                ),
              ),
              Visibility(
                maintainAnimation: true,
                maintainState: true,
                visible: controller.openMenu.value,
                child: AnimatedOpacity(
                  duration: const Duration(milliseconds: 500),
                  curve: Curves.fastOutSlowIn,
                  opacity: controller.openMenu.value ? 1 : 0,
                  child: Padding(
                    padding: const EdgeInsets.only(left: 20.0),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        const SizedBox(
                          height: 10,
                        ),
                        GestureDetector(
                          onTap: () {
                            controller.selectedMenu.value = Menu.home;
                          },
                          child: Obx(
                            () => Text(
                              StrRes.home.tr,
                              style: TextStyle(
                                  color:
                                      controller.selectedMenu.value == Menu.home
                                          ? Colors.white
                                          : Colors.black,
                                  fontSize: 12),
                            ),
                          ),
                        ),
                        const SizedBox(
                          height: 10,
                        ),
                        GestureDetector(
                          onTap: () {
                            controller.selectedMenu.value = Menu.faq;
                          },
                          child: Text(
                            StrRes.faq.tr,
                            style: TextStyle(
                                color: controller.selectedMenu.value == Menu.faq
                                    ? Colors.white
                                    : Colors.black,
                                fontSize: 12),
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
