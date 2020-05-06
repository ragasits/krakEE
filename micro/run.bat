set KRAKEE_RUNTRADE=false
set KRAKEE_PROXYENABLED=false

set KRAKEE_RUNCANDLE=true


java -jar payara-micro-5.201.jar --logtofile .\log\krakee --noCluster --deploy krakEE-80.war

REM java -jar payara-micro-5.201.jar --logtofile .\log\krakee --noCluster --deploy krakEE-60.war --domainConfig .\config\domain.xml --rootDir .\config
pause