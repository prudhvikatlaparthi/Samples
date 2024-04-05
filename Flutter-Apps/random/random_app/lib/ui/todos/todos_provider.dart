import 'package:flutter/material.dart';
import 'package:random_app/repository/api_repository.dart';

import '../../model/todo.dart';

class TodoProvider extends ChangeNotifier {
  final ApiRepository _apiRepository;

  TodoProvider(this._apiRepository);

  bool isLoading = false;
  List<Todo> _todos = [];
  List<Todo> get todos => _todos;

  Future<void> getAllTodos() async {
    isLoading = true;
    notifyListeners();

    final response = await _apiRepository.getTodos();

    _todos = response;
    isLoading = false;
    notifyListeners();
  }

  void clear() {
    _todos.clear();
    notifyListeners();
  }
}
