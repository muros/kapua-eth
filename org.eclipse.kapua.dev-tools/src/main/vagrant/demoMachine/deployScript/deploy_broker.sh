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
vagrant ssh -c "echo \"sudo mkdir -p /usr/local/activemq\"
	sudo mkdir -p /usr/local/activemq
	echo \"sudo rm -rf /usr/local/activemq/apache-activemq-5.14.0\"
	sudo rm -rf /usr/local/activemq/apache-activemq-5.14.0
	sudo mkdir -p /usr/local/activemq
	echo \"sudo rm -rf /usr/local/activemq/kapua-broker-*\"
	sudo rm -rf /usr/local/activemq/kapua-broker-*
	echo \"sudo tar xvzf /kapua/org.eclipse.kapua.broker.archive/target/kapua-broker-* -C /usr/local/activemq\"
	sudo tar xvzf /kapua/org.eclipse.kapua.broker.archive/target/kapua-broker-* -C /usr/local/activemq
	echo \"sudo chown -R vagrant:vagrant /usr/local/activemq/kapua-broker-*\"
	sudo chown -R vagrant:vagrant /usr/local/activemq/kapua-broker-*"