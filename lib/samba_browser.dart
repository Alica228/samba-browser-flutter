
import 'dart:async';
import 'dart:typed_data';

import 'package:flutter/services.dart';

class SambaBrowser {
  static const MethodChannel _channel = MethodChannel('samba_browser');

  /// List all directories and files under a given URL.
  /// All shares will be returned by their full URL.
  /// The [domain] parameter is only required under Android.
  static Future<List> getShareList(String url, String domain, String username, String password) async {
    Map<String, String> args = {
      'url': url,
      'domain': domain,
      'username': username,
      'password': password,
    };

    final List drives = await _channel.invokeMethod('getShareList', args);
    return drives;
  }

  /// Save a file with a specified name under a given folder.
  /// After the download has finished, the local file URL will be returned.
  /// The [domain] parameter is only required under Android.
  static Future<String> saveFile(String saveFolder, String fileName, String url, String domain, String username, String password) async {
    Map<String, String> args = {
      'saveFolder': saveFolder.endsWith('/') ? saveFolder : '$saveFolder/',
      'fileName': fileName.startsWith('/') ? fileName.replaceFirst('/', '') : fileName,
      'url': url,
      'domain': domain,
      'username': username,
      'password': password,
    };

    final String filePath = await _channel.invokeMethod('saveFile', args);
    return filePath;
  }

  /// Upload a file with a specified name under a given folder.
  /// The [domain] parameter is only required under Android.
  static Future<String> uploadFile(String uploadFolder, String fileName, String path, String domain, String username, String password) async {
    Map<String, String> args = {
      'uploadFolder': uploadFolder.endsWith('/') ? uploadFolder : '$uploadFolder/',
      'fileName': fileName.startsWith('/') ? fileName.replaceFirst('/', '') : fileName,
      'path': path,
      'domain': domain,
      'username': username,
      'password': password,
    };

    final String filePath = await _channel.invokeMethod('uploadFile', args);
    return filePath;
  }

  /// Delete a file with a specified name.
  /// The [domain] parameter is only required under Android.
  static Future<String> deleteFile(String fileName, String domain, String username, String password) async {
    Map<String, String> args = {
      'fileName': fileName.startsWith('/') ? fileName.replaceFirst('/', '') : fileName,
      'domain': domain,
      'username': username,
      'password': password,
    };

    final String filePath = await _channel.invokeMethod('deleteFile', args);
    return filePath;
  }
}
