# CGSynt (Counterexample Guided Synthesis)
-------------------------------------
## Prerequisites
To set up the prerequisites please follow the steps outlined on https://github.com/ultimate-pa/ultimate/wiki/Installation up to and including the "Create Ultimate workspace" heading. This will help you install all the libraries need.

-------------------------------------
## Setup
Once you have clone and set up the Ultimate Library, clone this repo into another folder outside the Ultimate Library repo folder. In eclipse import the projects contained in this repo into the workspace which has all the other library projects.

-------------------------------------
## Common Troubleshooting steps
1. Make sure that the jdk bin directory is in your PATH variable -> run `echo $PATH` on linux or mac
2. If you have mulltiple versions of the JDK installed make sure that the workspace is configured to compile with JDK 1.8. Once the projects is set up this can be found in the context menu as follows: `Window -> Preferences -> Java -> Installed JREs`
