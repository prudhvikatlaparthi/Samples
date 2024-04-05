import 'package:feb_07/pages/about/about_page.dart';
import 'package:feb_07/pages/home/home_controller.dart';
import 'package:feb_07/widgets/counter_text.dart';
import 'package:flutter/cupertino.dart';
import 'package:get/get.dart';

import '../../utils/getx_utils.dart';

class HomePage extends StatelessWidget {
  HomePage({super.key});
  final homeController = putNew(HomeController());

  @override
  Widget build(BuildContext context) {
    return CupertinoPageScaffold(
      navigationBar: const CupertinoNavigationBar(
        middle: Text('Getx counter'),
      ),
      child: SafeArea(
        child: Center(
            child: Column(
          children: [
            CounterText(),
            CupertinoButton(
              onPressed: () {
                homeController.increment();
              },
              child: const Icon(CupertinoIcons.add),
            ),
            CupertinoButton(
              onPressed: () {
                Get.to(() => AboutPage());
              },
              child: const Icon(CupertinoIcons.right_chevron),
            ),
          ],
        )),
      ),
    );
  }
}
