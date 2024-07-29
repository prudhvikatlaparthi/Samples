import 'package:citizen_portal/ui/home/home_controller.dart';
import 'package:citizen_portal/utils/colors.dart';
import 'package:citizen_portal/utils/my_translations.dart';
import 'package:citizen_portal/utils/screen_utils.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class HomeView extends StatelessWidget {
  HomeView({
    super.key,
  });

  final HomeController controller = Get.find<HomeController>();

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 15, vertical: 10),
          child: Container(
            padding: const EdgeInsets.all(10),
            width: double.infinity,
            decoration: BoxDecoration(
                color: grayColor, borderRadius: BorderRadius.circular(6)),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  StrRes.qlinks.tr,
                  style: const TextStyle(
                      color: Colors.white,
                      fontWeight: FontWeight.bold,
                      fontSize: 12),
                ),
                Container(
                    padding: const EdgeInsets.all(10),
                    margin: const EdgeInsets.only(top: 8),
                    width: double.infinity,
                    height: hp(50),
                    decoration: BoxDecoration(
                        color: Colors.white,
                        borderRadius: BorderRadius.circular(6)),
                    child: ListView.builder(
                        itemCount: controller.quickMenuItems.length,
                        itemBuilder: (ctx, index) {
                          final item = controller.quickMenuItems[index];
                          return Column(
                            children: [
                              Row(
                                children: [
                                  InkWell(
                                    onTap: () {},
                                    child: const Icon(
                                      Icons.file_open,
                                      size: 16,
                                      color: blueColor,
                                    ),
                                  ),
                                  const SizedBox(
                                    width: 10,
                                  ),
                                  InkWell(
                                      onTap: () {}, child: Text(item.menu.tr)),
                                ],
                              ),
                              Container(
                                width: double.infinity,
                                height: 1,
                                color: grayColor,
                                margin:
                                    const EdgeInsets.only(bottom: 10, top: 5),
                              )
                            ],
                          );
                        }))
              ],
            ),
          ),
        ),
        Container(
          margin: const EdgeInsets.symmetric(horizontal: 15, vertical: 10),
          width: double.infinity,
          decoration: BoxDecoration(
              border: Border.all(color: greenColor),
              borderRadius: BorderRadius.circular(6)),
          child: Padding(
              padding: const EdgeInsets.all(10),
              child: Text.rich(
                TextSpan(
                  text: controller.contentHeader,
                  style: const TextStyle(
                      fontSize: 12,
                      color: greenColor,
                      fontWeight: FontWeight.bold,
                      fontStyle: FontStyle.italic),
                  children: <TextSpan>[
                    TextSpan(
                        text: controller.content,
                        style: const TextStyle(
                            fontSize: 11,
                            color: greenColor,
                            fontWeight: FontWeight.w100,
                            fontStyle: FontStyle.italic)),
                  ],
                ),
              )),
        ),
        Container(
          alignment: Alignment.centerLeft,
          margin: const EdgeInsets.symmetric(horizontal: 15, vertical: 10),
          child: Text(
            StrRes.lupdates.tr,
            style: const TextStyle(
                color: orangeColor,
                fontSize: 12,
                decoration: TextDecoration.underline,
                decorationColor: orangeColor),
          ),
        )
      ],
    );
  }
}
