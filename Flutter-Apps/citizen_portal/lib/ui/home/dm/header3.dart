import 'package:citizen_portal/utils/colors.dart';
import 'package:citizen_portal/utils/my_translations.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class Header3 extends StatelessWidget {
  const Header3({
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        PopupMenuButton(
            tooltip: "",
            child: Row(
              children: [
                Text(
                  StrRes.contact.tr,
                  style: const TextStyle(fontSize: 13),
                ),
                const Icon(Icons.expand_more)
              ],
            ),
            onSelected: (value) {},
            itemBuilder: (ctxt) => [
                  PopupMenuItem(
                    value: "1",
                    child: Text(
                      "${StrRes.email.tr}:\nabc@email.com",
                      style: const TextStyle(fontSize: 12),
                    ),
                  ),
                  PopupMenuItem(
                    value: "2",
                    child: Text(
                      "${StrRes.website.tr}:\nwww.cmmaputo.gov.mz/",
                      style: const TextStyle(fontSize: 12),
                    ),
                  )
                ]),
        Container(
          width: 2,
          height: 30,
          margin: const EdgeInsets.symmetric(horizontal: 12),
          color: orangeColor,
        ),
        PopupMenuButton(
            tooltip: "",
            onSelected: (value) {
              Get.updateLocale(value);
            },
            child: Row(
              children: [
                Text(
                  (Get.locale?.languageCode ?? "") == "pt"
                      ? "Português"
                      : "English",
                  style: const TextStyle(fontSize: 13),
                ),
                const Icon(Icons.language)
              ],
            ),
            itemBuilder: (ctxt) => [
                  const PopupMenuItem(
                    value: Locale("en"),
                    child: Text(
                      "English",
                      style: TextStyle(fontSize: 12),
                    ),
                  ),
                  const PopupMenuItem(
                    value: Locale("pt"),
                    child: Text(
                      "Português",
                      style: TextStyle(fontSize: 12),
                    ),
                  )
                ]),
        Container(
          width: 2,
          height: 30,
          margin: const EdgeInsets.symmetric(horizontal: 12),
          color: orangeColor,
        ),
        // Expanded(
        // child:
        InkWell(
          onTap: () {},
          child: Container(
            decoration: const BoxDecoration(
                color: orangeColor,
                borderRadius: BorderRadius.all(Radius.circular(8))),
            alignment: Alignment.center,
            padding: const EdgeInsets.all(8),
            child: Text(
              StrRes.login.tr,
              style: const TextStyle(color: Colors.white),
            ),
          ),
        ),
        // )
      ],
    );
  }
}
