cd target
JAR="cultures-language-server-*.jar"

printf "Unpacking $JAR"
rm -rf unpack
mkdir unpack
cd unpack
jar -xf ../$JAR
cp -R META-INF BOOT-INF/classes

cd BOOT-INF/classes
export LIBPATH=`find ../../BOOT-INF/lib | tr '\n' ':'`
export CP=.:$LIBPATH

# This would run it here... (as an exploded jar)
#java -classpath $CP com.example.demo.DemoApplication

# Our feature being on the classpath is what triggers it
export CP=$CP:../../../../../target/spring-boot-graal-feature-0.5.0.BUILD-SNAPSHOT.jar

printf "\n\nCompile\n"
native-image \
  -Dio.netty.noUnsafe=true \
  --no-server \
  -H:Name=demo \
  -H:+ReportExceptionStackTraces \
  --no-fallback \
  --allow-incomplete-classpath \
  --report-unsupported-elements-at-runtime \
  -cp $CP me.nithanim.cultures.lsp.Main

# -DremoveUnusedAutoconfig=true \
mv demo ../../..

printf "\n\nCompiled app (demo)\n"
cd ../../..
time ./demo