ResourcesPath = /home/tacbanana/UniWork/Resources

CheckStyleJarPath = ${ResourcesPath}/checkstyle/checkstyle-10.3.4-all.jar
CheckStyleFormatPath = ${ResourcesPath}/checkstyle/inlinebrackets.xml

OBJS = *.java
MainFileName = Main

default:
	javac ${OBJS}
	java ${MainFileName}

checkstyle:
	java -jar ${CheckStyleJarPath} -c ${CheckStyleFormatPath} ${OBJS}

clear:
	rm *.class