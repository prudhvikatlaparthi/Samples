import 'package:citizen_portal/ui/home/home_controller.dart';
import 'package:citizen_portal/utils/colors.dart';
import 'package:citizen_portal/utils/my_translations.dart';
import 'package:citizen_portal/utils/screen_utils.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:youtube_player_iframe/youtube_player_iframe.dart';

class FAQView extends StatelessWidget {
  FAQView({super.key});
  final controller = Get.find<HomeController>();

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 15, vertical: 10),
      child: Container(
        width: double.infinity,
        decoration: BoxDecoration(
            border: Border.all(color: grayColor),
            borderRadius: BorderRadius.circular(6)),
        child: Padding(
          padding: const EdgeInsets.only(top: 10.0),
          child: Column(
            children: [
              Text(
                StrRes.faqHelpMsg.tr,
                style: const TextStyle(
                    color: orangeColor,
                    fontSize: 14,
                    fontWeight: FontWeight.bold,
                    letterSpacing: 1),
              ),
              Container(
                margin: const EdgeInsets.only(top: 10, bottom: 10),
                width: wp(48),
                child: TextField(
                  decoration: InputDecoration(
                    isDense: true,
                    contentPadding: const EdgeInsets.only(
                        left: 8, right: 0, top: 0, bottom: 0),
                    border: const OutlineInputBorder(),
                    hintText: StrRes.search.tr,
                    suffixIcon: const Icon(Icons.search),
                    suffixIconColor: orangeColor,
                    hintStyle: const TextStyle(fontSize: 11),
                    labelStyle: const TextStyle(fontSize: 11),
                  ),
                ),
              ),
              Padding(
                padding: const EdgeInsets.all(10),
                child: Container(
                  width: double.infinity,
                  decoration: BoxDecoration(
                      border: Border.all(color: grayColor),
                      borderRadius: BorderRadius.circular(6)),
                  child: Padding(
                    padding: const EdgeInsets.all(10),
                    child: Column(
                      children: [
                        Text(StrRes.videos.tr),
                        const SizedBox(
                          height: 10,
                        ),
                        Column(
                          children: controller.videoUrls
                              .map((e) => Container(
                                    margin: const EdgeInsets.only(
                                      top: 10,
                                    ),
                                    width: wp(60),
                                    height: wp(40),
                                    child: YoutubePlayer(
                                        controller:
                                            YoutubePlayerController.fromVideoId(
                                                videoId: e)),
                                  ))
                              .toList(),
                        )
                      ],
                    ),
                  ),
                ),
              )
            ],
          ),
        ),
      ),
    );
  }
}
