#/bin/sh
mvn install:install-file -DgroupId=javax.transaction -DartifactId=jta -Dversion=1.0.1B -Dpackaging=jar -Dfile=lib/jta-1.0.1B.jar
mvn install:install-file -DgroupId=android -DartifactId=android -Dversion=1.1 -Dpackaging=jar -Dfile=lib/android-1.1.jar
mvn install:install-file -DgroupId=android -DartifactId=android -Dversion=1.5 -Dpackaging=jar -Dfile=lib/android-1.5.jar
mvn install:install-file -DgroupId=android -DartifactId=android -Dversion=1.6 -Dpackaging=jar -Dfile=lib/android-1.6.jar
mvn install:install-file -DgroupId=android -DartifactId=android -Dversion=2.0 -Dpackaging=jar -Dfile=lib/android-2.0.jar
mvn install:install-file -DgroupId=android -DartifactId=android -Dversion=2.0.1 -Dpackaging=jar -Dfile=lib/android-2.0.1.jar
mvn install:install-file -DgroupId=android -DartifactId=android -Dversion=2.1 -Dpackaging=jar -Dfile=lib/android-2.1.jar
mvn install:install-file -DgroupId=com.google.android -DartifactId=maps -Dversion=3 -Dpackaging=jar -Dfile=lib/maps-3.jar
mvn install:install-file -DgroupId=com.google.android -DartifactId=maps -Dversion=4 -Dpackaging=jar -Dfile=lib/maps-4.jar
mvn install:install-file -DgroupId=com.google.android -DartifactId=maps -Dversion=5 -Dpackaging=jar -Dfile=lib/maps-5.jar
mvn install:install-file -DgroupId=com.google.android -DartifactId=maps -Dversion=6 -Dpackaging=jar -Dfile=lib/maps-6.jar
mvn install:install-file -DgroupId=com.google.android -DartifactId=maps -Dversion=7 -Dpackaging=jar -Dfile=lib/maps-7.jar
