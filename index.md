### Welcome to Health NLP Examples and Demos
This is a hosting site for Health NLP / cTAKES components Examples and Demos



### Available Hosted Demos
Component | URL | Description | Host | Source Code
------------ | ------------- | ------------- | ------------- | -------------
ctakes-dictionary-lookup-fast | [http://35.162.219.104:8080/index.jsp](http://35.162.219.104:8080/index.jsp) | This example outputs the entire CAS from the cTAKES pipeline using the Dictionary Lookup Fast Pipeline. | AWS EC2 | [Git](https://github.com/healthnlp/examples/tree/master/ctakes-web-client)
ctakes-temporal | [http://54.68.117.30:8080/index.jsp](http://54.68.117.30:8080/index.jsp) | This example outputs the entire CAS from the cTAKES pipeline using the Temporal Component. | AWS EC2 | [Git](https://github.com/healthnlp/examples/tree/master/ctakes-temporal-demo)
ctakes-assertion | TBA | TBA | TBA | TBA
ctakes-dependency-parser | TBA | TBA | TBA | TBA

### Adding a hosted new demo
1. Setup an EC2 instance for demo purposes
1. Ensure Java and Maven is installed
1. Git clone the project from the repo
1. export MAVEN_OPTS='-Xmx1500m -XX:+UseConcMarkSweepGC'
1. mvn jetty:run
1. Launch browser to your instance:8080/index.jsp
1. Update this index page to include your example

### Support and Help
1. Submit a issue via: https://issues.apache.org/jira/browse/CTAKES
1. Email chenpei@apache.org if you would like to add an demo instance


***

<div style="vertical-align:top">Demos and examples brought to you by: </div><br/><a href="http://www.wiredinformatics.com"><img src="http://www.wiredinformatics.com/wp-content/uploads/2015/08/wi_logo_thumbnail1.png" alt="Wired Informatics"> 
