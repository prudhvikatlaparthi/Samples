import 'dart:math';

import 'package:flutter/material.dart';

class OddOrEven extends StatefulWidget {
  const OddOrEven({super.key});

  @override
  State<StatefulWidget> createState() {
    return _OddOrEvenState();
  }
}

class _OddOrEvenState extends State<OddOrEven> {
  bool accepted = false;
  Color dotColor = Colors.blue;
  GlobalKey<ScaffoldState> scaffoldKey = GlobalKey();

  int val = 0;
  int score = 0;

  @override
  Widget build(BuildContext context) {
    // assign a random number to value which will be used as the box value
    val = Random().nextInt(100);
    return Scaffold(
      key: scaffoldKey,
      appBar: AppBar(),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
          children: <Widget>[
            // just a score and mock player name indicator
            Padding(
              padding: const EdgeInsets.all(16.0),
              child: Center(
                child: Center(
                  child: Chip(
                    avatar: CircleAvatar(
                      backgroundColor: Colors.teal,
                      child: Text(
                        score.toString(),
                        style: const TextStyle(color: Colors.white),
                      ),
                    ),
                    label: const Text(
                      'Player Alpha',
                      style: TextStyle(
                          fontSize: 20.0,
                          color: Colors.black,
                          fontStyle: FontStyle.italic),
                    ),
                  ),
                ),
              ),
            ),

            // here comes our draggable.
            // it holds data which is our random number
            // the child of the draggable is a container reactangural in shape and
            //
            Draggable(
              data: val,

              // This will be displayed when the widget is being dragged
              feedback: Container(
                width: 100.0,
                height: 100.0,
                color: Colors.pink,
                child: Center(
                  child: Text(
                    val.toString(),
                    style: const TextStyle(color: Colors.white, fontSize: 22.0),
                  ),
                ),
              ),
              child: Container(
                width: 100.0,
                height: 100.0,
                color: Colors.pink,
                child: Center(
                  child: Text(
                    val.toString(),
                    style: const TextStyle(color: Colors.white, fontSize: 22.0),
                  ),
                ),
              ),
              // You can also specify 'childWhenDragging' option to draw
              // the original widget changes at the time of drag.
            ),

            // and here this row holds our two DragTargets.
            // One for odd numbers and the other for even numbers.
            //
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: <Widget>[
                Container(
                  width: 100.0,
                  height: 100.0,
                  color: Colors.green,

                  // Even holder DragTarget
                  //
                  child: DragTarget<int>(
                    builder: (context, candidateData, rejectedData) {
                      print(candidateData);
                      return const Center(
                          child: Text(
                        "Even",
                        style: TextStyle(color: Colors.white, fontSize: 22.0),
                      ));
                    },

                    // On will accept gets called just before it accepts the drag source.
                    // if needed, we can reject the data here. But we are not doing that as this is a GAME !!! :)
                    onWillAccept: (data) {
                      print("Will accpt");
                      return true; //return false to reject it
                    },

                    // On accepting the data by the DragTarget we simply check whether the data is odd or even and accept based on that and increment the counter and rebuild the widget tree for a new random number at the source.
                    onAccept: (data) {
                      print("On accpt");
                      if (data % 2 == 0) {
                        setState(() {
                          score++;
                        });
                        // How did you manage to score 3 points😮
                        // Congrats. You won the game.
                        if (score >= 3) {
                          showDialog(
                              context: context,
                              builder: (BuildContext context) {
                                return AlertDialog(
                                  title: const Text("Congrats!!"),
                                  content: const Text("No-brainer...😮"),
                                  actions: <Widget>[
                                    TextButton(
                                      child: const Text("Ok."),
                                      onPressed: () {
                                        Navigator.of(context).pop();
                                        setState(() {
                                          score = 0;
                                        });
                                      },
                                    )
                                  ],
                                );
                              });
                        }
                      } else {
                        setState(() {});
                      }
                    },
                  ),
                ),

                // And here is the Odd-holder
                Container(
                  width: 100.0,
                  height: 100.0,
                  color: Colors.deepPurple,
                  child: DragTarget<int>(
                    builder: (context, candidateData, rejectedData) {
                      return const Center(
                          child: Text(
                        "Odd",
                        style: TextStyle(color: Colors.white, fontSize: 22.0),
                      ));
                    },
                    onWillAccept: (data) {
                      return true;
                    },
                    onAccept: (data) {
                      if (data % 2 != 0) {
                        setState(() {
                          score++;
                        });

                        if (score >= 10) {
                          showDialog(
                              context: context,
                              builder: (BuildContext context) {
                                return AlertDialog(
                                  title: const Text("Congrats!!"),
                                  content: const Text("No-brainer...😮"),
                                  actions: <Widget>[
                                    TextButton(
                                      child: const Text("Thanks"),
                                      onPressed: () {
                                        Navigator.of(context).pop();
                                        setState(() {
                                          score = 0;
                                        });
                                      },
                                    )
                                  ],
                                );
                              });
                        }
                      } else {
                        setState(() {});
                      }
                    },
                  ),
                )
              ],
            )
          ],
        ),
      ),
    );
  }
}
