import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:random_app/ui/test.dart';
import 'package:random_app/ui/todo_details/details.dart';

import 'todos_provider.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({
    super.key,
  });

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((timeStamp) {
      Provider.of<TodoProvider>(context, listen: false).getAllTodos();
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Provider API'),
      ),
      body: Consumer<TodoProvider>(
        builder: (context, value, child) {
          // If the loading it true then it will show the circular progressbar
          if (value.isLoading) {
            return const Center(
              child: CircularProgressIndicator(),
            );
          }
          // If loading is false then this code will show the list of todo item
          final todos = value.todos;
          if (todos.isEmpty) {
            return const Center(
              child: Text("No todos found."),
            );
          }
          return ListView.builder(
            itemCount: todos.length,
            itemBuilder: (context, index) {
              final todo = todos[index];
              return ListTile(
                leading: CircleAvatar(
                  child: Text(todo.id.toString()),
                ),
                title: Text(
                  todo.title ?? "",
                  style: TextStyle(
                    color:
                        (todo.completed ?? false) ? Colors.grey : Colors.black,
                  ),
                ),
                onTap: () => {
                  Navigator.push(
                      context,
                      MaterialPageRoute(
                          builder: (context) => const DetailScreen()))
                },
              );
            },
          );
        },
      ),
    );
  }
}
