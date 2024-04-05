import 'package:flutter/material.dart';

class Section extends StatelessWidget {
  const Section({super.key});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(4),
      child: Container(
        decoration: const BoxDecoration(
            color: Color(0xFFD9783B),
            borderRadius: BorderRadius.all(Radius.circular(10))),
        child: Stack(
          children: [
            Column(
              children: [
                Draggable(
                  data: 1,
                  feedback: Container(
                    width: 100.0,
                    height: 100.0,
                    color: Colors.pink,
                    child: const Center(
                      child: Text(
                        "sdfsdf",
                        style: TextStyle(color: Colors.white, fontSize: 22.0),
                      ),
                    ),
                  ),
                  child: Container(
                    width: 100.0,
                    height: 100.0,
                    color: Colors.pink,
                    child: const Center(
                      child: Text(
                        "sdsd",
                        style: TextStyle(color: Colors.white, fontSize: 22.0),
                      ),
                    ),
                  ),
                ),
                DragTarget<int>(
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
                  },
                ),
              ],
            ),
            Positioned(
              bottom: 0,
              right: 0,
              child: Padding(
                padding: const EdgeInsets.all(8.0),
                child: FloatingActionButton(
                  onPressed: () {},
                  child: const Icon(Icons.add),
                ),
              ),
            )
          ],
        ),
      ),
    );
  }
}
