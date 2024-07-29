import 'package:citizen_portal/utils/colors.dart';
import 'package:citizen_portal/utils/my_translations.dart';
import 'package:citizen_portal/utils/screen_utils.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class Footer extends StatelessWidget {
  const Footer({
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 20),
      width: double.infinity,
      decoration: BoxDecoration(
          color: grayColor, borderRadius: BorderRadius.circular(6)),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.end,
        children: [
          Padding(
            padding: const EdgeInsets.only(top: 4.0),
            child: Text(
              StrRes.visits.trArgs(["1000"]),
              style: TextStyle(color: Colors.white, fontSize: hp(2)),
            ),
          ),
          Padding(
            padding: const EdgeInsets.only(bottom: 4.0),
            child: Text(
              StrRes.copyRight.tr,
              style: TextStyle(color: Colors.white, fontSize: hp(2)),
            ),
          ),
        ],
      ),
    );
  }
}
