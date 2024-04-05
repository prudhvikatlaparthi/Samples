import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:random_app/remote/api_service.dart';
import 'package:random_app/repository/api_repository.dart';
import 'package:random_app/ui/todos/home.dart';
import 'package:random_app/ui/todos/todos_provider.dart';

void main() {
  runApp(const MainApp());
}

class MainApp extends StatelessWidget {
  const MainApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      providers: [
        ChangeNotifierProvider(
            create: (context) => TodoProvider(ApiRepository(ApiService())))
      ],
      child: const MaterialApp(
        debugShowCheckedModeBanner: false,
        home: HomeScreen(),
      ),
    );
  }
}
