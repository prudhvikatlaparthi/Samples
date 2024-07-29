import 'package:citizen_portal/utils/colors.dart';
import 'package:citizen_portal/utils/my_translations.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class Header4 extends StatelessWidget {
  const Header4({
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.end,
      children: [
        Text(
          StrRes.newUser.tr,
          style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 12),
        ),
        TextButton(
          onPressed: () {},
          child: Text(
            StrRes.register.tr,
            style: const TextStyle(
                color: blueColor, fontSize: 12, fontWeight: FontWeight.bold),
          ),
        ),
      ],
    );
  }
}
