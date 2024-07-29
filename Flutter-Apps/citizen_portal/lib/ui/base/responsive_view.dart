import 'package:flutter/material.dart';

import '../../utils/screen_utils.dart';

class ResponsiveView extends StatelessWidget {
  ResponsiveView(
      {super.key, required this.mobileView, required this.desktopView});

  Widget mobileView;
  Widget desktopView;

  @override
  Widget build(BuildContext context) {
    return LayoutBuilder(builder: (ctx, conts) {
      if (conts.maxWidth >= kThresholdValue) {
        return desktopView;
      } else {
        return mobileView;
      }
    });
  }
}
