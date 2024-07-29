import 'package:citizen_portal/ui/home/dm/f_a_q_view.dart';
import 'package:citizen_portal/ui/home/dm/header1.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../home_controller.dart';
import 'footer.dart';
import 'header2.dart';
import 'header3.dart';
import 'header4.dart';
import 'home_view.dart';
import 'menu_view.dart';

class HomeM extends StatelessWidget {
  HomeM({super.key});

  final controller = Get.find<HomeController>();

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Expanded(
          child: SingleChildScrollView(
            child: Padding(
              padding: const EdgeInsets.all(8.0),
              child: Column(
                children: [
                  Header1(),
                  const Header2(),
                  const Column(
                    crossAxisAlignment: CrossAxisAlignment.end,
                    children: [
                      Header3(),
                      SizedBox(
                        height: 8,
                      ),
                      Header4()
                    ],
                  ),
                  const SizedBox(
                    height: 10,
                  ),
                  MenuView(),
                  Obx(() {
                    if (controller.selectedMenu.value == Menu.home) {
                      return HomeView();
                    } else {
                      return FAQView();
                    }
                  })
                ],
              ),
            ),
          ),
        ),
        const Footer()
      ],
    );
  }
}
