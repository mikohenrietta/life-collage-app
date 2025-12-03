import 'package:flutter/foundation.dart';

class CollageItem extends ChangeNotifier {
  String id;
  String title;
  String description;
  int rating;
  DateTime date;
  String picturePath;

  CollageItem({
    required this.id,
    required this.title,
    required this.rating,
    required this.description,
    required this.date,
    required this.picturePath,
  });

  void update({
    String? title,
    String? description,
    int? rating,
    DateTime? date,
    String? picturePath,
  }) {
    if (title != null) this.title = title;
    if (description != null) this.description = description;
    if (rating != null) this.rating = rating;
    if (date != null) this.date = date;
    if (picturePath != null) this.picturePath = picturePath;

    notifyListeners();
  }

  CollageItem copyWith({
    String? id,
    String? title,
    String? description,
    int? rating,
    DateTime? date,
    String? picturePath,
  }) {
    return CollageItem(
      id: id ?? this.id,
      title: title ?? this.title,
      description: description ?? this.description,
      rating: rating ?? this.rating,
      date: date ?? this.date,
      picturePath: picturePath ?? this.picturePath,
    );
  }
}
