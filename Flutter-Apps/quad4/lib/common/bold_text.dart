import 'package:flutter/material.dart';

class BoldText extends StatelessWidget {
  final String text;

  const BoldText({super.key, required this.text});

  @override
  Widget build(BuildContext context) {
    return Text(
      text,
      style: const TextStyle(
          fontSize: 25, fontWeight: FontWeight.bold, color: Color(0xFF162950)),
    );
  }
}
