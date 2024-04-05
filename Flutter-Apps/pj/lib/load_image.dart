import 'package:cached_network_image/cached_network_image.dart';
import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_storage/firebase_storage.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:photo_view/photo_view.dart';
import 'package:pj/utils/global.dart';

class LoadImage extends StatefulWidget {
  final String imageUrl;
  final bool isAdmin;
  final String docName;

  const LoadImage(
      {super.key,
      required this.imageUrl,
      required this.isAdmin,
      required this.docName});

  @override
  State<LoadImage> createState() => _LoadImageState();
}

class _LoadImageState extends State<LoadImage> {
  Future<Widget> _getImage() async {
    return InkWell(
      onTap: () {
        Get.dialog(
            Stack(
              children: [
                Padding(
                  padding: const EdgeInsets.all(30.0),
                  child: InteractiveViewer(
                    panEnabled: false,
                    boundaryMargin: const EdgeInsets.all(100),
                    minScale: 0.5,
                    maxScale: 2,
                    child: PhotoView(
                      backgroundDecoration:
                          const BoxDecoration(color: Colors.transparent),
                      imageProvider: CachedNetworkImageProvider(
                        widget.imageUrl,
                      ),
                    ),
                  ),
                ),
                Positioned(
                  bottom: 30.0,
                  left: 30.0,
                  child: FloatingActionButton(
                      mini: true,
                      onPressed: () {
                        Get.back();
                      },
                      child: const Icon(Icons.arrow_back_ios_new)),
                ),
                if (widget.isAdmin)
                  Positioned(
                    bottom: 30,
                    right: 30,
                    child: FloatingActionButton(
                        mini: true,
                        onPressed: () async {
                          try {
                            showProgress("Deleting");
                            await FirebaseStorage.instance
                                .ref()
                                .child("images/${widget.docName}")
                                .delete();

                            await FirebaseFirestore.instance
                                .collection("images")
                                .doc(widget.docName)
                                .delete()
                                .catchError((e) {
                              hideProgress();
                              Get.back();
                            }).then((value) {
                              hideProgress();
                              Get.back();
                            });
                          } on Error {
                            hideProgress();
                            Get.back();
                          } on Exception {
                            hideProgress();
                            Get.back();
                          }
                        },
                        child: const Icon(Icons.delete)),
                  )
                else
                  Container(),
              ],
            ),
            barrierDismissible: true);
      },
      child: Card(
        elevation: 8,
        clipBehavior: Clip.antiAlias,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(10.0),
        ),
        child: CachedNetworkImage(
          width: double.infinity,
          height: double.infinity,
          imageUrl: widget.imageUrl,
          fit: BoxFit.cover,
          key: Key(widget.imageUrl),
          placeholder: (context, url) =>
              const Center(child: CircularProgressIndicator()),
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return FutureBuilder(
      future: _getImage(),
      builder: (context, snapshot) {
        return (snapshot.connectionState == ConnectionState.done)
            ? Center(child: snapshot.data)
            : const Center(child: CircularProgressIndicator());
      },
    );
  }
}
