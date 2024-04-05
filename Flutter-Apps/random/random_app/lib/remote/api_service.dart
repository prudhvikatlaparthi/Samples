import 'package:http/http.dart' as http;

class ApiService {
  Future<String?> makeApiCall(String url) async {
    try {
      final uri = Uri.parse(url);
      final response = await http.get(uri);

      return response.body;
    } catch (e) {
      return null;
    }
  }
}
