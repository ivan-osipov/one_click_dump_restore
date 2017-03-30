@echo off
reg delete "HKEY_CLASSES_ROOT\one_click_dump_restore" /f
reg add "HKEY_CLASSES_ROOT\one_click_dump_restore\shell\Восстановить БД\command" /ve /d "%~dp0run.bat %%1"
reg add "HKEY_CLASSES_ROOT\one_click_dump_restore\DefaultIcon" /ve /d "%~dp0icon.ico"
reg add "HKEY_CLASSES_ROOT\.backup" /ve /d one_click_dump_restore /f
reg add "HKEY_CLASSES_ROOT\.backup" /v "DefaultValue" /d "one_click_dump_restore"  /f
reg add "HKEY_CLASSES_ROOT\.dump" /ve /d one_click_dump_restore /f
reg add "HKEY_CLASSES_ROOT\.dump" /v "DefaultValue" /d "one_click_dump_restore" /f
ie4uinit.exe -ClearIconCache
pause