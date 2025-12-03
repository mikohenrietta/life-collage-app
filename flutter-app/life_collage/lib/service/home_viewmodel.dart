import 'package:flutter/material.dart';
import '../model/collage_item.dart';

class HomeViewModel extends ChangeNotifier {
  final List<CollageItem> _collageItems = [
    CollageItem(
      id: 'item_001',
      title: 'First Day',
      description: 'Beautiful first day.',
      rating: 5,
      date: DateTime(2025, 1, 10),
      picturePath: 'assets/uni.png',
    ),
    CollageItem(
      id: 'item_002',
      title: 'Vacation Trip',
      description:
          'Going on a trip to Greece with my friends, amazing experience.',
      rating: 4,
      date: DateTime(2025, 5, 15),
      picturePath: 'assets/greece.png',
    ),
    CollageItem(
      id: 'item_003',
      title: 'New Pet',
      description: 'Got a new puppy!',
      rating: 5,
      date: DateTime(2025, 10, 28),
      picturePath: 'assets/puppy.png',
    ),
  ];

  List<CollageItem> get collageItems => _collageItems;

  void addItem(CollageItem newItem) {
    _collageItems.add(newItem);
    notifyListeners();
  }

  void deleteItem(String itemId) {
    _collageItems.removeWhere((item) => item.id == itemId);
    notifyListeners();
  }
}
