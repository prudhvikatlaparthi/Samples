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

class HomeD extends StatelessWidget {
  HomeD({super.key});

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
                  Row(
                    children: [
                      Header1(),
                      const SizedBox(
                        width: 10,
                      ),
                      const Header2(),
                      const SizedBox(
                        width: 10,
                      ),
                      const Header3(),
                      // const Header4()
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
