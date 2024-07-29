import 'package:flutter/cupertino.dart';

class Header2 extends StatelessWidget {
  const Header2({
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    return const Padding(
      padding: EdgeInsets.symmetric(vertical: 15),
      child: Text(
        "Municipio de Maputo",
        style: TextStyle(
          fontSize: 16,
          fontWeight: FontWeight.bold,
        ),
      ),
    );
  }
}
