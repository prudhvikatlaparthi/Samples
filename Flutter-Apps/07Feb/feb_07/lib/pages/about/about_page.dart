import 'package:feb_07/pages/home/home_controller.dart';
import 'package:feb_07/utils/getx_utils.dart';
import 'package:flutter/cupertino.dart';

import 'package:get/get.dart';

class AboutPage extends StatelessWidget {
  AboutPage({super.key});
  final controller = putNew(HomeController());

  @override
  Widget build(BuildContext context) {
    return CupertinoPageScaffold(
      navigationBar: const CupertinoNavigationBar(
        middle: Text("About"),
      ),
      child: SafeArea(
        child: Center(
          child: Column(
            children: [
              Obx(() => Text("count ${controller.getCount()}")),
              CupertinoButton(
                  onPressed: () {
                    controller.increment();
                  },
                  child: const Text("Add"))
            ],
          ),
        ),
      ),
    );
  }
}
