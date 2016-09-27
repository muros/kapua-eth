#*******************************************************************************
# /*******************************************************************************
#  * Copyright (c) 2011, 2016 Eurotech and/or its affiliates
#  *
#  * All rights reserved. This program and the accompanying materials
#  * are made available under the terms of the Eclipse Public License v1.0
#  * which accompanies this distribution, and is available at
#  * http://www.eclipse.org/legal/epl-v10.html
#  *
#  * Contributors:
#  *      Eurotech - initial API and implementation
#  *******************************************************************************/
#*******************************************************************************
echo 'create tar.gz'
cd ../../../../../org.eclipse.kapua.assembly/target
curl -O http://mirror.nohup.it/apache/tomcat/tomcat-8/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz
tar xvzf apache-tomcat-${TOMCAT_VERSION}.tar.gz
cp ../../org.eclipse.kapua.app.console/target/console-${KAPUA_VERSION}.war apache-tomcat-${TOMCAT_VERSION}/webapps/console.war
cp ../../org.eclipse.kapua.app.api/target/api.war apache-tomcat-${TOMCAT_VERSION}/webapps/api.war
mv apache-tomcat-${TOMCAT_VERSION} kapua-tomcat-apps
tar -zcvf kapua-tomcat-apps.tar.gz kapua-tomcat-apps
rm -rf apache-tomcat-${TOMCAT_VERSION}*
rm -rf kapua-tomcat-apps
echo 'deploy to vagrant machine'
cd ../../org.eclipse.kapua.dev-tools/src/main/vagrant
vagrant ssh -c "echo \"sudo mkdir -p /usr/local/tomcat\"
	sudo mkdir -p /usr/local/tomcat
	echo \"sudo rm -rf /usr/local/tomcat/apache-tomcat-*\"
	sudo rm -rf /usr/local/tomcat/apache-tomcat-*
	echo \"sudo rm -rf /usr/local/tomcat/apps\"
	sudo rm -rf /usr/local/tomcat/apps
	echo \"sudo tar -xvzf /kapua/org.eclipse.kapua.assembly/target/kapua-tomcat-apps.tar.gz -C /usr/local/tomcat\"
	sudo tar -xvzf /kapua/org.eclipse.kapua.assembly/target/kapua-tomcat-apps.tar.gz -C /usr/local/tomcat
	echo \"sudo chown -R vagrant:vagrant /usr/local/tomcat/kapua-tomcat-apps*\"
	sudo chown -R vagrant:vagrant /usr/local/tomcat/kapua-tomcat-apps*"