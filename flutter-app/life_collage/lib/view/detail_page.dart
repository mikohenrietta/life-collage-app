import 'package:flutter/material.dart';
import 'package:life_collage/view/edit_page.dart';
import 'package:provider/provider.dart';
import 'dart:io';

import '../model/collage_item.dart';
import '../service/home_viewmodel.dart';

class DetailPage extends StatelessWidget {
  final CollageItem item;

  const DetailPage({super.key, required this.item});

  Widget _buildImage(String path) {
    if (path.startsWith('assets/')) {
      return Image.asset(
        path,
        fit: BoxFit.cover,
        height: 300,
        width: double.infinity,
      );
    } else if (path.isNotEmpty) {
      final file = File(path);
      if (file.existsSync()) {
        return Image.file(
          file,
          fit: BoxFit.cover,
          height: 300,
          width: double.infinity,
        );
      }
    }
    return Container(
      height: 300,
      color: Colors.grey.shade300,
      child: const Center(
        child: Icon(Icons.broken_image, size: 50, color: Colors.grey),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final homeViewModel = Provider.of<HomeViewModel>(context, listen: false);

    return Scaffold(
      appBar: AppBar(
        title: Text(item.title),
        centerTitle: true,
        foregroundColor: Colors.black,
        actions: [
          IconButton(
            icon: const Icon(Icons.delete),
            onPressed: () => _confirmDelete(context, homeViewModel),
          ),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            ClipRRect(
              borderRadius:
                  BorderRadius.circular(12.0), // Modern rounded corners
              child: _buildImage(item.picturePath),
            ),
            const SizedBox(height: 20),
            Text(
              'Rating: ${item.rating} / 5',
              style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 10),
            Text(
              'Created On: ${item.date.day}/${item.date.month}/${item.date.year}',
              style: const TextStyle(fontSize: 16, color: Colors.grey),
            ),
            const SizedBox(height: 20),
            Text(
              item.description,
              style: const TextStyle(fontSize: 18),
            ),
            const SizedBox(height: 10)
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          Navigator.of(context).push(MaterialPageRoute(builder: (context) {
            return EditPage(item: item);
          }));
        },
        child: const Icon(Icons.edit),
      ),
    );
  }

  Future<void> _confirmDelete(
      BuildContext context, HomeViewModel viewModel) async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          title: const Text('Confirm Deletion'),
          content: Text('Are you sure you want to delete "${item.title}"?'),
          actions: <Widget>[
            TextButton(
              onPressed: () => Navigator.of(context).pop(false),
              child: const Text('Cancel'),
            ),
            TextButton(
              onPressed: () => Navigator.of(context).pop(true),
              child: const Text('Delete', style: TextStyle(color: Colors.red)),
            ),
          ],
        );
      },
    );

    if (confirmed == true) {
      viewModel.deleteItem(item.id);
      Navigator.of(context).pop();
    }
  }
}
