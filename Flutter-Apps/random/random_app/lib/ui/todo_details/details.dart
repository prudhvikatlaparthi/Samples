import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:random_app/ui/todos/todos_provider.dart';

class DetailScreen extends StatelessWidget {
  const DetailScreen({super.key});

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
                onTap: () => {value.clear()},
              );
            },
          );
        },
      ),
    );
  }
}
