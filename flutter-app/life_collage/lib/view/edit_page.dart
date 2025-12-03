// lib/views/edit_page.dart

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'dart:io';
import 'package:image_picker/image_picker.dart';
import 'package:path_provider/path_provider.dart';

import '../service/home_viewmodel.dart';
import '../model/collage_item.dart';
import '../utils/colors.dart';

class EditPage extends StatefulWidget {
  final CollageItem item;

  const EditPage({super.key, required this.item});

  @override
  State<EditPage> createState() => _EditPageState();
}

class _EditPageState extends State<EditPage> {
  final _formKey = GlobalKey<FormState>();

  late final TextEditingController _titleController;
  late final TextEditingController _descriptionController;
  late int _rating;
  late DateTime _selectedDate;
  String? _picturePath;

  final ImagePicker _picker = ImagePicker();

  @override
  void initState() {
    super.initState();
    _titleController = TextEditingController(text: widget.item.title);
    _descriptionController =
        TextEditingController(text: widget.item.description);
    _rating = widget.item.rating;
    _selectedDate = widget.item.date;
    _picturePath = widget.item.picturePath;
  }

  @override
  void dispose() {
    _titleController.dispose();
    _descriptionController.dispose();
    super.dispose();
  }

  Future<void> _pickImage() async {
    try {
      final XFile? image = await _picker.pickImage(source: ImageSource.gallery);

      if (image != null) {
        final appDir = await getApplicationDocumentsDirectory();

        final String fileName =
            '${DateTime.now().millisecondsSinceEpoch}_${image.name}';
        final newFilePath = '${appDir.path}/$fileName';

        final File newImage = await File(image.path).copy(newFilePath);

        setState(() {
          _picturePath = newImage.path;
        });
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Failed to pick image')),
        );
      }
    }
  }

  Future<void> _selectDate(BuildContext context) async {
    final DateTime? picked = await showDatePicker(
      context: context,
      initialDate: _selectedDate,
      firstDate: DateTime(2000),
      lastDate: DateTime.now(),
    );
    if (picked != null && picked != _selectedDate) {
      setState(() {
        _selectedDate = picked;
      });
    }
  }

  void _submitForm(HomeViewModel viewModel) {
    if (_formKey.currentState!.validate()) {
      if (_picturePath == null) {
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(
                content: Text('Please select a picture for your memory!')),
          );
        }
        return;
      }

      widget.item.update(
        title: _titleController.text,
        description: _descriptionController.text,
        rating: _rating,
        date: _selectedDate,
        picturePath: _picturePath!,
      );
      Navigator.of(context).pop();
      Navigator.of(context).pop(true);
    }
  }

  @override
  Widget build(BuildContext context) {
    final homeViewModel = Provider.of<HomeViewModel>(context, listen: false);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Edit Memory'),
        centerTitle: true,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Form(
          key: _formKey,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              _buildTitleField(),
              const SizedBox(height: 16),
              _buildDateField(context),
              const SizedBox(height: 16),
              _buildRatingSelector(),
              const SizedBox(height: 16),
              _buildDescriptionField(),
              const SizedBox(height: 24),
              _buildPicturePicker(),
              const SizedBox(height: 32),
              ElevatedButton(
                onPressed: () => _submitForm(homeViewModel),
                style: ElevatedButton.styleFrom(
                  backgroundColor: AppColors.dustyRose,
                  padding: const EdgeInsets.symmetric(vertical: 16),
                ),
                child: const Text(
                  'Save Changes',
                  style: TextStyle(fontSize: 18, color: AppColors.white),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildTitleField() {
    return TextFormField(
      controller: _titleController,
      decoration: const InputDecoration(
        labelText: 'Title',
        border: OutlineInputBorder(),
      ),
      validator: (value) {
        if (value == null || value.isEmpty) {
          return 'Please enter a title.';
        }
        return null;
      },
    );
  }

  Widget _buildDescriptionField() {
    return TextFormField(
      controller: _descriptionController,
      decoration: const InputDecoration(
        labelText: 'Description',
        border: OutlineInputBorder(),
        alignLabelWithHint: true,
      ),
      maxLines: 4,
    );
  }

  Widget _buildDateField(BuildContext context) {
    return Row(
      children: [
        const Icon(Icons.calendar_today, color: AppColors.dustyRose),
        const SizedBox(width: 16),
        Expanded(
          child: Text(
            'Date: ${_selectedDate.day}/${_selectedDate.month}/${_selectedDate.year}',
            style: const TextStyle(fontSize: 16),
          ),
        ),
        TextButton(
          onPressed: () => _selectDate(context),
          child: const Text('Change Date',
              style: TextStyle(color: AppColors.dustyRose)),
        ),
      ],
    );
  }

  Widget _buildRatingSelector() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text('Rating:',
            style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
        const SizedBox(height: 8),
        Row(
          mainAxisAlignment: MainAxisAlignment.start,
          children: List.generate(5, (index) {
            return IconButton(
              icon: Icon(
                index < _rating ? Icons.star : Icons.star_border,
                color: AppColors.mutedPeach,
                size: 32,
              ),
              onPressed: () {
                setState(() {
                  _rating = index + 1;
                });
              },
            );
          }),
        ),
      ],
    );
  }

  Widget _buildPicturePicker() {
    return Container(
      padding: const EdgeInsets.all(16.0),
      decoration: BoxDecoration(
        border: Border.all(color: Colors.grey.shade300),
        borderRadius: BorderRadius.circular(8.0),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          Text(
            _picturePath == null
                ? 'No Picture Selected'
                : 'Picture Selected: ${_picturePath!.split(Platform.pathSeparator).last}',
            style: const TextStyle(color: Colors.grey),
          ),
          const SizedBox(height: 8),
          ElevatedButton.icon(
            onPressed: _pickImage,
            icon: const Icon(Icons.photo_library),
            label: const Text('Change Photo'),
            style: ElevatedButton.styleFrom(
              backgroundColor: AppColors.softBeige,
              foregroundColor: AppColors.warmBrown,
            ),
          ),
          if (_picturePath != null)
            Padding(
              padding: const EdgeInsets.only(top: 16.0),
              child: _buildPicturePreview(),
            ),
        ],
      ),
    );
  }

  Widget _buildPicturePreview() {
    if (_picturePath == null) return const SizedBox.shrink();

    final isAsset = !_picturePath!.startsWith('/');
    // File paths on iOS/Android always start with /, assets don’t.

    return ClipRRect(
      borderRadius: BorderRadius.circular(8.0),
      child: isAsset
          ? Image.asset(
              _picturePath!,
              height: 150,
              fit: BoxFit.cover,
            )
          : Image.file(
              File(_picturePath!),
              height: 150,
              fit: BoxFit.cover,
            ),
    );
  }
}
