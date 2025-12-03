import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../service/home_viewmodel.dart';
import '../widgets/collage_item_widget.dart';
import '../view/add_page.dart';

class CollageGridScreen extends StatelessWidget {
  const CollageGridScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text(
          'My Life Collage',
          style: TextStyle(fontWeight: FontWeight.bold),
        ),
        centerTitle: true,
      ),
      body: Consumer<HomeViewModel>(
        builder: (context, viewModel, child) {
          final items = viewModel.collageItems;

          if (items.isEmpty) {
            return const Center(child: Text('No collage items yet!'));
          }

          return GridView.builder(
            padding: const EdgeInsets.all(8.0),
            itemCount: items.length,
            gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
              crossAxisCount: 2, // 2 items per row
              crossAxisSpacing: 10,
              mainAxisSpacing: 10,
            ),
            itemBuilder: (context, index) {
              return ChangeNotifierProvider.value(
                value: items[index],
                child: const CollageItemWidget(),
              );
            },
          );
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          Navigator.of(context).push(
            MaterialPageRoute(
              builder: (context) {
                return const AddPage();
              },
            ),
          );
        },
        child: const Icon(Icons.add),
      ),
    );
  }
}
