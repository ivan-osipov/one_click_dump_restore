One click dump restore
===

[![Build Status](https://travis-ci.org/ivan-osipov/one_click_dump_restore.svg?branch=master)](https://travis-ci.org/ivan-osipov/one_click_dump_restore)
---
The application for windows users.
If you have many .dump or .backup files which you need to restore often instead some database, it's well for you.
After installation you will can restore your db by one click

**Installation**

1. Build distributive with command 'gradlew assembleDist' (like Travis CI)
2. Look to './build/distributions'
3. Unzip **one_click_dump_restore-X.X.zip** to your directory for software
4. (Warning: Here you will change your Windows' register)
Run file **installHere.bat** (naturally for Windows only)
5. Change default settings for your db inside one_click_dump_restore-1.0.jar (app.properties)
6. When you do right mouse click on *.dump or *.backup file you will see new item of menu **Restore DB**

**Default Settings**

db.type=POSTGRES
db.host=localhost
db.port=5432
db.user=postgres
db.password=postgres
db.name=example
db.utils.home=C:/Program Files/PostgreSQL/9.5/bin/
