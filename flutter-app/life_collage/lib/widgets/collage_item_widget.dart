import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'dart:io';

import '../model/collage_item.dart';
import '../view/detail_page.dart';
import '../utils/colors.dart';

class CollageItemWidget extends StatelessWidget {
  const CollageItemWidget({super.key});

  Widget _buildImage(String path) {
    if (path.startsWith('assets/')) {
      return Image.asset(
        path,
        fit: BoxFit.cover,
        errorBuilder: (context, error, stackTrace) {
          return const Center(child: Icon(Icons.broken_image, size: 50));
        },
      );
    } else if (path.isNotEmpty) {
      final file = File(path);
      if (file.existsSync()) {
        return Image.file(
          file, // Uses the dart:io File class
          fit: BoxFit.cover,
        );
      }
    }
    return Container(
      color: AppColors.softBeige,
      child: const Center(
        child: Icon(Icons.broken_image, size: 50, color: Colors.grey),
      ),
    );
  }

  Widget _buildRatingIndicator(int rating) {
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: List.generate(5, (index) {
        return Icon(
          index < rating ? Icons.star : Icons.star_border,
          color: AppColors.mutedPeach,
          size: 14.0,
        );
      }),
    );
  }

  @override
  Widget build(BuildContext context) {
    final item = Provider.of<CollageItem>(context);
    return InkWell(
      onTap: () {
        Navigator.of(context).push(
          MaterialPageRoute(
            builder: (context) {
              return DetailPage(item: item);
            },
          ),
        );
      },
      child: Card(
        elevation: 2,
        shape:
            RoundedRectangleBorder(borderRadius: BorderRadius.circular(12.0)),
        clipBehavior: Clip.antiAlias,
        child: Stack(
          fit: StackFit.expand,
          children: [
            _buildImage(item.picturePath),
            Align(
              alignment: Alignment.bottomLeft,
              child: Container(
                padding:
                    const EdgeInsets.symmetric(horizontal: 8.0, vertical: 4.0),
                decoration: const BoxDecoration(
                  gradient: LinearGradient(
                    colors: [Colors.transparent, Colors.black87],
                    begin: Alignment.topCenter,
                    end: Alignment.bottomCenter,
                    stops: [0.3, 1.0],
                  ),
                ),
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      item.title,
                      style: const TextStyle(
                        color: AppColors.white,
                        fontWeight: FontWeight.bold,
                        fontSize: 16,
                      ),
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                    ),
                    _buildRatingIndicator(item.rating),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
