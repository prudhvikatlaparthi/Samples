import 'package:flutter/material.dart';
import 'package:quad4/section.dart';

import 'common/bold_text.dart';

class QuadrantScreen extends StatefulWidget {
  const QuadrantScreen({super.key});

  @override
  State<QuadrantScreen> createState() => _QuadrantScreenState();
}

class _QuadrantScreenState extends State<QuadrantScreen> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const BoldText(text: "Time Management Matrix"),
        centerTitle: true,
      ),
      body: const Row(
        children: [
          Column(
            children: [
              Expanded(
                  child: Center(
                      child: RotatedBox(
                          quarterTurns: 3, child: Text("Important")))),
              Expanded(
                  child: Center(
                      child: RotatedBox(
                          quarterTurns: 3, child: Text("Not Important"))))
            ],
          ),
          Expanded(
            child: Column(
              children: [
                Expanded(
                  child: Row(
                    children: [
                      Expanded(
                        child: Column(
                          children: [
                            Text("Urgent"),
                            Expanded(child: Section()),
                          ],
                        ),
                      ),
                      Expanded(
                        child: Column(
                          children: [
                            Text("Not Urgent"),
                            Expanded(child: Section()),
                          ],
                        ),
                      )
                    ],
                  ),
                ),
                Expanded(
                  child: Row(
                    children: [
                      Expanded(
                          child: Column(
                        children: [
                          Text("a"),
                          Expanded(child: Section()),
                        ],
                      )),
                      Expanded(
                          child: Column(
                        children: [
                          Text("a"),
                          Expanded(child: Section()),
                        ],
                      ))
                    ],
                  ),
                )
              ],
            ),
          ),
        ],
      ),
    );
  }
}
