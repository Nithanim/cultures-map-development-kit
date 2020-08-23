mvn clean package -Pfat-jar
cd target
java -agentlib:native-image-agent=config-output-dir=native-image-configs -jar cultures-language-server-*
mv native-image-configs ../
cd ..

