import json
import os
import sys
import subprocess

# Check for dependencies

file = open("depend.json")
data = json.load(file)
file.close()

for name in data["list"]:
    if os.path.exists("lib\\" + name) == False:
        print("You need to get ", name)
        sys.exit()

# Compile code

javac = ""
javac = "javac --module-path lib\\javafx-sdk-11.0.2\\lib --add-modules javafx.base,javafx.media,javafx.graphics,javafx.swing,javafx.controls,javafx.fxml,javafx.web -cp lib\\fontawesomefx-8.2.jar;..\\src ..\\src\\kryptos3dit\\"
end_at = " -d ..\\" + "src"

subprocess.run(javac + "Main.java" + end_at, shell=True)
subprocess.run(javac + "crypto\\" + "AES256CTR.java" + end_at, shell=True)
subprocess.run(javac + "filters\\" + "Filters.java" + end_at, shell=True)
subprocess.run(javac + "ui\\" + "homepageController.java" + end_at, shell=True)
subprocess.run(javac + "ui\\" + "encryptionController.java" + end_at, shell=True)
subprocess.run(javac + "ui\\" + "UifxmlController.java" + end_at, shell=True)

# Copy manifest.txt file to src\ for .jar creation
copy_manifest = "copy manifest.txt ..\\src"
subprocess.run(copy_manifest, shell=True)

# Make .jar file
jar = "cd ..\\src && jar cvfm app.jar manifest.txt kryptos3dit"
subprocess.run(jar, shell=True)

# Remove existing output directory
remove_output = "rmdir /Q /S ..\\output"
subprocess.run(remove_output, shell=True)

# Make output directory
output = "mkdir ..\\output"
subprocess.run(output, shell=True)

# Copy dependencies to output
copy_dependencies = "Xcopy /E /I lib ..\\output\\lib"
subprocess.run(copy_dependencies, shell=True)

# Copy manifest.txt to output
copy_manifest = "copy manifest.txt ..\\output"
subprocess.run(copy_manifest, shell=True)

# Copy produced .jar file to output
copy_jar = "copy ..\\src\\app.jar ..\\output"
subprocess.run(copy_jar, shell=True)

# Copy launcher to output
copy_launcher = "copy launcher.bat ..\\output"
subprocess.run(copy_launcher, shell=True)

# Make temp folder in output
make_temp = "mkdir ..\\output\\temp"

# Delete .jar and manifest.txt from src folder
del_files = "del ..\\src\\app.jar && del ..\\src\\manifest.txt"
subprocess.run(del_files, shell=True)

print("Done!")