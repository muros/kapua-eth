#*******************************************************************************
# Copyright (c) 2011, 2016 Eurotech and/or its affiliates and others
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
echo 'cleanup the symbolic links to Kapua jars'
for name in $(find lib/extra -type l); do echo remove symbolic link from ./$name; rm ./$name; done;
echo 'create the symbolic links to Kapua jars'
echo '===> copy dependencies for broker core'
for name in $(ls /kapua/org.eclipse.kapua.broker.core/target/dependency/ | grep -Ev 'jaxb-|activemq-|org.eclipse.kapua'); do echo create symbolic link from ./lib/extra$name /kapua/org.eclipse.kapua.broker.core/target/dependency/$name; ln -s /kapua/org.eclipse.kapua.broker.core/target/dependency/$name ./lib/extra/$name; done;
echo '===> copy dependencies for guice'
for name in $(ls /kapua/org.eclipse.kapua.locator.guice/target/dependency/ | grep -Ev 'jaxb-|activemq-|org.eclipse.kapua'); do echo create symbolic link from ./lib/extra/$name /kapua/org.eclipse.kapua.locator.guice/target/dependency/$name; ln -s /kapua/org.eclipse.kapua.locator.guice/target/dependency/$name ./lib/extra/$name; done;
echo '===> copy kapua modules'
for name in $(find /kapua -name 'org.eclipse.kapua*.jar' | grep target | grep -Ev 'test|console|api|dependency|mysql'); do jar_name=$(echo - $name - $name | awk -F"/" '{print $NF}'); echo create symbolic link from ./lib/extra/${jar_name} ${name}; ln -s $name ./lib/extra/$jar_name; done;
echo 'create the symbolic links to Kapua jars DONE'
echo 'remove old config links'
rm /usr/local/activemq/apache-activemq-ACTIVEMQ_VERSION/conf/activemq.xml
echo 'create new config links'
ln -s /kapua/org.eclipse.kapua.assembly/src/main/resources/conf/broker/activemq.xml /usr/local/activemq/apache-activemq-ACTIVEMQ_VERSION/conf/activemq.xml
