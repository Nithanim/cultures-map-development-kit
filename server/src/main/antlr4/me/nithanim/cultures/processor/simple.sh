cd .antlr
javac -cp ~/Downloads/antlr-4.7.2-complete.jar:. *.java
java -cp ~/Downloads/antlr-4.7.2-complete.jar:. org.antlr.v4.gui.TestRig CulturesIni fullfile -trace -diagnostics -gui ../map.ini
cd ..
