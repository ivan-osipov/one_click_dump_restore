@echo off
reg delete "HKEY_CLASSES_ROOT\one_click_dump_restore" /f
ie4uinit.exe -ClearIconCache
pause