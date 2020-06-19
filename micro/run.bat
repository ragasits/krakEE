set KRAKEE_RUNTRADE=false
set KRAKEE_PROXYENABLED=false

set KRAKEE_RUNCANDLE=false

REM java -jar payara-micro-5.201.jar --logtofile .\log\krakee --noCluster --deploy krakEE-80.war --rootDir .\root

java -jar payara-micro-5.2020.2.jar --logtofile .\log\krakee --noCluster --deploy krakEE-90.war --rootDir .\root

pause