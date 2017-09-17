# Run on Linux

Update UMLS account settings

```
cd ctakes-temporal-demo
mvn clean install
export ctakes_umlsuser='<your-username>'
export ctakes_umlspw='<your-password>'
./configure.sh
MAVEN_OPTS="-Xmx4096m -Xms3072m -Xss128M -XX:+CMSClassUnloadingEnabled -XX:-UseGCOverheadLimit" mvn jetty:run
mvn jetty:run
```

You don't need to re-export the `umlsuser` and `umlspassword` after the configuration has been updated. You need to run `./configure.sh` every time `mvn clean` was issued.




