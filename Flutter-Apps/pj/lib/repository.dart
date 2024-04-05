import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_storage/firebase_storage.dart';
import 'package:image_picker/image_picker.dart';
import 'utils/global.dart';

Future<XFile?> getPhoto(ImagePicker picker) async {
  final XFile? photo = await picker.pickImage(source: ImageSource.gallery);
  return photo;
}

void uploadImage(
    String fileName, UploadTask uploadTask, CollectionReference data) {
  uploadTask.then((res) async {
    final url = await res.ref.getDownloadURL();
    showToast("Uploaded");
    hideProgress();
    await writeUrlToFireStore(fileName, {'path': url, 'name': fileName}, data);
  }).catchError((onError) {
    hideProgress();
    showToast(onError);
  });
}

Future<void> writeUrlToFireStore(
    String docName, Object payload, CollectionReference data) async {
  showProgress("Saving");
  return data.doc(docName).set(payload).then((value) async {
    hideProgress();
    showToast("Saved");
  }).catchError((onError) async {
    hideProgress();
    final msg = "onError $onError";
    showToast(msg);
  });
}
