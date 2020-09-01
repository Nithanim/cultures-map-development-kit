mvn clean package -Pfat-jar
cd target
mkdir configs
java -agentlib:native-image-agent=config-output-dir=configs -jar native-*
mv configs ../
cd ..

