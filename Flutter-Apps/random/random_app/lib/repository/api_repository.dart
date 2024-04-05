import 'dart:convert';

import 'package:random_app/model/todo.dart';
import 'package:random_app/remote/api_service.dart';

class ApiRepository {
  final ApiService _apiService;

  ApiRepository(this._apiService);

  Future<List<Todo>> getTodos() async {
    final data = await _apiService
        .makeApiCall("https://jsonplaceholder.typicode.com/todos");

    if (data == null) {
      return [];
    }

    final json = jsonDecode(data) as List;

    return json.map((e) => Todo.fromJson(e)).toList();
  }
}
