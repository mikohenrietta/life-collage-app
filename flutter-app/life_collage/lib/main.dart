import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'view/home_page.dart';
import 'service/home_viewmodel.dart';
import 'package:google_fonts/google_fonts.dart';
import 'utils/colors.dart';

void main() {
  runApp(const MainApp());
}

class MainApp extends StatelessWidget {
  const MainApp({super.key});

  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider(
      create: (context) => HomeViewModel(),
      child: MaterialApp(
        debugShowCheckedModeBanner: false,
        title: 'Life Collage App',
        theme: ThemeData(
          floatingActionButtonTheme: FloatingActionButtonThemeData(
            backgroundColor: AppColors.white,
            foregroundColor: AppColors.dustyRose,
          ),
          colorScheme: ColorScheme.fromSeed(
              seedColor: AppColors.dustyRose,
              brightness: Brightness.light,
              surface: const Color.fromARGB(255, 235, 214, 219)),
          textTheme: GoogleFonts.quicksandTextTheme(
            Theme.of(context).textTheme,
          ),
        ),
        home: const CollageGridScreen(),
      ),
    );
  }
}
