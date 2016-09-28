#*******************************************************************************
# Copyright (c) 2011, 2016 Eurotech and/or its affiliates
#
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
#
# Contributors:
#     Eurotech - initial API and implementation
#
#*******************************************************************************
echo 'create tar.gz'
cd ../../../../../org.eclipse.kapua.app.console.archive/target
curl -O http://mirror.nohup.it/apache/tomcat/tomcat-8/v8.0.37/bin/apache-tomcat-8.0.37.tar.gz
tar xvzf apache-tomcat-8.0.37.tar.gz
cp ../../org.eclipse.kapua.app.console/target/console.war apache-tomcat-8.0.37/webapps
mv apache-tomcat-8.0.37 console
tar -zcvf console.tar.gz console
rm -rf apache-tomcat-8.0.37*
rm -rf console
echo 'deploy to vagrant machine'
cd ../../org.eclipse.kapua.dev-tools/src/main/vagrant/demoMachine
pwd
vagrant ssh -c "echo \"sudo mkdir -p /usr/local/tomcat\"
	sudo mkdir -p /usr/local/tomcat
	echo \"sudo rm -rf /usr/local/tomcat/apache-tomcat-*\"
	sudo rm -rf /usr/local/tomcat/apache-tomcat-*
	echo \"sudo rm -rf /usr/local/tomcat/console\"
	sudo rm -rf /usr/local/tomcat/console
	echo \"sudo tar -xvzf /kapua/org.eclipse.kapua.app.console.archive/target/console.tar.gz -C /usr/local/tomcat\"
	sudo tar -xvzf /kapua/org.eclipse.kapua.app.console.archive/target/console.tar.gz -C /usr/local/tomcat
	echo \"sudo chown -R vagrant:vagrant /usr/local/tomcat/console*\"
	sudo chown -R vagrant:vagrant /usr/local/tomcat/console*"