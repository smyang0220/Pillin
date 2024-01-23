import 'package:flutter/material.dart';

class MainAppBarSearch extends StatefulWidget implements PreferredSizeWidget {
  MainAppBarSearch({Key? key, this.barColor}) : super(key: key);

  var barColor; // 상단바 색상

  @override
  _MainAppBarSearchState createState() => _MainAppBarSearchState();

  @override
  Size get preferredSize => Size.fromHeight(45);
}

class _MainAppBarSearchState extends State<MainAppBarSearch> {
  @override
  Widget build(BuildContext context) {
    return AppBar(
      toolbarHeight: 45,
      backgroundColor: widget.barColor,
      title: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(
            "YourPilling",
            style: TextStyle(
              fontSize: 18,
            ),
          ),
          Row(
            children: [
              IconButton(
                onPressed: () {},
                icon: Icon(Icons.search),
              ),
              IconButton(
                onPressed: () {},
                icon: Icon(Icons.notifications_none),
              ),
            ],
          ),
        ],
      ),
    );
  }
}