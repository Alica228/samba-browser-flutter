import 'dart:async';

import 'package:flutter/services.dart';

class SambaBrowser {
  static const MethodChannel _channel = MethodChannel('samba_browser');

  /// List all directories and files under a given URL.
  /// All shares will be returned by their full URL.
  /// The [domain] parameter is only required under Android.
  static Future<List> getShareList(
      String url, String domain, String username, String password) async {
    Map<String, String> args = {
      'url': url,
      'domain': domain,
      'username': username,
      'password': password,
    };

    return await _channel.invokeMethod('getShareList', args);
  }

  /// Save a file with a specified name under a given folder.
  /// After the download has finished, the local file URL will be returned.
  /// The [domain] parameter is only required under Android.
  static Future<String> saveFile(String saveFolder, String fileName, String url,
      String domain, String username, String password) async {
    Map<String, String> args = {
      'saveFolder': saveFolder.endsWith('/') ? saveFolder : '$saveFolder/',
      'fileName':
          fileName.startsWith('/') ? fileName.replaceFirst('/', '') : fileName,
      'url': url,
      'domain': domain,
      'username': username,
      'password': password,
    };

    return await _channel.invokeMethod('saveFile', args);
  }

  /// Upload a file with a specified name under a given folder.
  /// The [domain] parameter is only required under Android.
  static Future<String> uploadFile(String uploadFolder, String uploadFileName,
      String filePath, String domain, String username, String password) async {
    Map<String, String> args = {
      'uploadFolder':
          uploadFolder.endsWith('/') ? uploadFolder : '$uploadFolder/',
      'uploadFileName': uploadFileName.startsWith('/')
          ? uploadFileName.replaceFirst('/', '')
          : uploadFileName,
      'filePath': filePath,
      'domain': domain,
      'username': username,
      'password': password,
    };

    return await _channel.invokeMethod('uploadFile', args);
  }

  /// Delete a file with a specified name.
  /// The [domain] parameter is only required under Android.
  static Future<String> deleteFile(
      String url, String domain, String username, String password) async {
    Map<String, String> args = {
      'url': url,
      'domain': domain,
      'username': username,
      'password': password,
    };

    return await _channel.invokeMethod('deleteFile', args);
  }

  /// Copy a file with a specified sourcePath to destinationPath withour downloading.
  /// The [domain] parameter is only required under Android.
  static Future<String> copyFile(
      String sourceFilePath,
      String destinationPath,
      String destinationFile,
      String domain,
      String username,
      String password) async {
    Map<String, String> args = {
      'sourceFilePath': sourceFilePath,
      'destinationPath':
          destinationPath.endsWith('/') ? destinationPath : '$destinationPath/',
      'destinationFile': destinationFile,
      'domain': domain,
      'username': username,
      'password': password,
    };

    return await _channel.invokeMethod('copyFile', args);
  }

  /// Returns size of file under requested url.
  /// The [domain] parameter is only required under Android.
  static Future<int> sizeFile(
      String url, String domain, String username, String password) async {
    Map<String, String> args = {
      'url': url,
      'domain': domain,
      'username': username,
      'password': password,
    };

    return await _channel.invokeMethod('sizeFile', args);
  }
}
