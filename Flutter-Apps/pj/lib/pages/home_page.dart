import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_storage/firebase_storage.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:image_picker/image_picker.dart';
import 'package:pj/load_image.dart';
import 'package:pj/utils/global.dart';
import 'package:flutter_image_compress/flutter_image_compress.dart';
import '../repository.dart';
import '../utils/preferences.dart';

class HomePage extends StatefulWidget {
  const HomePage({super.key, required this.title});

  final String title;

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  late final CollectionReference _collectionRef;
  late final ImagePicker _picker;
  late final Reference _storage;
  bool isAdmin = false;
  final _token = "prudhvi";

  @override
  void initState() {
    _collectionRef = FirebaseFirestore.instance.collection("images");
    _picker = ImagePicker();
    _storage = FirebaseStorage.instance.ref();
    WidgetsBinding.instance.addPostFrameCallback((_) async {
      isAdmin = await getIsAdminPref();
      setState(() {});
    });
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          backgroundColor: Theme.of(context).colorScheme.inversePrimary,
          title: Text(widget.title),
          centerTitle: true,
          leading: IconButton(
              onPressed: () {
                final tokenController =
                    TextEditingController(text: isAdmin ? _token : '');

                Get.dialog(Column(
                  crossAxisAlignment: CrossAxisAlignment.center,
                  children: [
                    Card(
                      child: SizedBox(
                        width: Get.width * 0.5,
                        height: Get.height * 0.5,
                        child: Column(children: [
                          Padding(
                            padding: const EdgeInsets.all(16.0),
                            child: TextField(
                              controller: tokenController,
                              decoration: const InputDecoration(
                                labelText: 'Enter token',
                              ),
                            ),
                          ),
                          const SizedBox(
                            height: 20,
                          ),
                          ElevatedButton(
                              onPressed: () async {
                                if (isAdmin) {
                                  await setIsAdminPref(false);
                                  setState(() {
                                    isAdmin = false;
                                  });
                                  Get.back();
                                } else {
                                  if (tokenController.text == _token) {
                                    await setIsAdminPref(true);
                                    setState(() {
                                      isAdmin = true;
                                    });
                                  } else {
                                    showToast("Invalid");
                                  }
                                  Get.back();
                                }
                              },
                              child: Text(isAdmin ? "Logout" : "proceed"))
                        ]),
                      ),
                    ),
                  ],
                ));
              },
              icon: Icon(
                Icons.account_circle,
                color: isAdmin ? Colors.purple : Colors.white,
              )),
          actions: [
            Padding(
              padding: const EdgeInsets.only(right: 8),
              child: ElevatedButton(
                onPressed: () async {
                  try {
                    XFile? photo = await getPhoto(_picker);
                    if (photo == null) return;
                    showProgress("Uploading");
                    final fileName =
                        "Img-${DateTime.now().millisecondsSinceEpoch}.${photo.name.split(".")[1]}";
                    Reference ref = _storage.child("images/$fileName");
                    final data = await FlutterImageCompress.compressWithList(
                      await photo.readAsBytes(),
                      quality: 80,
                    );
                    UploadTask uploadTask = ref.putData(
                        data, SettableMetadata(contentType: photo.mimeType));

                    uploadImage(fileName, uploadTask, _collectionRef);
                  } on Exception {
                    hideProgress();
                  }
                },
                child: const Text("Add"),
              ),
            ),
          ],
        ),
        body: Padding(
          padding: const EdgeInsets.all(10.0),
          child: StreamBuilder(
              stream:
                  _collectionRef.orderBy('path', descending: true).snapshots(),
              builder: ((context, snapshot) {
                if (snapshot.data == null) {
                  return const Center(child: CircularProgressIndicator());
                }
                return GridView.builder(
                  shrinkWrap: true,
                  itemCount: snapshot.data?.docs.length ?? 0,
                  gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                      mainAxisSpacing: 1,
                      childAspectRatio: (Get.width / 2) /
                          ((Get.height - kToolbarHeight - 24) / 2),
                      crossAxisSpacing: 1,
                      crossAxisCount: 2),
                  itemBuilder: (BuildContext context, int index) {
                    final imageUrl =
                        snapshot.data!.docs[index]['path'].toString();
                    final docName =
                        snapshot.data!.docs[index]['name'].toString();
                    return LoadImage(
                        imageUrl: imageUrl, isAdmin: isAdmin, docName: docName);
                  },
                );
              })),
        ));
  }
}
