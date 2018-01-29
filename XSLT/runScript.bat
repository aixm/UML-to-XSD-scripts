ECHO OFF
set OLDCLASSPATH=%CLASSPATH%
set CLASSPATH=c:\Other Program Files\xalan-j_2_7_1\xercesImpl.jar;c:\Other Program Files\xalan-j_2_7_1\xml-apis.jar;c:\Other Program Files\xalan-j_2_7_1\xalan.jar;
ECHO ON

java -Xms64m -Xmx512m org.apache.xalan.xslt.Process -in AIXM_Features_EA.xsd -xsl "AIXM_Features_Script.xsl" -out AIXM_Features.xsd


ECHO OFF
set CLASSPATH=%OLDCLASSPATH%
set OLDCLASSPATH=
ECHO ON